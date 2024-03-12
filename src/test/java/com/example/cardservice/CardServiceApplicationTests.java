package com.example.cardservice;

import com.example.cardservice.domain.card.Card;
import com.example.cardservice.web.dto.AddCardPayload;
import com.example.cardservice.web.dto.ChangeCardTitlePayload;
import com.example.cardservice.web.proxy.BoardClient;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "dev")
@Testcontainers
@Import(TestChannelBinderConfiguration.class)
class CardServiceApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private OutputDestination output;

    private static KeycloakToken bjornTokens;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BoardClient boardClient;
    @Container

    private static final KeycloakContainer keycloakContainer =  new KeycloakContainer("quay.io/keycloak/keycloak:19.0")
            .withRealmImportFile("test-realm-config.json");

    @Container
    static PostgreSQLContainer<?> database = new PostgreSQLContainer<>(DockerImageName.parse("postgres:14.4"));

    static {
        keycloakContainer.start();
        database.start();
    }

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloakContainer.getAuthServerUrl() + "realms/TaskAgile");
        registry.add("spring.datasource.url", database::getJdbcUrl);
        registry.add("spring.datasource.username", database::getUsername);
        registry.add("spring.datasource.password", database::getPassword);
    }

    @BeforeAll
    static void generateAccessTokens() {
        WebClient webClient = WebClient.builder()
                .baseUrl(keycloakContainer.getAuthServerUrl() + "realms/TaskAgile/protocol/openid-connect/token")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();

        //isabelleTokens = authenticateWith("isabelle", "password", webClient);
        bjornTokens = authenticateWith("bjorn", "password", webClient);
    }

    @Test
    void whenPutChangeCardTitleWithValidUserThenReturn(){
        var payload = new ChangeCardTitlePayload("updated title");
        Long boardId = 1L;
        Long cardId= 1L;
        when(boardClient.isUserMemberOfBoard(boardId, bjornTokens.extractSubject())).thenReturn(true);
        webTestClient.put()
                .uri("cards/"+cardId+"/title")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(bjornTokens.accessToken()))
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri("/cards/"+cardId)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(bjornTokens.accessToken()))
                .exchange()
                .expectBody(Card.class)
                .value(card -> {
                    assertThat(card.id()).isEqualTo(cardId);
                    assertThat(card.title()).isEqualTo(payload.title());
                });

    }

    @Test
    void whenPostCardAndUserIsBoardMemberThenReturnCreatedPost(){
        Long boardId = 1L;
        Long cardListId = 1L;
        when(boardClient.isUserMemberOfBoard(boardId, bjornTokens.extractSubject())).thenReturn(true);
        AddCardPayload cardPayload = new AddCardPayload(boardId, cardListId, "C1", 1);
        webTestClient.post()
                .uri("/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cardPayload)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(bjornTokens.accessToken()))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Card.class)
                .value(receivedCard -> {
                    assertThat(receivedCard.id()).isNotNull();
                    assertThat(receivedCard.title()).isEqualTo(cardPayload.title());
                });

       /* assertThat(objectMapper.readValue(output.receive().getPayload(), .class))
                .isEqualTo(new OrderAcceptedMessage(createdOrder.id()));*/
    }


    private static KeycloakToken authenticateWith(String username, String password, WebClient webClient) {
        return webClient
                .post()
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("client_id", "polar-test")
                        .with("username", username)
                        .with("password", password)
                )
                .retrieve()
                .bodyToMono(KeycloakToken.class)
                .block();
    }
    private record KeycloakToken(String accessToken) {

        @JsonCreator
        private KeycloakToken(@JsonProperty("access_token") final String accessToken) {
            this.accessToken = accessToken;
        }

        public String extractSubject() {
            try {
                JWT jwt = JWTParser.parse(accessToken);
                JWTClaimsSet jwtClaimsSet = jwt.getJWTClaimsSet();
                return jwtClaimsSet.getSubject();
            } catch (Exception e) {
                // Handle parsing exceptions
                throw new RuntimeException("Failed to extract subject from JWT", e);
            }
        }
    }

}
