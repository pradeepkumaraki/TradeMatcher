package org.example.book;

import org.example.model.Order;
import org.example.model.Side;
import org.example.model.UnfilledOrder;

import java.util.List;

public interface OrderBook {

    String getInstrument();

    void addBuyOrder(Order order);

    void addSellOrder(Order order);

    Order peekBestBuy();

    Order peekBestSell();

    void removeEmptyOrder(Order order, Side side);

    List<UnfilledOrder> getUnfilledOrders();
}
