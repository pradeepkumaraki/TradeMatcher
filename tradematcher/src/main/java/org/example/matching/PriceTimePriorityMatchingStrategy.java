package org.example.matching;

import org.example.book.OrderBook;
import org.example.model.Match;
import org.example.model.MatchResult;
import org.example.model.Order;
import org.example.model.Side;

import java.math.BigDecimal;

public class PriceTimePriorityMatchingStrategy implements MatchingStrategy {

    @Override
    public void match(Order order, OrderBook book, MatchResult result) {
        if (order.getSide() == Side.BUY) {
            matchBuy(order, book, result);
        } else {
            matchSell(order, book, result);
        }
    }

    private void matchBuy(Order buy, OrderBook book, MatchResult result) {
        while (buy.getQuantity() > 0) {
            Order sell = book.peekBestSell();
            if (sell == null || !canMatch(buy.getPrice(), sell.getPrice(), Side.BUY)) {
                break;
            }

            int qty = Math.min(buy.getQuantity(), sell.getQuantity());
            BigDecimal px = getPrice(buy, sell, Side.BUY);

            result.addMatch(new Match(
                buy.getOrderId(), 
                sell.getOrderId(),
                buy.getInstrument(), 
                qty, 
                px
            ));

            buy.setQuantity(buy.getQuantity() - qty);
            sell.setQuantity(sell.getQuantity() - qty);

            if (sell.getQuantity() == 0) {
                book.removeEmptyOrder(sell, Side.SELL);
            }
        }
    }

    private void matchSell(Order sell, OrderBook book, MatchResult result) {
        while (sell.getQuantity() > 0) {
            Order buy = book.peekBestBuy();
            if (buy == null || !canMatch(sell.getPrice(), buy.getPrice(), Side.SELL)) {
                break;
            }

            int qty = Math.min(sell.getQuantity(), buy.getQuantity());
            BigDecimal px = getPrice(buy, sell, Side.SELL);

            result.addMatch(new Match(
                buy.getOrderId(), 
                sell.getOrderId(),
                sell.getInstrument(), 
                qty, 
                px
            ));

            sell.setQuantity(sell.getQuantity() - qty);
            buy.setQuantity(buy.getQuantity() - qty);

            if (buy.getQuantity() == 0) {
                book.removeEmptyOrder(buy, Side.BUY);
            }
        }
    }

    protected boolean canMatch(BigDecimal incomingPx, BigDecimal restingPx, Side side) {
        return side == Side.BUY
                ? incomingPx.compareTo(restingPx) >= 0
                : incomingPx.compareTo(restingPx) <= 0;
    }

    protected BigDecimal getPrice(Order buy, Order sell, Side side) {
        return side == Side.BUY ? sell.getPrice() : buy.getPrice();
    }
}
