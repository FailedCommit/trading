package com.upstox.trade.service.util.thread;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upstox.trade.bean.Trade;
import com.upstox.trade.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.upstox.trade.bean.Constants.BAR_INTERVAL_IN_SECONDS;

public class ReaderThread implements Runnable {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BlockingQueue<List<Trade>> rawTradesQueue;
    private Long barStart;
    private final AtomicInteger barNum = new AtomicInteger(1);
    private Long barInterval = 0L;

    @Autowired
    public ReaderThread(Long barStart,
                        BlockingQueue<List<Trade>> rawTradesQueue) {
        this.barStart = barStart;
        this.rawTradesQueue = rawTradesQueue;
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
        try {
            // File reading goes here
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = classloader.getResourceAsStream("trades.json");
            InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(streamReader);
            Long startTime = barStart;
            List<Trade> tradesForInterval = new ArrayList<>();
            for (String line; (line = reader.readLine()) != null; ) {
                Long timeElapsed = 0L;
                while(timeElapsed < BAR_INTERVAL_IN_SECONDS) {
                    Long currentTime = System.currentTimeMillis();
                    final Trade trade = objectMapper.readValue(line, Trade.class);
                    trade.setBarNum(barNum.intValue());
                    tradesForInterval.add(trade);
                    timeElapsed = currentTime - startTime;
                    if(timeElapsed >= BAR_INTERVAL_IN_SECONDS) {
                        startTime = currentTime;
                        List<Trade> copy = new ArrayList<>();
                        copy.addAll(tradesForInterval);
                        rawTradesQueue.put(copy);
                        tradesForInterval.clear();
                        barNum.getAndIncrement();
                        System.out.println("############## Reader Thread: RawTradesQueue is populated ############");
                    }
                }
            }
            System.out.println("############## Reader Thread: Processed the complete file................... ############");
        } catch (IOException | InterruptedException ex) {
            System.out.println("Producer Read INTERRUPTED");
        }
    }
}
