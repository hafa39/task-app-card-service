package com.example.cardservice.web.result;

import com.example.cardservice.domain.card.Card;

public record CardResult(
        long id,
        String title,
        String description,
        boolean archived
) {
    public static CardResult of(Card card){
        return new CardResult(card.id(), card.title(), card.description(), card.archived());
    }
}

