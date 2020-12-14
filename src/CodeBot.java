import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import ResponseClasses.Comment;
import ResponseClasses.CommentData;
import com.google.gson.Gson;

import java.nio.file.Files;
import java.nio.file.Path;

public class CodeBot {
    static String user_access_token = "";
    static String page_access_token = "";
    static String page_ID = "";

    //The post we're using for testing
    static String postID = "111194267489510";

    public static void loadConfig() throws IOException {

        Path fileName = Path.of("src/config.json");
        String configJSON = Files.readString(fileName);

        Gson gson = new Gson();
        Config config = gson.fromJson(configJSON, Config.class);
        user_access_token = config.getUser_access_token();
        page_access_token = config.getPage_access_token();
        page_ID = config.getPage_ID();

    }

    public static void executeComments(String APIResponse) throws IOException, InterruptedException {

        Gson gson = new Gson();
        CommentData messageWrapper = gson.fromJson(APIResponse, CommentData.class);
        Comment[] comments = messageWrapper.getData();

        for(Comment c : comments){

            //API request for replies to comment c
            String commentReplies = requestComments(c.getId());

            Gson repliesGson = new Gson();
            CommentData repliesMessageWrapper = repliesGson.fromJson(commentReplies, CommentData.class);
            Comment[] replies = repliesMessageWrapper.getData();

            //Test whether comment c has already received a reply from CodeBot.
            boolean commentAlreadyRepliedTo = false;
            for(Comment r : replies){
                if(r.getFrom().getId().equals(page_ID)){
                    commentAlreadyRepliedTo = true;
                    break;
                }
            }
            //If so, the comment is ignored.
            if(commentAlreadyRepliedTo){continue;}

            ScriptExecutor scriptExecutor = new ScriptExecutor(c.getMessage());
            scriptExecutor.parseScript();
            scriptExecutor.executeProgram();

            String result = scriptExecutor.getConsoleOutput();
            System.out.println(result);

            System.out.println("replying to comment: "+c.getId());
            replyComment(c.getId(), result);
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
//        String res = publishPost("New Test Post!!!");
//        System.out.println(res);
        String commentData = requestComments(String.format("%s_%s",page_ID,postID));
        executeComments(commentData);
    }
}
