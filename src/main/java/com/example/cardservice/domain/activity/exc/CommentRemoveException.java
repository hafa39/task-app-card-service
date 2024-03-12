package com.example.cardservice.domain.activity.exc;

public class CommentRemoveException extends RuntimeException{
    public CommentRemoveException(long commentId, String userId) {

        super("Use r" +userId+" is not allowed to remove comment with ID "+commentId);
    }
}
