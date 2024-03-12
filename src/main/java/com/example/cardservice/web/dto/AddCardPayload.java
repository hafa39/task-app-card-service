package com.example.cardservice.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddCardPayload(
        @NotNull
        long boardId,
        @NotNull
        long cardListId,
        @NotBlank
        String title,
        @NotNull
        int position
) {}

