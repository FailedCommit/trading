package com.upstox.trade.service;

import com.upstox.trade.bean.Trade;
import com.upstox.trade.dto.OrderRequest;
import com.upstox.trade.dto.OrderResponse;
import com.upstox.trade.service.util.thread.PublisherThread;
import com.upstox.trade.service.util.thread.ReaderThread;
import com.upstox.trade.service.util.thread.WorkerThread;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.isNull;

@Service
public class TradeService {
    // Can consider PriorityBlockingQueue to sort by timestamps, will help with 15 sec blocks
    private final BlockingQueue<List<Trade>> rawTradesQueue = new LinkedBlockingQueue<>(10);
    private final BlockingQueue<Map<String, List<OrderResponse>>> computedTradesQueue = new LinkedBlockingQueue<>(10);
//    private final BlockingQueue<String> symbolsToTrack = new LinkedBlockingQueue<>(1000);
    final WorkerThread workerThread = new WorkerThread(rawTradesQueue, computedTradesQueue);
    private volatile Long barStart;

    public void process(OrderRequest request) throws InterruptedException {

        // This method should ideally, just update the publisherTread that
        // Some new client has placed order for  new trade, so the published batch should contain that Trade info starting immediately
        workerThread.addSymbolToTrack(request.getSymbol());
        initialize();

        // Thread 1 (ReaderThread) reads the JSON line by line and hands over the read line to Thread 2 (WorkerThread)
        // WorkerThread computes OHLC packets based on 15 sec interval
        // Publisher Thread reads the batches created by WorkerThread and

        // Prepare a batch with assumption that there is only one user investing in only one Trade

        // Update for One user subscribing multiple trades

        // Update for multiple users subscribing multiple trades

        // How to terminate the program
    }

    private void initialize() {
        if(isNull(barStart)) {
            synchronized (this) {
                if(isNull(barStart)) {
                    barStart = System.currentTimeMillis();
                    new Thread(new ReaderThread(barStart, rawTradesQueue)).start();
                    new Thread(workerThread).start();
                    new Thread(new PublisherThread(computedTradesQueue)).start();
                }
            }
        }
    }
}
