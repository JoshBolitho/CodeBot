package main.JSONClasses;

public class Post {
    String currentPostID;
    String currentPostText;

    String[] queuedPosts;
    String[] pastPosts;

    String currentSubmissionsID;
    String[] facebookSubmissions;

    String[] curatedChallenges;

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

    public String getCurrentSubmissionsID() {
        return currentSubmissionsID;
    }

    public String[] getFacebookSubmissions() {
        return facebookSubmissions;
    }

    public String[] getCuratedChallenges() {
        return curatedChallenges;
    }

    public Post(String currentPostID, String currentPostText, String[] queuedPosts, String[] pastPosts, String currentSubmissionsID, String[] facebookSubmissions, String[] curatedChallenges) {
        this.currentPostID = currentPostID;
        this.currentPostText = currentPostText;
        this.queuedPosts = queuedPosts;
        this.pastPosts = pastPosts;
        this.currentSubmissionsID = currentSubmissionsID;
        this.facebookSubmissions = facebookSubmissions;
        this.curatedChallenges = curatedChallenges;
    }

}
