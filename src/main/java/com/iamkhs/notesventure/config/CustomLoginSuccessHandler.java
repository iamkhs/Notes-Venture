package com.iamkhs.notesventure.config;

import com.iamkhs.notesventure.entities.User;
import com.iamkhs.notesventure.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
/**
 * CustomLoginSuccessHandler is responsible for handling successful user authentication and determining where
 * the user should be redirected after a successful login. This handler performs several tasks when a user
 * successfully logs in:

 * 1. Determines the user's authorities based on the authentication method (OAuth2 or traditional form registered username/password).
 * 2. Creates and saves a new user to the database if the user is authenticated via OAuth2 and doesn't exist.
 * 3. Determines the appropriate redirect URL based on the user's authorities (roles).
 * 4. Performs the actual redirection using Spring Security's DefaultRedirectStrategy.

 * This class helps manage the post-authentication flow, creating users if needed and directing them to the
 * appropriate landing page based on their roles.
 */
@Component
@AllArgsConstructor
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    // Autowired UserService to manage user operations
    private final UserService userService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        CustomUserDetails userDetails;
        Collection<? extends GrantedAuthority> authorities;

        // Check if the user authenticated via OAuth2 (e.g., Google)
        if (authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
            authorities = oAuth2User.getAuthorities();

            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");

            // Save the user to the database if not already present
            saveUserToDB(name, email);
        } else {
            userDetails = (CustomUserDetails) authentication.getPrincipal();
            authorities = userDetails.getAuthorities();
        }

        // Determine the URL to redirect the user based on their authorities
        String redirectUrl = getRedirectUrl(request, authorities);

        // Perform the actual redirection
        new DefaultRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    // Helper method to construct the redirect URL based on user authorities
    private static String getRedirectUrl(HttpServletRequest request, Collection<? extends GrantedAuthority> authorities) {
        String redirectUrl = request.getContextPath();

        for (var auth : authorities) {
            if (auth.getAuthority().equals("ROLE_ADMIN")) {
                redirectUrl += "/admin/home";
                break;
            } else if (auth.getAuthority().equals("ROLE_USER") || auth.getAuthority().equals("OIDC_USER")) {
                redirectUrl += "/user/0/u/dashboard";
                break;
            }
        }

        if (redirectUrl.isEmpty()) {
            redirectUrl += '/';
        }
        return redirectUrl;
    }

    // Helper method to save a user to the database if not already present
    private void saveUserToDB(String name, String email) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (userService.getUser(email) != null) {
            return; // User already exists, no need to save
        }

        // Create a new user with default values and save it to the database
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("dummy")); // Dummy password for saving to DB
        user.setRole("ROLE_USER");
        user.setUserRegisterDate(LocalDateTime.now());
        user.setEnable(true);

        this.userService.saveUser(user);
    }
}