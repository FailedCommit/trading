package com.upstox.trade.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class Trade implements Cloneable {
    //{"sym":"XZECXXBT", "T":"Trade",  "P":0.01947, "Q":0.1, "TS":1538409720.3813, "side": "s", "TS2":1538409725339216503}
    @JsonProperty("sym")
    private String symbol;
    @JsonProperty("P")
    private String price;
    @JsonProperty("Q")
    private String quantity;
    @JsonProperty("TS2")
    private Long timestamp;
    private int barNum;
}
