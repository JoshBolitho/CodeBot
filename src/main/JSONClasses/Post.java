package main.JSONClasses;

public class Post {
    String currentPost;
    String[] repliedComments;

    public Post(String currentPost, String[] repliedComments) {
        this.currentPost = currentPost;
        this.repliedComments = repliedComments;
    }

    public String getCurrentPost() {
        return currentPost;
    }

    public String[] getRepliedComments() {
        return repliedComments;
    }
}
