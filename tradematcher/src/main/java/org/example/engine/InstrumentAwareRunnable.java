package org.example.engine;

import org.example.model.Order;

public interface InstrumentAwareRunnable extends Runnable {
    void send(Order order) throws InterruptedException;
    int getAssignedInstruments();
}
