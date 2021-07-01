package main.JSONClasses;

public class CommentData {
    Comment[] data;
    CommentResponseInfo paging;

    public Comment[] getData() {
        return data;
    }
    public CommentResponseInfo getPaging() {
        return paging;
    }
}
