package com.task.coindeskdemo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@EqualsAndHashCode
public class SupportedCurrency {
    private String currency;
    private String country;

}
