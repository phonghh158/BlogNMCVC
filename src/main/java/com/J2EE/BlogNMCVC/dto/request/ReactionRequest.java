package com.J2EE.BlogNMCVC.dto.request;

import com.J2EE.BlogNMCVC.constant.ReactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ReactionRequest {

    @NotNull(message = "Topic id must not be null")
    private UUID topicId;

    @NotNull(message = "Reaction type must not be null")
    private ReactionType type;
}