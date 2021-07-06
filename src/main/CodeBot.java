package main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.JSONClasses.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

public class CodeBot {
    static String user_access_token = "";
    static String page_access_token = "";
    static String page_ID = "";

    //The current post
    static String postID = "";
    static String postText = "";
    static String[] queuedPosts;
    static String[] pastPosts;

    //Cloudinary config
    static String cloudinary_upload_preset = "";

    //Profanity filter
    static String[] profanity_list = new String[]{};

    public static void loadConfig() throws IOException {

        Path fileName = Path.of("src/main/config.json");
        String configJSON = Files.readString(fileName);

        Gson gson = new Gson();
        Config config = gson.fromJson(configJSON, Config.class);
        user_access_token = config.getUser_access_token();
        page_access_token = config.getPage_access_token();
        page_ID = config.getPage_ID();
        cloudinary_upload_preset = config.getCloudinary_upload_preset();
        profanity_list = config.getProfanity_list();

    }

    public static void loadPostData() throws IOException {

        Path fileName = Path.of("src/main/post.json");
        String postJSON = Files.readString(fileName);

        Gson gson = new Gson();
        Post post = gson.fromJson(postJSON, Post.class);
        postID = post.getCurrentPostID();
        postText = post.getCurrentPostText();
        queuedPosts = post.getQueuedPosts();
        pastPosts = post.getPastPosts();

    }

    public static void writePostData() throws IOException {

        Post post = new Post(postID,postText,queuedPosts,pastPosts);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String postJSON = gson.toJson(post);

        Path fileName = Path.of("src/main/post.json");
        Files.writeString(fileName,postJSON);
    }

    public static void executeComments(String APIResponse) throws InterruptedException {

        Gson gson = new Gson();
        CommentData commentData = gson.fromJson(APIResponse, CommentData.class);

        if(commentData.getError()!=null){
            log(commentData.getError(),"FB Graph API - Error retrieving comments");
            return;
        }

        Comment[] comments = commentData.getData();

        if(comments==null){
            log("FB Graph API - Error retrieving comments","Comments array is null");
            return;
        }

        for(Comment c : comments){

            try {
                //Test whether Bot has replied to comment c already.
                if (c.getComments() != null) {
                    boolean replied = false;
                    Comment[] replies = c.getComments().getData();
                    for (Comment r : replies) {
                        //If Comment c has been replied to by this bot already, ignore it.
                        if (r.getFrom().getId().equals(page_ID)) {
                            replied = true;
                            break;
                        }
                    }
                    if (replied) {
                        continue;
                    }
                }

                //Empty message
                if (c.getMessage().equals("")) {
                    if (c.getAttachment() != null) {
                        publishComment(c.getId(), "[^_^] Nice pic!");
                        continue;
                    }
                    continue;
                }

                BufferedImage inputImage = null;
                if (c.getAttachment() != null) {
                    try {
                        String commentImageURL = c.getAttachment().getMedia().getImage().getSrc();
                        inputImage = ImageIO.read(new URL(commentImageURL));

                        //Trim image if it exceeds maxImageSize
                        int width = inputImage.getWidth();
                        int height = inputImage.getHeight();
                        int max = ScriptExecutor.getMaxImageSize();

                        //truncate width
                        if (width > max) {
                            int startPoint = Math.floorDiv(width - max, 2);
                            inputImage = inputImage.getSubimage(startPoint, 0, max, height);

                            System.out.printf("rescaling %sx%s to %sx%s\n", width, height, max, height);
                        }

                        //Updated dimensions
                        width = inputImage.getWidth();
                        height = inputImage.getHeight();

                        //truncate height
                        if (height > max) {
                            int startPoint = Math.floorDiv(height - max, 2);
                            inputImage = inputImage.getSubimage(0, startPoint, width, max);
                            System.out.printf("rescaling %sx%s to %sx%s\n", width, height, width, max);
                        }

                    } catch (IOException e) {
                        log(e, "IOException while trying to load input image");
                        //non critical error, no need to break.
                    }
                }

                //Create and run the scriptExecutor
                ScriptExecutor scriptExecutor = new ScriptExecutor(c.getMessage(), inputImage);
                scriptExecutor.run();
                String result = scriptExecutor.getResult();

                System.out.println(result);

                //Test comment and result for profanity
                boolean containsProfanity = false;
                for (String s : profanity_list) {
                    if (c.getMessage().contains(s) || result.contains(s)) {
                        System.out.println("Profanity detected");
                        publishComment(c.getId(), "Profanity detected. Delete your comment.");

                        log(c, "Comment failed the profanity filter.");
                        containsProfanity = true;
                    }
                }
                if (containsProfanity) {
                    continue;
                }


                //Reply to comment, with or without an image!
                if (scriptExecutor.getCanvasVisibility() && scriptExecutor.getCanvas() != null) {
                    publishCommentImage(c.getId(), result, scriptExecutor.getCanvas(), cloudinary_upload_preset);
                } else {
                    publishComment(c.getId(), result);
                }
            }catch (InterruptedException e){
                throw e;
            }catch (Throwable e){
                log(e,"Unanticipated error type thrown during executeComments");
            }
        }
    }

    // Request the comments on a Facebook Graph API Element by its object ID
    public static String requestComments(String objectID) throws InterruptedException {
        // Create a client
        var client = HttpClient.newHttpClient();

        // Get comments
        var getComments = HttpRequest.newBuilder(
                URI.create(String.format("https://graph.facebook.com/v9.0/%s/comments?access_token=%s&fields=message,id,attachment,from,comments",objectID, user_access_token))
        ).build();

        // use the client to send the request
        HttpResponse<String> response;
        try {
            // Return Facebook Graph response:
            response = client.send(getComments, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException e){
            log(e,"IOException while trying to get FB comments");
            return null;
        }
    }

    public static void publishComment(String objectID, String message) throws InterruptedException {
        System.out.println("replying to: "+objectID);
        if(message.equals("")){message = "[^_^]";}
        try {
            message = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
        }catch (UnsupportedEncodingException e){
            log(e,"UnsupportedEncodingException while trying to encode response message to UTF8");
            return;
        }
        // Create a client
        var client = HttpClient.newHttpClient();

        // Publish Comment
        var publishComment = HttpRequest.newBuilder().uri(
                URI.create(String.format("https://graph.facebook.com/v9.0/%s/comments/",objectID)))
                .POST(HttpRequest.BodyPublishers.ofString(
                        String.format("message=%s&access_token=%s",message,user_access_token))
        ).build();

        // use the client to send the request
        try {
            HttpResponse<String> response = client.send(publishComment, HttpResponse.BodyHandlers.ofString());
            System.out.println(response);
        } catch (IOException e){
            log("Connection error while trying to send a commentF",e.getMessage());
        }
    }

    public static void publishCommentImage(String objectID, String message, BufferedImage bufferedImage, String uploadPreset) throws InterruptedException {

        //write bufferedImage to byte array
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "PNG", out);
        }catch (IOException e){
            log("Connection error while trying to write bufferedImage to ByteArrayOutputStream",e.getMessage());
            publishComment(objectID,message +"\n(Image upload failed)");
            return;
        }
        byte[] bytes = out.toByteArray();

        //encode byte array as base64 string
        String base64bytes = Base64.getEncoder().encodeToString(bytes);
        //add data URI string formatting
        String imageDataURI = "data:image/png;base64," + base64bytes;
        //convert string to url escaped string
        String encodedImageDataURI;

        try {
            encodedImageDataURI = URLEncoder.encode(imageDataURI, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e){
            log(e,"UnsupportedEncodingException while trying to encode response message to UTF8");
            publishComment(objectID,message +"\n(Image upload failed)");
            return;
        }

        // Create a client for cloudinary image upload
        var cloudinaryClient = HttpClient.newHttpClient();
        String cloudinaryBody = String.format("file=%s&upload_preset=%s",encodedImageDataURI,uploadPreset);
        var uploadImage = HttpRequest.newBuilder().uri(
                (URI.create("https://api.cloudinary.com/v1_1/factbotimagehost/image/upload/")))
                .POST(HttpRequest.BodyPublishers.ofString(cloudinaryBody))
                .build();

        // use the client to send the request
        HttpResponse<String> cloudinaryResponse;
        try {
            cloudinaryResponse = cloudinaryClient.send(uploadImage, HttpResponse.BodyHandlers.ofString());
        }catch (IOException e){
            log(e,"IO Exception while uploading image to cloudinary");
            publishComment(objectID,message +"\n(Image upload failed)");
            return;
        }

        //Parse Cloudinary response body JSON
        Gson gson = new Gson();
        CloudinaryResponse cloudinaryResponseWrapper = gson.fromJson(cloudinaryResponse.body(), CloudinaryResponse.class);

        //Cloudinary API response included an error.
        if(cloudinaryResponseWrapper.getError()!=null){
            log(cloudinaryResponseWrapper.getError(),"Cloudinary Error: Failed to upload image");
            publishComment(objectID,message +"\n(Image upload failed)");
            return;
        }

        if(cloudinaryResponseWrapper.getUrl()==null){
            log("Cloudinary response didn't include an image URL","");
            publishComment(objectID,message +"\n(Image upload failed)");
            return;
        }

        String imageURL = cloudinaryResponseWrapper.getUrl();
        System.out.println("imageURL: "+imageURL);

        //Cloudinary api fail
        if(imageURL==null){
            publishComment(objectID,message +"\n(Image upload failed)");
            return;
        }

        if(message.equals("")){message = "[^_^]";}
        try {
            message = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            log(e,"UnsupportedEncodingException while trying to encode response message to UTF8");
            return;
        }

        // Create a client for facebook image comment upload
        var facebookClient = HttpClient.newHttpClient();
        String facebookBody = String.format("message=%s&attachment_url=%s&access_token=%s",message,imageURL,user_access_token);
        var publishComment = HttpRequest.newBuilder().uri(
                URI.create(String.format("https://graph.facebook.com/v9.0/%s/comments/",objectID)))
                .POST(HttpRequest.BodyPublishers.ofString(facebookBody))
                .build();

        System.out.println("replying to: "+objectID);

        // use the client to send the request
        HttpResponse<String> facebookResponse;
        try {
            facebookResponse = facebookClient.send(publishComment, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            log(e,"IOException while trying to publish comment");
            return;
        }

        System.out.println(facebookResponse);

    }

    //requires loadConfig() and loadPostData() to have been run.
    public static String publishPost(String message, boolean overrideQueuedPosts) throws InterruptedException {
        System.out.println("\n===============Publishing new post==================\n");

        //check for queued posts first
        //queued posts override any message sent if the boolean parameter is set.
        if(queuedPosts.length>0 & !overrideQueuedPosts){

            //post first string in queuedPosts
            message = queuedPosts[0];

            //Update queuedPosts: remove first string from queuedPosts
            queuedPosts = Arrays.copyOfRange(queuedPosts,1,queuedPosts.length);
        }

        // create a client
        var client = HttpClient.newHttpClient();

        // Publish Post
        var publishPost = HttpRequest.newBuilder().uri(
                URI.create(String.format("https://graph.facebook.com/v9.0/%s/feed",page_ID)))
                .POST(HttpRequest.BodyPublishers.ofString(
                        String.format("message=%s&access_token=%s",message, user_access_token))
                ).build();


        //Use the client to send the request
        HttpResponse<String> response;
        try {
            response = client.send(publishPost, HttpResponse.BodyHandlers.ofString());
        }catch (IOException e){
            log(e,"IOException while trying to upload new post");
            return null;
        }

        //Handle the response
        Gson gson = new Gson();
        PostResponse postResponse = gson.fromJson(response.body(), PostResponse.class);

        //Check for API error response
        if(postResponse.getError()!=null){
            log(postResponse.getError(),"FB Graph API - Error uploading new post: \""+message+"\"");
            return null;
        }
        //Ensure API response contains an ID
        if(postResponse.getId()==null){
            log(postResponse.getError(),"FB Graph API - Error getting ID from new post: \""+message+"\"");
            return null;
        }

        //Post a comment update to previous post to indicate that the post is no longer
        //tracked by the bot.
        publishComment(postID,"This post is no longer active! Check the page for the most recent post [^_^]");


        //Grow past posts array by 1
        String[] newPastPosts = new String[pastPosts.length+1];
        for(int i=0;i<pastPosts.length;i++){
            newPastPosts[i] = pastPosts[i];
        }

        //Update pastPosts array
        newPastPosts[newPastPosts.length-1] = postText;
        pastPosts = newPastPosts;

        //Update postID and postText
        postID = postResponse.getId();
        postText = message;

        try{
            writePostData();
        }catch (IOException e){
            log(e,"IOException while trying to save new post data");
            return null;
        }
        // the response from facebook graph API:
        return postResponse.getId();
    }

    //Record Errors that caused program failure.
    //Generally these will be internet connection errors, and occasionally API errors.

    //Log any string
    private static void log(String message, String errorMessage) {
        String dateTime = dateTimeString();
        updateLog(message,errorMessage,dateTime);
    }

    //Log FB Graph API error
    private static void log(APIError e, String message){
        String errorMessage = "";

        //Safely add all the parameters to the error message
        errorMessage += e.getMessage()==null ? "" : "Message: "+e.getMessage()+"\n";
        errorMessage += e.getType()==null ? "" : "Type: "+e.getType()+"\n";
        errorMessage += e.getCode()==null ? "" : "Code: "+e.getCode()+"\n";
        errorMessage += e.getError_subcode()==null ? "" : "Sub-Code"+e.getError_subcode()+"\n";
        errorMessage += e.getFbtrace_id()==null ? "" : "FBTraceID"+e.getFbtrace_id()+"\n";

        try{
            loadPostData();
            errorMessage += String.format("Post: %s\nPost ID: %s\n",postText,postID);
        }catch (IOException ignored){
            //Not exactly critical that we get post text and ID
        }

        String dateTime = dateTimeString();
        updateLog(message,errorMessage,dateTime);
    }

    //Log generic Error/Exception
    private static void log(Throwable e, String message){

        String dateTime = dateTimeString();
        updateLog(message,e.getMessage(),dateTime);
    }

    //Log comment which failed the profanity test
    private static void log(Comment c, String message){
        String details = String.format("Message: %s\nUser: %s\nUser ID: %s\nPost: %s\nPost ID: %s",
                c.getMessage(),
                c.getFrom().getName(),
                c.getFrom().getId(),
                postID,
                postText
        );

        String dateTime = dateTimeString();
        updateLog(message,details,dateTime);
    }

    //printout of current date and time
    private static String dateTimeString(){
        long currentTime = System.currentTimeMillis();
        Date date = new Date(currentTime);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd MMM yyyy");
        String dateString = dateFormat.format(date);

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        String timeString = timeFormat.format(date);

        return String.format("%s at %s",dateString,timeString);
    }

    //Add a new log to logs JSON file.
    //Reads logs.json, adds an entry, then writes back to logs.json
    private static void updateLog(String message, String errorMessage, String dateTime){
        try {
            //Load logs.json
            Path inFile = Path.of("src/main/logs.json");
            String logJSON = Files.readString(inFile);

            //parse JSON to Logs object
            Gson readGson = new Gson();
            Logs logs = readGson.fromJson(logJSON, Logs.class);

            //Retrieve array of Log objects from Logs
            Log[] logArray = logs.getLogs();
            if(logArray==null){
                System.out.printf("Log failed: %s\n%s\n%s\n",message,errorMessage,dateTime);
                return;
            }

            //Grow array by 1
            Log[] newLogArray = new Log[logArray.length+1];
            for(int i=0;i<logArray.length;i++){
                newLogArray[i] = logArray[i];
            }
            //Add new Log to the end of the new array
            newLogArray[newLogArray.length-1] = new Log(message,errorMessage,dateTime);

            //Initialise new Logs object to write back to file as JSON
            Logs newLogs = new Logs(newLogArray);

            Gson writeGson = new GsonBuilder().setPrettyPrinting().create();
            String logsJSON = writeGson.toJson(newLogs);

            Path outFile = Path.of("src/main/logs.json");
            Files.writeString(outFile,logsJSON);

            System.out.printf("\n============= New Log =============\nMessage: %s\nError: %s\n%s\n===================================\n",message,errorMessage,dateTime);
        }catch (IOException e){
            System.out.println(e);
        }
    }

    public static void main(String[] args) {

        //Load config data
        try {
            loadConfig();
            loadPostData();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        //Default mode. Options: Comment, Post
        String mode = "Comment";
        //Default post message
        String message = "New post: "+dateTimeString();

        //CLI args overwrite default mode and message
        if(args.length>0){
            mode = args[0];
        }
        if(args.length>1){
            message = args[1];
        }

        try{
            if(mode.equals("Comment")){
                String commentData = requestComments(postID);
                if(commentData==null){return;}
                executeComments(commentData);
            }

            if(mode.equals("Post")){
                String res = publishPost(message,false);
                if(res!=null){System.out.println(res);}
            }
        } catch (InterruptedException e) {
            log(e,"InterruptedException in CodeBot.main, mode: "+mode);
        }

    }

}
