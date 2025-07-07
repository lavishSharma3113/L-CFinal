package com.newsaggregator.service.impl;

import com.newsaggregator.dao.IUserDAO;
import com.newsaggregator.dao.impl.UserDAOImpl;
import com.newsaggregator.model.User;
import com.newsaggregator.service.IUserService;

import java.util.List;

public class UserServiceImpl implements IUserService {
    private final IUserDAO userDAO = new UserDAOImpl();

    @Override
    public boolean registerUser(User user) {
        if (userDAO.getUserByEmail(user.getEmail()) == null) {
            return userDAO.createUser(user);
        }
        return false;
    }

    @Override
    public User loginUser(String email, String password) {
        return userDAO.validateLogin(email, password);
    }

    @Override
    public User getUser(String email) {
        return userDAO.getUserByEmail(email);
    }

    @Override
    public List<User> getSubscribedUsers() {
        return userDAO.getSubscribedUsers();
    }
}
