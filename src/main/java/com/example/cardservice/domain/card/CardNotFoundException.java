package com.example.cardservice.domain.card;

import com.example.cardservice.web.exc.NotFoundException;

public class CardNotFoundException extends NotFoundException {
    public CardNotFoundException(Long cardId) {
        super("Card with id "+cardId+" is not found");
    }
}
