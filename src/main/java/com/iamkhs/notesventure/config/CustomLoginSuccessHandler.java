package com.iamkhs.notesventure.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String redirectUrl = request.getContextPath();

        var authorities = userDetails.getAuthorities();

        for (var auth : authorities) {
            if (auth.getAuthority().equals("ROLE_ADMIN")) {
                redirectUrl += "/admin/home";
                break;
            } else if (auth.getAuthority().equals("ROLE_USER")) {
                redirectUrl += "/user/0/u/dashboard";
                break;
            }
        }

        if (redirectUrl.isEmpty()) {
            redirectUrl += '/';
        }

        new DefaultRedirectStrategy().sendRedirect(request, response, redirectUrl);

    }
}
