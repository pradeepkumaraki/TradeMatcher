package org.example.model;

import java.math.BigDecimal;

public class UnfilledOrder {

    private final String orderId;
    private final Side side;
    private final String instrument;
    private final int remainingQuantity;
    private final BigDecimal price;

    public UnfilledOrder(String orderId, Side side, String instrument, int remainingQuantity, BigDecimal price) {
        this.orderId = orderId;
        this.side = side;
        this.instrument = instrument;
        this.remainingQuantity = remainingQuantity;
        this.price = price;
    }

    public String getOrderId() {
        return orderId;
    }

    public Side getSide() {
        return side;
    }

    public String getInstrument() {
        return instrument;
    }

    public int getRemainingQuantity() {
        return remainingQuantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "UnfilledOrder{" +
                "id='" + orderId + '\'' +
                ", side=" + side +
                ", inst='" + instrument + '\'' +
                ", qty=" + remainingQuantity +
                ", px=" + price +
                '}';
    }
}
