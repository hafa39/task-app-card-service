package com.example.cardservice.domain.activity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

class ActivityDetail {

  private Map<String, Object> detail = new HashMap<>();

  private ActivityDetail() {
  }

  static ActivityDetail blank() {
    return new ActivityDetail();
  }

  public ActivityDetail add(String key, Object value) {
    detail.put(key, value);
    return this;
  }

  String toJson() {
    try {
      return new ObjectMapper().writeValueAsString(detail);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
