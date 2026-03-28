package org.example.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderTest {

    private final Instant validTimestamp = Instant.now();
    private final BigDecimal validPrice = new BigDecimal("100");

    @Test
    void constructor_withValidArguments_succeeds() {
        assertDoesNotThrow(() ->
                new Order("ID1", "INSTR1", Side.BUY, 10, validPrice, validTimestamp)
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void constructor_withInvalidOrderId_throwsException(String orderId) {
        var ex = assertThrows(IllegalArgumentException.class, () ->
                new Order(orderId, "INSTR1", Side.BUY, 10, validPrice, validTimestamp)
        );
        assertEquals("Order ID cannot be null or empty", ex.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void constructor_withInvalidInstrument_throwsException(String instrument) {
        var ex = assertThrows(IllegalArgumentException.class, () ->
                new Order("ID1", instrument, Side.BUY, 10, validPrice, validTimestamp)
        );
        assertEquals("Instrument cannot be null or empty", ex.getMessage());
    }

    @Test
    void constructor_withNullSide_throwsException() {
        var ex = assertThrows(IllegalArgumentException.class, () ->
                new Order("ID1", "INSTR1", null, 10, validPrice, validTimestamp)
        );
        assertEquals("Side cannot be null", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    void constructor_withInvalidQuantity_throwsException(int quantity) {
        var ex = assertThrows(IllegalArgumentException.class, () ->
                new Order("ID1", "INSTR1", Side.BUY, quantity, validPrice, validTimestamp)
        );
        assertEquals("Initial quantity must be positive", ex.getMessage());
    }

    @Test
    void constructor_withNullPrice_throwsException() {
        var ex = assertThrows(IllegalArgumentException.class, () ->
                new Order("ID1", "INSTR1", Side.BUY, 10, null, validTimestamp)
        );
        assertEquals("Price must be positive", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-0.01", "-100"})
    void constructor_withInvalidPrice_throwsException(String priceStr) {
        var price = new BigDecimal(priceStr);
        var ex = assertThrows(IllegalArgumentException.class, () ->
                new Order("ID1", "INSTR1", Side.BUY, 10, price, validTimestamp)
        );
        assertEquals("Price must be positive", ex.getMessage());
    }

    @Test
    void constructor_withNullTimestamp_throwsException() {
        var ex = assertThrows(IllegalArgumentException.class, () ->
                new Order("ID1", "INSTR1", Side.BUY, 10, validPrice, null)
        );
        assertEquals("Timestamp cannot be null", ex.getMessage());
    }

    @Test
    void setQuantity_toNegative_throwsException() {
        Order order = new Order("ID1", "INSTR1", Side.BUY, 10, validPrice, validTimestamp);
        var ex = assertThrows(IllegalArgumentException.class, () ->
                order.setQuantity(-1)
        );
        assertEquals("Quantity cannot be negative", ex.getMessage());
    }

    @Test
    void setQuantity_toZeroOrPositive_succeeds() {
        Order order = new Order("ID1", "INSTR1", Side.BUY, 10, validPrice, validTimestamp);
        assertDoesNotThrow(() -> order.setQuantity(0));
        assertEquals(0, order.getQuantity());
        assertDoesNotThrow(() -> order.setQuantity(100));
        assertEquals(100, order.getQuantity());
    }
}
