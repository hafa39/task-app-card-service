package com.example.cardservice.web.api;

import com.example.cardservice.CardServiceApplication;
import com.example.cardservice.config.SecurityConfig;
import com.example.cardservice.domain.card.Card;
import com.example.cardservice.domain.card.CardService;
import com.example.cardservice.security.SecurityService;
import com.example.cardservice.web.dto.AddCardPayload;
import com.example.cardservice.web.dto.ChangeCardTitlePayload;
import com.example.cardservice.web.result.AddCardResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CardController.class)
@Import({SecurityConfig.class, CardServiceApplication.class})
class CardControllerTest {

    @MockBean
    private CardService cardService;


    // set the bean name like in @PreAuthorize("@boardServiceImpl.isUserMemberOfBoard(#id,#jwt.subject)")
    @MockBean(name = "securityService")
    private SecurityService securityService;

    @MockBean
    JwtDecoder jwtDecoder;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    private final String USER_ID = "user_id";
    private final long BOARD_ID = 1L;
    private final long CARD_ID = 10L;

    @Test
    void whenAddingCardAsMemberToBoard_thenReturn201() throws Exception {
        long cardListId = 2L;
        // Mocking securityService.canManipulateCardsOnBoard
        when(securityService.canManipulateCardsOnBoard(BOARD_ID, USER_ID)).thenReturn(true);

        // Creating AddCardPayload
        AddCardPayload requestPayload = new AddCardPayload(BOARD_ID, cardListId, "Test-title", 1);

        // Mocking cardService.addCard
        Card savedCard = new Card(1L, requestPayload.title(), "", requestPayload.position(), false, Instant.now(), "", cardListId);
        when(cardService.addCard(requestPayload)).thenReturn(savedCard);

        // Creating expected result
        AddCardResult expectedResult = AddCardResult.of(savedCard);

        // Converting payloads and results to JSON strings
        String jsonRequestPayload = mapper.writeValueAsString(requestPayload);
        String expectedResultJson = mapper.writeValueAsString(expectedResult);

        // Performing the HTTP request and validating the response
        String actualResultJson = mockMvc.perform(post("/cards")
                        .with(jwt()
                                .jwt(c -> c.subject(USER_ID)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestPayload))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Asserting that the actual result matches the expected result
        assertThat(expectedResultJson).isEqualTo(actualResultJson);
    }


    @Test
    void whenAddingCardAsNonMemberToBoard_thenReturn403() throws Exception {
        when(securityService.canManipulateCardsOnBoard(BOARD_ID, USER_ID)).thenReturn(false);
        // Creating AddCardPayload
        long cardListId = 2L;
        AddCardPayload requestPayload = new AddCardPayload(BOARD_ID, cardListId, "Test-title", 1);
        // Converting payloads and results to JSON strings
        String jsonRequestPayload = mapper.writeValueAsString(requestPayload);

        // Performing the HTTP request and validating the response
        mockMvc.perform(post("/cards")
                        .with(jwt()
                                .jwt(c -> c.subject(USER_ID)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestPayload))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenChangingCardTitleAsNonMember_thenReturn403() throws Exception {
        when(securityService.canManipulateCard(CARD_ID, USER_ID)).thenReturn(false);
        ChangeCardTitlePayload title = new ChangeCardTitlePayload("new title");
        String body = mapper.writeValueAsString(title);

        mockMvc.perform(put("/cards/{cardId}/title", CARD_ID)
                        .with(jwt()
                                .jwt(c -> c.subject(USER_ID)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}