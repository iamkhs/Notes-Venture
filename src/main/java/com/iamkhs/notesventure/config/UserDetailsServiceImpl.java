package com.iamkhs.notesventure.config;

import com.iamkhs.notesventure.entities.User;
import com.iamkhs.notesventure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository repository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // fetching the user from database;
        User user = repository.findUserByEmail(username);
        if (user == null){
            throw new UsernameNotFoundException("User NOT FOUND!");
        }

        return new CustomUserDetails(user);
    }
}
