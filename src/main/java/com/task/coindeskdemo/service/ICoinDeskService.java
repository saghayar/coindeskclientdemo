package com.task.coindeskdemo.service;

import com.task.coindeskdemo.model.BitcoinRate;
import com.task.coindeskdemo.model.BitcoinRateStatistics;
import com.task.coindeskdemo.model.SupportedCurrency;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface ICoinDeskService {

    /**
     * @param currency
     * @return currrent bitcoin rate based on currency
     * @throws IOException
     */
    BitcoinRate fetchCurrentBitcoinRate(String currency) throws IOException;

    /**
     * @return list of supported currencies
     */
    List<SupportedCurrency> fetchSupportedCurrencies();

    /**
     * @param currency
     * @param startDate
     * @param endDate
     * @return lowest and highest bitcoin rates between startDate and endDate based on provided currency
     * @throws IOException
     */
    BitcoinRateStatistics fetchHistoricalRateDetails(String currency, LocalDate startDate, LocalDate endDate) throws IOException;
}
