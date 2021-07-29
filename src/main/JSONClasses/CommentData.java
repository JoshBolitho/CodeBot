package main.JSONClasses;

public class CommentData {

    Comment[] data;

    APIError error;

    Paging paging;

    public Comment[] getData() {
        return data;
    }

    public APIError getError() {
        return error;
    }

    public Paging getPaging() {
        return paging;
    }
}
