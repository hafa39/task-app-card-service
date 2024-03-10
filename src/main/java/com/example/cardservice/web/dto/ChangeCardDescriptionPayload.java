package com.example.cardservice.web.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangeCardDescriptionPayload(
        @NotBlank
        String description
) {
}
