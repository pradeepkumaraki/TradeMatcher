package org.example.engine;

import org.example.book.OrderBook;
import org.example.book.OrderBookFactory;
import org.example.matching.MatchingStrategy;
import org.example.matching.PriceTimePriorityMatchingStrategy;
import org.example.model.MatchResult;
import org.example.model.Order;
import org.example.model.Side;
import org.example.model.UnfilledOrder;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MatchingEngine {

    private final MatchingStrategy strategy;
    private final OrderBookFactory factory;
    private final Map<String, OrderBook> books = new HashMap<>();

    public MatchingEngine() {
        this(new PriceTimePriorityMatchingStrategy(), OrderBookFactory.defaultFactory());
    }

    public MatchingEngine(MatchingStrategy strategy) {
        this(strategy, OrderBookFactory.defaultFactory());
    }

    public MatchingEngine(MatchingStrategy strategy, OrderBookFactory factory) {
        this.strategy = strategy;
        this.factory = factory;
    }

    public MatchResult processBatch(List<Order> orders) {
        List<Order> sortedOrders = orders.stream()
                .sorted(Comparator.comparing(Order::getTimestamp))
                .collect(Collectors.toList());

        MatchResult result = new MatchResult();

        for (Order order : sortedOrders) {
            processOrder(order, result);
        }

        for (OrderBook book : books.values()) {
            for (UnfilledOrder u : book.getUnfilledOrders()) {
                result.addUnfilledOrder(u);
            }
        }

        return result;
    }

    public void processOrder(Order order, MatchResult result) {
        if (order.getQuantity() <= 0) return;

        var book = books.computeIfAbsent(order.getInstrument(), factory::create);
        
        strategy.match(order, book, result);

        if (order.getQuantity() > 0) {
            if (order.getSide() == Side.BUY) {
                book.addBuyOrder(order);
            } else {
                book.addSellOrder(order);
            }
        }
    }

    public void reset() {
        books.clear();
    }
}
