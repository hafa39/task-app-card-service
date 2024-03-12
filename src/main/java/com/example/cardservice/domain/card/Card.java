package com.example.cardservice.domain.card;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
@Table("cards")
public record Card(
        @Id Long id,
        String title,
        String description,
        int position,
        boolean archived,
        Instant createdDate,
        String coverImage,
        @Column("cardlist_id")
        Long cardListId) {

    static Card of(String title,int position,Long cardListId){
        return new Card(null,title,null,position,false,Instant.now(),"",cardListId);
    }

    static Card updatePosition(Card card,int position,long cardListId){
        return new Card(card.id(), card.title(), card.description(), position, card.archived(), card.createdDate(), card.coverImage(), cardListId);
    }

    static Card updateTitle(Card card,String title){
        return new Card(card.id(), title, card.description(), card.position(), card.archived(), card.createdDate(), card.coverImage(), card.cardListId());
    }

    static Card updateDescription(Card card,String description){
        return new Card(card.id(), card.title(), description, card.position(), card.archived(), card.createdDate(), card.coverImage(), card.cardListId());
    }

    static Card updateArchived(Card card,boolean archived){
        return new Card(card.id(), card.title(), card.description(), card.position(), archived, card.createdDate(), card.coverImage(), card.cardListId());

    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", position=" + position +
                ", archived=" + archived +
                ", createdDate=" + createdDate +
                ", coverImage='" + coverImage + '\'' +
                ", cardListId=" + cardListId +
                '}';
    }
}
