package org.example.book;

@FunctionalInterface
public interface OrderBookFactory {

    OrderBook create(String instrument);

    static OrderBookFactory defaultFactory() {
        return TreeMapOrderBook::new;
    }
}
