package main.JSONClasses;

public class Post {
    String currentPostID;
    String currentPostText;

    String[] queuedPosts;
    String[] pastPosts;

    public String getCurrentPostID() {
        return currentPostID;
    }

    public String getCurrentPostText() {
        return currentPostText;
    }

    public String[] getQueuedPosts() {
        return queuedPosts;
    }

    public String[] getPastPosts() {
        return pastPosts;
    }

    public Post(String currentPostID, String currentPostText, String[] queuedPosts, String[] pastPosts) {
        this.currentPostID = currentPostID;
        this.currentPostText = currentPostText;
        this.queuedPosts = queuedPosts;
        this.pastPosts = pastPosts;
    }
}
