package main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.JSONClasses.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

public class CodeBot {
    static String user_access_token = "";
    static String page_access_token = "";
    static String page_ID = "";

    //The current post
    static String postID = "";
    static ArrayList<String> repliedComments = new ArrayList<>();

    //Cloudinary config
    static String cloudinary_upload_preset = "";

    public static void loadConfig() throws IOException {

        Path fileName = Path.of("src/main/config.json");
        String configJSON = Files.readString(fileName);

        Gson gson = new Gson();
        Config config = gson.fromJson(configJSON, Config.class);
        user_access_token = config.getUser_access_token();
        page_access_token = config.getPage_access_token();
        page_ID = config.getPage_ID();
        cloudinary_upload_preset = config.getCloudinary_upload_preset();

    }

    public static void loadPostData() throws IOException {

        Path fileName = Path.of("src/main/post.json");
        String postJSON = Files.readString(fileName);

        Gson gson = new Gson();
        Post post = gson.fromJson(postJSON, Post.class);
        postID = post.getCurrentPost();
        repliedComments = new ArrayList<>(Arrays.asList(post.getRepliedComments()));

    }

    public static void writePostData() throws IOException {

        String[] myReplies = repliedComments.toArray(new String[0]);
        Post post = new Post(postID,myReplies);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String postJSON = gson.toJson(post);

        Path fileName = Path.of("src/main/post.json");
        Files.writeString(fileName,postJSON);
    }

    public static void executeComments(String APIResponse) throws IOException, InterruptedException {

        Gson gson = new Gson();
        CommentData messageWrapper = gson.fromJson(APIResponse, CommentData.class);
        Comment[] comments = messageWrapper.getData();

        for(Comment c : comments){
            //if this comment has been replied to already, ignore it.
            if(repliedComments.contains(c.getId())){continue;}

            //Empty message
            if(c.getMessage().equals("")){
                if(c.getAttachment()!=null){
                    System.out.println("replying to comment: "+c.getId());
                    replyComment(c.getId(), "[^_^] Nice pic!");

                    //update repliedComments and write to post.json
                    repliedComments.add(c.getId());
                    writePostData();

                    continue;
                }
                continue;
            }

            BufferedImage inputImage = null;
            if(c.getAttachment()!=null){
                String commentImageURL = c.getAttachment().getMedia().getImage().getSrc();
                inputImage = ImageIO.read(new URL(commentImageURL));
            }

            //Create and run the scriptExecutor with
            ScriptExecutor scriptExecutor = new ScriptExecutor(c.getMessage(), inputImage);
            scriptExecutor.run();
            String result = scriptExecutor.getResult();

            System.out.println(result);
            System.out.println("replying to comment: "+c.getId());

            if(scriptExecutor.getCanvasVisibility() && scriptExecutor.getCanvas()!=null){
                replyCommentImage(c.getId(), result,scriptExecutor.getCanvas(), cloudinary_upload_preset);
            } else{
                replyComment(c.getId(), result);
            }

            //update repliedComments and write to post.json
            repliedComments.add(c.getId());
            writePostData();
        }

    }

    // Request the comments on a Facebook Graph API Element by its object ID
    public static String requestComments(String objectID) throws IOException, InterruptedException {
        // Create a client
        var client = HttpClient.newHttpClient();

        // Get comments
        var getComments = HttpRequest.newBuilder(
                URI.create(String.format("https://graph.facebook.com/v9.0/%s/comments?access_token=%s&fields=message,id,attachment",objectID, user_access_token))
        ).build();

        // use the client to send the request
        HttpResponse<String> response = client.send(getComments, HttpResponse.BodyHandlers.ofString());

        // the response from facebook graph API:
        return response.body();
    }

    public static void replyComment(String objectID, String message) throws IOException, InterruptedException {
        if(message.equals("")){message = "[^_^]";}
        message = URLEncoder.encode(message,StandardCharsets.UTF_8.toString());

        // Create a client
        var client = HttpClient.newHttpClient();

        // Publish Comment
        var publishComment = HttpRequest.newBuilder().uri(
                URI.create(String.format("https://graph.facebook.com/v9.0/%s/comments/",objectID)))
                .POST(HttpRequest.BodyPublishers.ofString(
                        String.format("message=%s&access_token=%s",message,user_access_token))
        ).build();

        // use the client to send the request
        HttpResponse<String> response = client.send(publishComment, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);
    }

    public static void replyCommentImage(String objectID, String message, BufferedImage bufferedImage,String uploadPreset) throws IOException, InterruptedException {

        //write bufferedImage to byte array
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "PNG", out);
        byte[] bytes = out.toByteArray();

        //encode byte array as base64 string
        String base64bytes = Base64.getEncoder().encodeToString(bytes);
        //add data URI string formatting
        String imageDataURI = "data:image/png;base64," + base64bytes;
        //convert string to url escaped string
        String encodedImageDataURI;

        encodedImageDataURI = URLEncoder.encode(imageDataURI, StandardCharsets.UTF_8.toString());

        // Create a client for cloudinary image upload
        var cloudinaryClient = HttpClient.newHttpClient();
        String cloudinaryBody = String.format("file=%s&upload_preset=%s",encodedImageDataURI,uploadPreset);
        var uploadImage = HttpRequest.newBuilder().uri(
                (URI.create("https://api.cloudinary.com/v1_1/factbotimagehost/image/upload/")))
                .POST(HttpRequest.BodyPublishers.ofString(cloudinaryBody))
                .build();

        // use the client to send the request
        HttpResponse<String> cloudinaryResponse = cloudinaryClient.send(uploadImage, HttpResponse.BodyHandlers.ofString());

        //Parse Cloudinary response body JSON
        Gson gson = new Gson();
        CloudinaryResponse cloudinaryResponseWrapper = gson.fromJson(cloudinaryResponse.body(), CloudinaryResponse.class);
        String imageURL = cloudinaryResponseWrapper.getUrl();
        System.out.println("imageURL: "+imageURL);

        //Cloudinary api fail
        if(imageURL==null){
            replyComment(objectID,message +"\n(Image upload failed)");
            return;
        }

        if(message.equals("")){message = "[^_^]";}
        message = URLEncoder.encode(message,StandardCharsets.UTF_8.toString());

        // Create a client for facebook image comment upload
        var facebookClient = HttpClient.newHttpClient();
        String facebookBody = String.format("message=%s&attachment_url=%s&access_token=%s",message,imageURL,user_access_token);
        var publishComment = HttpRequest.newBuilder().uri(
                URI.create(String.format("https://graph.facebook.com/v9.0/%s/comments/",objectID)))
                .POST(HttpRequest.BodyPublishers.ofString(facebookBody))
                .build();

        // use the client to send the request
        HttpResponse<String> facebookResponse = facebookClient.send(publishComment, HttpResponse.BodyHandlers.ofString());
//        System.out.println(facebookBody);
        System.out.println(facebookResponse);

    }

    public static String publishPost(String message) throws IOException, InterruptedException {
        // create a client
        var client = HttpClient.newHttpClient();

        // Publish Post
        var publishPost = HttpRequest.newBuilder().uri(
                URI.create(String.format("https://graph.facebook.com/v9.0/%s/feed",page_ID)))
                .POST(HttpRequest.BodyPublishers.ofString(
                        String.format("message=%s&access_token=%s",message, user_access_token))
                ).build();


        //Use the client to send the request
        HttpResponse<String> response = client.send(publishPost, HttpResponse.BodyHandlers.ofString());
        //Handle the response
        Gson gson = new Gson();
        PostResponse postResponse = gson.fromJson(response.body(), PostResponse.class);

        String newPostID = postResponse.getId();

        //Overwrite post.json with new post ID and an empty repliedComments array.
        postID = newPostID.split("_")[1];

        repliedComments = new ArrayList<>();
        writePostData();

        // the response from facebook graph API:
        return postResponse.getId();
    }

    public static void publishComment(String objectID, String message) throws IOException, InterruptedException {
        // Create a client
        var client = HttpClient.newHttpClient();

        // Publish Comment
        var publishComment = HttpRequest.newBuilder().uri(
                URI.create(String.format("https://graph.facebook.com/v9.0/%s/comments/",objectID)))
                .POST(HttpRequest.BodyPublishers.ofString(
                        String.format("message=%s&access_token=%s",message,user_access_token))
                ).build();

        // use the client to send the request
        HttpResponse<String> response = client.send(publishComment, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        //Options: Comment, Post
        String mode = "Comment";
        String message = "Test Post 1st July!";

        if(mode.equals("Comment")){
            loadConfig();
            loadPostData();

            String commentData = requestComments(String.format("%s_%s",page_ID,postID));
            executeComments(commentData);
        }

        if(mode.equals("Post")){
            loadConfig();
            System.out.println(publishPost(message));
        }

    }
}
