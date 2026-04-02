package com.J2EE.BlogNMCVC.controller;

import com.J2EE.BlogNMCVC.constant.AuthorReaction;
import com.J2EE.BlogNMCVC.dto.request.CommentRequest;
import com.J2EE.BlogNMCVC.dto.response.CommentResponse;
import com.J2EE.BlogNMCVC.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/comments")
@ResponseBody
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(@Valid @RequestBody CommentRequest req) {
        CommentResponse comment = commentService.addComment(req);
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/topics/{id}")
    public ResponseEntity<Page<CommentResponse>> getCommentsByTopic(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<CommentResponse> commentPage = commentService.getAllByTopic(id, page, size);
        return ResponseEntity.ok(commentPage);
    }

    @PatchMapping("/{id}/author-reaction")
    public ResponseEntity<CommentResponse> authorReactComment(
            @PathVariable UUID id,
            @RequestParam(required = false) AuthorReaction authorReaction
    ) {
        CommentResponse comment = commentService.authorReactComment(id, authorReaction);
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable UUID id,
            @RequestParam String content
    ) {
        CommentResponse comment = commentService.updateComment(id, content);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommentResponse> deleteComment(
            @PathVariable UUID id
    ) {
        CommentResponse comment = commentService.deteleComment(id);
        return ResponseEntity.ok(comment);
    }
}