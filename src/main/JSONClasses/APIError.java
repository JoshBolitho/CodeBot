package main.JSONClasses;

public class APIError {
    //FB Graph API Errors contain all these fields.
    //Cloudinary API only contains message field.
    String message;
    String type;
    Integer code;
    Integer error_subcode;
    String fbtrace_id;

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public Integer getCode() {
        return code;
    }

    public Integer getError_subcode() {
        return error_subcode;
    }

    public String getFbtrace_id() {
        return fbtrace_id;
    }
}