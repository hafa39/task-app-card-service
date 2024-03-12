package com.example.cardservice.security;

import com.example.cardservice.domain.card.Card;
import com.example.cardservice.domain.card.CardService;
import com.example.cardservice.domain.cardlist.CardList;
import com.example.cardservice.domain.cardlist.CardListService;
import com.example.cardservice.web.proxy.BoardClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SecurityService {

    private CardService cardService;
    private CardListService cardListService;
    private BoardClient boardClient;
    private static final Logger logger = LoggerFactory.getLogger(SecurityService.class);

    public SecurityService(CardService cardService, CardListService cardListService, BoardClient boardClient) {
        this.cardService = cardService;
        this.cardListService = cardListService;
        this.boardClient = boardClient;
    }

    public Boolean canManipulateCard(long cardId, String userId) {

        if (Objects.isNull(userId)) {
            throw new IllegalArgumentException("userId should not be null");
        }

        logger.info("Checking permission to read and modify card with ID {} for user {}.", cardId, userId);

        Card card = cardService.getCardById(cardId);
        CardList cardList = cardListService.getCardListById(card.cardListId());
        Long boardId = cardList.boardId();

        logger.info("Card with ID {} belongs to CardList with ID {} on Board with ID {}.", cardId, card.cardListId(), boardId);

        logger.info("Checking is user {} a member of Board with ID {}", userId, boardId);

        Boolean isMemberOfBoard = boardClient.isUserMemberOfBoard(boardId, userId);

        logger.info("User {} is a member of Board with ID {}: {}", userId, boardId, isMemberOfBoard);

        if (isMemberOfBoard){
            logger.info("User {} is permitted to read and modify the Card with ID {}", userId, cardId);
        }
        else {
            logger.info("User {} has no permission to read and modify the Card with ID {}", userId, cardId);
        }
        return isMemberOfBoard;

    }

    public Boolean canManipulateCardsOnBoard(long boardId, String userId){
        if (Objects.isNull(userId)) {
            throw new IllegalArgumentException("userId should not be null");
        }

        logger.info("Checking permission to read and modify cards on board ID {} for user {}.", boardId, userId);

        logger.info("Checking is user {} a member of Board with ID {}", userId, boardId);

        Boolean isMemberOfBoard = boardClient.isUserMemberOfBoard(boardId, userId);

        logger.info("User {} is a member of Board with ID {}: {}", userId, boardId, isMemberOfBoard);

        if (isMemberOfBoard){
            logger.info("User {} is permitted to read and modify cards on board with ID {}", userId, boardId);
        }
        else {
            logger.info("User {} has no permission to read and modify cards on board with ID {}", userId, boardId);
        }
        return isMemberOfBoard;
    }
}
