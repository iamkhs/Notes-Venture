package com.iamkhs.notesventure.repository;

import com.iamkhs.notesventure.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String email);
    User findUserByVerificationCode(String code);
}
