package JSONClasses;

public class Config {
    String user_access_token;
    String page_access_token;
    String page_ID;
    String cloudinary_upload_preset;


    public String getUser_access_token() {
        return user_access_token;
    }

    public String getPage_access_token() {
        return page_access_token;
    }

    public String getPage_ID() {
        return page_ID;
    }

    public String getCloudinary_upload_preset() { return cloudinary_upload_preset; }
}
