package com.J2EE.BlogNMCVC.controller;

import com.J2EE.BlogNMCVC.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private HomeService homeService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Trang chủ");

        // Featured data
        model.addAttribute("featuredCollections", homeService.getFeaturedCollections());
        model.addAttribute("featuredTopics", homeService.getFeaturedTopics());

        return "homepage";
    }

    @GetMapping("/about-me")
    public String aboutMe(Model model) {
        model.addAttribute("pageTitle", "Về Nhà");
        return "about-me";
    }

    @GetMapping("/404")
    public String notFound() {
        return "404";
    }
}