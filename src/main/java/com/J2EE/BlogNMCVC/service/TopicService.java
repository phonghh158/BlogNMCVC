package com.J2EE.BlogNMCVC.service;

import com.J2EE.BlogNMCVC.dto.request.TopicRequest;
import com.J2EE.BlogNMCVC.dto.response.TopicResponse;
import com.J2EE.BlogNMCVC.dto.response.UserResponse;
import com.J2EE.BlogNMCVC.mapper.TopicMapper;
import com.J2EE.BlogNMCVC.model.Collection;
import com.J2EE.BlogNMCVC.model.Topic;
import com.J2EE.BlogNMCVC.model.User;
import com.J2EE.BlogNMCVC.repository.CollectionRepository;
import com.J2EE.BlogNMCVC.repository.TopicRepository;
import com.J2EE.BlogNMCVC.utils.SlugUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TopicService {
    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private CollectionRepository collectionRepository;

    // CREATE
    public TopicResponse createTopic(TopicRequest req) {
        String slug = SlugUtils.toSlug(req.getTitle());

        slug = SlugUtils.toUniqueSlug(topicRepository.existsBySlug(slug), slug, LocalDateTime.now());

        User user = userService.getCurrentUser();

        Collection collection = collectionRepository
                .findById(req.getCollectionId())
                .orElseThrow(() -> new IllegalArgumentException("Collection not found"));

        Topic topic = topicMapper.toEntity(req, user, collection, slug);

        Topic newTopic = topicRepository.save(topic);

        return topicMapper.toResponse(newTopic);
    }

    // READ
    // Read All
    public Page<TopicResponse> getTopics(Pageable pageable) {
        return topicRepository.findAllByDeletedAtIsNull(pageable).map(topicMapper::toResponse);
    }

    public Page<TopicResponse> getTopicsForAdmin(Pageable pageable) {
        return topicRepository.findAll(pageable).map(topicMapper::toResponse);
    }

    // Read One
}
