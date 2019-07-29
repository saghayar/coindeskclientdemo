package com.task.coindeskdemo.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@Builder
public class SupportedCurrency {
    private String currency;
    private String country;

}
