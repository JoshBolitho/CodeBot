package main.JSONClasses;

public class Post {
    String currentPostID;
    String currentPostText;

    public String getCurrentPostID() {
        return currentPostID;
    }

    public String getCurrentPostText() {
        return currentPostText;
    }

    public Post(String currentPostID, String currentPostText) {
        this.currentPostID = currentPostID;
        this.currentPostText = currentPostText;
    }
}
