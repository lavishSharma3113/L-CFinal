package com.newsaggregator.dao;

import com.newsaggregator.model.User;

public interface IUserDAO {
    User getUserByEmail(String email);
    boolean createUser(User user);
    boolean validateLogin(String email, String password);
}
