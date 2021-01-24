package com.upstox.trade.service.util.thread;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upstox.trade.bean.Trade;
import com.upstox.trade.dto.OrderResponse;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
public class PublisherThread implements Runnable {
    private final BlockingQueue<Map<String, List<OrderResponse>>> computedTradesQueue;

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try {
            while (true) {
                Map<String, List<OrderResponse>> clientData = computedTradesQueue.poll(5, TimeUnit.SECONDS);
                if(isNull(clientData)) {
                    System.out.println("############## Publisher Thread: ComputedTradesQueue is empty ############");
                    continue;
                }
                for (Map.Entry entry : clientData.entrySet()) {
                    System.out.println("*************** " + entry.getKey() + " ***************");
                    System.out.println(entry.getValue());
                    System.out.println("*******************************************************");
                }
            }
        } catch (InterruptedException ex) {
            System.out.println("CONSUMER INTERRUPTED");
        }
    }
}
