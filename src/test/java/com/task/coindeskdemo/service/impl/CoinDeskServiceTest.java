package com.task.coindeskdemo.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.coindeskdemo.CoinDeskDemoApplication;
import com.task.coindeskdemo.model.BitcoinRate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("{test}")
@TestPropertySource("classpath:application-default.yml")
public class CoinDeskServiceTest {
    public static final String USD = "USD";
    private static final String RESPONSE_BODY = "BODY";
    private BitcoinRate actualRate;
    private CoinDeskService coinDeskService;

    @MockBean
    private ObjectMapper mapper;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private ResponseEntity<String> response;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Rule
    public ErrorCollector collector = new ErrorCollector();


    @Before
    public void setUp() throws Exception {
        coinDeskService = new CoinDeskService(restTemplate, mapper);
        actualRate = new BitcoinRate();
        actualRate.setCode("USD");
        actualRate.setSymbol("$");
        actualRate.setRate("1234.56");
        actualRate.setDescription("Description");
        actualRate.setRateFloat(12.98D);
    }

    @Test
    public void fetchCurrentBitcoinRate() throws IOException {
        //Arrange
        JsonNode root = Mockito.mock(JsonNode.class, Mockito.CALLS_REAL_METHODS);
        when(root.path(anyString())).thenReturn(root);
        when(root.get(anyString())).thenReturn(root);
        when(mapper.readTree(anyString())).thenReturn(root);
        when(mapper.convertValue(any(JsonNode.class), eq(BitcoinRate.class))).thenReturn(actualRate);
        when(response.getBody()).thenReturn(RESPONSE_BODY);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        //Act
        BitcoinRate expectedRate = coinDeskService.fetchCurrentBitcoinRate(USD);

        //Assert
        collector.checkThat(expectedRate.getCode(), is(equalTo(actualRate.getCode())));
        collector.checkThat(expectedRate.getDescription(), is(equalTo(actualRate.getDescription())));
        collector.checkThat(expectedRate.getRate(), is(equalTo(actualRate.getRate())));
        collector.checkThat(expectedRate.getRateFloat(), is(equalTo(actualRate.getRateFloat())));
        collector.checkThat(expectedRate.getSymbol(), is(equalTo(actualRate.getSymbol())));
    }

    @Test
    public void fetchHistoricalRateDetails() {
    }

    @Test
    public void fetchSupportedCurrencies() {
    }
}