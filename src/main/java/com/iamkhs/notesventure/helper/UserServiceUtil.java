package com.iamkhs.notesventure.helper;

import com.iamkhs.notesventure.entities.User;
import com.iamkhs.notesventure.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@AllArgsConstructor
public class UserServiceUtil {

    private final UserService userService;

    public User getLoggedUser(Principal principal, OAuth2User oAuth2User) {
        String email = principal.getName();

        User user = userService.getUser(email);

        if (user == null){
            email = oAuth2User.getAttribute("email");
            user = userService.getUser(email);
        }

        return user;
    }
}
