package com.sdaprojvideostreamingservice.sdaproj.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String home() { return "main"; }

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/register")
    public String register() { return "register"; }

    @GetMapping("/watchlist")
    public String watchlist() { return "watchlist"; }

    @GetMapping("/profile")
    public String profile() { return "profile"; }

    @GetMapping("/subscription")
    public String subscription() { return "subscription"; }

    @GetMapping("/videos/{id}")
    public String videoDetail() { return "video-detail"; }

    @GetMapping("/admin")
    public String admin() { return "admin"; }

    @GetMapping("/forgot-password")
    public String forgotPassword() { return "forgot-password"; }

    @GetMapping("/reset-password")
    public String resetPassword() { return "reset-password"; }
}
