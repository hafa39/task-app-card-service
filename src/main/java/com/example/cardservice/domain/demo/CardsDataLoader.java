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

            // Create "To Do" Card List
            CardList toDoList = new CardList(null, "To Do", 0, false, Instant.now(), 1L, "bjorn");
            toDoList = cardListRepository.save(toDoList);
            log.info("Created Card List: {}", toDoList);

            // Create Cards for "To Do" List
            Card card1 = new Card(null, "Implement Feature X", "Implement the new feature X as per specifications", 0, false, Instant.now(), "image_url", toDoList.id());
            cardRepository.save(card1);
            log.info("Created Card: {}", card1);

            Card card2 = new Card(null, "Bug Fixing", "Fix the reported bugs in module Y", 1, false, Instant.now(), "image_url", toDoList.id());
            cardRepository.save(card2);
            log.info("Created Card: {}", card2);

            // Create "In Progress" Card List
            CardList inProgressList = new CardList(null, "In Progress", 0, false, Instant.now(), 1L, "bjorn");
            inProgressList = cardListRepository.save(inProgressList);
            log.info("Created Card List: {}", inProgressList);

            // Create Card for "In Progress" List
            Card card3 = new Card(null, "Launch Product Z Campaign", "Plan and execute marketing campaigns for the launch of product Z", 0, false, Instant.now(), "image_url", inProgressList.id());
            cardRepository.save(card3);
            log.info("Created Card: {}", card3);

            log.info("Sample data initialized successfully.");
        } else {
            log.info("Data already present. Skipping data initialization.");
        }
    }
}
