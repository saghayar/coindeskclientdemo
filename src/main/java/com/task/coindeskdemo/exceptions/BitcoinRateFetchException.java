package com.task.coindeskdemo.exceptions;

public class BitcoinRateFetchException extends RuntimeException {

    public BitcoinRateFetchException() {
        super();
    }

    public BitcoinRateFetchException(String message) {
        super(message);
    }
}
