package com.J2EE.BlogNMCVC.controller;

import com.J2EE.BlogNMCVC.dto.response.BookmarkResponse;
import com.J2EE.BlogNMCVC.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/bookmarks")
@ResponseBody
public class BookmarkController {
    @Autowired
    private BookmarkService bookmarkService;

    @PostMapping("/topics/{topicId}")
    public ResponseEntity<Map<String, Object>> addBookmark(@PathVariable UUID topicId) {
        BookmarkResponse bookmark = bookmarkService.addBookmark(topicId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", "Bookmark created successfully");
        response.put("bookmarked", true);
        response.put("bookmark", bookmark);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/topics/{topicId}")
    public ResponseEntity<Map<String, Object>> deleteBookmark(@PathVariable UUID topicId) {
        BookmarkResponse bookmark = bookmarkService.findBookmarkByTopicAndMe(topicId);
        bookmarkService.deleteBookmark(bookmark.getId());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", "Bookmark deleted successfully");
        response.put("bookmarked", false);

        return ResponseEntity.ok(response);
    }
}