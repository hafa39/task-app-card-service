package com.example.cardservice.domain.activity;
public record ActivityProxy(
        String userId,
        long cardId,
        String activityType,
        String detail) {

}



