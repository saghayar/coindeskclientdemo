package com.task.coindeskdemo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "code",
        "symbol",
        "rate",
        "description",
        "rate_float"
})
public class BitcoinRate {

    @JsonProperty("code")
    private String code;
    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("rate")
    private String rate;
    @JsonProperty("description")
    private String description;
    @JsonProperty("rate_float")
    @JsonDeserialize(as = Double.class)
    private Double rateFloat;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();
}

