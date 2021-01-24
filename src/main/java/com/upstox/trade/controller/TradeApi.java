package com.upstox.trade.controller;

import com.upstox.trade.dto.OrderRequest;
import com.upstox.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class TradeApi {
    private final TradeService tradeService;

    @PostMapping
    public String placeOrder(@RequestBody OrderRequest request) throws InterruptedException {
        tradeService.process(request);
        return "Order Placed Successfully: Check console for real time updates";
    }
}
