package com.example.cardservice.domain.attachment;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface AttachmentRepository extends CrudRepository<Attachment,Long> {

    Iterable<Attachment> findByCardId(Long cardId);
    @Modifying
    @Query("delete from attachments a where a.card_id = :cardId")
    void deleteAllByCardId(@Param("cardId") Long cardId);
}
