package com.J2EE.BlogNMCVC.service;

import com.J2EE.BlogNMCVC.constant.TopicStatus;
import com.J2EE.BlogNMCVC.dto.request.TopicRequest;
import com.J2EE.BlogNMCVC.dto.response.TopicResponse;
import com.J2EE.BlogNMCVC.dto.response.UploadResponse;
import com.J2EE.BlogNMCVC.mapper.TopicMapper;
import com.J2EE.BlogNMCVC.model.*;
import com.J2EE.BlogNMCVC.repository.*;
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
import java.util.List;
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
    @Autowired
    private BookmarkRepository bookmarkRepository;

    // CREATE
    @Transactional
    public TopicResponse createTopic(TopicRequest req, MultipartFile thumbnail) {
        String slug = SlugUtils.toSlug(req.getTitle());

        slug = SlugUtils.toUniqueSlug(topicRepository.existsBySlug(slug), slug, LocalDateTime.now());

        UUID userId = UserUtils.getCurrentUserId();

        if (userId == null) {
            throw new UsernameNotFoundException("User Not Found");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("Cannot find user with id: " + userId));

        Collection collection = collectionRepository.findById(req.getCollectionId())
                .orElseThrow(() -> new IllegalArgumentException("Topic not found"));

        if (thumbnail == null || thumbnail.isEmpty()) {
            throw new IllegalStateException("Thumbnail is null");
        }

        UploadResponse uploadResponse = uploadService.uploadImage(thumbnail, "topics");

        if (uploadResponse == null) {
            throw new RuntimeException("Upload Failed");
        }

        String fbLink = req.getFacebookLink().startsWith("http") ? req.getFacebookLink() : "https://" + req.getFacebookLink();

        Topic topic = topicMapper.toEntity(req, user, collection, slug, uploadResponse.getSecureUrl());
        topic.setFacebookLink(fbLink);

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

    public Page<TopicResponse> getTopicsByCollectionAndStatus(UUID collectionId, TopicStatus status, int page, int size) {
        Collection collection = collectionRepository.findById(collectionId).orElseThrow(() -> new IllegalArgumentException("Collection not found"));

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

    public List<TopicResponse> getFeaturedTopicsByCollection(UUID id, int size) {
        Collection collection = collectionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Collection not found"));

        Pageable pageable = PageRequest.of(
                0,
                size,
                Sort.by("publishedAt").descending()
        );

        Page<TopicResponse> topics = topicRepository.findAllByCollectionAndDeletedAtIsNull(collection, pageable)
                .map(topicMapper::toResponse);

        return topics.getContent();
    }

    public Page<TopicResponse> getAllTopicByCollections(List<UUID> collectionIds, int page, int size) {
        if (collectionIds == null || collectionIds.isEmpty()) {
            throw new IllegalArgumentException("Collection ids must not be empty");
        }

        List<Collection> collections = collectionRepository.findAllById(collectionIds);

        if (collections.isEmpty()) {
            throw new IllegalArgumentException("Collections not found");
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return topicRepository.findAllByCollectionInAndDeletedAtIsNull(collections, pageable)
                .map(topicMapper::toResponse);
    }

    public Page<TopicResponse> getAllTopicByCollectionsAndStatus(List<UUID> collectionIds, TopicStatus status, int page, int size) {
        if (collectionIds == null || collectionIds.isEmpty()) {
            throw new IllegalArgumentException("Collection ids must not be empty");
        }

        if (status == null) {
            throw new IllegalArgumentException("Status must not be null");
        }

        List<Collection> collections = collectionRepository.findAllById(collectionIds);

        if (collections.isEmpty()) {
            throw new IllegalArgumentException("Collections not found");
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return topicRepository.findAllByCollectionInAndStatus(collections, status, pageable)
                .map(topicMapper::toResponse);
    }

    // Read One
    // Read By Slug
    public TopicResponse getTopicBySlug(String slug) {
        Topic topic = topicRepository.findBySlugAndDeletedAtIsNull(slug)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found"));

        return topicMapper.toResponse(topic);
    }

    public TopicResponse getTopicBySlugForAdmin(String slug) {
        Topic topic = topicRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found"));

        return topicMapper.toResponse(topic);
    }

    // Read By Title
    public TopicResponse getTopicByTitle(String title) {
        Topic topic = topicRepository.findByTitleAndDeletedAtIsNull(title)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found"));

        return topicMapper.toResponse(topic);
    }

    public TopicResponse getTopicByTitleForAdmin(String title) {
        Topic topic = topicRepository.findByTitle(title)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found"));

        return topicMapper.toResponse(topic);
    }

    // Read By Id (Just For Admin)
    public TopicResponse getTopicById(UUID id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found"));

        return topicMapper.toResponse(topic);
    }

    // UPDATE
    public TopicResponse updateTopic(UUID id, TopicRequest req, MultipartFile thumbnail) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found"));

        Collection collection = collectionRepository.findById(req.getCollectionId())
                .orElseThrow(() -> new IllegalArgumentException("Collection not found"));

        String thumbnailUrl = topic.getThumbnail();

        if (thumbnail != null && !thumbnail.isEmpty()) {
            UploadResponse uploadResponse = uploadService.uploadImage(thumbnail, "topics");
            thumbnailUrl = uploadResponse.getSecureUrl();
        }

        String slug = topic.getSlug();
        String newSlug = SlugUtils.toSlug(req.getTitle());

        if (!newSlug.equals(topic.getSlug())) {
            slug = SlugUtils.toUniqueSlug(topicRepository.existsBySlug(newSlug), newSlug, LocalDateTime.now());
        }

        String fbLink = req.getFacebookLink();
        if (fbLink != null && !fbLink.isBlank() && !fbLink.startsWith("http")) {
            fbLink = "https://" + fbLink;
        }

        topic.setCollection(collection);
        topic.setTitle(req.getTitle());
        topic.setSlug(slug);
        topic.setContent(req.getContent());
        topic.setFootnote(req.getFootnote());
        topic.setFacebookLink(fbLink);
        topic.setStatus(req.getStatus());
        topic.setThumbnail(thumbnailUrl);

        topic = topicRepository.save(topic);

        return topicMapper.toResponse(topic);
    }

    // DELETE
    public String deleteTopic(UUID id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found"));

        topic.setDeletedAt(LocalDateTime.now());
        topic.setStatus(TopicStatus.DELETED);

        topicRepository.save(topic);

        List<Bookmark> bookmarks = bookmarkRepository.findAllByTopic(topic);
        bookmarkRepository.deleteAll(bookmarks);

        return "Deleted topic with id: " + id;
    }

    // RESTORE
    public String restoreTopic(UUID id, TopicStatus status) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found"));

        topic.setDeletedAt(null);
        topic.setStatus(status);

        topicRepository.save(topic);

        return "Restored topic with id: " + id;
    }
}
