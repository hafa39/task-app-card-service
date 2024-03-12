package com.example.cardservice.domain.attachment;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;

@Table("attachments")
public record Attachment(
        @Id
        Long id,
        Long cardId,
        String userId,
        String fileName,
        byte[] content,
        boolean archived,
        Instant createdDate
){
        static Attachment of(Long cardId, String userId, MultipartFile file) throws IOException {
                return new Attachment(null,cardId,userId, file.getOriginalFilename(), file.getBytes(), false,Instant.now());
        }
}
