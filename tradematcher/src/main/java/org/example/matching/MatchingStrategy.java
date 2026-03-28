package org.example.matching;

import org.example.book.OrderBook;
import org.example.model.MatchResult;
import org.example.model.Order;

public interface MatchingStrategy {

    void match(Order order, OrderBook book, MatchResult result);
}
