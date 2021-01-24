package com.upstox.trade.service.util.thread;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upstox.trade.bean.Trade;
import com.upstox.trade.dto.OrderResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Double.valueOf;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class WorkerThread implements Runnable {
    private final BlockingQueue<List<Trade>> rawTradesQueue;
    private final BlockingQueue<Map<String, List<OrderResponse>>> computedTradesQueue;
    private final List<String> symbolsToTrack = new ArrayList<>();

    @Autowired
    public WorkerThread(BlockingQueue<List<Trade>> rawTradesQueue,
                        BlockingQueue<Map<String, List<OrderResponse>>> computedTradesQueue) {
        this.rawTradesQueue = rawTradesQueue;
        this.computedTradesQueue = computedTradesQueue;
    }

    public void addSymbolToTrack(String symbol) {
        symbolsToTrack.add(symbol);
    }

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
        final Map<String, List<OrderResponse>> computeMap = new HashMap<>();
        final Map<String, OrderResponse> symbolToLastComputedTradeMapping = new HashMap<>();

        OrderResponse lastComputedTrade = null;
        try {
            while (true) {
                /*
                   1. Take the latest record from rawTradesQueue - A
                   2. Take the latest record from the computedQueue (15 second interval) - B
                   3. Find the rawTradeStr Name from A and find the same Trade (last in the list should be the most recent one)
                   4. Compute and append to the list in ComputeTradeQueue
                 */
                final List<Trade> tradeBatchForBarInterval = rawTradesQueue.poll(5, TimeUnit.SECONDS);
                if(isNull(tradeBatchForBarInterval)) {
                    System.out.println("############## Worker Thread: RawTradesQueue is empty ############");
                    continue;
                }
                computeMap .clear();
                symbolToLastComputedTradeMapping.clear();
                // No need for isEmpty check as it will automatically block
//                final Map<String, List<OrderResponse>> computedTradeData = computedTradesQueue.take();
//                lastComputedTrade = transform(rawTrade, lastComputedTrade);
                for(Trade trade : tradeBatchForBarInterval) {
                    String currentSymbol = trade.getSymbol();
                    if(symbolsToTrack.contains(currentSymbol)) {
                        OrderResponse resp = transform(trade, symbolToLastComputedTradeMapping.get(currentSymbol));
                        symbolToLastComputedTradeMapping.put(currentSymbol, resp);
                        if(isNull(computeMap.get(currentSymbol))) {
                            computeMap.put(currentSymbol, new ArrayList<>(Arrays.asList(resp)));
                        } else {
                            final List<OrderResponse> orderResponses = computeMap.get(currentSymbol);
                            orderResponses.add(resp);
                        }
                    }
                }
                if(!computeMap.isEmpty()) {
                    final Map<String, List<OrderResponse>> copy = new HashMap<>();
                    copy.putAll(computeMap);
                    computedTradesQueue.put(copy);
                    computeMap.clear();
                    System.out.println("############## Worker Thread: ComputedTradesQueue is populated ############");
                }
            }
        } catch (InterruptedException ex) {
            System.out.println("CONSUMER INTERRUPTED");
        }
    }

    private OrderResponse transform(Trade source, OrderResponse target) {
        if(isNull(target)) {
            target = new OrderResponse();
        }

        String openingPriceStr = isNull(target.getO()) ? source.getPrice() : target.getO();
        target.setO(openingPriceStr);

        String highestPrice = (valueOf(source.getPrice()) > valueOf(target.getH())) ? source.getPrice() : target.getH();
        target.setH(highestPrice);

        String lowestPrice = (valueOf(source.getPrice()) < valueOf(target.getL())) ? source.getPrice() : target.getL();
        target.setL(lowestPrice);

        String closingPrice = source.getPrice();
        target.setC(closingPrice);

        double volume = valueOf(source.getQuantity()) + valueOf(target.getVolume());
        target.setVolume(String.valueOf(volume));

        target.setEvent("ohlc_notify");

        target.setSymbol(source.getSymbol());

        target.setBar_num(String.valueOf(source.getBarNum()));
        return target;
    }
}
