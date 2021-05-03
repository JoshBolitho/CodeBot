package JSONClasses;

public class Comment {
    String created_time;
    CommentNameID from;
    String message;
    String id;


    public String getCreated_time() {
        return created_time;
    }

    public CommentNameID getFrom() {
        return from;
    }

    public String getMessage() {
        return message;
    }

    public String getId() {
        return id;
    }
}
