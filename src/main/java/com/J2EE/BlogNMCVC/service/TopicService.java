package com.J2EE.BlogNMCVC.service;

import com.J2EE.BlogNMCVC.constant.TopicStatus;
import com.J2EE.BlogNMCVC.dto.request.TopicRequest;
import com.J2EE.BlogNMCVC.dto.response.TopicResponse;
import com.J2EE.BlogNMCVC.dto.response.UploadResponse;
import com.J2EE.BlogNMCVC.mapper.TopicMapper;
import com.J2EE.BlogNMCVC.model.Collection;
import com.J2EE.BlogNMCVC.model.Topic;
import com.J2EE.BlogNMCVC.model.User;
import com.J2EE.BlogNMCVC.repository.CollectionRepository;
import com.J2EE.BlogNMCVC.repository.TopicRepository;
import com.J2EE.BlogNMCVC.repository.UserRepository;
import com.J2EE.BlogNMCVC.utils.SlugUtils;
import com.J2EE.BlogNMCVC.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TopicService {
    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CollectionRepository collectionRepository;
    @Autowired
    private UploadService uploadService;

    // CREATE
    @Transactional
    public TopicResponse createTopic(TopicRequest req, MultipartFile thumbnail) {
        String slug = SlugUtils.toSlug(req.getTitle());

        slug = SlugUtils.toUniqueSlug(topicRepository.existsBySlug(slug), slug, LocalDateTime.now());

        User user = userRepository.findById(UserUtils.getCurrentUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Collection collection = collectionRepository.findById(req.getCollectionId())
                .orElseThrow(() -> new IllegalArgumentException("Collection not found"));

        UploadResponse uploadResponse = uploadService.uploadImage(thumbnail, "topics");

        if (uploadResponse == null) {
            throw new RuntimeException("Upload Failed");
        }

        Topic topic = topicMapper.toEntity(req, user, collection, slug, uploadResponse.getSecureUrl());

        topic = topicRepository.save(topic);

        return topicMapper.toResponse(topic);
    }

    // READ
    // Read All
    public Page<TopicResponse> getTopics(int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return topicRepository.findAllByDeletedAtIsNull(pageable).map(topicMapper::toResponse);
    }

    public Page<TopicResponse> getTopicsForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return topicRepository.findAll(pageable).map(topicMapper::toResponse);
    }

    public Page<TopicResponse> getTopicsByCollection(UUID id, int page, int size) {
        Collection collection = collectionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Collection not found"));

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return topicRepository.findAllByCollectionAndDeletedAtIsNull(collection, pageable).map(topicMapper::toResponse);
    }

    public Page<TopicResponse> getTopicsByCollectionForAdmin(UUID id, int page, int size) {
        Collection collection = collectionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Collection not found"));

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return topicRepository.findAllByCollection(collection, pageable).map(topicMapper::toResponse);
    }

    public Page<TopicResponse> getTopicsByStatus(TopicStatus status, int page, int size) {
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return topicRepository.findAllByStatus(status, pageable).map(topicMapper::toResponse);
    }

    public Page<TopicResponse> getTopicsByCollectionAndStatus(UUID topicId, TopicStatus status, int page, int size) {
        Collection collection = collectionRepository.findById(topicId).orElseThrow(() -> new IllegalArgumentException("Collection not found"));

        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return topicRepository.findAllByCollectionAndStatus(collection, status, pageable).map(topicMapper::toResponse);
    }

    // Read One
    // Read By Slug
    public TopicResponse getTopicBySlug(String slug) {
        Topic topic = topicRepository.findBySlugAndDeletedAtIsNull(slug)
                .orElseThrow(() -> new IllegalArgumentException("Collection not found"));

        return topicMapper.toResponse(topic);
    }

    public TopicResponse getTopicBySlugForAdmin(String slug) {
        Topic topic = topicRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Collection not found"));

        return topicMapper.toResponse(topic);
    }

    // Read By Title
    public TopicResponse getTopicByTitle(String title) {
        Topic topic = topicRepository.findByTitleAndDeletedAtIsNull(title)
                .orElseThrow(() -> new IllegalArgumentException("Collection not found"));

        return topicMapper.toResponse(topic);
    }

    public TopicResponse getTopicByTitleForAdmin(String title) {
        Topic topic = topicRepository.findByTitle(title)
                .orElseThrow(() -> new IllegalArgumentException("Collection not found"));

        return topicMapper.toResponse(topic);
    }

    // Read By Id (Just For Admin)
    public TopicResponse getTopicById(UUID id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Collection not found"));

        return topicMapper.toResponse(topic);
    }

    // UPDATE
    public TopicResponse updateTopic(UUID id, TopicRequest req, MultipartFile thumbnail) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Collection not found"));

        Collection collection = collectionRepository.findById(req.getCollectionId())
                .orElseThrow(() -> new IllegalArgumentException("Collection not found"));

        UploadResponse uploadResponse = uploadService.uploadImage(thumbnail, "topics");

        if (uploadResponse != null) {
            topic.setThumbnail(uploadResponse.getSecureUrl());
        }

        topic.setCollection(collection);
        topic.setTitle(req.getTitle());
        topic.setContent(req.getContent());
        topic.setFootnote(req.getFootnote());
        topic.setFacebookLink(req.getFacebookLink());
        topic.setStatus(req.getStatus());

        topicRepository.save(topic);
        return topicMapper.toResponse(topic);
    }

    // DELETE
    public String deleteTopic(UUID id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Collection not found"));

        topic.setDeletedAt(LocalDateTime.now());
        topic.setStatus(TopicStatus.DELETED);

        topicRepository.save(topic);

        return "Deleted topic with id: " + id;
    }

    // RESTORE
    public String restoreTopic(UUID id, TopicStatus status) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Collection not found"));

        topic.setDeletedAt(LocalDateTime.now());
        topic.setStatus(status);

        topicRepository.save(topic);

        return "Restored topic with id: " + id;
    }
}
