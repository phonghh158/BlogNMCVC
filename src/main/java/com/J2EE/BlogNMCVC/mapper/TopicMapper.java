package com.J2EE.BlogNMCVC.mapper;

import com.J2EE.BlogNMCVC.dto.request.TopicRequest;
import com.J2EE.BlogNMCVC.dto.response.TopicResponse;
import com.J2EE.BlogNMCVC.model.Collection;
import com.J2EE.BlogNMCVC.model.Topic;
import com.J2EE.BlogNMCVC.model.User;
import org.springframework.stereotype.Component;

@Component
public class TopicMapper {

    public Topic toEntity(TopicRequest req, User user, Collection collection, String slug, String thumbnail) {
        return Topic.builder()
                .user(user)
                .collection(collection)
                .title(req.getTitle())
                .slug(slug)
                .content(req.getContent())
                .footnote(req.getFootnote())
                .facebookLink(req.getFacebookLink())
                .thumbnail(thumbnail)
                .status(req.getStatus())
                .build();
    }

    public TopicResponse toResponse(Topic topic) {
        return TopicResponse.builder()
                .id(topic.getId())
                .title(topic.getTitle())
                .slug(topic.getSlug())
                .content(topic.getContent())
                .footnote(topic.getFootnote())
                .facebookLink(topic.getFacebookLink())
                .thumbnail(topic.getThumbnail())
                .status(topic.getStatus())
                .publishedAt(topic.getPublishedAt())
                .createdAt(topic.getCreatedAt())
                .updatedAt(topic.getUpdatedAt())

                .authorId(topic.getUser().getId())
                .authorUsername(topic.getUser().getUsername())
                .authorName(topic.getUser().getName())

                .collectionId(topic.getCollection().getId())
                .collectionName(topic.getCollection().getName())

                .build();
    }
}