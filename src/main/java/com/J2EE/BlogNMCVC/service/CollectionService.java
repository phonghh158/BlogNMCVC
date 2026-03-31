package com.J2EE.BlogNMCVC.service;

import com.J2EE.BlogNMCVC.constant.TopicStatus;
import com.J2EE.BlogNMCVC.dto.request.CollectionRequest;
import com.J2EE.BlogNMCVC.dto.response.CollectionResponse;
import com.J2EE.BlogNMCVC.mapper.CollectionMapper;
import com.J2EE.BlogNMCVC.model.Collection;
import com.J2EE.BlogNMCVC.model.Topic;
import com.J2EE.BlogNMCVC.repository.CollectionRepository;
import com.J2EE.BlogNMCVC.repository.TopicRepository;
import com.J2EE.BlogNMCVC.utils.SlugUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CollectionService {
    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private TopicRepository topicRepository;

    // CREATE
    public CollectionResponse createCollection(CollectionRequest req) {
        String slug = SlugUtils.toSlug(req.getName());
        slug = SlugUtils.toUniqueSlug(collectionRepository.existsBySlug(slug), slug, LocalDateTime.now());

        Collection collection = collectionMapper.toEntity(req, slug);

        Collection newCollection = collectionRepository.save(collection);

        return collectionMapper.toResponse(newCollection);
    }

    // READ
    // Read All
    public Page<CollectionResponse> getCollections(int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return collectionRepository.findAllByDeletedAtIsNull(pageable).map(collectionMapper::toResponse);
    }

    public Page<CollectionResponse> getCollectionsForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return collectionRepository.findAll(pageable).map(collectionMapper::toResponse);
    }

    // Read One
    // Read By Slug
    public CollectionResponse getCollectionBySlug(String slug) {
        Collection collection = collectionRepository.findBySlugAndDeletedAtIsNull(slug)
                .orElseThrow( () -> new RuntimeException("Collection with Slug " + slug + " not found"));

        return collectionMapper.toResponse(collection);
    }

    public CollectionResponse getCollectionBySlugForAdmin(String slug) {
        Collection collection = collectionRepository.findBySlug(slug)
                .orElseThrow( () -> new RuntimeException("Collection with Slug " + slug + " not found"));

        return collectionMapper.toResponse(collection);
    }

    // Read By Name
    public CollectionResponse getCollectionByName(String name) {
        Collection collection = collectionRepository.findByNameAndDeletedAtIsNull(name)
                .orElseThrow( () -> new RuntimeException("Collection with Name " + name + " not found"));

        return collectionMapper.toResponse(collection);
    }

    public CollectionResponse getCollectionByNameforAdmin(String name) {
        Collection collection = collectionRepository.findByNameAndDeletedAtIsNull(name)
                .orElseThrow( () -> new RuntimeException("Collection with Name " + name + " not found"));

        return collectionMapper.toResponse(collection);
    }

    // Read By Id (Just For Admin)
    public CollectionResponse getCollectionByID(UUID id) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow( () -> new RuntimeException("Collection with ID " + id + " not found"));

        return collectionMapper.toResponse(collection);
    }

    // Update
    public CollectionResponse updateCollection(UUID id, CollectionRequest req) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow( () -> new RuntimeException("Collection with id " + id + " not found"));

        String slug = SlugUtils.toSlug(req.getName());
        slug = SlugUtils.toUniqueSlug(collectionRepository.existsBySlug(slug), slug, collection.getCreatedAt());

        collection.setName(req.getName());
        collection.setDescription(req.getDescription());
        collection.setSlug(slug);

        Collection updatedCollection = collectionRepository.save(collection);

        return collectionMapper.toResponse(updatedCollection);
    }

    // Delete
    public String deleteCollection(UUID id) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow( () -> new RuntimeException("Collection with id " + id + " not found"));

        collection.setDeletedAt(LocalDateTime.now());
        collectionRepository.save(collection);

        List<Topic> topics = topicRepository.findAllByCollectionAndDeletedAtIsNull(collection);

        if (!topics.isEmpty()) {
            topics.forEach((topic) -> {
                topic.setDeletedAt(LocalDateTime.now());
                topic.setStatus(TopicStatus.DELETED);
            });
        }

        topicRepository.saveAll(topics);

        return "Deleted collection with Name: " + collection.getName();
    }

    // Restore
    public String restoreCollection(UUID id) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow( () -> new RuntimeException("Collection with id " + id + " not found"));

        collection.setDeletedAt(null);
        collectionRepository.save(collection);

        List<Topic> topics = topicRepository.findAllByCollectionAndDeletedAtIsNotNull(collection);

        if (!topics.isEmpty()) {
            topics.forEach((topic) -> {
                topic.setDeletedAt(null);
                topic.setStatus(TopicStatus.PUBLISHED);
            });
        }

        topicRepository.saveAll(topics);

        return "Restored collection with Name: " + collection.getName();
    }
}
