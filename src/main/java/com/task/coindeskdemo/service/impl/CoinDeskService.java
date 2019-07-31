package com.task.coindeskdemo.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.coindeskdemo.exception.BitcoinRateFetchException;
import com.task.coindeskdemo.model.BitcoinRate;
import com.task.coindeskdemo.model.BitcoinRateStatistics;
import com.task.coindeskdemo.model.SupportedCurrency;
import com.task.coindeskdemo.service.ICoinDeskService;
import com.task.coindeskdemo.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class CoinDeskService implements ICoinDeskService {

    private static final String BPI = "bpi";
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    @Value("${api.coinDesk.current-bitcoin-rate.url}")
    private String currentBitcoinRateUrl;
    @Value("${api.coinDesk.historical-bitcoin-rate.url}")
    private String historicalBitcoinRateUrl;
    @Value("${api.coinDesk.supported-currencies.url}")
    private String supportedCurrenciesUrl;
    private List<SupportedCurrency> supportedCurrencyList = new ArrayList<>();

    public CoinDeskService(RestTemplate restTemplate, ObjectMapper mapper) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BitcoinRate fetchCurrentBitcoinRate(String currency) throws IOException {
        ResponseEntity<String> response = restTemplate.getForEntity(String.format(currentBitcoinRateUrl, currency),
                String.class);
        if (HttpStatus.OK != response.getStatusCode()) {
            throw new BitcoinRateFetchException(Constants.ERR_TRY_AGAIN_LATER.value());
        }

        //Parsing result , extracting bpi and converting it to java bean, in case null object throw exception
        JsonNode root = mapper.readTree(response.getBody());
        JsonNode requiredCurrency = root.path(BPI).get(currency);
        Optional<BitcoinRate> rateDetailsOpt = Optional.ofNullable(mapper.convertValue(requiredCurrency,
                BitcoinRate.class));
        return rateDetailsOpt.orElseThrow(BitcoinRateFetchException::new);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BitcoinRateStatistics fetchHistoricalRateDetails(String currency, LocalDate startDate, LocalDate endDate)
            throws IOException {
        ResponseEntity<String> response = restTemplate.getForEntity(String.format(historicalBitcoinRateUrl,
                currency, startDate.toString(), endDate.toString()), String.class);

        if (HttpStatus.OK != response.getStatusCode()) {
            throw new BitcoinRateFetchException(Constants.ERR_TRY_AGAIN_LATER.value());
        }

        //Parsing result ,extracting bpi and getting it as Iterable in order to obtain Stream of JsonNode
        //Calculating  lowest ,highest value from summaryStatistics()
        JsonNode root = mapper.readTree(response.getBody());
        Iterable<JsonNode> iterable = () -> root.path(BPI).iterator();
        DoubleSummaryStatistics statistics = StreamSupport.stream(iterable.spliterator(), false)
                .mapToDouble(JsonNode::asDouble)
                .summaryStatistics();

        return BitcoinRateStatistics.builder()
                .highest(statistics.getMax())
                .lowest(statistics.getMin())
                .build();
    }

    /**
     * {@inheritDoc}
     */
    public List<SupportedCurrency> fetchSupportedCurrencies() {
        if (!supportedCurrencyList.isEmpty()) {
            return supportedCurrencyList;
        }

        ResponseEntity<List<SupportedCurrency>> response =
                restTemplate.exchange(supportedCurrenciesUrl, HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<SupportedCurrency>>() {
                        });

        if (!CollectionUtils.isEmpty(response.getBody()))
            this.supportedCurrencyList = response.getBody();

        return this.supportedCurrencyList;
    }
}
