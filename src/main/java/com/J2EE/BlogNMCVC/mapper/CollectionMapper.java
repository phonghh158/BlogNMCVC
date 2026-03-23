package com.J2EE.BlogNMCVC.mapper;

import com.J2EE.BlogNMCVC.dto.request.CollectionRequest;
import com.J2EE.BlogNMCVC.dto.response.CollectionResponse;
import com.J2EE.BlogNMCVC.model.Collection;
import org.springframework.stereotype.Component;

@Component
public class CollectionMapper {
    // Request -> Entity
    public Collection toEntity(CollectionRequest request, String slug) {
        return Collection.builder()
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .build();
    }

    // Entity -> Response
    public CollectionResponse toResponse(Collection collection) {
        return CollectionResponse.builder()
                .id(collection.getId())
                .name(collection.getName())
                .slug(collection.getSlug())
                .description(collection.getDescription())
                .createdAt(collection.getCreatedAt())
                .build();
    }
}