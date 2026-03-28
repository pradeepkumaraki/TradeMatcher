package org.example.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class Order {

    // A sentinel object for graceful shutdown of worker threads
    public static final Order POISON_PILL = new Order();

    private final String orderId;
    private final String instrument;
    private final Side side;
    private final BigDecimal price;
    private final Instant timestamp;

    private int quantity;

    // Private constructor for the sentinel object
    private Order() {
        this.orderId = "POISON_PILL";
        this.instrument = null;
        this.side = null;
        this.price = null;
        this.timestamp = null;
        this.quantity = -1;
    }

    public Order(String orderId, String instrument, Side side, int quantity, BigDecimal price, Instant timestamp) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty");
        }
        if (instrument == null || instrument.isBlank()) {
            throw new IllegalArgumentException("Instrument cannot be null or empty");
        }
        if (side == null) {
            throw new IllegalArgumentException("Side cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Initial quantity must be positive");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp cannot be null");
        }

        this.orderId = orderId;
        this.instrument = instrument;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = timestamp;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getInstrument() {
        return instrument;
    }

    public Side getSide() {
        return side;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + orderId + '\'' +
                ", side=" + side +
                ", qty=" + quantity +
                ", px=" + price +
                '}';
    }
}
