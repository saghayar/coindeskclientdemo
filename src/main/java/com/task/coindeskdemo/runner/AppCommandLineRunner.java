package com.task.coindeskdemo.runner;

import com.task.coindeskdemo.model.BitcoinRate;
import com.task.coindeskdemo.model.BitcoinRateStatistics;
import com.task.coindeskdemo.service.ICoinDeskService;
import com.task.coindeskdemo.util.CoinDeskUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

import static com.task.coindeskdemo.util.Constants.*;

@Component
@Slf4j
@Profile("dev")
public class AppCommandLineRunner implements CommandLineRunner {

    private final ICoinDeskService coinDeskService;

    private final CoinDeskUtil coinDeskUtil;

    public AppCommandLineRunner(ICoinDeskService coinDeskService, CoinDeskUtil coinDeskUtil) {
        this.coinDeskService = coinDeskService;
        this.coinDeskUtil = coinDeskUtil;
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        try {
            do {
                log.info(INPUT_REQUEST.value());
                String currency = scanner.next();
                if (coinDeskUtil.isCurrencyValid(currency.toUpperCase())) {
                    displayResult(currency);
                } else {
                    log.warn(INVALID_CURRENCY.value(), currency);
                }
            } while (nextStage(scanner));
        } catch (Exception ex) {
            log.error(ex.getMessage());
            if (nextStage(scanner))
                run(EMPTY.value());
        }
    }

    private void displayResult(String currency) throws IOException {
        //Fetching current rate
        final BitcoinRate currentRate = coinDeskService.fetchCurrentBitcoinRate(currency.toUpperCase());

        //Fetching historical rates for last 30 days (as per requirement)
        final LocalDate endDate = LocalDate.now();
        final LocalDate startDate = endDate.minusDays(30);
        final BitcoinRateStatistics historicalRate = coinDeskService.fetchHistoricalRateDetails(currency.toUpperCase(),
                startDate, endDate);

        //Displaying result
        final long diff = ChronoUnit.DAYS.between(startDate, endDate);
        log.info(CURRENT_RATE.value(), currentRate.getRate());
        log.info(HISTORICAL_RATE_MIN.value(), diff, historicalRate.getLowest(), currency);
        log.info(HISTORICAL_RATE_MAX.value(), diff, historicalRate.getHighest(), currency);
    }

    /**
     * @param scanner
     * @return True if user answers Y ,then process will be repeated
     *          False if user answers N ,then current session will be terminated
     *          if provided input is invalid then it will continue requesting input from user until to get valid
     *          Y OR N input in order to decide moving forward
     */
    private boolean nextStage(Scanner scanner) {
        boolean result = true;
        log.info(NEXT_STAGE_REQUEST.value());
        String input = scanner.next();
        if (!isRetry(input)) {
            if (isTerminate(input)) {
                result = false;
            } else {
                do {
                    //requesting input from user until to get valid  Y or N
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
        return YES.value().equalsIgnoreCase(input);
    }

    private boolean isTerminate(String input) {
        return NO.value().equalsIgnoreCase(input);
    }
}
