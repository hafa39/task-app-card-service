package com.example.cardservice.domain.activity;

import java.time.Instant;


public record Activity(
        Long id,
        String userId,
        Long cardId,
        String type,
        String detail,
        Instant createdDate) {

}
