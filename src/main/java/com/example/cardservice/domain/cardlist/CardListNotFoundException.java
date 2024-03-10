package com.example.cardservice.domain.cardlist;

import com.example.cardservice.web.exc.NotFoundException;

public class CardListNotFoundException extends NotFoundException {
    public CardListNotFoundException(Long cardListId) {
        super("CardList with id "+cardListId+" is not found");
    }
}
