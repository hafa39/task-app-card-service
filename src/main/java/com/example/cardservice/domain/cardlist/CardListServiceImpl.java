package com.example.cardservice.domain.cardlist;

import com.example.cardservice.web.dto.AddCardListPayload;
import com.example.cardservice.web.dto.ChangeCardListPositionsPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardListServiceImpl implements CardListService{

    private final CardListRepository cardListRepository;
    private static final Logger log = LoggerFactory.getLogger(CardListServiceImpl.class);

    public CardListServiceImpl(CardListRepository cardListRepository) {
        this.cardListRepository = cardListRepository;
    }

    @Override
    public List<CardList> findByBoardId(Long id) {
        List<CardList> result = new ArrayList<>();

        log.info("Finding card lists by board with id: {}", id);
        cardListRepository.findByBoardId(id).forEach(result::add);
        log.info("Card lists size: {}", result.size());

        return result;
    }

    @Override
    @Transactional
    public List<CardList> changePositions(ChangeCardListPositionsPayload payload) {

        return payload.cardListPositions().stream()
                .map(cardListPosition -> {
                    long cardListId = cardListPosition.cardListId();
                    log.info("Finding card lists by board with id: {}", cardListId);
                    CardList existing = cardListRepository
                            .findById(cardListId)
                            .orElseThrow();
                    log.info("Card list id: {}", existing.id());
                    CardList updated = CardList.updatePosition(existing, cardListPosition.position());
                    log.info("Updating card list position from {} to {}", existing.position(),updated.position());
                    CardList saved = cardListRepository.save(updated);
                    log.info("card list updated id: {}",saved.id());
                    return saved;
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CardList addCardList(AddCardListPayload payload, String userId) {
        CardList toSave = CardList.of(payload.name(), payload.position(), payload.boardId(), userId);
        log.info("Adding card list");
        CardList saved = cardListRepository.save(toSave);
        log.info("Card list saved id: {}",saved.id());
        return saved;
    }

    @Override
    public CardList getCardListById(long listId) {
        log.info("Fetching card list by ID: {}", listId);

        CardList cardList = cardListRepository.findById(listId)
                .orElseThrow(() -> {
                    log.warn("Card list  with ID {} not found. Throwing CardListNotFoundException.", listId);
                    return new CardListNotFoundException(listId);
                });
        log.info("Successfully fetched card list");
        return cardList;
    }
}
