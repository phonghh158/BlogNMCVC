package com.J2EE.BlogNMCVC.service;

import com.J2EE.BlogNMCVC.dto.request.CollectionRequest;
import com.J2EE.BlogNMCVC.dto.response.CollectionResponse;
import com.J2EE.BlogNMCVC.mapper.CollectionMapper;
import com.J2EE.BlogNMCVC.model.Collection;
import com.J2EE.BlogNMCVC.repository.CollectionRepository;
import com.J2EE.BlogNMCVC.utils.SlugUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CollectionService {
    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private CollectionMapper collectionMapper;

    // Create
    public CollectionResponse createCollection(CollectionRequest req) {
        String slug = SlugUtils.toSlug(req.getName());
        slug = SlugUtils.toUniqueSlug(collectionRepository.existsBySlug(slug), slug, LocalDateTime.now());

        Collection collection = collectionMapper.toEntity(req, slug);

        Collection newCollection = collectionRepository.save(collection);

        return collectionMapper.toResponse(newCollection);
    }

    // READ
    // Read All
    public Page<CollectionResponse> getCollections(Pageable pageable) {
        return collectionRepository.findAllByDeletedAtIsNull(pageable).map(collectionMapper::toResponse);
    }

    public Page<CollectionResponse> getCollectionsForAdmin(Pageable pageable, String slug) {
        return collectionRepository.findAll(pageable).map(collectionMapper::toResponse);
    }

    // Read One
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

        return "Deleted collection with Name: " + collection.getName();
    }

    // Restore
    public String restoreCollection(UUID id) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow( () -> new RuntimeException("Collection with id " + id + " not found"));

        collection.setDeletedAt(null);
        collectionRepository.save(collection);

        return "Restored collection with Name: " + collection.getName();
    }
}
