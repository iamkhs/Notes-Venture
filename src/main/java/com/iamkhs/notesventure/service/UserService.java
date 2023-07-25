package com.iamkhs.notesventure.service;

import com.iamkhs.notesventure.entities.User;

public interface UserService {
    User saveUser(User user);
    User getUser(String email);
}
