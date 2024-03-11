package com.example.cardservice.domain.card;

import com.example.cardservice.PostgresTestBase;
import com.example.cardservice.domain.cardlist.CardList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class CardRepositoryTest extends PostgresTestBase {

    @Autowired
    private CardRepository repository;
    @Autowired
    private JdbcAggregateTemplate jdbc;

    @Test
    void findByCardListId() {
        CardList l1 = CardList.of("L1", 0, 1L, "test");
        CardList l2 = CardList.of("L2", 1, 1L, "test");
        jdbc.insertAll(List.of(l1,l2));
        long expectedCardListId = 1l;
        long otherCardListId = 2l;

        Card c1 = Card.of("C1", 0, expectedCardListId);
        Card c2 = Card.of("C2", 1, expectedCardListId);
        Card c3 = Card.of("C3", 2, otherCardListId);
        jdbc.insertAll(List.of(c1,c2,c3));
        List<Card> byCardListId = repository.findByCardListId(expectedCardListId);
        assertThat(byCardListId).hasSize(2);
        byCardListId.forEach(card ->
                assertThat(card.cardListId()).isEqualTo(expectedCardListId));
    }
}