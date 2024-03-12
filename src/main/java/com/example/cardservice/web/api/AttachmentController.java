package com.example.cardservice.web.api;

import com.example.cardservice.domain.attachment.Attachment;
import com.example.cardservice.domain.attachment.AttachmentService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/attachments")
public class AttachmentController {

    private AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Attachment addAttachment(@RequestParam(name = "cardId") long cardId,
                                    @RequestParam("file") MultipartFile file,
                                    @AuthenticationPrincipal Jwt jwt){

        return attachmentService.addAttachment(cardId,file,jwt.getSubject());
    }

    @GetMapping
    public List<Attachment> getAttachments(@RequestParam(name = "cardId") Long cardId){
        return attachmentService.getByCard(cardId);
    }

    @DeleteMapping()
    public void removeAttachmentByCard(@RequestParam Long cardId){
        attachmentService.removeByCard(cardId);
    }

    @DeleteMapping("/{id}")
    public void removeAttachment(@PathVariable Long id,@AuthenticationPrincipal Jwt jwt){
        attachmentService.removeById(jwt.getSubject(), id);
    }
}
