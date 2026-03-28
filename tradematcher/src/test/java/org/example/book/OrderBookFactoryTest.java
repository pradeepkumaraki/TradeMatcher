package org.example.book;

import org.example.model.Order;
import org.example.model.Side;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class OrderBookFactoryTest {

    @Test
    void defaultFactory_createsTreeMapOrderBook() {
        var factory = OrderBookFactory.defaultFactory();
        var book = factory.create("AAPL");

        assertNotNull(book);
        assertTrue(book instanceof TreeMapOrderBook);
        assertEquals("AAPL", book.getInstrument());
    }

    @Test
    void defaultFactory_createsIndependentInstances() {
        var factory = OrderBookFactory.defaultFactory();
        var book1 = factory.create("AAPL");
        var book2 = factory.create("GOOGL");

        assertNotSame(book1, book2);
        assertEquals("AAPL", book1.getInstrument());
        assertEquals("GOOGL", book2.getInstrument());
    }

    @Test
    void customFactory_createsConfiguredOrderBook() {
        OrderBookFactory factory = TreeMapOrderBook::new;
        var book = factory.create("TSLA");

        assertNotNull(book);
        assertEquals("TSLA", book.getInstrument());
        book.addBuyOrder(new Order("B1", "TSLA", Side.BUY, 10, new BigDecimal("200.00"), Instant.EPOCH));
        assertEquals(1, book.getUnfilledOrders().size());
    }
}
