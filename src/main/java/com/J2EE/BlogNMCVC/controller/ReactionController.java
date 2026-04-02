package com.J2EE.BlogNMCVC.controller;

import com.J2EE.BlogNMCVC.constant.ReactionType;
import com.J2EE.BlogNMCVC.dto.response.ReactionResponse;
import com.J2EE.BlogNMCVC.service.ReactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/reactions")
@ResponseBody
public class ReactionController {
    @Autowired
    private ReactionService reactionService;

    @PostMapping("/topics/{topicId}")
    public ResponseEntity<Map<String, Object>> addReaction(@PathVariable UUID topicId) {
        ReactionResponse reaction = reactionService.addReaction(topicId, ReactionType.LOVE);
        long count = reactionService.countAllReactionByTopic(topicId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", "Reaction created successfully");
        response.put("reacted", true);
        response.put("reaction", reaction);
        response.put("count", count);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/topics/{topicId}")
    public ResponseEntity<Map<String, Object>> deleteReaction(@PathVariable UUID topicId) {
        ReactionResponse reaction = reactionService.findReactionByTopicAndMe(topicId);
        reactionService.deleteReaction(reaction.getId());

        long count = reactionService.countAllReactionByTopic(topicId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", "Reaction deleted successfully");
        response.put("reacted", false);
        response.put("count", count);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/topics/{topicId}/count")
    public ResponseEntity<Map<String, Object>> countAllReactionByTopic(@PathVariable UUID topicId) {
        long count = reactionService.countAllReactionByTopic(topicId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("count", count);

        return ResponseEntity.ok(response);
    }
}