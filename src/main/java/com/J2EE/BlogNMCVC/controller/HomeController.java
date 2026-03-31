package com.J2EE.BlogNMCVC.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Trang chủ");
        return "homepage";
    }

    @GetMapping("/about-me")
    public String aboutMe(Model model) {
        model.addAttribute("pageTitle", "Về Nhà");
        return "about-me";
    }

    @GetMapping("/404")
    public String test404() {
        return "404";
    }
}