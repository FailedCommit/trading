package com.upstox.trade.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.upstox.trade.bean.EventType;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderRequest {
    private EventType event;

    /**
     * Trade name against which user wants to place order.
     *
     * Ideally, this will be an enum to restrict user choices.
     */
    private String symbol;
    private int interval;
}
