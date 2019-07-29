package com.task.coindeskdemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.coindeskdemo.service.impl.CoinDeskService;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public abstract class CoinDeskDemoApplicationTest {

    @SpyBean
    protected ObjectMapper mapper;

    @MockBean
    protected RestTemplate restTemplate;

    @Bean
    public CoinDeskService coinDeskService() {
        return new CoinDeskService(restTemplate, mapper);
    }
}
