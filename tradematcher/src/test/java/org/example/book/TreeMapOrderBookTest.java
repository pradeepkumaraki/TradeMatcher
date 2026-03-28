package org.example.book;

import org.example.model.Order;
import org.example.model.Side;
import org.example.model.UnfilledOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TreeMapOrderBookTest {

    private TreeMapOrderBook book;
    private Instant baseTime;

    @BeforeEach
    void setUp() {
        book = new TreeMapOrderBook("AAPL");
        baseTime = Instant.EPOCH;
    }

    @Test
    void peekBestSell_returnsLowestPriceFirst() {
        book.addSellOrder(new Order("S1", "AAPL", Side.SELL, 50, new BigDecimal("151.00"), baseTime));
        book.addSellOrder(new Order("S2", "AAPL", Side.SELL, 50, new BigDecimal("149.00"), baseTime.plusSeconds(1)));

        var best = book.peekBestSell();
        assertNotNull(best);
        assertEquals("S2", best.getOrderId());
        assertEquals(new BigDecimal("149.00"), best.getPrice());
    }

    @Test
    void peekBestBuy_returnsHighestPriceFirst() {
        book.addBuyOrder(new Order("B1", "AAPL", Side.BUY, 50, new BigDecimal("149.00"), baseTime));
        book.addBuyOrder(new Order("B2", "AAPL", Side.BUY, 50, new BigDecimal("151.00"), baseTime.plusSeconds(1)));

        var best = book.peekBestBuy();
        assertNotNull(best);
        assertEquals("B2", best.getOrderId());
        assertEquals(new BigDecimal("151.00"), best.getPrice());
    }

    @Test
    void peekBestSell_emptyBook_returnsNull() {
        assertNull(book.peekBestSell());
    }

    @Test
    void peekBestBuy_emptyBook_returnsNull() {
        assertNull(book.peekBestBuy());
    }

    @Test
    void removeEmptyOrder_removesFilledOrder() {
        var order = new Order("S1", "AAPL", Side.SELL, 50, new BigDecimal("150.00"), baseTime);
        book.addSellOrder(order);
        order.setQuantity(0);

        book.removeEmptyOrder(order, Side.SELL);

        assertNull(book.peekBestSell());
    }

    @Test
    void getUnfilledOrders_returnsAllRestingOrders() {
        book.addBuyOrder(new Order("B1", "AAPL", Side.BUY, 100, new BigDecimal("150.00"), baseTime));
        book.addSellOrder(new Order("S1", "AAPL", Side.SELL, 50, new BigDecimal("151.00"), baseTime.plusSeconds(1)));

        var unfilled = book.getUnfilledOrders();

        assertEquals(2, unfilled.size());
        assertTrue(unfilled.stream().anyMatch(u -> "B1".equals(u.getOrderId()) && u.getRemainingQuantity() == 100));
        assertTrue(unfilled.stream().anyMatch(u -> "S1".equals(u.getOrderId()) && u.getRemainingQuantity() == 50));
    }

    @Test
    void getInstrument_returnsCorrectInstrument() {
        assertEquals("AAPL", book.getInstrument());
    }

    @Test
    void peekBestSell_samePrice_returnsEarliestTimestampFirst() {
        book.addSellOrder(new Order("S1", "AAPL", Side.SELL, 50, new BigDecimal("150.00"), baseTime));
        book.addSellOrder(new Order("S2", "AAPL", Side.SELL, 50, new BigDecimal("150.00"), baseTime.plusSeconds(1)));

        var best = book.peekBestSell();
        assertNotNull(best);
        assertEquals("S1", best.getOrderId());
    }

    @Test
    void peekBestBuy_samePrice_returnsEarliestTimestampFirst() {
        book.addBuyOrder(new Order("B1", "AAPL", Side.BUY, 50, new BigDecimal("150.00"), baseTime.plusSeconds(1)));
        book.addBuyOrder(new Order("B2", "AAPL", Side.BUY, 50, new BigDecimal("150.00"), baseTime));

        var best = book.peekBestBuy();
        assertNotNull(best);
        assertEquals("B2", best.getOrderId());
    }

    @Test
    void getUnfilledOrders_excludesZeroQuantityOrders() {
        var order = new Order("S1", "AAPL", Side.SELL, 50, new BigDecimal("150.00"), baseTime);
        book.addSellOrder(order);
        order.setQuantity(0);

        var unfilled = book.getUnfilledOrders();
        assertEquals(0, unfilled.size());
    }
}
