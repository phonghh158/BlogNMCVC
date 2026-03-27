package com.J2EE.BlogNMCVC.mapper;

import com.J2EE.BlogNMCVC.dto.response.BookmarkResponse;
import com.J2EE.BlogNMCVC.model.Bookmark;
import com.J2EE.BlogNMCVC.model.Topic;
import com.J2EE.BlogNMCVC.model.User;
import org.springframework.stereotype.Component;

@Component
public class BookmarkMapper {
    public BookmarkResponse toResponse(Bookmark bookmark) {
        return BookmarkResponse.builder()
                .id(bookmark.getId())
                .createdAt(bookmark.getCreatedAt())

                .userId(bookmark.getUser().getId())
                .username(bookmark.getUser().getUsername())
                .name(bookmark.getUser().getName())

                .topicId(bookmark.getTopic().getId())
                .topicTitle(bookmark.getTopic().getTitle())
                .build();
    }
}