package main.JSONClasses;

public class CloudinaryResponse {
    String asset_id;
    String public_id;
    String version;
    String version_id;
    String signature;
    Integer width;
    Integer height;
    String format;
    String resource_type;
    String created_at;
    String[] tags;
    Integer bytes;
    String type;
    String etag;
    Boolean placeholder;
    String url;
    String secure_url;
    String access_mode;

    public String getUrl() {
        return url;
    }
}
