package main.JSONClasses;

public class Comment {
    String message;
    String id;
    Attachment attachment;
    CommentData comments;

    From from;

    public String getMessage() {
        return message;
    }

    public String getId() {
        return id;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public CommentData getComments() {
        return comments;
    }

    public From getFrom() {
        return from;
    }
}
