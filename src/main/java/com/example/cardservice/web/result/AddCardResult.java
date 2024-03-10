package com.example.cardservice.web.result;

import com.example.cardservice.domain.card.Card;

public record AddCardResult (
        Long id,
        String title,
        int position
){
    public static AddCardResult of(Card card){
        return new AddCardResult(card.id(), card.title(), card.position());
    }
}
