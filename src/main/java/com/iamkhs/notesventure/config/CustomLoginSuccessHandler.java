package com.iamkhs.notesventure.config;

import com.iamkhs.notesventure.entities.User;
import com.iamkhs.notesventure.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    public CustomLoginSuccessHandler(UserService userService) {
        this.userService = userService;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {


        CustomUserDetails userDetails;
        Collection<? extends GrantedAuthority> authorities;

        if (authentication.getPrincipal() instanceof OAuth2User oAuth2User){
            authorities = oAuth2User.getAuthorities();

            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");

            saveUserToDB(name, email);
        }
        else {
            userDetails= (CustomUserDetails) authentication.getPrincipal();
            authorities = userDetails.getAuthorities();
        }

        String redirectUrl = getRedirectUrl(request, authorities);

        new DefaultRedirectStrategy().sendRedirect(request, response, redirectUrl);

    }

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


    private void saveUserToDB(String name, String email){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (userService.getUser(email) != null){
            return;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("dummy")); // I have to provide dummy password for saving to DB
        user.setRole("ROLE_USER");
        user.setUserRegisterDate(LocalDateTime.now());
        user.setEnable(true);

        this.userService.saveUser(user);
    }
}
