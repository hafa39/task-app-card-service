package com.example.cardservice.domain.activity;

import com.example.cardservice.web.proxy.BoardClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class ActivityClient {

    @Value("${activity.service.url}")
    private String ACTIVITY_SERVICE_URL;
    private static final Logger logger = LoggerFactory.getLogger(BoardClient.class);

    public List<Activity> getActivitiesByCard(long cardId){
        String url = ACTIVITY_SERVICE_URL + "/activities?cardId="+cardId;

        logger.info("Initiating GET request to URL: {}", url);

        List<Activity> activities = WebClient.create(ACTIVITY_SERVICE_URL)
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Activity>>() {})
                .doOnNext(result ->logger.info("Result received: {}", result))
                .block();

        logger.info("GET request completed to URL: {}", url);
        return activities;
    }

    public Activity getActivity(long activityId){
        String url = ACTIVITY_SERVICE_URL + "/activities/"+activityId;

        logger.info("Initiating GET request to URL: {}", url);

        Activity activity = WebClient.create(ACTIVITY_SERVICE_URL)
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Activity.class)
                .doOnNext(result ->logger.info("Result received: {}", result))
                .block();

        logger.info("GET request completed to URL: {}", url);
        return activity;
    }
}
