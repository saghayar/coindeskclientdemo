package com.task.coindeskdemo.runner;

import com.task.coindeskdemo.model.BitcoinRate;
import com.task.coindeskdemo.model.HistoricalBitcoinRate;
import com.task.coindeskdemo.service.ICoinDeskService;
import com.task.coindeskdemo.utils.CoinDeskUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

import static com.task.coindeskdemo.utils.Constants.*;

@Component
@Slf4j
@Profile("dev")
public class CoinDeskCommandLineRunner implements CommandLineRunner {

    private final ICoinDeskService coinDeskService;

    private final CoinDeskUtil coinDeskUtil;

    public CoinDeskCommandLineRunner(ICoinDeskService coinDeskService, CoinDeskUtil coinDeskUtil) {
        this.coinDeskService = coinDeskService;
        this.coinDeskUtil = coinDeskUtil;
    }

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        do {
            log.info(INPUT_REQUEST.value());
            String currency = scanner.next();
            if (coinDeskUtil.isCurrencyValid(currency)) {
                //Fetching current rate
                BitcoinRate currentRate = coinDeskService.fetchCurrentBitcoinRate(currency);

                //Fetching historical rates for last 30 days
                final LocalDate endDate = LocalDate.now();
                final LocalDate startDate = endDate.minusDays(30);
                HistoricalBitcoinRate historicalRate = coinDeskService.fetchHistoricalRateDetails(currency,
                        startDate, endDate);

                //Displaying result
                long diff = ChronoUnit.DAYS.between(startDate, endDate);
                log.info(CURRENT_RATE.value(), currentRate.getRate());
                log.info(HISTORICAL_RATE_MIN.value(), diff, historicalRate.getLowest(), currency);
                log.info(HISTORICAL_RATE_MAX.value(), diff, historicalRate.getHighest(), currency);
            } else {
                log.warn(INVALID_CURRENCY.value(), currency);
            }
            log.info(NEXT_STAGE_REQUEST.value());
        } while (nextStage(scanner));

        log.info(SESSION_CLOSED.value());
    }

    /**
     * @param scanner
     * @return True if user answer 1  , then process will be repeated
     * False if user answers 2 ,then session will be terminated
     * if provided input is invalid then continue requesting input from user until to get valid  1 OR 2 answer
     * in order to decide moving forward
     */
    private boolean nextStage(Scanner scanner) {
        boolean result = true;
        String input = scanner.next();
        if (!isRetry(input)) {
            if (isTerminate(input)) {
                result = false;
            } else {
                do {
                    log.info(INVALID_COMMAND.value(), input);
                    log.info(NEXT_STAGE_REQUEST.value());
                    input = scanner.next();
                } while (!isRetry(input) && !isTerminate(input));

                result = isRetry(input) ? Boolean.TRUE : Boolean.FALSE;
            }
        }
        return result;
    }

    private boolean isRetry(String input) {
        return "1".equalsIgnoreCase(input);
    }

    private boolean isTerminate(String input) {
        return "2".equalsIgnoreCase(input);
    }
}
