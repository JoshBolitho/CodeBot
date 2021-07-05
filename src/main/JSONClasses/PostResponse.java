package main.JSONClasses;

public class PostResponse {
    String id;
    APIError error;

    public String getId() {
        return id;
    }

    public APIError getError() {
        return error;
    }
}
