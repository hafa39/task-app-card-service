package com.example.cardservice.domain.cardlist;

import com.example.cardservice.web.dto.ChangeCardListPositionsPayload;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardListServiceImplTest {

    @Mock
    private CardListRepository repository;
    @InjectMocks
    private CardListServiceImpl cardListService;

    @Test
    void changePositions() {
        long boardId = 1l;
        String creatorId = "CreatorId";
        int posList1 = 0;
        int posList2 = 1;

        var existingL1 = CardList.of("L1", posList1, boardId, creatorId);
        var existingL2 = CardList.of("L2", posList2, boardId, creatorId);

        when(repository.findById(1L)).thenReturn(Optional.of(existingL1));
        when(repository.findById(2L)).thenReturn(Optional.of(existingL2));

        var newPosList1 = new CardListPosition(1L, 1);
        var newPosList2 =new CardListPosition(2L, 0);

        var updatedL1 = CardList.updatePosition(existingL1,newPosList1.position());
        var updatedL2 = CardList.updatePosition(existingL2,newPosList2.position());

        when(repository.save(updatedL1)).thenReturn(updatedL1);
        when(repository.save(updatedL2)).thenReturn(updatedL2);

        var positions = new ChangeCardListPositionsPayload(1L,
                List.of(newPosList1,newPosList2));

        var result = cardListService.changePositions(positions);
        assertThat(result).hasSize(2);
        result.forEach(cardList -> {
            if (cardList.name().equals("L1")){
                assertThat(cardList.position()).isEqualTo(newPosList1.position());
            }
            else{
                assertThat(cardList.position()).isEqualTo(newPosList2.position());
            }
        });

    }
}