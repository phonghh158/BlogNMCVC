package com.J2EE.BlogNMCVC.controller;

import com.J2EE.BlogNMCVC.constant.UserStatus;
import com.J2EE.BlogNMCVC.dto.request.ChangeUsernameRequest;
import com.J2EE.BlogNMCVC.dto.response.BookmarkResponse;
import com.J2EE.BlogNMCVC.dto.response.TopicResponse;
import com.J2EE.BlogNMCVC.dto.response.UserResponse;
import com.J2EE.BlogNMCVC.service.BookmarkService;
import com.J2EE.BlogNMCVC.service.TopicService;
import com.J2EE.BlogNMCVC.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private BookmarkService bookmarkService;

    @Autowired
    private TopicService topicService;

    @GetMapping("/profile")
    public String getMyProfile(
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        UserResponse user = userService.getMe();

        if (user == null) {
            return "redirect:/authentication";
        }

        Page<BookmarkResponse> bookmark = bookmarkService.findAllByMe(page, 6);

        List<TopicResponse> topics = new ArrayList<>();

        for (BookmarkResponse item : bookmark.getContent()) {
            TopicResponse topic = topicService.getTopicById(item.getTopicId());

            if (topic != null) {
                topics.add(topic);
            }
        }

        model.addAttribute("pageTitle", "My Profile");
        model.addAttribute("user", user);
        model.addAttribute("bookmark", bookmark);
        model.addAttribute("topics", topics);
        model.addAttribute("isOwner", true);

        return "profile/index";
    }

    @GetMapping("/profile/{username}")
    public String getPublicProfile(
            @PathVariable
            @NotBlank(message = "Username must not be blank")
            @Size(max = 16, message = "Username must not exceed 16 characters")
            String username,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        UserResponse user = userService.getPublicUserByUsername(username);

        Page<BookmarkResponse> bookmark = bookmarkService.findAllByUser(user.getId(), page, 6);

        List<TopicResponse> topics = new ArrayList<>();

        for (BookmarkResponse item : bookmark.getContent()) {
            TopicResponse topic = topicService.getTopicById(item.getTopicId());

            if (topic != null) {
                topics.add(topic);
            }
        }

        model.addAttribute("pageTitle", "Profile");
        model.addAttribute("user", user);
        model.addAttribute("bookmark", bookmark);
        model.addAttribute("topics", topics);
        model.addAttribute("isOwner", false);

        return "profile/index";
    }

    @GetMapping("/users")
    public String getUsersByStatus(
            @RequestParam(defaultValue = "ACTIVE") UserStatus status,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Page<UserResponse> userPage = userService.getAllUsersByStatus(status, page, 9);

        model.addAttribute("pageTitle", "Khách thăm nhà");
        model.addAttribute("userPage", userPage);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("currentPage", page);
        model.addAttribute("baseUrl", "/users");

        return "profile/list";
    }

    @GetMapping("/profile/edit")
    public String showEditProfileForm(Model model) {
        UserResponse user = userService.getMe();

        if (user == null) {
            return "redirect:/authentication";
        }

        model.addAttribute("pageTitle", "Chỉnh sửa hồ sơ");
        model.addAttribute("user", user);
        model.addAttribute("isOwner", true);

        return "profile/form";
    }

    @GetMapping("/admin/users/id/{id}")
    public String getUserByIdForAdmin(
            @PathVariable UUID id,
            Model model
    ) {
        UserResponse user = userService.getUserById(id);

        model.addAttribute("profileUser", user);
        model.addAttribute("isOwner", false);

        return "profile/index";
    }

    @GetMapping("/admin/users/username/{username}")
    public String getUserByUsernameForAdmin(
            @PathVariable
            @NotBlank(message = "Username must not be blank")
            @Size(max = 16, message = "Username must not exceed 16 characters")
            String username,
            Model model
    ) {
        UserResponse user = userService.getUserByUsername(username);

        model.addAttribute("profileUser", user);
        model.addAttribute("isOwner", false);

        return "profile/index";
    }

    @PostMapping("/profile/edit/name")
    public String updateName(
            @RequestParam
            @NotBlank(message = "Name must not be blank")
            @Size(max = 96, message = "Name must not exceed 96 characters")
            String name,
            RedirectAttributes redirectAttributes
    ) {
        try {
            userService.updateName(name);
            redirectAttributes.addFlashAttribute("message", "Tên hiển thị đã được thay đổi.");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }

        return "redirect:/profile/edit";
    }

    @PostMapping("/profile/edit/avatar")
    public String updateAvatar(
            @RequestParam("avatar") MultipartFile avatar,
            RedirectAttributes redirectAttributes
    ) {
        if (avatar == null || avatar.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Ảnh không được để trống");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/profile/edit";
        }

        if (avatar.getContentType() == null || !avatar.getContentType().startsWith("image/")) {
            redirectAttributes.addFlashAttribute("message", "File phải là ảnh");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/profile/edit";
        }

        try {
            userService.updateAvatar(avatar);
            redirectAttributes.addFlashAttribute("message", "Cập nhật ảnh đại diện thành công");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }

        return "redirect:/profile/edit";
    }

    @PostMapping("/profile/edit/bio")
    public String updateBio(
            @RequestParam(required = false)
            @Size(max = 512, message = "Bio must not exceed 5000 characters")
            String bio,
            RedirectAttributes redirectAttributes
    ) {
        try {
            userService.updateBio(bio);
            redirectAttributes.addFlashAttribute("message", "Cập nhật mô tả thành công");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }

        return "redirect:/profile/edit";
    }

    @PostMapping("/profile/edit/username")
    public String changeUsername(
            @Valid ChangeUsernameRequest req,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "message",
                    bindingResult.getFieldError() != null
                            ? bindingResult.getFieldError().getDefaultMessage()
                            : "Dữ liệu không hợp lệ"
            );
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/profile/edit";
        }

        try {
            userService.changeUsername(req);
            redirectAttributes.addFlashAttribute("message", "Đổi tên người dùng thành công");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }

        return "redirect:/profile/edit";
    }

    @PostMapping("/profile/edit/lock")
    public String lockAccount(RedirectAttributes redirectAttributes) {
        try {
            userService.lockAccount();
            redirectAttributes.addFlashAttribute(
                    "message",
                    "Đã gửi email xác nhận khóa tài khoản"
            );
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }

        return "redirect:/profile/edit";
    }

    @GetMapping("/auth/lock-account")
    public String confirmLockAccount(
            @RequestParam("token") String token,
            RedirectAttributes redirectAttributes
    ) {
        try {
            userService.confirmLockAccount(token);
            return "redirect:/logout";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/profile/edit";
        }
    }
}