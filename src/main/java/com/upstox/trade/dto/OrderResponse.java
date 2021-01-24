package com.upstox.trade.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import static java.lang.Double.MAX_VALUE;
import static java.lang.Double.MIN_VALUE;
import static java.lang.String.valueOf;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderResponse {
    private String o;
    private String h = valueOf(MIN_VALUE);
    private String l = valueOf(MAX_VALUE);
    private String c = "0.0";
    private String volume = "0";
    private String event = "ohlc_notify";
    private String symbol;
    private String bar_num = "1";
}
