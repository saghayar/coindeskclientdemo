package com.task.coindeskdemo.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.task.coindeskdemo.CoinDeskDemoApplicationTest;
import com.task.coindeskdemo.exception.BitcoinRateFetchException;
import com.task.coindeskdemo.model.BitcoinRate;
import com.task.coindeskdemo.model.BitcoinRateStatistics;
import com.task.coindeskdemo.model.SupportedCurrency;
import com.task.coindeskdemo.util.Constants;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CoinDeskServiceTest extends CoinDeskDemoApplicationTest {

    private static final String DESCRIPTION = "Description";
    private static final String RESPONSE_BODY = "BODY";
    private static final String BPI = "bpi";
    private static final String USD = "USD";
    private static final LocalDate START_DATE = LocalDate.of(2019, 06, 27);
    private static final LocalDate END_DATE = LocalDate.of(2019, 07, 27);
    private static String historicalRate;
    private BitcoinRate actualRate;
    private BitcoinRateStatistics actualRateStatistics;
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Rule
    public ErrorCollector collector = new ErrorCollector();
    @Value("classpath:historical-rates.json")
    private Resource stateFile;
    @Autowired
    private CoinDeskService coinDeskService;
    @MockBean
    private ResponseEntity<String> stringResponseEntity;
    @MockBean
    private ResponseEntity<List<SupportedCurrency>> currencyResponseEntity;
    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        actualRate = BitcoinRate.builder()
                .code(USD)
                .symbol("$")
                .rate("1234.56")
                .description(DESCRIPTION)
                .rateFloat(12.98D)
                .build();

        actualRateStatistics = BitcoinRateStatistics.builder()
                .highest(12563.215)
                .lowest(9422.4517)
                .build();

        historicalRate = new String(Files.readAllBytes(Paths.get(stateFile.getFile().getCanonicalPath())));
    }

    @Test
    public void whenFetchCurrentBitcoinRateThenReturnSuccess() throws IOException {
        //Arrange
        commonStubsFetchCurrentRate();
        when(stringResponseEntity.getStatusCode()).thenReturn(HttpStatus.OK);

        //Act
        BitcoinRate expectedRate = coinDeskService.fetchCurrentBitcoinRate(USD);

        //Assert
        verify(mapper, times(1)).readTree(RESPONSE_BODY);
        verify(mapper, times(1)).convertValue(any(JsonNode.class), eq(BitcoinRate.class));
        verifyNoMoreInteractions(mapper);

        collector.checkThat(stringArgumentCaptor.getAllValues(), is(equalTo(Arrays.asList(RESPONSE_BODY, BPI, USD))));

        collector.checkThat(expectedRate.getCode(), is(equalTo(actualRate.getCode())));
        collector.checkThat(expectedRate.getDescription(), is(equalTo(actualRate.getDescription())));
        collector.checkThat(expectedRate.getRate(), is(equalTo(actualRate.getRate())));
        collector.checkThat(expectedRate.getRateFloat(), is(equalTo(actualRate.getRateFloat())));
        collector.checkThat(expectedRate.getSymbol(), is(equalTo(actualRate.getSymbol())));
    }


    @Test
    public void whenFetchCurrentBitcoinRateThenThrowBitcoinRateFetchException() throws IOException {
        thrown.expect(BitcoinRateFetchException.class);
        thrown.expectMessage(Constants.ERR_TRY_AGAIN_LATER.value());

        //Arrange
        commonStubsFetchCurrentRate();
        when(stringResponseEntity.getStatusCode()).thenReturn(HttpStatus.SERVICE_UNAVAILABLE);

        //Act
        coinDeskService.fetchCurrentBitcoinRate(USD);
    }

    @Test
    public void whenFetchHistoricalRateDetailsThenReturnSuccess() throws IOException {
        //Arrange
        when(stringResponseEntity.getBody()).thenReturn(historicalRate);
        when(stringResponseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        when(restTemplate.getForEntity(stringArgumentCaptor.capture(), eq(String.class))).thenReturn(stringResponseEntity);

        //Act
        BitcoinRateStatistics expectedStatistics = coinDeskService.
                fetchHistoricalRateDetails(USD, START_DATE, END_DATE);

        //Assert
        verify(mapper, times(1)).readTree(historicalRate);

        collector.checkThat(stringArgumentCaptor.getAllValues().size(), is(equalTo(1)));

        collector.checkThat(expectedStatistics.getHighest(), is(equalTo(actualRateStatistics.getHighest())));
        collector.checkThat(expectedStatistics.getLowest(), is(equalTo(actualRateStatistics.getLowest())));
    }

    @Test
    public void whenFetchHistoricalRateDetailsThenThrowBitcoinRateFetchException() throws IOException {
        thrown.expect(BitcoinRateFetchException.class);
        thrown.expectMessage(Constants.ERR_TRY_AGAIN_LATER.value());

        //Arrange
        when(stringResponseEntity.getBody()).thenReturn(historicalRate);
        when(stringResponseEntity.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.getForEntity(stringArgumentCaptor.capture(), eq(String.class))).thenReturn(stringResponseEntity);

        //Act
        coinDeskService.fetchHistoricalRateDetails(USD, START_DATE, END_DATE);
    }

    @Test
    public void fetchSupportedCurrencies() {
        //Arrange
        SupportedCurrency actualSupportedCurrency = SupportedCurrency.builder().country("US").currency("USD").build();
        when(currencyResponseEntity.getBody()).thenReturn(Collections.singletonList(actualSupportedCurrency));
        when(restTemplate.exchange(stringArgumentCaptor.capture(), eq(HttpMethod.GET), eq(null),
                any(ParameterizedTypeReference.class))).thenReturn(currencyResponseEntity);

        //Act
        List<SupportedCurrency> expectedSupportedCurrencies = coinDeskService.fetchSupportedCurrencies();

        //Assert
        collector.checkThat(stringArgumentCaptor.getAllValues().size(), is(equalTo(1)));
        collector.checkThat(actualSupportedCurrency.getCurrency(), is(equalTo(expectedSupportedCurrencies.get(0).getCurrency())));
        collector.checkThat(actualSupportedCurrency.getCountry(), is(equalTo(expectedSupportedCurrencies.get(0).getCountry())));
    }

    private void commonStubsFetchCurrentRate() throws IOException {
        JsonNode root = Mockito.mock(JsonNode.class, Mockito.CALLS_REAL_METHODS);
        when(root.path(stringArgumentCaptor.capture())).thenReturn(root);
        when(root.get(stringArgumentCaptor.capture())).thenReturn(root);
        doReturn(root).when(mapper).readTree(stringArgumentCaptor.capture());
        doReturn(actualRate).when(mapper).convertValue(root, BitcoinRate.class);
        when(stringResponseEntity.getBody()).thenReturn(RESPONSE_BODY);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(stringResponseEntity);
    }
}
