package com.iamkhs.notesventure.controller;

import com.iamkhs.notesventure.entities.User;
import com.iamkhs.notesventure.helper.UserServiceUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/user")
@AllArgsConstructor
public class ProfileController {

    private final UserServiceUtil userServiceUtil;

    // User profile settings handler
    @GetMapping("/0/u/profile")
    public String profileSetting(Principal principal, @AuthenticationPrincipal OAuth2User oAuth2User, Model model){
        User loggedUser = this.userServiceUtil.getLoggedUser(principal, oAuth2User);
        model.addAttribute("user", loggedUser);
        return "/user/profile-details";
    }

}
