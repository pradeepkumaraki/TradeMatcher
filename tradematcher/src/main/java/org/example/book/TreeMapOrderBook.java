package org.example.book;

import org.example.model.Order;
import org.example.model.Side;
import org.example.model.UnfilledOrder;

import java.math.BigDecimal;
import java.util.*;

public class TreeMapOrderBook implements OrderBook {

    private final String instrument;
    private final TreeMap<BigDecimal, LinkedList<Order>> buyOrders;
    private final TreeMap<BigDecimal, LinkedList<Order>> sellOrders;

    public TreeMapOrderBook(String instrument) {
        this.instrument = instrument;
        this.buyOrders = new TreeMap<>(Comparator.reverseOrder());
        this.sellOrders = new TreeMap<>();
    }

    @Override
    public String getInstrument() {
        return instrument;
    }

    @Override
    public void addBuyOrder(Order order) {
        add(buyOrders, order);
    }

    @Override
    public void addSellOrder(Order order) {
        add(sellOrders, order);
    }

    private void add(TreeMap<BigDecimal, LinkedList<Order>> book, Order order) {
        LinkedList<Order> level = book.computeIfAbsent(order.getPrice(), k -> new LinkedList<>());

        if (level.isEmpty() || !level.getLast().getTimestamp().isAfter(order.getTimestamp())) {
            level.addLast(order);
            return;
        }

        ListIterator<Order> it = level.listIterator();
        while (it.hasNext()) {
            if (it.next().getTimestamp().isAfter(order.getTimestamp())) {
                it.previous();
                it.add(order);
                return;
            }
        }
        level.addLast(order);
    }

    @Override
    public Order peekBestSell() {
        return getBest(sellOrders);
    }

    @Override
    public Order peekBestBuy() {
        return getBest(buyOrders);
    }

    private Order getBest(TreeMap<BigDecimal, LinkedList<Order>> book) {
        if (book.isEmpty()) {
            return null;
        }
        // Since levels are sorted by price and orders within levels are sorted by time,
        // the first order of the first level is the best one.
        var bestLevel = book.firstEntry().getValue();
        return bestLevel.isEmpty() ? null : bestLevel.peekFirst();
    }

    @Override
    public void removeEmptyOrder(Order order, Side side) {
        var book = side == Side.BUY ? buyOrders : sellOrders;
        var level = book.get(order.getPrice());
        
        if (level != null) {
            // Use a direct object removal for efficiency
            level.remove(order);
            if (level.isEmpty()) {
                book.remove(order.getPrice());
            }
        }
    }

    @Override
    public List<UnfilledOrder> getUnfilledOrders() {
        List<UnfilledOrder> result = new ArrayList<>();
        collect(buyOrders, Side.BUY, result);
        collect(sellOrders, Side.SELL, result);
        return result;
    }

    private void collect(TreeMap<BigDecimal, LinkedList<Order>> book, Side side, List<UnfilledOrder> out) {
        for (LinkedList<Order> level : book.values()) {
            for (Order o : level) {
                if (o.getQuantity() > 0) {
                    out.add(new UnfilledOrder(
                        o.getOrderId(), 
                        side, 
                        instrument, 
                        o.getQuantity(), 
                        o.getPrice()
                    ));
                }
            }
        }
    }
}
