package com.example.cardservice.domain.cardlist;



import com.example.cardservice.web.dto.AddCardListPayload;
import com.example.cardservice.web.dto.ChangeCardListPositionsPayload;

import java.util.List;

public interface CardListService {
    /**
     * Retrieves a list of CardList objects associated with the specified board ID from the database.
     *
     * @param id the ID of the board containing the lists to retrieve.
     * @return a list of CardList objects associated with the specified board ID.
     */
    List<CardList> findByBoardId(Long id);

    /**
     * Change card list positions
     *
     * @param payload the command instance
     */
    List<CardList> changePositions(ChangeCardListPositionsPayload payload);

    CardList addCardList(AddCardListPayload payload, String userId);

    /**
     * Retrieves a list of Card objects associated with the specified list ID from the database.
     *
     * @param listId the ID of the list containing the cards to retrieve.
     * @return a CardList object associated with the specified list ID.
     * @throws CardListNotFoundException if no list with the specified ID is found in the database.
     */
    CardList getCardListById(long listId);
}
