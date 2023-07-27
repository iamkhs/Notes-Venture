package com.iamkhs.notesventure.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(){
        return "home";
    }

    @GetMapping("/signup")
    public String register(){
        return "signup";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }
}
