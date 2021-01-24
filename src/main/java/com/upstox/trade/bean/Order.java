package com.upstox.trade.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {
    /** Type of order. */
    private String eventType;

    /** Trade name against which user wants to place order. */
    private String symbol;

    /** Interval in Seconds */
    private int interval;
}
