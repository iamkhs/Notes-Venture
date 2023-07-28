package com.iamkhs.notesventure.controller;

import com.iamkhs.notesventure.entities.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(){
        return "home";
    }

    @GetMapping("/signup")
    public String register(Model model){
        model.addAttribute("user", new User());
        return "signup";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }
}
