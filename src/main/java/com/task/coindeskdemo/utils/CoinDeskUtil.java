package com.task.coindeskdemo.utils;

import com.task.coindeskdemo.service.ICoinDeskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoinDeskUtil {

    @Autowired
    private ICoinDeskService coinDeskService;

    public boolean isCurrencyValid(final String currency) {
        if (currency == null || currency.isEmpty()) return false;

        return coinDeskService.fetchSupportedCurrencies()
                .stream()
                .anyMatch(c -> currency.equalsIgnoreCase(c.getCurrency()));
    }
}
