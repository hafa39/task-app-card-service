package com.example.cardservice.web.result;

import com.example.cardservice.domain.card.Card;
import com.example.cardservice.domain.cardlist.CardList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardlistResult {

    public static Map<String,Object> build(CardList cardList, List<Card> cards){
        Map<String, Object> cardlistData = new HashMap<>();
        cardlistData.put("id", cardList.id());
        cardlistData.put("name", cardList.name());
        cardlistData.put("position", cardList.position());

        List<CardInList> cardsInList = cards.stream()
                .map(card -> new CardInList(card.id(), card.title(), card.position(), card.archived()))
                .toList();

        cardlistData.put("cards",cardsInList);
        return cardlistData;
    }

    record CardInList(Long id, String title, int position, boolean archived){ }
}
