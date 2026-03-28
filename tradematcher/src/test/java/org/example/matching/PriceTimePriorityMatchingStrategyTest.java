package org.example.matching;

import org.example.book.TreeMapOrderBook;
import org.example.model.MatchResult;
import org.example.model.Order;
import org.example.model.Side;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class PriceTimePriorityMatchingStrategyTest {

    private PriceTimePriorityMatchingStrategy strategy;
    private TreeMapOrderBook book;
    private MatchResult result;
    private Instant baseTime;

    @BeforeEach
    void setUp() {
        strategy = new PriceTimePriorityMatchingStrategy();
        baseTime = Instant.EPOCH;
    }

    @Test
    void match_buyMatchesBestSell() {
        book = new TreeMapOrderBook("AAPL");
        result = new MatchResult();

        book.addSellOrder(new Order("S1", "AAPL", Side.SELL, 50, new BigDecimal("150.00"), baseTime.plusSeconds(1)));
        book.addSellOrder(new Order("S2", "AAPL", Side.SELL, 50, new BigDecimal("149.00"), baseTime.plusSeconds(2)));

        var buyOrder = new Order("B1", "AAPL", Side.BUY, 60, new BigDecimal("151.00"), baseTime);
        strategy.match(buyOrder, book, result);

        assertEquals(2, result.getMatches().size());
        assertEquals(new BigDecimal("149.00"), result.getMatches().get(0).getExecutionPrice());
        assertEquals(new BigDecimal("150.00"), result.getMatches().get(1).getExecutionPrice());
        assertEquals(0, buyOrder.getQuantity());
    }

    @Test
    void match_sellMatchesBestBuy() {
        book = new TreeMapOrderBook("AAPL");
        result = new MatchResult();

        book.addBuyOrder(new Order("B1", "AAPL", Side.BUY, 30, new BigDecimal("150.00"), baseTime));
        book.addBuyOrder(new Order("B2", "AAPL", Side.BUY, 40, new BigDecimal("151.00"), baseTime.plusSeconds(1)));

        var sellOrder = new Order("S1", "AAPL", Side.SELL, 50, new BigDecimal("149.00"), baseTime.plusSeconds(2));
        strategy.match(sellOrder, book, result);

        assertEquals(2, result.getMatches().size());
        assertEquals(new BigDecimal("151.00"), result.getMatches().get(0).getExecutionPrice());
        assertEquals(new BigDecimal("150.00"), result.getMatches().get(1).getExecutionPrice());
        assertEquals(0, sellOrder.getQuantity());
    }

    @Test
    void match_noMatchWhenPriceDoesNotCross() {
        book = new TreeMapOrderBook("AAPL");
        result = new MatchResult();

        book.addSellOrder(new Order("S1", "AAPL", Side.SELL, 50, new BigDecimal("152.00"), baseTime));

        var buyOrder = new Order("B1", "AAPL", Side.BUY, 50, new BigDecimal("151.00"), baseTime.plusSeconds(1));
        strategy.match(buyOrder, book, result);

        assertTrue(result.getMatches().isEmpty());
        assertEquals(50, buyOrder.getQuantity());
    }

    @Test
    void match_emptyBook_noMatches() {
        book = new TreeMapOrderBook("AAPL");
        result = new MatchResult();

        var buyOrder = new Order("B1", "AAPL", Side.BUY, 50, new BigDecimal("150.00"), baseTime);
        strategy.match(buyOrder, book, result);

        assertTrue(result.getMatches().isEmpty());
        assertEquals(50, buyOrder.getQuantity());
    }
}
