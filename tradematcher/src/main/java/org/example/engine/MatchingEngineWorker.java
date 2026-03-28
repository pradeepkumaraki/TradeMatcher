package org.example.engine;

import org.example.book.OrderBook;
import org.example.book.OrderBookFactory;
import org.example.matching.MatchingStrategy;
import org.example.model.MatchResult;
import org.example.model.Order;
import org.example.model.Side;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class MatchingEngineWorker implements InstrumentAwareRunnable {

    private final BlockingQueue<Order> inputQueue;
    private final Map<String, OrderBook> books;
    private final MatchingStrategy strategy;
    private final OrderBookFactory factory;
    private final AtomicBoolean running;
    private final int workerId;
    private int assignedInstrumentsCount = 0;

    public MatchingEngineWorker(int workerId, MatchingStrategy strategy, OrderBookFactory factory) {
        this.workerId = workerId;
        this.strategy = strategy;
        this.factory = factory;
        this.inputQueue = new LinkedBlockingQueue<>();
        this.books = new HashMap<>();
        this.running = new AtomicBoolean(true);
    }

    @Override
    public void send(Order order) throws InterruptedException {
        inputQueue.put(order);
    }

    @Override
    public void run() {
        System.out.println("Worker " + workerId + " started.");
        while (running.get() || !inputQueue.isEmpty()) {
            try {
                Order order = inputQueue.take();
                
                // Poison pill check (optional, but good for clean shutdown)
                if (order == Order.POISON_PILL) {
                    running.set(false);
                    continue;
                }

                processOrder(order);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("Worker " + workerId + " error processing order: " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("Worker " + workerId + " stopped.");
    }

    private void processOrder(Order order) {
        if (order.getQuantity() <= 0) return;

        // Lazy initialization of the book. 
        // Since this worker is the ONLY thread accessing 'books', no synchronization is needed here.
        OrderBook book = books.computeIfAbsent(order.getInstrument(), k -> {
            assignedInstrumentsCount++;
            return factory.create(k);
        });

        MatchResult result = new MatchResult();
        strategy.match(order, book, result);

        // In a real system, we would publish 'result' to an output queue/bus here.
        // For now, we'll just print or log it to simulate downstream processing.
        handleMatchResult(result);

        if (order.getQuantity() > 0) {
            if (order.getSide() == Side.BUY) {
                book.addBuyOrder(order);
            } else {
                book.addSellOrder(order);
            }
        }
    }

    private void handleMatchResult(MatchResult result) {
        // This method simulates the "Output Gateway" or "Market Data Publisher"
        if (!result.getMatches().isEmpty()) {
            // System.out.println("Worker " + workerId + " generated " + result.getMatches().size() + " matches.");
        }
    }

    public void stop() {
        running.set(false);
        // Offer a poison pill to unblock the queue if it's waiting
        inputQueue.offer(Order.POISON_PILL);
    }

    @Override
    public int getAssignedInstruments() {
        return assignedInstrumentsCount;
    }
}
