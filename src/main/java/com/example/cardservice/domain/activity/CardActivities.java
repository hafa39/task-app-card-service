package com.example.cardservice.domain.activity;


import com.example.cardservice.domain.attachment.Attachment;

public class CardActivities {

    public static ActivityProxy fromCardTitleChanged(String userId, Long cardId, String oldTitle, String newTitle) {
        String detail = ActivityDetail.blank()
                .add("newTitle", newTitle)
                .add("oldTitle", oldTitle)
                .toJson();
        return new ActivityProxy(userId, cardId,ActivityType.CHANGE_CARD_TITLE.getType(), detail);
    }

    public static ActivityProxy fromCardDescriptionChanged(String userId, Long cardId, String cardTitle, String oldDescription, String newDescription) {
        String detail = ActivityDetail.blank()
                .add("cardTitle", cardTitle)
                .add("newDescription", newDescription)
                .add("oldDescription", oldDescription)
                .toJson();
        return new ActivityProxy(userId, cardId, ActivityType.CHANGE_CARD_DESCRIPTION.getType(), detail);
    }

    public static ActivityProxy fromAddedComment(String userId, Long cardId, String comment) {
        String detail = ActivityDetail.blank()
                .add("comment", comment)
                .toJson();
        return new ActivityProxy(userId, cardId, ActivityType.ADD_COMMENT.getType(), detail);
    }

    public static ActivityProxy fromCardAttachmentAdded(String userId, Long cardId, Attachment attachment) {
        String detail = ActivityDetail.blank()
                .add("attachmentId", attachment.id())
                .add("fileName", attachment.fileName())
                .toJson();

        return new ActivityProxy(userId, cardId, ActivityType.ADD_ATTACHMENT.getType(), detail);
    }

    public static ActivityProxy fromCardAttachmentRemoved(String userId, Long cardId, Attachment attachment) {
        String detail = ActivityDetail.blank()
                .add("attachmentId", attachment.id())
                .add("fileName", attachment.fileName())
                .toJson();

        return new ActivityProxy(userId, cardId, ActivityType.REMOVE_ATTACHMENT.getType(), detail);
    }

    public static ActivityProxy fromRemovedComment(String userId, Long cardId, Long commentId){

        return new ActivityProxy(userId,cardId,ActivityType.DELETE_COMMENT.getType(), commentId.toString());
    }
}
