package com.iamkhs.notesventure.controller;

import com.iamkhs.notesventure.entities.User;
import com.iamkhs.notesventure.helper.Messages;
import com.iamkhs.notesventure.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Objects;

@Controller
@RequestMapping("/user")
@AllArgsConstructor
public class UpdateUserDetails {

    private final UserService userService;
    private PasswordEncoder passwordEncoder;

    @GetMapping("/update-profile")
    public String updateProfileHandler(Principal principal, Model model){
        User user = userService.getUser(principal.getName());
        model.addAttribute("user", user);
        return "/user/edit-profile";
    }


    @PostMapping("/update-user")
    public String updateProfile(@ModelAttribute User user, @RequestParam String oldPassword, Principal principal, HttpSession session){
        User loggedUser = userService.getUser(principal.getName());

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
