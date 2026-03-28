package org.example;

import org.example.engine.MatchingEngine;
import org.example.model.Match;
import org.example.model.MatchResult;
import org.example.model.Order;
import org.example.model.Side;
import org.example.model.UnfilledOrder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Order> orders = createExampleOrders();
        MatchingEngine engine = new MatchingEngine();
        MatchResult result = engine.processBatch(orders);

        System.out.println("Matches:");
        int i = 1;
        for (Match m : result.getMatches()) {
            System.out.printf("%d. Instrument: %s, Buy: %s, Sell: %s, Quantity: %d, Price: $%s%n",
                    i++, m.getInstrument(), m.getBuyOrderId(), m.getSellOrderId(),
                    m.getQuantity(), m.getExecutionPrice());
        }

        System.out.println("\nUnfilled Orders:");
        i = 1;
        for (UnfilledOrder u : result.getUnfilledOrders()) {
            System.out.printf("%d. %s: %s, Instrument: %s, Quantity: %d, Price: $%s%n",
                    i++, u.getOrderId(), u.getSide(), u.getInstrument(),
                    u.getRemainingQuantity(), u.getPrice());
        }
    }

    private static List<Order> createExampleOrders() {
        List<Order> orders = new ArrayList<>();

        // Buy Orders
        orders.add(new Order("B1", "AAPL", Side.BUY, 100, new BigDecimal("150.00"), parseTime("10:00:00")));
        orders.add(new Order("B2", "AAPL", Side.BUY, 50, new BigDecimal("151.00"), parseTime("10:00:05")));
        orders.add(new Order("B3", "GOOGL", Side.BUY, 75, new BigDecimal("2800.00"), parseTime("10:00:10")));

        // Sell Orders
        orders.add(new Order("S1", "AAPL", Side.SELL, 80, new BigDecimal("150.50"), parseTime("10:00:02")));
        orders.add(new Order("S2", "AAPL", Side.SELL, 30, new BigDecimal("149.00"), parseTime("10:00:07")));
        orders.add(new Order("S3", "GOOGL", Side.SELL, 100, new BigDecimal("2795.00"), parseTime("10:00:12")));

        return orders;
    }

    private static Instant parseTime(String time) {
        String[] parts = time.split(":");
        return Instant.EPOCH
                .plusSeconds(Long.parseLong(parts[0]) * 3600)
                .plusSeconds(Long.parseLong(parts[1]) * 60)
                .plusSeconds(Long.parseLong(parts[2]));
    }
}
