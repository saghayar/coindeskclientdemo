package com.task.coindeskdemo.model;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BitcoinRateStatistics {
    private Double lowest;
    private Double highest;
}
