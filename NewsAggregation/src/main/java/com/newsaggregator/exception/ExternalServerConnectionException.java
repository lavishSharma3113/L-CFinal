package com.newsaggregator.exception;

public class ExternalServerConnectionException extends Exception {
    public ExternalServerConnectionException(String serverName) {
        super("Failed to connect or retrieve data from external server: " + serverName);
    }
}

