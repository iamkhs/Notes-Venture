package com.iamkhs.notesventure.service;

import com.iamkhs.notesventure.entities.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    User getUser(String email);
    List<User> getAllUsers();
    boolean isUserVerified(String code);
}
