package com.iamkhs.notesventure.service.impl;

import com.iamkhs.notesventure.entities.User;
import com.iamkhs.notesventure.repository.UserRepository;
import com.iamkhs.notesventure.service.UserService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User saveUser(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public User getUser(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAllByRole("ROLE_USER");
    }

    @Override
    public boolean isUserVerified(String code) {

        // Fetching the User from Database by the Verification Code
        User user = this.userRepository.findUserByVerificationCode(code);

        if (user == null) {
            // User not found
            return false;
        } else {
            var currentTime = LocalDateTime.now();
            var userRegisterDate = user.getUserRegisterDate();
            // Calculate the time difference (in minutes) between the current time and the registration time
            long minutes = Duration.between(userRegisterDate, currentTime).toMinutes();

            if (minutes >= 1) {
                // The verification code is expired because 1 minute passed!
                // Delete the User so that He can register again.
                this.userRepository.deleteById(user.getId());
                return false;
            } else {
                // Everything is fine now updating the User
                user.setEnable(true);
                user.setVerificationCode(null); // setting the verification code null so that the user cannot try to verify again
                user.setPassword(user.getPassword()); // couldn't save the user with blank password & confirm password field
                user.setConfirmPassword(user.getPassword());

                // Saving the User to database
                this.saveUser(user);
                return true;
            }
        }
    }


    // Verifying the User by OTP
    @Override
    public boolean verifyUserByOTP(String otp) {
        User user = this.userRepository.findUserByVerificationCode(otp);
        if (user == null){
            // Invalid OTP
            return false;
        }else{
            // User is verified
            user.setVerificationCode(null); // setting the verification code null
            user.setPassword(user.getPassword());

            // Saving the User to Database
            this.userRepository.save(user);
            return true;
        }
    }
}
