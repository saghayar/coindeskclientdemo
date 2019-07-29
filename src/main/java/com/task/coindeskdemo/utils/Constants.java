package com.task.coindeskdemo.utils;

public enum Constants {

    INPUT_REQUEST("Please enter currency (USD, EUR, GBP, etc) ::"),
    INVALID_CURRENCY("Entered currency ({}) is not supported "),
    INVALID_COMMAND("Entered command ({}) is invalid please try again "),
    NEXT_STAGE_REQUEST("Retry [Y] , Terminate [N]"),
    SESSION_CLOSED("Session closed"),
    CURRENT_RATE("Current Bitcoin rate :: {}"),
    HISTORICAL_RATE_MIN("Lowest  Bitcoin rate for last {} days  :: {} for currency :: {} "),
    HISTORICAL_RATE_MAX("Highest Bitcoin rate for last {} days :: {} for currency :: {}"),
    ERR_TRY_AGAIN_LATER("Something went wrong ,please try again later"),
    YES("Y"),
    NO("N"),
    AROUND_LOG_MSG("Data Fetched from  *** {} in {} ms"),
    DISCLAIMER("This data was produced from the CoinDesk Bitcoin Price Index (USD). Non-USD currency data " +
            "converted using hourly conversion rate from openexchangerates.org");

    String value;

    Constants(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}