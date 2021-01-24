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

import static com.upstox.trade.bean.Constants.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class TradeService {
    private static final BlockingQueue<List<Trade>> rawTradesQueue = new LinkedBlockingQueue<>(1);
    private static final BlockingQueue<Map<String, List<OrderResponse>>> computedTradesQueue = new LinkedBlockingQueue<>(1);
    private static volatile Long barStart;
    private static Map<String, Runnable> threads;

    public void process(OrderRequest request) {
        threads = initialize();
        if(nonNull(threads)) {
            final ReaderThread readerThread = (ReaderThread)threads.get(READER_THREAD);
            readerThread.setBarInterval(request.getInterval());
            new Thread(readerThread).start();

            final WorkerThread workerThread = (WorkerThread)threads.get(WORKER_THREAD);
            workerThread.addSymbolToTrack(request.getSymbol());
            new Thread(workerThread).start();

            final PublisherThread publisherThread = (PublisherThread)threads.get(PUBLISHER_THREAD);
            // Can set Client info here
            new Thread(publisherThread).start();
        }
    }

    private static Map<String, Runnable> initialize() {
        if(isNull(barStart)) {
            synchronized (TradeService.class) {
                if(isNull(barStart)) {
                    barStart = System.currentTimeMillis();
                    final WorkerThread workerThread = new WorkerThread(rawTradesQueue, computedTradesQueue);
                    final ReaderThread readerThread = new ReaderThread(barStart, rawTradesQueue);
                    final PublisherThread publisherThread = new PublisherThread(computedTradesQueue);
                    threads = new HashMap<>();
                    threads.put(READER_THREAD, readerThread);
                    threads.put(WORKER_THREAD, workerThread);
                    threads.put(PUBLISHER_THREAD, publisherThread);
                }
            }
        }
        return threads;
    }
}
