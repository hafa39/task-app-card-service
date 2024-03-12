package com.example.cardservice.domain.card;

public record CardPosition(
        long cardListId,
        long cardId,
        int position
) {
}
