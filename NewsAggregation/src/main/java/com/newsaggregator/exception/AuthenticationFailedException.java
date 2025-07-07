package com.newsaggregator.exception;

public class AuthenticationFailedException extends Exception {
    public AuthenticationFailedException() {
        super("Invalid email or password.");
    }
}

