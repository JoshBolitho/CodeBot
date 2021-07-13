package main.JSONClasses;

public class Comment {
    String message;
    String id;
    Attachment attachment;
    CommentData comments;

    From from;

    Reactions reactions;

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

    public Reactions getReactions() {
        return reactions;
    }
}
