package com.iamkhs.notesventure.controller;

import com.iamkhs.notesventure.entities.User;
import com.iamkhs.notesventure.helper.Messages;
import com.iamkhs.notesventure.helper.UserServiceUtil;
import com.iamkhs.notesventure.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Objects;
/**
 * This controller class handles user profile updates, including name and password changes.
 * It is responsible for rendering the user profile update page and processing user input.
 *
 */

@Controller
@RequestMapping("/user")
@AllArgsConstructor
public class UpdateUserDetails {

    private final UserService userService;
    private PasswordEncoder passwordEncoder;
    private UserServiceUtil userServiceUtil;



    @GetMapping("/update-profile")
    public String updateProfileHandler(Principal principal, @AuthenticationPrincipal OAuth2User oAuth2User, Model model){

        // Get the currently logged-in user
        User loggedUser = this.userServiceUtil.getLoggedUser(principal, oAuth2User);

        model.addAttribute("user", loggedUser);
        return "/user/edit-profile";
    }


    @PostMapping("/update-user")
    public String updateProfile(@ModelAttribute User user, @RequestParam String oldPassword, Principal principal, @AuthenticationPrincipal OAuth2User oAuth2User, HttpSession session){
        User loggedUser = this.userServiceUtil.getLoggedUser(principal, oAuth2User);

        boolean updated;

        if (oldPassword.isEmpty() && user.getPassword().isEmpty()) {
            updated = !Objects.equals(user.getName(), loggedUser.getName());
            updateUserNameOnly(loggedUser, user);
        } else {
            updated = updateUserNameAndPassword(loggedUser, user, oldPassword, session);
        }

        if (updated) {
            String successMessage = (oldPassword.isEmpty() && user.getPassword().isEmpty()) ?
                    "Profile Updated Successfully" : "Password Updated Successfully";
            session.setAttribute("message", new Messages(successMessage, "success"));
        }
        return "redirect:/user/update-profile";
    }

    // Only name updating
    private void updateUserNameOnly(User loggedUser, User user) {
        // if both old and new name are same then no need for updating
        if (loggedUser.getName().equals(user.getName())) return;

        loggedUser.setName(user.getName());
        loggedUser.setPassword(loggedUser.getPassword());
        loggedUser.setConfirmPassword(loggedUser.getPassword());
        userService.saveUser(loggedUser);
    }

    // Name and Password updating
    private boolean updateUserNameAndPassword(User loggedUser, User user, String oldPassword, HttpSession session) {
        if (passwordEncoder.matches(oldPassword, loggedUser.getPassword())) {
            loggedUser.setName(user.getName());
            loggedUser.setPassword(passwordEncoder.encode(user.getPassword()));
            loggedUser.setConfirmPassword(passwordEncoder.encode(user.getPassword()));
            userService.saveUser(loggedUser);
            return true;
        } else {
            session.setAttribute("message", new Messages("Old Password Not Match!", "danger"));
            return false;
        }
    }

}
