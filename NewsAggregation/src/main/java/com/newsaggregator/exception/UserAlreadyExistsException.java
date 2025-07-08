package com.newsaggregator.exception;

public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String email) {
        super("User already exists with email: " + email);
    }
}

