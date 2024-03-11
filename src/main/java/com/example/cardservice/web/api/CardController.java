package com.example.cardservice.web.api;

import com.example.cardservice.domain.activity.Activity;
import com.example.cardservice.domain.card.Card;
import com.example.cardservice.domain.card.CardService;
import com.example.cardservice.security.SecurityService;
import com.example.cardservice.web.dto.*;
import com.example.cardservice.web.result.AddCardResult;
import com.example.cardservice.web.result.CardResult;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/cards")
public class CardController {

    private CardService cardService;
    private SecurityService securityService;

    public CardController(CardService cardService, SecurityService securityService) {
        this.cardService = cardService;
        this.securityService = securityService;
    }

    @PostMapping()
    @PreAuthorize("@securityService.canManipulateCardsOnBoard(#payload.boardId(),#jwt.subject)")
    public ResponseEntity<AddCardResult> addCard(@RequestBody @Valid AddCardPayload payload, @AuthenticationPrincipal Jwt jwt) {
        Card card = cardService.addCard(payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(AddCardResult.of(card));
    }

    @PostMapping("/positions")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@securityService.canManipulateCardsOnBoard(#payload.boardId(),#jwt.subject)")
    public void changeCardPositions(@RequestBody ChangeCardPositionsPayload payload,@AuthenticationPrincipal Jwt jwt) {
        cardService.changePositions(payload);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityService.canManipulateCard(#cardId,#jwt.subject)")
    public CardResult getCard(@PathVariable("id") Long cardId,  @AuthenticationPrincipal Jwt jwt){
        Card card = cardService.getCardById(cardId);
        return CardResult.of(card);
    }

    @PutMapping("/{cardId}/title")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@securityService.canManipulateCard(#cardId,#jwt.subject)")
    public void changeTitle(@PathVariable long cardId, @RequestBody @Valid ChangeCardTitlePayload payload, @AuthenticationPrincipal Jwt jwt) {
        cardService.changeCardTitle(payload,cardId,jwt.getSubject());
    }

    @PutMapping("/{cardId}/description")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@securityService.canManipulateCard(#cardId,#jwt.subject)")
    public void changeDescription(@PathVariable long cardId, @RequestBody @Valid ChangeCardDescriptionPayload payload,@AuthenticationPrincipal Jwt jwt) {
        cardService.changeCardDescription(payload,cardId,jwt.getSubject());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityService.canManipulateCard(#cardId,#jwt.subject)")
    public void removeCard(@PathVariable(name = "id")Long cardId, @AuthenticationPrincipal Jwt jwt){
        cardService.removeCard(cardId);
    }

    @PostMapping("/{cardId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@securityService.canManipulateCard(#cardId,#jwt.subject)")
    public void addCardComment(@PathVariable long cardId, @RequestBody @Valid AddCardCommentPayload payload, @AuthenticationPrincipal Jwt jwt) {
        cardService.addComment(payload,cardId, jwt.getSubject());
    }

    @DeleteMapping("/{cardId}/comments")
    @PreAuthorize("@securityService.canManipulateCard(#cardId,#jwt.subject)")
    public void removeComment(@PathVariable long cardId,
                              @RequestParam("commentId") long commentId,
                              @AuthenticationPrincipal Jwt jwt){
        cardService.removeComment(cardId,commentId, jwt.getSubject());
    }

    @GetMapping("/{cardId}/activities")
    @PreAuthorize("@securityService.canManipulateCard(#cardId,#jwt.subject)")
    public List<Activity> getCardActivities(@PathVariable long cardId, @AuthenticationPrincipal Jwt jwt){
        return cardService.getCardActivities(cardId);
    }

    @PutMapping("/{cardId}/archived")
    @PreAuthorize("@securityService.canManipulateCard(#cardId,#jwt.subject)")
    public void updateArchived(@PathVariable("cardId") long cardId,
                               @RequestParam("archived") boolean archived,
                               @AuthenticationPrincipal Jwt jwt){
        cardService.updateArchived(cardId,archived);
    }
}
