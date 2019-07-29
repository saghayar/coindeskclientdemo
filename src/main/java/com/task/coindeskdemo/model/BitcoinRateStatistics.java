package com.task.coindeskdemo.model;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class BitcoinRateStatistics {
    private Double lowest;
    private Double highest;
}
