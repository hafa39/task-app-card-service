package com.example.cardservice.domain.demo;

import com.example.cardservice.domain.attachment.AttachmentRepository;
import com.example.cardservice.domain.card.Card;
import com.example.cardservice.domain.card.CardRepository;
import com.example.cardservice.domain.cardlist.CardList;
import com.example.cardservice.domain.cardlist.CardListRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
@Component
@Profile("dev")
public class CardsDataLoader {

    private CardListRepository cardListRepository;
    private CardRepository cardRepository;
    private AttachmentRepository attachmentRepository;

    private static final Logger log = LoggerFactory.getLogger(CardsDataLoader.class);


    public CardsDataLoader(CardListRepository cardListRepository, CardRepository cardRepository, AttachmentRepository attachmentRepository) {
        this.cardListRepository = cardListRepository;
        this.cardRepository = cardRepository;
        this.attachmentRepository = attachmentRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadData() {
        log.info("Loading data...");

        if (cardRepository.count() == 0 && cardListRepository.count() == 0) {
            log.info("Data not found. Initializing sample data...");

            CardList cardList = new CardList(null, "Sample Card List 1", 0, false, Instant.now(), 1L, "bjorn");
            cardList = cardListRepository.save(cardList);
            log.info("Created Card List: {}", cardList);

            // Create Cards
            Card card1 = new Card(null, "Card 1", "Description for Card 1", 0, false, Instant.now(), "image_url", cardList.id());
            cardRepository.save(card1);
            log.info("Created Card: {}", card1);

            Card card2 = new Card(null, "Card 2", "Description for Card 2", 1, false, Instant.now(), "image_url", cardList.id());
            cardRepository.save(card2);
            log.info("Created Card: {}", card2);

            // Repeat the same for cardList2, card3, and card4

            log.info("Sample data initialized successfully.");
        } else {
            log.info("Data already present. Skipping data initialization.");
        }
    }}
