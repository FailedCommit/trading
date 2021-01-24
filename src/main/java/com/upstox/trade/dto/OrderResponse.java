package com.upstox.trade.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderResponse {
    private String o = "0.0";
    private String h = "0.0";
    private String l = "0.0";
    private String c = "0.0";
    private String volume = "0";
    private String event = "ohlc_notify";
    private String symbol;
    private String bar_num = "1";

    @Override
    public String toString() {
        return "{" +
                "\"o\":'" + o + '\'' +
                ", \"h\":'" + h + '\'' +
                ", \"l\":'" + l + '\'' +
                ", \"c\":'" + c + '\'' +
                ", \"volume\":'" + volume + '\'' +
                ", \"event\":'" + event + '\'' +
                ", \"symbol\":'" + symbol + '\'' +
                ", \"bar_num\":'" + bar_num + '\'' +
                '}';
    }
}
