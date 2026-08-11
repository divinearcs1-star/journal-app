package com.firstproject.journalApp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AngularController {

    @GetMapping("/auth/callback")
    public String authCallback() {
        return "forward:/index.html";
    }
}