package com.example.cardservice.web.api;

import com.example.cardservice.domain.card.CardService;
import com.example.cardservice.domain.cardlist.CardList;
import com.example.cardservice.domain.cardlist.CardListService;
import com.example.cardservice.web.dto.AddCardListPayload;
import com.example.cardservice.web.dto.ChangeCardListPositionsPayload;
import com.example.cardservice.web.result.CardlistResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cardlists")
public class CardListController {
    private CardListService cardListService;
    private CardService cardService;

    public CardListController(CardListService cardListService, CardService cardService) {
        this.cardListService = cardListService;
        this.cardService = cardService;
    }

    @GetMapping("")
    public List<Map<String,Object>> getByBoardId(@RequestParam(value = "boardId") String boardId){
        return cardListService.findByBoardId(Long.valueOf(boardId))
                .stream()
                .map(cardList -> CardlistResult.build(cardList,cardService.findByCardlist(cardList.id())))
                .collect(Collectors.toList());
    }

    @PostMapping("/positions")
    public ResponseEntity<Void> changeCardListPositions(@RequestBody ChangeCardListPositionsPayload payload) {
        cardListService.changePositions(payload);
        return ResponseEntity.ok().build();
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CardList addCardList(@RequestBody AddCardListPayload payload,
                                @AuthenticationPrincipal Jwt jwt) {
        CardList cardList = cardListService.addCardList(payload, jwt.getSubject());
        return cardList;
    }
}
