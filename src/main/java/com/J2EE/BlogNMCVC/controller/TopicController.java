package com.J2EE.BlogNMCVC.controller;

import com.J2EE.BlogNMCVC.constant.AuthorReaction;
import com.J2EE.BlogNMCVC.constant.TopicStatus;
import com.J2EE.BlogNMCVC.dto.request.TopicRequest;
import com.J2EE.BlogNMCVC.dto.response.*;
import com.J2EE.BlogNMCVC.model.Reaction;
import com.J2EE.BlogNMCVC.service.*;
import com.J2EE.BlogNMCVC.utils.DateTimeUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Controller
public class TopicController {
    @Autowired
    private TopicService topicService;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ReactionService reactionService;
    @Autowired
    private BookmarkService bookmarkService;

    @GetMapping("/topics")
    public String getTopics(
            @RequestParam(required = false) List<UUID> collectionIds,
            @RequestParam(required = false) TopicStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @ModelAttribute("isAdmin") boolean isAdmin,
            Model model
    ) {
        Page<TopicResponse> topicPage;
        boolean hasCollections = collectionIds != null && !collectionIds.isEmpty();

        if (hasCollections) {
            if (isAdmin && status != null) {
                topicPage = topicService.getAllTopicByCollectionsAndStatus(collectionIds, status, page, size);
            } else {
                topicPage = topicService.getAllTopicByCollections(collectionIds, page, size);
            }
        } else {
            if (isAdmin && status != null) {
                topicPage = topicService.getTopicsByStatus(status, page, size);
            } else {
                topicPage = topicService.getTopics(page, size);
            }
        }

        model.addAttribute("pageTitle", "Topics");
        model.addAttribute("topicPage", topicPage);
        model.addAttribute("collections", collectionService.getListCollectionsByDeletedAtIsNull());
        model.addAttribute("statuses", TopicStatus.values());
        model.addAttribute("selectedCollectionIds", collectionIds);
        model.addAttribute("selectedStatus", isAdmin ? status : null);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);

        return "topic/list";
    }

    @GetMapping("/topics/{slug}")
    public String getTopicBySlug(
            @PathVariable String slug,
            Model model) {
        TopicResponse topic = topicService.getTopicBySlug(slug);

        ReactionResponse reaction = reactionService.findReactionByTopicAndMe(topic.getId());
        UUID reactionId = reaction == null ? null : reaction.getId();

        BookmarkResponse bookmark = bookmarkService.findBookmarkByTopicAndMe(topic.getId());
        UUID bookmarkId = bookmark == null ? null : bookmark.getId();

        String publishedAt = DateTimeUtils.toStringDateTime(topic.getPublishedAt(), "dd 'Th'MM yyyy");

        List<String> paragraphs = Arrays.asList(topic.getContent().split("\\R"));

        List<String> authorReactions = Arrays.stream(AuthorReaction.values())
                .map(Enum::name)
                .toList();

        model.addAttribute("pageTitle", "Read Topic");
        model.addAttribute("topic", topic);
        model.addAttribute("authorReactions", authorReactions);
        model.addAttribute("paragraphs", paragraphs);
        model.addAttribute("publishedAt", publishedAt);
        model.addAttribute("reactionId", reactionId);
        model.addAttribute("bookmarkId", bookmarkId);

        return "topic/topic";
    }

    @GetMapping("/admin/topics/create")
    public String createTopicForm(Model model) {
        UserResponse author = userService.getMe();

        String publishedAt = DateTimeUtils.toStringDateTime(LocalDateTime.now(), "dd 'Th'MM yyyy");

        model.addAttribute("authorName", author.getName());
        model.addAttribute("publishedAt", publishedAt);
        model.addAttribute("pageTitle", "Create A New Topic");
        model.addAttribute("topicRequest", new TopicRequest());
        model.addAttribute("collections", collectionService.getListCollectionsByDeletedAtIsNull());
        model.addAttribute("statuses", TopicStatus.values());
        model.addAttribute("formAction", "/admin/topics/create");
        model.addAttribute("isEdit", false);

        return "topic/form";
    }

    @PostMapping("/admin/topics/create")
    public String createTopic(
            @Valid @ModelAttribute("topicRequest") TopicRequest topicRequest,
            BindingResult bindingResult,
            @RequestParam("thumbnail") MultipartFile thumbnail,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("collections", collectionService.getListCollectionsByDeletedAtIsNull());
            model.addAttribute("statuses", TopicStatus.values());
            model.addAttribute("isEdit", false);

            return "topic/form";
        }

        TopicResponse topic = topicService.createTopic(topicRequest, thumbnail);
        redirectAttributes.addFlashAttribute("successMessage", "Topic created successfully");

        return "redirect:/topics/" + topic.getSlug();
    }

    @GetMapping("/admin/topics/{id}/edit")
    public String updateTopicForm(@PathVariable UUID id, Model model) {
        TopicResponse topic = topicService.getTopicById(id);

        TopicRequest topicRequest = new TopicRequest();
        topicRequest.setCollectionId(topic.getCollectionId());
        topicRequest.setTitle(topic.getTitle());
        topicRequest.setContent(topic.getContent());
        topicRequest.setFootnote(topic.getFootnote());
        topicRequest.setFacebookLink(topic.getFacebookLink());
        topicRequest.setStatus(topic.getStatus());

        String publishedAt = DateTimeUtils.toStringDateTime(LocalDateTime.now(), "dd 'Th'MM yyyy");

        model.addAttribute("authorName", topic.getAuthorName());
        model.addAttribute("publishedAt", publishedAt);

        model.addAttribute("pageTitle", "Edit Topic");
        model.addAttribute("topicId", id);
        model.addAttribute("topic", topic);
        model.addAttribute("topicRequest", topicRequest);
        model.addAttribute("collections", collectionService.getListCollectionsByDeletedAtIsNull());
        model.addAttribute("statuses", TopicStatus.values());
        model.addAttribute("formAction", "/admin/topics/" + id + "/edit");
        model.addAttribute("isEdit", true);

        return "topic/form";
    }

    @PostMapping("/admin/topics/{id}/edit")
    public String updateTopic(
            @PathVariable UUID id,
            @Valid @ModelAttribute("topicRequest") TopicRequest topicRequest,
            BindingResult bindingResult,
            @RequestParam("thumbnail") MultipartFile thumbnail,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            TopicResponse topic = topicService.getTopicById(id);

            model.addAttribute("topicId", id);
            model.addAttribute("topic", topic);
            model.addAttribute("topicRequest", topicRequest);
            model.addAttribute("collections", collectionService.getListCollectionsByDeletedAtIsNull());
            model.addAttribute("statuses", TopicStatus.values());
            model.addAttribute("isEdit", true);

            return "topic/form";
        }

        TopicResponse topic = topicService.updateTopic(id, topicRequest, thumbnail);
        redirectAttributes.addFlashAttribute("successMessage", "Topic updated successfully");

        return "redirect:/topics/" + topic.getSlug();
    }

    @PostMapping("/admin/topics/{id}/delete")
    public String deleteTopic(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes
    ) {
        topicService.deleteTopic(id);
        redirectAttributes.addFlashAttribute("successMessage", "Topic deleted successfully");

        return "redirect:/topics";
    }
}