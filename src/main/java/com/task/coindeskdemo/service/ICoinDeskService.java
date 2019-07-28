package com.task.coindeskdemo.service;

import com.task.coindeskdemo.model.BitcoinRate;
import com.task.coindeskdemo.model.HistoricalBitcoinRate;
import com.task.coindeskdemo.model.SupportedCurrency;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface ICoinDeskService {

    BitcoinRate fetchCurrentBitcoinRate(String currency) throws IOException;

    List<SupportedCurrency> fetchSupportedCurrencies();

    HistoricalBitcoinRate fetchHistoricalRateDetails(String currency, LocalDate startDate, LocalDate endDate) throws IOException;
}
