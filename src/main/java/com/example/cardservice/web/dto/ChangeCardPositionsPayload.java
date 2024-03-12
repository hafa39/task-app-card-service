package com.example.cardservice.web.dto;

import com.example.cardservice.domain.card.CardPosition;

import java.util.List;

public record ChangeCardPositionsPayload(
        long boardId,
        List<CardPosition> cardPositions
) {
}
