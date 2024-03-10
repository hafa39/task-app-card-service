package com.example.cardservice.domain.cardlist;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("cardlists")
public record CardList(
        @Id
        Long id,
        String name,
        int position,
        boolean archived,
        Instant createdDate,
        @Column("board_id")
        Long boardId,
        @Column("creator_id")
        String creatorId
) {
    public static CardList of(String name, int position, Long boardId, String creatorId) {
        return new CardList(null, name, position, false, Instant.now(), boardId, creatorId);
    }

    public static CardList updatePosition(CardList cl, int position){
        return new CardList(cl.id(), cl.name(), position, cl.archived(), cl.createdDate(), cl.boardId(), cl.creatorId());
    }

    @Override
    public String toString() {
        return "CardList{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", position=" + position +
                ", archived=" + archived +
                ", createdDate=" + createdDate +
                ", boardId=" + boardId +
                ", creatorId='" + creatorId + '\'' +
                '}';
    }
}
