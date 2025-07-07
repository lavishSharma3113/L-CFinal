package com.newsaggregator.dao;

import com.newsaggregator.model.User;

import java.util.List;

public interface IUserDAO {
    User getUserByEmail(String email);
    boolean createUser(User user);
    User validateLogin(String email, String password);
    List<User> getSubscribedUsers();
}
