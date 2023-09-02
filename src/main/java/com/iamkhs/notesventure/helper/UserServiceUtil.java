package com.iamkhs.notesventure.helper;

import com.iamkhs.notesventure.entities.User;
import com.iamkhs.notesventure.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.security.Principal;
/**
 * This class, UserServiceUtil, is a utility class for handling user-related operations within the NotesVenture application.
 * It provides a method for retrieving the currently logged-in user based on the Spring Security Principal and OAuth2User.
 * If the user is not found in the local database based on the Principal's email, it falls back to using the email attribute from OAuth2User.
 */
@Service
@AllArgsConstructor
public class UserServiceUtil {

    private final UserService userService;

    /**
     * Retrieves the currently logged-in user.
     *
     * @param principal   The Principal object representing the currently logged-in user.
     * @param oAuth2User  The OAuth2User object representing user information from OAuth2 authentication.
     * @return            The User object representing the currently logged-in user, or null if not found.
     */
    public User getLoggedUser(Principal principal, OAuth2User oAuth2User) {

        // Get the email from the Spring Security Principal
        String email = principal.getName();

        // Try to retrieve the user from the local database based on the email
        User user = userService.getUser(email);

        // If the user is not found in the local database, fall back to using the email attribute from OAuth2User
        if (user == null){
            email = oAuth2User.getAttribute("email");
            user = userService.getUser(email);
        }

        return user;
    }
}
