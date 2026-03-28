package org.example.model;

import java.math.BigDecimal;

public class Match {

    private final String buyOrderId;
    private final String sellOrderId;
    private final String instrument;
    private final int quantity;
    private final BigDecimal executionPrice;

    public Match(String buyOrderId, String sellOrderId, String instrument, int quantity, BigDecimal executionPrice) {
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.instrument = instrument;
        this.quantity = quantity;
        this.executionPrice = executionPrice;
    }

    public String getBuyOrderId() {
        return buyOrderId;
    }

    public String getSellOrderId() {
        return sellOrderId;
    }

    public String getInstrument() {
        return instrument;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getExecutionPrice() {
        return executionPrice;
    }

    @Override
    public String toString() {
        return "Match{" +
                "buy='" + buyOrderId + '\'' +
                ", sell='" + sellOrderId + '\'' +
                ", inst='" + instrument + '\'' +
                ", qty=" + quantity +
                ", px=" + executionPrice +
                '}';
    }
}
