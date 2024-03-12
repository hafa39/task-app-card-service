package com.example.cardservice.web.dto;

import com.example.cardservice.domain.cardlist.CardListPosition;

import java.util.List;

public record ChangeCardListPositionsPayload(
        long boardId,
        List<CardListPosition>cardListPositions
) {
}
