package com.J2EE.BlogNMCVC.controller;

import com.J2EE.BlogNMCVC.dto.response.TopicResponse;
import com.J2EE.BlogNMCVC.dto.response.UserResponse;
import com.J2EE.BlogNMCVC.service.TopicService;
import com.J2EE.BlogNMCVC.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/search")
public class ExploreController {

    @Autowired
    private TopicService topicService;

    @Autowired
    private UserService userService;

    @GetMapping("/topic")
    public String searchTopics(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @ModelAttribute("isAdmin") boolean isAdmin,
            Model model
    ) {
        String searchKeyword = keyword.trim();
        if (searchKeyword.isEmpty()) {
            return "redirect:/topics";
        }

        Page<TopicResponse> topicPage = topicService.searchPublicTopics(searchKeyword, page, size);

        if (isAdmin) {
            topicPage = topicService.searchAllTopics(searchKeyword, page, size);
        }

        model.addAttribute("pageTitle", "Tìm kiếm bài viết");
        model.addAttribute("topicPage", topicPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("baseUrl", "/search/topic");

        return "topic/list";
    }

    @GetMapping("/user")
    public String searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @ModelAttribute("isAdmin") boolean isAdmin,
            Model model
    ) {
        String searchKeyword = keyword.trim();
        if (searchKeyword.isEmpty()) {
            return "redirect:/users";
        }

        Page<UserResponse> userPage = userService.searchPublicUsers(searchKeyword, page, size);

        if (isAdmin) {
            userPage = userService.searchAllUsers(searchKeyword, page, size);
        }

        model.addAttribute("userPage", userPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageTitle", "Tìm kiếm người dùng");
        model.addAttribute("baseUrl", "/search/user");

        return "profile/list";
    }
}