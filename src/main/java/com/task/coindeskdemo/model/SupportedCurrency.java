package com.task.coindeskdemo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@Getter
@EqualsAndHashCode
public class SupportedCurrency {
    private String currency;
    private String country;

}
