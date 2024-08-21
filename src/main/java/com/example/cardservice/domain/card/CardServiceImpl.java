package com.example.cardservice.domain.card;

import com.example.cardservice.domain.activity.Activity;
import com.example.cardservice.domain.activity.ActivityClient;
import com.example.cardservice.domain.activity.ActivityProxy;
import com.example.cardservice.domain.activity.CardActivities;
import com.example.cardservice.domain.activity.exc.CommentRemoveException;
import com.example.cardservice.domain.attachment.AttachmentRepository;
import com.example.cardservice.domain.cardlist.CardListService;
import com.example.cardservice.web.dto.*;
import com.example.cardservice.web.proxy.BoardClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CardServiceImpl implements CardService{
    private static final Logger log = LoggerFactory.getLogger(CardServiceImpl.class);
    private final CardRepository cardRepository;
    private final AttachmentRepository attachmentRepository;
    private final BoardClient boardClient;
    private final ActivityClient activityClient;
    private CardListService cardListService;
    private final StreamBridge streamBridge;

    public CardServiceImpl(CardRepository cardRepository, AttachmentRepository attachmentRepository, BoardClient boardClient, ActivityClient activityClient, CardListService cardListService, StreamBridge streamBridge) {
        this.cardRepository = cardRepository;
        this.attachmentRepository = attachmentRepository;
        this.boardClient = boardClient;
        this.activityClient = activityClient;
        this.cardListService = cardListService;
        this.streamBridge = streamBridge;
    }

    @Override
    public List<Card> findByCardlist(Long id) {
        log.info("Finding cards by card list with id: {}",id);
        List<Card> byCardListId = cardRepository.findByCardListId(id);
        log.info("Cards founded: {}",byCardListId);
        return byCardListId;
    }

    @Override
    public Card addCard(AddCardPayload payload) {
        Card toInsert = Card.of(payload.title(), payload.position(), payload.cardListId());
        log.info("Add card: {}",toInsert);
        Card saved = cardRepository.save(toInsert);
        log.info("Added card with id: {}",saved.id());
        return saved;
    }

    @Override
    public void changePositions(ChangeCardPositionsPayload payload) {
        payload.cardPositions().stream()
                .map(cardPosition -> {
                    log.info("Finding card with id: {}",cardPosition.cardId());
                    Card existingCard = cardRepository.findById(cardPosition.cardId()).orElseThrow();
                    log.info("Card found: {}",existingCard);

                    log.info("Updating card position from {} to {}",existingCard.position(),cardPosition.position());
                    log.info("Moving card from list {} to {}",existingCard.cardListId(),cardPosition.cardListId());
                    Card updatedCard = Card.updatePosition(existingCard, cardPosition.position(), cardPosition.cardListId());

                    Card savedCard = cardRepository.save(updatedCard);

                    return savedCard;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Card getCardById(Long cardId) {
        log.info("Fetching card by ID: {}", cardId);
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    log.warn("Card with ID {} not found. Throwing CardNotFoundException.", cardId);
                    return new CardNotFoundException(cardId);
                });

        log.info("Successfully fetched card");
        return card;
    }

    public void updateArchived(long cardId,boolean isArchived){
        log.info("Fetching card by ID: {}", cardId);
        Card existingCard = cardRepository.findById(cardId).orElseThrow(() -> {
            log.warn("Card with ID {} not found. Throwing CardNotFoundException.", cardId);
            return new CardNotFoundException(cardId);
        });
        log.info("Card fetched with id: {}", existingCard.id());

        log.info("Setting archived to {}",isArchived);
        Card updatedCard = Card.updateArchived(existingCard, isArchived);

        log.info("Saving updated card");
        Card savedCard = cardRepository.save(updatedCard);
        log.info("Card updated successfully with id: {}", savedCard.id());

    }
    @Override
    public void changeCardTitle(ChangeCardTitlePayload payload, Long cardId, String userId) {
        log.info("Fetching card by ID: {}", cardId);
        Card existingCard = cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    log.warn("Card with ID {} not found. Throwing CardNotFoundException.", cardId);
                    return new CardNotFoundException(cardId);
        });
        log.info("Card fetched with id: {}", existingCard.id());

        log.info("Updating card title from '{}' to '{}'", existingCard.title(), payload.title());
        Card updatedCard = Card.updateTitle(existingCard, payload.title());

        log.info("Saving updated card");
        Card savedCard = cardRepository.save(updatedCard);
        log.info("Card updated successfully wit id: {}", savedCard.id());

        ActivityProxy proxy = CardActivities.fromCardTitleChanged(userId,
                cardId, existingCard.title(), updatedCard.title());

        sendActivity(proxy);
    }

    @Override
    public void changeCardDescription(ChangeCardDescriptionPayload payload, Long cardId, String userId) {
        log.info("Fetching card by ID: {}", cardId);
        Card existingCard = cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    log.warn("Card with ID {} not found. Throwing CardNotFoundException.", cardId);
                    return new CardNotFoundException(cardId);
        });
        log.info("Card fetched with id: {}", existingCard.id());

        log.info("Updating card description from '{}' to '{}'", existingCard.description(), payload.description());
        Card updatedCard = Card.updateDescription(existingCard, payload.description());

        log.info("Saving updated card");
        Card savedCard = cardRepository.save(updatedCard);
        log.info("Card updated successfully with id: {}", savedCard.id());

        ActivityProxy proxy = CardActivities
                .fromCardDescriptionChanged(userId, cardId, savedCard.title(),existingCard.description(), savedCard.description());
        sendActivity(proxy);
    }

    @Override
    public void addComment(AddCardCommentPayload payload, Long cardId, String userId) {
        ActivityProxy proxy = CardActivities.fromAddedComment(userId,cardId, payload.comment());
        sendActivity(proxy);
    }
    private void sendActivity(ActivityProxy proxy) {
        boolean isActivitySent = streamBridge.send("sendActivity-out-0", proxy);
        log.info("{} activity was sent: {}", proxy.activityType(), isActivitySent);
    }

    @Override
    public void removeCard(Long cardId) {
        log.info("Deleting attachments for card with ID: {}", cardId);
        attachmentRepository.deleteAllByCardId(cardId);

        log.info("Deleting card with ID: {}", cardId);
        cardRepository.deleteById(cardId);
        log.info("Card deleted successfully with ID: {}", cardId);
    }

    @Override
    public List<Activity> getCardActivities(long cardId) {
        log.info("Fetching card with ID: {}", cardId);
        cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));

        log.info("Fetching activities for card with ID: {}", cardId);
        List<Activity> cardActivities = activityClient.getActivitiesByCard(cardId);

        log.info("Activities fetched successfully for card with ID: {}", cardId);

        return cardActivities;
    }

    @Override
    public void removeComment(long cardId, long commentId, String userId) {
        log.info("Removing comment with ID {} for card with ID {} by user {}", commentId, cardId, userId);

        Activity activity = activityClient.getActivity(commentId);

        if (!activity.userId().equals(userId)) {
            log.info("User: {}, CommentUser: {}",userId,activity.userId());
            log.error("CommentRemoveException: User {} is not allowed to remove comment with ID {}", userId, commentId);
            throw new CommentRemoveException(commentId, userId);
        }

        ActivityProxy proxy = CardActivities.fromRemovedComment(userId, cardId, commentId);

        log.info("Sending activity proxy for removed comment with ID {}",commentId);

        sendActivity(proxy);
    }
}
