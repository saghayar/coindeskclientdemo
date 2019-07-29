package com.task.coindeskdemo.exception;

public class BitcoinRateFetchException extends RuntimeException {

    public BitcoinRateFetchException() {
        super();
    }

    public BitcoinRateFetchException(String message) {
        super(message);
    }
}
