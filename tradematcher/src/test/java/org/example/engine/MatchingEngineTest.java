package org.example.engine;

import org.example.model.MatchResult;
import org.example.model.Order;
import org.example.model.Side;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchingEngineTest {

    private MatchingEngine engine;
    private Instant baseTime;

    @BeforeEach
    void setUp() {
        engine = new MatchingEngine();
        baseTime = Instant.EPOCH;
    }

    @Test
    void fullFill_oneToOneMatching() {
        var orders = List.of(
                new Order("B1", "AAPL", Side.BUY, 100, new BigDecimal("150.00"), baseTime),
                new Order("S1", "AAPL", Side.SELL, 100, new BigDecimal("150.00"), baseTime.plusSeconds(1))
        );

        var result = engine.processBatch(orders);

        assertEquals(1, result.getMatches().size());
        var match = result.getMatches().get(0);
        assertEquals("B1", match.getBuyOrderId());
        assertEquals("S1", match.getSellOrderId());
        assertEquals("AAPL", match.getInstrument());
        assertEquals(100, match.getQuantity());
        assertEquals(new BigDecimal("150.00"), match.getExecutionPrice());

        assertTrue(result.getUnfilledOrders().isEmpty());
    }

    @Test
    void partialFill_oneOrderMatchedAgainstMultiple() {
        var orders = List.of(
                new Order("B1", "AAPL", Side.BUY, 100, new BigDecimal("151.00"), baseTime),
                new Order("S1", "AAPL", Side.SELL, 40, new BigDecimal("150.00"), baseTime.plusSeconds(1)),
                new Order("S2", "AAPL", Side.SELL, 60, new BigDecimal("150.50"), baseTime.plusSeconds(2))
        );

        var result = engine.processBatch(orders);

        assertEquals(2, result.getMatches().size());
        assertEquals(40, result.getMatches().get(0).getQuantity());
        assertEquals(new BigDecimal("151.00"), result.getMatches().get(0).getExecutionPrice());
        assertEquals(60, result.getMatches().get(1).getQuantity());
        assertEquals(new BigDecimal("151.00"), result.getMatches().get(1).getExecutionPrice());

        assertTrue(result.getUnfilledOrders().isEmpty());
    }

    @Test
    void partialFill_sellMatchedAgainstMultipleBuys() {
        var orders = List.of(
                new Order("B1", "AAPL", Side.BUY, 30, new BigDecimal("151.00"), baseTime),
                new Order("B2", "AAPL", Side.BUY, 50, new BigDecimal("150.50"), baseTime.plusSeconds(1)),
                new Order("S1", "AAPL", Side.SELL, 60, new BigDecimal("149.00"), baseTime.plusSeconds(2))
        );

        var result = engine.processBatch(orders);

        assertEquals(2, result.getMatches().size());
        assertEquals(30, result.getMatches().get(0).getQuantity());
        assertEquals(30, result.getMatches().get(1).getQuantity());

        assertEquals(1, result.getUnfilledOrders().size());
        var unfilled = result.getUnfilledOrders().get(0);
        assertEquals("B2", unfilled.getOrderId());
        assertEquals(20, unfilled.getRemainingQuantity());
    }

    @Test
    void noMatch_priceMismatch() {
        var orders = List.of(
                new Order("B1", "AAPL", Side.BUY, 100, new BigDecimal("149.00"), baseTime),
                new Order("S1", "AAPL", Side.SELL, 100, new BigDecimal("150.00"), baseTime.plusSeconds(1))
        );

        var result = engine.processBatch(orders);

        assertTrue(result.getMatches().isEmpty());
        assertEquals(2, result.getUnfilledOrders().size());
    }

    @Test
    void multipleInstruments() {
        var orders = List.of(
                new Order("B1", "AAPL", Side.BUY, 50, new BigDecimal("150.00"), baseTime),
                new Order("B2", "GOOGL", Side.BUY, 25, new BigDecimal("2800.00"), baseTime.plusSeconds(1)),
                new Order("S1", "AAPL", Side.SELL, 50, new BigDecimal("149.00"), baseTime.plusSeconds(2)),
                new Order("S2", "GOOGL", Side.SELL, 25, new BigDecimal("2799.00"), baseTime.plusSeconds(3))
        );

        var result = engine.processBatch(orders);

        assertEquals(2, result.getMatches().size());
        assertTrue(result.getMatches().stream().anyMatch(m -> "AAPL".equals(m.getInstrument())));
        assertTrue(result.getMatches().stream().anyMatch(m -> "GOOGL".equals(m.getInstrument())));
        assertTrue(result.getUnfilledOrders().isEmpty());
    }

    @Test
    void emptyOrderBooks() {
        var orders = List.of(
                new Order("B1", "AAPL", Side.BUY, 100, new BigDecimal("150.00"), baseTime)
        );

        var result = engine.processBatch(orders);

        assertTrue(result.getMatches().isEmpty());
        assertEquals(1, result.getUnfilledOrders().size());
        assertEquals(100, result.getUnfilledOrders().get(0).getRemainingQuantity());
    }

    @Test
    void zeroQuantityOrder_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Order("B1", "AAPL", Side.BUY, 0, new BigDecimal("150.00"), baseTime)
        );
    }

    @Test
    void identicalPricesAndTimestamps_priceTimePriority() {
        var orders = List.of(
                new Order("B1", "AAPL", Side.BUY, 50, new BigDecimal("150.00"), baseTime),
                new Order("B2", "AAPL", Side.BUY, 50, new BigDecimal("150.00"), baseTime),
                new Order("S1", "AAPL", Side.SELL, 50, new BigDecimal("150.00"), baseTime.plusSeconds(1))
        );

        var result = engine.processBatch(orders);

        assertEquals(1, result.getMatches().size());
        assertEquals("B1", result.getMatches().get(0).getBuyOrderId());
        assertEquals(1, result.getUnfilledOrders().size());
        assertEquals("B2", result.getUnfilledOrders().get(0).getOrderId());
    }

    @Test
    void largeQuantityDifference() {
        var orders = List.of(
                new Order("B1", "AAPL", Side.BUY, 1_000_000, new BigDecimal("150.00"), baseTime),
                new Order("S1", "AAPL", Side.SELL, 10, new BigDecimal("149.00"), baseTime.plusSeconds(1))
        );

        var result = engine.processBatch(orders);

        assertEquals(1, result.getMatches().size());
        assertEquals(10, result.getMatches().get(0).getQuantity());
        assertEquals(1, result.getUnfilledOrders().size());
        assertEquals(999_990, result.getUnfilledOrders().get(0).getRemainingQuantity());
    }

    @Test
    void readmeExample() {
        var orders = List.of(
                new Order("B1", "AAPL", Side.BUY, 100, new BigDecimal("150.00"), baseTime),
                new Order("B2", "AAPL", Side.BUY, 50, new BigDecimal("151.00"), baseTime.plusSeconds(5)),
                new Order("B3", "GOOGL", Side.BUY, 75, new BigDecimal("2800.00"), baseTime.plusSeconds(10)),
                new Order("S1", "AAPL", Side.SELL, 80, new BigDecimal("150.50"), baseTime.plusSeconds(2)),
                new Order("S2", "AAPL", Side.SELL, 30, new BigDecimal("149.00"), baseTime.plusSeconds(7)),
                new Order("S3", "GOOGL", Side.SELL, 100, new BigDecimal("2795.00"), baseTime.plusSeconds(12))
        );

        var result = engine.processBatch(orders);

        assertEquals(3, result.getMatches().size());

        var m1 = result.getMatches().get(0);
        assertEquals("AAPL", m1.getInstrument());
        assertEquals(50, m1.getQuantity());
        assertEquals(new BigDecimal("150.50"), m1.getExecutionPrice());

        var m2 = result.getMatches().get(1);
        assertEquals("AAPL", m2.getInstrument());
        assertEquals(30, m2.getQuantity());
        assertEquals(new BigDecimal("150.00"), m2.getExecutionPrice());

        var m3 = result.getMatches().get(2);
        assertEquals("GOOGL", m3.getInstrument());
        assertEquals(75, m3.getQuantity());
        assertEquals(new BigDecimal("2800.00"), m3.getExecutionPrice());

        assertEquals(3, result.getUnfilledOrders().size());
        assertTrue(result.getUnfilledOrders().stream().anyMatch(u -> "B1".equals(u.getOrderId()) && u.getRemainingQuantity() == 70));
        assertTrue(result.getUnfilledOrders().stream().anyMatch(u -> "S1".equals(u.getOrderId()) && u.getRemainingQuantity() == 30));
        assertTrue(result.getUnfilledOrders().stream().anyMatch(u -> "S3".equals(u.getOrderId()) && u.getRemainingQuantity() == 25));
    }

    @Test
    void customStrategy_engineUsesInjectedStrategy() {
        var noOpStrategy = new org.example.matching.MatchingStrategy() {
            @Override
            public void match(Order order, org.example.book.OrderBook book, MatchResult result) {
            }
        };
        engine = new MatchingEngine(noOpStrategy);

        var orders = List.of(
                new Order("B1", "AAPL", Side.BUY, 100, new BigDecimal("150.00"), baseTime),
                new Order("S1", "AAPL", Side.SELL, 100, new BigDecimal("149.00"), baseTime.plusSeconds(1))
        );

        var result = engine.processBatch(orders);

        assertTrue(result.getMatches().isEmpty());
        assertEquals(2, result.getUnfilledOrders().size());
    }

    @Test
    void processOrder_incrementalProcessing_accumulatesMatches() {
        var result = new MatchResult();
        engine.processOrder(new Order("B1", "AAPL", Side.BUY, 50, new BigDecimal("150.00"), baseTime), result);
        engine.processOrder(new Order("S1", "AAPL", Side.SELL, 50, new BigDecimal("149.00"), baseTime.plusSeconds(1)), result);

        assertEquals(1, result.getMatches().size());
        assertEquals("B1", result.getMatches().get(0).getBuyOrderId());
        assertEquals("S1", result.getMatches().get(0).getSellOrderId());
        assertEquals(50, result.getMatches().get(0).getQuantity());
    }

    @Test
    void reset_clearsStateBetweenBatches() {
        var batch1 = List.of(
                new Order("B1", "AAPL", Side.BUY, 50, new BigDecimal("150.00"), baseTime)
        );
        engine.processBatch(batch1);

        engine.reset();

        var batch2 = List.of(
                new Order("S1", "AAPL", Side.SELL, 50, new BigDecimal("149.00"), baseTime)
        );
        var result = engine.processBatch(batch2);

        assertEquals(0, result.getMatches().size());
        assertEquals(1, result.getUnfilledOrders().size());
        assertEquals("S1", result.getUnfilledOrders().get(0).getOrderId());
    }
}
