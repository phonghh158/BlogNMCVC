package com.J2EE.BlogNMCVC.controller;

import com.J2EE.BlogNMCVC.dto.request.CollectionRequest;
import com.J2EE.BlogNMCVC.dto.response.CollectionResponse;
import com.J2EE.BlogNMCVC.dto.response.TopicResponse;
import com.J2EE.BlogNMCVC.service.CollectionService;
import com.J2EE.BlogNMCVC.service.TopicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private TopicService topicService;

    // GET to JSON fetch by JS
    @GetMapping("/collections/{id}/detail")
    @ResponseBody
    public ResponseEntity<?> getCollectionDetail(@PathVariable UUID id) {
        CollectionResponse collection = collectionService.getCollectionByID(id);
        List<TopicResponse> topics = topicService.getFeaturedTopicsByCollection(id, 4);

        Map<String, Object> response = new HashMap<>();
        response.put("collection", collection);
        response.put("topics", topics);

        return ResponseEntity.ok(response);
    }

    // USER: list collections
    @GetMapping("/collections")
    public String getCollections(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model
    ) {
        Page<CollectionResponse> collectionPage = collectionService.getCollections(page, size);

        model.addAttribute("collectionPage", collectionPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);

        return "collection/list";
    }

    // USER: collection detail in same list page
    @GetMapping("/collections/{slug}")
    public String getCollectionBySlug(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model
    ) {
        Page<CollectionResponse> collectionPage = collectionService.getCollections(page, size);
        CollectionResponse collection = collectionService.getCollectionBySlug(slug);

        model.addAttribute("collectionPage", collectionPage);
        model.addAttribute("collection", collection);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);

        return "collection/list";
    }

    // ADMIN: list all collections, including soft deleted
    @GetMapping("/admin/collections")
    public String getCollectionsForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model
    ) {
        Page<CollectionResponse> collectionPage = collectionService.getCollectionsForAdmin(page, size);

        model.addAttribute("collectionPage", collectionPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("isAdmin", true);

        return "collection/list";
    }

    // ADMIN: show create form
    @GetMapping("/admin/collections/create")
    public String showCreateForm(Model model) {
        model.addAttribute("pageTitle", "Create Collection");
        model.addAttribute("collectionRequest", new CollectionRequest());
        model.addAttribute("formAction", "/admin/collections/create");
        model.addAttribute("formTitle", "Create Collection");
        model.addAttribute("isEdit", false);

        return "collection/form";
    }

    // ADMIN: create collection
    @PostMapping("/admin/collections/create")
    public String createCollection(
            @Valid @ModelAttribute("collectionRequest") CollectionRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formAction", "/admin/collections/create");
            model.addAttribute("formTitle", "Create Collection");
            model.addAttribute("isEdit", false);
            model.addAttribute(
                    "message",
                    bindingResult.getFieldError() != null
                            ? bindingResult.getFieldError().getDefaultMessage()
                            : "Dữ liệu không hợp lệ"
            );
            model.addAttribute("messageType", "error");

            return "collection/form";
        }

        collectionService.createCollection(request);
        redirectAttributes.addFlashAttribute("message", "Tạo tuyển tập thành công");
        redirectAttributes.addFlashAttribute("messageType", "success");

        return "redirect:/collections";
    }

    // ADMIN: show edit form
    @GetMapping("/admin/collections/{id}/edit")
    public String showEditForm(
            @PathVariable UUID id,
            Model model
    ) {
        CollectionResponse collection = collectionService.getCollectionByID(id);

        CollectionRequest request = new CollectionRequest();
        request.setName(collection.getName());
        request.setDescription(collection.getDescription());

        model.addAttribute("pageTitle", "Chỉnh sửa");
        model.addAttribute("collectionRequest", request);
        model.addAttribute("collectionId", id);
        model.addAttribute("collection", collection);
        model.addAttribute("formAction", "/admin/collections/" + id + "/edit");
        model.addAttribute("formTitle", "Edit Collection");
        model.addAttribute("isEdit", true);

        return "collection/form";
    }

    // ADMIN: update collection
    @PostMapping("/admin/collections/{id}/edit")
    public String updateCollection(
            @PathVariable UUID id,
            @Valid @ModelAttribute("collectionRequest") CollectionRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("collectionId", id);
            model.addAttribute("formAction", "/admin/collections/" + id + "/edit");
            model.addAttribute("formTitle", "Edit Collection");
            model.addAttribute("isEdit", true);
            model.addAttribute(
                    "message",
                    bindingResult.getFieldError() != null
                            ? bindingResult.getFieldError().getDefaultMessage()
                            : "Dữ liệu không hợp lệ"
            );
            model.addAttribute("messageType", "error");

            return "collection/form";
        }

        collectionService.updateCollection(id, request);
        redirectAttributes.addFlashAttribute("message", "Cập nhật tuyển tập thành công");
        redirectAttributes.addFlashAttribute("messageType", "success");

        return "redirect:/collections";
    }

    // ADMIN: soft delete collection
    @PostMapping("/admin/collections/{id}/delete")
    public String deleteCollection(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            String message = collectionService.deleteCollection(id);
            redirectAttributes.addFlashAttribute("message", message);
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }

        return "redirect:/collections";
    }

    // ADMIN: restore collection
    @PostMapping("/admin/collections/{id}/restore")
    public String restoreCollection(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            String message = collectionService.restoreCollection(id);
            redirectAttributes.addFlashAttribute("message", message);
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }

        return "redirect:/collections";
    }
}