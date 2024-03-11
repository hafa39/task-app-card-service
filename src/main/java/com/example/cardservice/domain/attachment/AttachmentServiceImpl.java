package com.example.cardservice.domain.attachment;

import com.example.cardservice.domain.activity.ActivityProxy;
import com.example.cardservice.domain.activity.CardActivities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AttachmentServiceImpl implements AttachmentService {

    private AttachmentRepository attachmentRepository;
    private StreamBridge streamBridge;

    private static final Logger log =
            LoggerFactory.getLogger(AttachmentServiceImpl.class);

    public AttachmentServiceImpl(AttachmentRepository attachmentRepository, StreamBridge streamBridge) {
        this.attachmentRepository = attachmentRepository;
        this.streamBridge = streamBridge;
    }

    @Override
    public Attachment addAttachment(long cardId, MultipartFile file, String userId) {
        try {
            log.info("Creating attachment for card with ID: {}", cardId);

            Attachment newAttachment = Attachment.of(cardId, userId, file);
            Attachment savedAttachment = attachmentRepository.save(newAttachment);

            log.info("Attachment created with id: {}", savedAttachment.id());

            ActivityProxy activityProxy = CardActivities.fromCardAttachmentAdded(userId, cardId, savedAttachment);
            boolean eventSent = streamBridge.send("sendActivity-out-0", activityProxy);

            log.info("Add attachment event was sent: {}", eventSent);

            return savedAttachment;
        } catch (IOException ex) {
            log.error("Error creating or saving attachment for card with ID {}: {}", cardId, ex.getMessage());
            throw new AttachmentCreationException(ex.getMessage(), ex);
        }
    }


    @Override
    public List<Attachment> getByCard(long cardId) {
        log.info("Fetching attachments for card with ID: {}", cardId);

        List<Attachment> attachments = new ArrayList<>();
        attachmentRepository.findByCardId(cardId).forEach(attachments::add);

        log.info("Fetched {} attachments for card with ID: {}", attachments.size(), cardId);

        return attachments;
    }


    @Override
    public void removeByCard(Long cardId) {
        log.info("Removing attachments for card with ID: {}", cardId);
        attachmentRepository.deleteAllByCardId(cardId);
        log.info("Attachments with card ID {} have been successfully removed", cardId);
    }

    @Override
    public void removeById(String userId,Long id) {
        log.info("Removing attachment with ID: {}", id);
        Attachment existingAttachment = attachmentRepository.findById(id).orElseThrow();
        attachmentRepository.deleteById(id);
        log.info("Attachment with ID {} has been successfully removed", id);
        ActivityProxy proxy = CardActivities.fromCardAttachmentRemoved(userId, existingAttachment.cardId(), existingAttachment);
        sendActivity(proxy);
    }

    private void sendActivity(ActivityProxy proxy) {
        boolean isActivitySent = streamBridge.send("sendActivity-out-0", proxy);
        log.info("{} activity was sent: {}", proxy.activityType(), isActivitySent);
    }
}
