package org.example.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Test
    void order_holdsAllAttributes() {
        Instant ts = Instant.EPOCH.plusSeconds(3600);
        Order order = new Order("O1", "AAPL", Side.BUY, 100, new BigDecimal("150.50"), ts);

        assertEquals("O1", order.getOrderId());
        assertEquals("AAPL", order.getInstrument());
        assertEquals(Side.BUY, order.getSide());
        assertEquals(100, order.getQuantity());
        assertEquals(new BigDecimal("150.50"), order.getPrice());
        assertEquals(ts, order.getTimestamp());

        order.setQuantity(50);
        assertEquals(50, order.getQuantity());
    }

    @Test
    void match_holdsAllAttributes() {
        Match match = new Match("B1", "S1", "AAPL", 75, new BigDecimal("149.99"));

        assertEquals("B1", match.getBuyOrderId());
        assertEquals("S1", match.getSellOrderId());
        assertEquals("AAPL", match.getInstrument());
        assertEquals(75, match.getQuantity());
        assertEquals(new BigDecimal("149.99"), match.getExecutionPrice());
    }

    @Test
    void match_toString_containsKeyFields() {
        Match match = new Match("B1", "S1", "AAPL", 50, new BigDecimal("150.00"));
        String str = match.toString();

        assertTrue(str.contains("B1"));
        assertTrue(str.contains("S1"));
        assertTrue(str.contains("AAPL"));
        assertTrue(str.contains("50"));
    }

    @Test
    void unfilledOrder_holdsAllAttributes() {
        UnfilledOrder u = new UnfilledOrder("O1", Side.SELL, "GOOGL", 25, new BigDecimal("2800.00"));

        assertEquals("O1", u.getOrderId());
        assertEquals(Side.SELL, u.getSide());
        assertEquals("GOOGL", u.getInstrument());
        assertEquals(25, u.getRemainingQuantity());
        assertEquals(new BigDecimal("2800.00"), u.getPrice());
    }

    @Test
    void matchResult_accumulatesMatchesAndUnfilled() {
        MatchResult result = new MatchResult();
        result.addMatch(new Match("B1", "S1", "AAPL", 50, new BigDecimal("150.00")));
        result.addUnfilledOrder(new UnfilledOrder("B2", Side.BUY, "AAPL", 50, new BigDecimal("149.00")));

        assertEquals(1, result.getMatches().size());
        assertEquals(1, result.getUnfilledOrders().size());
        assertNotNull(result.getMatches().get(0));
        assertNotNull(result.getUnfilledOrders().get(0));
    }

    @Test
    void side_hasBuyAndSell() {
        assertEquals(Side.BUY, Side.valueOf("BUY"));
        assertEquals(Side.SELL, Side.valueOf("SELL"));
        assertEquals(2, Side.values().length);
    }
}
