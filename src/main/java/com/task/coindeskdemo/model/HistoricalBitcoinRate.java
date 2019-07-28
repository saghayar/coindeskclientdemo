package com.task.coindeskdemo.model;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class HistoricalBitcoinRate {
    private Double lowest;
    private Double highest;
}
