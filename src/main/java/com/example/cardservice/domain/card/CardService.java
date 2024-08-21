package com.example.cardservice.domain.card;

import com.example.cardservice.domain.activity.Activity;
import com.example.cardservice.web.dto.*;

import java.util.List;

public interface CardService {

    List<Card> findByCardlist(Long id);

    Card addCard(AddCardPayload payload);

    void changePositions(ChangeCardPositionsPayload payload);

    /**
     * Retrieves a Card object with the specified ID from the database.
     *
     * @param cardId the ID of the Card to retrieve.
     * @return a Card object with the specified ID if found.
     * @throws CardNotFoundException if no card with the specified ID is found in the database.
     */
    Card getCardById(Long cardId);

    void changeCardTitle(ChangeCardTitlePayload payload, Long cardId, String userId);

    void changeCardDescription(ChangeCardDescriptionPayload payload, Long cardId, String userId);

    void addComment(AddCardCommentPayload payload, Long cardId, String userId);

    void removeCard(Long cardId);

    List<Activity> getCardActivities(long cardId);

    void removeComment(long cardId, long commentId, String userId);

    void updateArchived(long cardId,boolean isArchived);
}
