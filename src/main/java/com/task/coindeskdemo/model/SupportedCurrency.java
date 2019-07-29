package com.task.coindeskdemo.model;

import lombok.*;

@EqualsAndHashCode
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportedCurrency {
    private String currency;
    private String country;

}
