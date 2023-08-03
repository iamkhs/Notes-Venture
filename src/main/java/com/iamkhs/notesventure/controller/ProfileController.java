package com.iamkhs.notesventure.controller;

import com.iamkhs.notesventure.entities.User;
import com.iamkhs.notesventure.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/user")
@AllArgsConstructor
public class ProfileController {

    private final UserService userService;

    // User profile settings handler
    @GetMapping("/0/u/profile")
    public String profileSetting(Principal principal, Model model){
        User user = userService.getUser(principal.getName());
        model.addAttribute("user", user);
        return "/user/profile-details";
    }

}
