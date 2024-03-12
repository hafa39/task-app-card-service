package com.example.cardservice.domain.cardlist;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface CardListRepository extends CrudRepository<CardList,Long> {
    @Transactional(readOnly = true)
    Iterable<CardList> findByBoardId(Long id);
}
