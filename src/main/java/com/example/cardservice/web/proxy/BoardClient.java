package com.example.cardservice.web.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class BoardClient {
    @Value("${board.service.url}")
    private String BOARD_SERVICE_URL;
    private static final Logger logger = LoggerFactory.getLogger(BoardClient.class);


    public Boolean isUserMemberOfBoard(Long boardId, String userId) {

        String url = BOARD_SERVICE_URL + "/intern/isMember?userId="+ userId +"&boardId=" + boardId;

        logger.info("Initiating GET request to URL: {}", url);

        Boolean isMember = WebClient.create(BOARD_SERVICE_URL)
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Boolean.class)
                .doOnNext(result ->logger.info("Result received: {}", result))
                .block();

        logger.info("GET request completed to URL: {}", url);

        return isMember;
    }
}
