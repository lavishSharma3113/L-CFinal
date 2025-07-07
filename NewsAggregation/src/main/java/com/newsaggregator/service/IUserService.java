package com.newsaggregator.service;

import com.newsaggregator.model.User;

import java.util.List;

public interface IUserService {
    boolean registerUser(User user);
    User loginUser(String email, String password);
    User getUser(String email);
    List<User> getSubscribedUsers();
}
