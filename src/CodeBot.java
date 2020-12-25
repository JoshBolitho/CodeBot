import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import JSONClasses.Comment;
import JSONClasses.CommentData;
import JSONClasses.Config;
import JSONClasses.Post;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public class CodeBot {
    static String user_access_token = "";
    static String page_access_token = "";
    static String page_ID = "";

    //The current post
    static String postID = "";
    static ArrayList<String> repliedComments = new ArrayList<>();

    public static void loadConfig() throws IOException {

        Path fileName = Path.of("src/config.json");
        String configJSON = Files.readString(fileName);

        Gson gson = new Gson();
        Config config = gson.fromJson(configJSON, Config.class);
        user_access_token = config.getUser_access_token();
        page_access_token = config.getPage_access_token();
        page_ID = config.getPage_ID();

    }

    public static void loadPostData() throws IOException {

        Path fileName = Path.of("src/post.json");
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

        System.out.println("writing post data:");
        System.out.println(postJSON);

        Path fileName = Path.of("src/post.json");
        Files.writeString(fileName,postJSON);
    }



    public static void executeComments(String APIResponse) throws IOException, InterruptedException {

        Gson gson = new Gson();
        CommentData messageWrapper = gson.fromJson(APIResponse, CommentData.class);
        Comment[] comments = messageWrapper.getData();

        //whether any changes need to be written to post.json
        boolean updatePostJSON = false;

        for(Comment c : comments){

            //if this comment has been replied to already, ignore it.
            if(repliedComments.contains(c.getId())){continue;}

            ScriptExecutor scriptExecutor = new ScriptExecutor(c.getMessage());
            scriptExecutor.parseScript();
            scriptExecutor.displayProgram();
            System.out.println("\n=====================Execute========================");
            scriptExecutor.executeProgram();

            String result = scriptExecutor.getConsoleOutput();
            System.out.println(result);

            System.out.println("replying to comment: "+c.getId());
            replyComment(c.getId(), result);

            repliedComments.add(c.getId());
            updatePostJSON = true;
        }

        if(updatePostJSON){
            //write updated list of replied comments to post.json
            writePostData();
        }

    }

    // Request the comments on a Facebook Graph API Element by its object ID
    public static String requestComments(String objectID) throws IOException, InterruptedException {
        // Create a client
        var client = HttpClient.newHttpClient();

        // Get comments
        var getComments = HttpRequest.newBuilder(
                URI.create(String.format("https://graph.facebook.com/v9.0/%s/comments?access_token=%s",objectID, user_access_token))
        ).build();

        // use the client to send the request
        HttpResponse<String> response = client.send(getComments, HttpResponse.BodyHandlers.ofString());

        // the response from facebook graph API:
        return response.body();
    }

    public static void replyComment(String objectID, String message) throws IOException, InterruptedException {
        message = "=====Result=====\n"+message;

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

    public static String publishPost(String message) throws IOException, InterruptedException {
        // create a client
        var client = HttpClient.newHttpClient();

        // Publish Post
        var publishPost = HttpRequest.newBuilder().uri(
                URI.create(String.format("https://graph.facebook.com/v9.0/%s/feed",page_ID)))
                .POST(HttpRequest.BodyPublishers.ofString(
                        String.format("message=%s&access_token=%s",message, user_access_token))
                ).build();

        // use the client to send the request
        HttpResponse<String> response = client.send(publishPost, HttpResponse.BodyHandlers.ofString());

        // the response from facebook graph API:
        return response.body();
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
        loadConfig();
        loadPostData();
//        String res = publishPost("NEW NEW NEW NEW POST");
//        System.out.println(res);
        String commentData = requestComments(String.format("%s_%s",page_ID,postID));
        executeComments(commentData);
    }
}
