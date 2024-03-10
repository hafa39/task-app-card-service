package com.example.cardservice.domain.attachment;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AttachmentService {

    Attachment addAttachment(long cardId, MultipartFile file, String userId);

    List<Attachment> getByCard(long cardId);

    void removeByCard(Long cardId);

    void removeById(String userId, Long id);
}
