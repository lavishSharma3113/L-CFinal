package com.newsaggregator.service;

import com.newsaggregator.model.User;

public interface IUserService {
    boolean registerUser(User user);
    boolean loginUser(String email, String password);
    User getUser(String email);
}
