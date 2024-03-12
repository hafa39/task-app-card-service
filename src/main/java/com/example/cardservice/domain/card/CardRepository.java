package com.example.cardservice.domain.card;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CardRepository extends CrudRepository<Card,Long> {

    public List<Card> findByCardListId(Long id);
}
