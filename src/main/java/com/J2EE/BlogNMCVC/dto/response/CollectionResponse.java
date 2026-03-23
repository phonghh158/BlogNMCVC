package com.J2EE.BlogNMCVC.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CollectionResponse {
    private UUID id;

    private String name;

    private String slug;

    private String description;

    private LocalDateTime createdAt;
}