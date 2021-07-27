package main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.JSONClasses.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
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
import java.util.*;

public class CodeBot {

    //Config
    static String user_access_token = "";
    static String page_access_token = "";
    static String page_ID = "";
    static String cloudinary_upload_preset = "";
    static String[] profanity_list = new String[]{};

    //The current post
    static String currentPostID = "";
    static String currentPostText = "";

    static String[] queuedPosts = new String[]{};
    static String[] pastPosts = new String[]{};

    static String currentSubmissionsID = "";
    static String[] facebookSubmissions = new String[]{};

    static String[] curatedChallenges = new String[]{};

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

        currentPostID = post.getCurrentPostID();
        currentPostText = post.getCurrentPostText();

        queuedPosts = post.getQueuedPosts();
        pastPosts = post.getPastPosts();

        currentSubmissionsID = post.getCurrentSubmissionsID();
        facebookSubmissions = post.getFacebookSubmissions();

        curatedChallenges = post.getCuratedChallenges();
    }

    public static void writePostData() throws IOException {

        Post post = new Post(
                currentPostID,
                currentPostText,
                queuedPosts,
                pastPosts,
                currentSubmissionsID,
                facebookSubmissions,
                curatedChallenges
                );

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String postJSON = gson.toJson(post);

        Path fileName = Path.of("src/main/post.json");
        Files.writeString(fileName,postJSON);
    }

    //Return string contains the most reacted comment
    public static void executeComments(Comment[] comments) throws InterruptedException {

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
                    publishComment(c.getId(), "[^_^]");
                    continue;
                }

                //Load input image from comment attachment
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

    public static void loadSubmissionComments(Comment[] comments) throws InterruptedException {

        ArrayList<String> submissionsList = new ArrayList<>();

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

                //Test comment and result for profanity
                boolean containsProfanity = false;
                for (String s : profanity_list) {
                    if (c.getMessage().contains(s)) {
                        System.out.println("Profanity detected");
                        publishComment(c.getId(), "Profanity detected. Delete your comment.");

                        log(c, "Comment failed the profanity filter.");
                        containsProfanity = true;
                    }
                }
                if (containsProfanity) {
                    continue;
                }

                //Add comment to temporary submissions array
                submissionsList.add(String.format("%s\n\nThanks to %s for this idea! [^_^]",c.getMessage(),c.getFrom().getName()));


                String[] thankYous = new String[]{
                        "Thanks so much for your submission! [^_^]",
                        "Cheers! Awesome idea :)",
                        "Good on ya! Thanks for the submission :D",
                        "Great work, keep it up [^_^]",
                        "Legend! [^_^]",
                        "Much appreciated!!",
                        "Awesome, thanks :)",
                        "thank u <3",
                        "thanks xx",
                        "Yay! Thanks <333",
                        "Love it! [^_^]",
                        "CodeBot appreciates your generosity!",
                        "How very kind of you! Thanks!",
                        "Thanks heaps <3",
                        "Thankies :)",
                        "Thanks for supporting my page :)",
                        "Thanks, very epic [^_^]"
                };
                String message = randomString(thankYous);

                //Reply to comment with nice message
                publishComment(c.getId(), message);

            }catch (InterruptedException e){
                throw e;
            }catch (Throwable e){
                log(e,"Unanticipated error type thrown during executeComments");
            }
        }

        //add facebookSubmissions and submissionsList together in new array, submissionArray
        String[] submissionArray = new String[facebookSubmissions.length+submissionsList.size()];
        for(int i=0; i<facebookSubmissions.length;i++){
            submissionArray[i] = facebookSubmissions[i];
        }
        for(int i=0; i<submissionsList.size();i++){
            submissionArray[i+facebookSubmissions.length] = submissionsList.get(i);
        }
        //replace facebookSubmissions with the newly grown array
        facebookSubmissions = submissionArray;

        try{
            writePostData();
        }catch (IOException e){
            log(e, "IOException while trying to save new facebook submissions");
        }
    }

    // Request the comments on a Facebook Graph API Element by its object ID
    public static Comment[] requestComments(String objectID) throws InterruptedException {
        // Create a client
        var client = HttpClient.newHttpClient();

        // Get comments
        var getComments = HttpRequest.newBuilder(
                URI.create(String.format("https://graph.facebook.com/v9.0/%s/comments?access_token=%s&fields=message,id,attachment,from,comments,reactions.summary(total_count)",objectID, user_access_token))
        ).build();

        // use the client to send the request
        HttpResponse<String> response;
        String responseBody;
        try {
            //Get Facebook Graph response:
            response = client.send(getComments, HttpResponse.BodyHandlers.ofString());
            responseBody = response.body();
        } catch (IOException e){
            log(e,"IOException while trying to get FB comments");
            return new Comment[]{};
        }

        //Parse JSON response
        Gson gson = new Gson();
        CommentData commentData = gson.fromJson(responseBody, CommentData.class);

        if(commentData.getError()!=null){
            log(commentData.getError(),"FB Graph API - Error retrieving comments");
            return new Comment[]{};
        }

        Comment[] comments = commentData.getData();

        if(comments==null){
            log("FB Graph API - Error retrieving comments","Comments array is null");
            return new Comment[]{};
        }

        //recursively page comments
        if(commentData.getPaging()!=null && commentData.getPaging().getNext()!=null){
            Comment[] nextComments = pageComments(commentData.getPaging().getNext());
            Comment[] concat = new Comment[comments.length+nextComments.length];
            System.arraycopy(comments,0,concat,0,comments.length);
            System.arraycopy(nextComments,0,concat,comments.length,nextComments.length);


            return concat;
        }

        return comments;
    }

    //Recursive version of requestComments that handles Facebook Graph API's paging
    public static Comment[] pageComments(String next) throws InterruptedException{

        // Create a client
        var client = HttpClient.newHttpClient();

        // Get next page of comments
        var nextPage = HttpRequest.newBuilder(
                URI.create(next)
        ).build();

        // Use the client to send the request
        HttpResponse<String> response;
        String responseBody;
        try {
            // Get Facebook Graph response:
            response = client.send(nextPage, HttpResponse.BodyHandlers.ofString());
            responseBody = response.body();
        } catch (IOException e){
            log(e,"IOException while trying to get FB comments");
            return new Comment[]{};
        }

        //Parse JSON response
        Gson gson = new Gson();
        CommentData commentData = gson.fromJson(responseBody, CommentData.class);

        if(commentData.getError()!=null){
            log(commentData.getError(),"FB Graph API - Error retrieving comments");
            return new Comment[]{};
        }

        Comment[] comments = commentData.getData();

        if(comments==null){
            log("FB Graph API - Error retrieving comments","Comments array is null");
            return new Comment[]{};
        }

        //recursively page comments
        if(commentData.getPaging()!=null && commentData.getPaging().getNext()!=null){
            Comment[] nextComments = pageComments(commentData.getPaging().getNext());
            Comment[] concat = new Comment[comments.length+nextComments.length];
            System.arraycopy(comments,0,concat,0,comments.length);
            System.arraycopy(nextComments,0,concat,comments.length,nextComments.length);

            return concat;
        }

        return comments;
    }

    public static void publishComment(String objectID, String message) throws InterruptedException {
        System.out.println("replying to: "+objectID);

        if(message == null || message.equals("")){message = "[^_^]";}

        String newMessage;
        try {
            newMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
        }catch (UnsupportedEncodingException e){
            log(e,"UnsupportedEncodingException while trying to encode response message to UTF8");
            return;
        }

        // Create a client
        var client = HttpClient.newHttpClient();

        // Publish Comment
        var publishComment = HttpRequest.newBuilder(
                URI.create(String.format("https://graph.facebook.com/v9.0/%s/comments/",objectID)))
                .POST(HttpRequest.BodyPublishers.ofString(
                        String.format("message=%s&access_token=%s",newMessage,user_access_token))
        ).build();

        HttpResponse<String> facebookResponse;
        // use the client to send the request
        try {
            facebookResponse = client.send(publishComment, HttpResponse.BodyHandlers.ofString());

        } catch (IOException e){
            log("Connection error while trying to send a comment",e.getMessage());
            return;
        }
        System.out.println(facebookResponse);

        //Parse JSON response
        Gson gson = new Gson();
        PostResponse postResponse = gson.fromJson(facebookResponse.body(), PostResponse.class);

        if(postResponse.getError()!=null){
            log(postResponse.getError(),"FB Graph API - Error publishing comment");

            //Try upload failure comment
            publishComment(objectID, "Original comment response failed to upload [x_x]");
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

        String imageURL = cloudinaryResponseWrapper.getUrl();
        System.out.println("imageURL: "+imageURL);

        //Cloudinary api fail
        if(imageURL==null){
            log("Cloudinary response didn't include an image URL","");
            publishComment(objectID,message +"\n(Image upload failed)");
            return;
        }

        if(message == null || message.equals("")){message = "[^_^]";}

        String newMessage;
        try {
            newMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            log(e,"UnsupportedEncodingException while trying to encode response message to UTF8");
            return;
        }

        // Create a client for facebook image comment upload
        var facebookClient = HttpClient.newHttpClient();
        String facebookBody = String.format("message=%s&attachment_url=%s&access_token=%s",newMessage,imageURL,user_access_token);
        var publishComment = HttpRequest.newBuilder(
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

        //Parse JSON response
        PostResponse postResponse = gson.fromJson(facebookResponse.body(), PostResponse.class);

        if(postResponse.getError()!=null){
            log(postResponse.getError(),"FB Graph API - Error publishing comment");

            //Try upload failure comment
            publishCommentImage(objectID,"Original comment response failed to upload [x_x]",bufferedImage,uploadPreset);
        }
    }

    //requires loadConfig() and loadPostData() to have been run.
    public static String publishPost(String message, boolean overrideQueuedPosts, boolean submissionsPost) throws InterruptedException {
        System.out.println("\n===============Publishing new post==================\n");

        //check for queued posts first
        //queued posts override any message sent if the boolean parameter is set.
        if(queuedPosts.length>0 & !overrideQueuedPosts){

            //post first string in queuedPosts
            message = queuedPosts[0];

            //Update queuedPosts: remove first string from queuedPosts
            queuedPosts = Arrays.copyOfRange(queuedPosts,1,queuedPosts.length);
        }
        System.out.println("Message: "+message);

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
        if(submissionsPost) {
            if(currentSubmissionsID!=null && !currentSubmissionsID.equals("")) {
                publishComment(currentSubmissionsID, "This post is no longer active! Check the page for the most recent post [^_^]");
            }
        }else{
            if(currentPostID!=null && !currentPostID.equals("")) {
                publishComment(currentPostID, "This post is no longer active! Check the page for the most recent post [^_^]");
            }
        }

        if(!currentPostText.equals("")) {
            //Grow past posts array by 1
            String[] newPastPosts = new String[pastPosts.length + 1];
            System.arraycopy(pastPosts, 0, newPastPosts, 0, pastPosts.length);

            //Update pastPosts array
            newPastPosts[newPastPosts.length - 1] = currentPostText;
            pastPosts = newPastPosts;
        }

        if (submissionsPost) {
            //Update submissions ID
            currentSubmissionsID = postResponse.getId();
        } else {
            //Update post ID and post text
            currentPostID = postResponse.getId();
            currentPostText = message;
        }

        try{
            writePostData();
        }catch (IOException e){
            if(submissionsPost){
                log(e,String.format("IOException while trying to save new submissions post data: %s, %s",message,currentSubmissionsID));
            }else{
                log(e,String.format("IOException while trying to save new post data: %s, %s",message,currentPostID));
            }
            return null;
        }
        // the response from facebook graph API:
        return postResponse.getId();
    }

    public static String randomString(String[] array){
        if(array!=null && array.length>0){
            return array[(int)(Math.random()*array.length)];
        }
        return "[^_^]";
    }

    public static String randomString(ArrayList<String> array){
        if(array!=null && array.size()>0){
            return array.get( (int)( Math.random()*array.size() ) );
        }
        return "[^_^]";
    }

    public static int randomDigit(){
        return (int)(Math.random()*10);
    }

    public static ArrayList<String> loadWords(){
        ArrayList<String> words = new ArrayList<>();
        try {
            File wordFile = new File("src/main/words.txt");
            Scanner scanner = new Scanner(wordFile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                words.add(line);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            log(e,"FileNotFoundException while loading words.txt for random post generator");
            ArrayList<String> temp = new ArrayList<>();
            temp.add("[^_^]");
            return temp;
        }
        return words;
    }

    public static String generatePost(){

        //Random chance to do cute fun challenge
        if(Math.random()<0.05){

            ArrayList<String> words = loadWords();

            String[] consonants = new String[]{
                    "B","C","D","F","G","H","J","K","L","M","N","P","Q","R","S","T","V","W","X","Y","Z"
            };

            String[] vowels = new String[]{
                    "A","E","I","O","U"
            };
            String[] prompts = new String[]{
                    String.format(
                            "Countdown numbers challenge! Use +, -, *, /, and the following numbers up to once each: %s, %s, %s, %s%s, %s%s, %s%s"
                            +"\n Your target is: %s%s%s. Write your answer in the comments, no cheating!",
                            randomDigit(),randomDigit(),randomDigit(),randomDigit(),randomDigit(),randomDigit(),randomDigit(),randomDigit(),randomDigit(),
                            randomDigit(),randomDigit(),randomDigit()
                    ),
                    String.format(
                            "Countdown word challenge! Use the following letters up to once each to create the longest word possible: %s, %s, %s, %s, %s, %s, %s, %s"
                            +"\nWrite your answer in the comments, no cheating!",
                            randomString(consonants), randomString(vowels), randomString(consonants), randomString(consonants),
                            randomString(vowels), randomString(vowels),randomString(consonants), randomString(consonants)
                    ),
                    String.format("Give your %s @ a nice compliment!",
                            randomString(new String[]{"1st","2nd","3rd","4th","5th"})
                    ),
                    String.format("Art challenge! Feel free to use the following words for inspiration: %s, %s, %s",
                            randomString(words), randomString(words), randomString(words)
                    ),
                    String.format("Writing challenge! write a %s, using the following words for inspiration: %s, %s, %s",
                            randomString(new String[]{"haiku","limerick","short paragraph","recipe","review"}),
                            randomString(words), randomString(words), randomString(words)
                    )
            };

            return randomString(prompts)
                    + "\nCode execution is still running on this post if you just wanna mess around with some code instead! [^_^]";
        }


        //Pick a challenge generation mode
        String[] modes = new String[]{
                "facebook submission",
                "sentence generator",
                "curated from website"
        };
        String mode = modes[(int)(Math.random()* modes.length)];

        //Generate challenge string
        switch (mode) {
            case "facebook submission" -> {
                return "Today's challenge: "
                        + facebookSubmissions[(int) (Math.random() * facebookSubmissions.length)]
                        + "\n\nAlternatively, do whatever you feel like!";
            }
            case "sentence generator" -> {
                //load random words array
                ArrayList<String> words = loadWords();

                String[] functions = new String[]{
                        "Sine(x)",
                        "Tangent(x)",
                        "Cosine(x)",
                        "ln(x)",
                        "n^x",
                        "x^n",
                        "mx+c",
                        "arcTangent(x)",
                        "arcCosine(x)",
                        "arcSine(x)",
                        "floor(x)"
                };
                String[] imageFilters = new String[]{
                        "using the canny edge detection filter",
                        "with a brightness threshold filter",
                        "with a blur filter",
                        "with a filter you created!",
                        "by using a paint-bucket fill effect at some random pixel locations",
                        "with a black an white filter",
                        "to increase or decrease the contrast between bright and dark pixels",
                        "by warping it in a cool way",
                        "using a convolution filter of some kind!",
                        "by sorting all the pixels in order of brightness",
                        "by changing it to a 16-colour palette"
                };
                String[] shapes = new String[]{
                        "a triangle defined by random points",
                        "a circle defined by random parameters",
                        "a square defined by random parameters",
                        "a spiral",
                        "a smiley face!",
                        "a polygon defined by random points",
                        "a hexagon"
                };
                String[] renders = new String[]{
                        "a Mandelbrot set fractal",
                        "a Sierpiński triangle fractal",
                        "an Apollonian gasket fractal",
                        "a Cantor set fractal",
                        "a rectangular cuboid from random x,y,z coordinates",
                        "a Koch snowflake fractal",
                        "a pretty looking gradient",
                        "an interesting noise function",
                        "a simulation of Conway's game of life",
                        "a simulation of Langton's ant or come up with rules for a new \"ant\" simulation"
                };
                String[] sortingAlgorithms = new String[]{
                        "using the quicksort algorithm",
                        "using the mergesort algorithm",
                        "using the bubble sort algorithm",
                        "using the bogosort algorithm",
                        "using the insertion sort algorithm",
                        "using the selection sort algorithm"
                };

                String[] prompts = new String[]{
                    String.format(
                            "Code something random using these words as inspiration! \"%s,\" \"%s\"",
                            randomString(words),
                            randomString(words)
                    ),
                    String.format(
                            "Write a script that can graph a mathematical function. Test it out with variations of the function %s",
                            randomString(functions)
                    ),
                    String.format(
                            "Write a script that filters an image %s ",
                            randomString(imageFilters)
                    ),
                    //a shape
                    String.format(
                            "Write a script that draws %s",
                            randomString(shapes)
                    ),
                    String.format(
                            "Render %s",
                            randomString(renders)
                    ),
                    String.format(
                            "Write a script that creates an abstract art piece incorporating the concepts of \"%s\" and \"%s\"",
                            randomString(words),
                            randomString(words)
                    ),
                    String.format(
                            "Write a random %s generator using your own idea or something relating to \"%s\"",
                            randomString(new String[]{"sentence","shopping list","newspaper headline","username","movie title","video game idea","short story","groundbreaking invention"}),
                            randomString(words)
                    ),
                    String.format(
                            "Design a calculator function for something related to \"%s\"",
                            randomString(words)
                    ),
                    String.format(
                            "Write a function that can manually convert a string to a%s value",
                            randomString(new String[]{"n integer", " float", " boolean","n array"})
                    ),
                    String.format(
                            "Write a script that sorts an array %s",
                            randomString(sortingAlgorithms)
                    ),
                    String.format(
                            "Write a script that draws some ASCII art to the console! You might like to use \"%s\" or \"%s\" for inspiration",
                            randomString(words),
                            randomString(words)
                    ),
                };

                String[] extra = new String[]{
                        "You must use recursion",
                        "You can only use the print() function once",
                        "Using the + symbol is not allowed",
                        "No casting functions are allowed",
                        "No array variables are allowed",
                        "Incorporate the concept of \""+randomString(words)+"\"",
                        "While loops are not allowed",
                        "If statements are not allowed!!",
                        "Code golf - Try to complete the challenge in as few characters as possible",
                        "You can't use the letter \"E\"",
                        "You can't use the letter \"A\""
                };

                return "Today's challenge: " + randomString(prompts)
                        + "\nExtra for experts: " + randomString(extra)
                        + "\n\nAlternatively, do whatever you feel like! [^_^]";
            }
            case "curated from website" -> {
                return "Today's challenge: "
                        + curatedChallenges[(int) (Math.random() * curatedChallenges.length)]
                        + "\n\nAlternatively, do whatever you feel like! [^_^]";
            }
            default -> {
                return "ERROR ERROR ERROR BEEP BOOP";
            }
        }
    }

    private static String getMostReacted(Comment[] comments) throws InterruptedException {

        //Congratulate the most reacted comment
        Comment mostReacted = null;
        Integer maxReacted = 0;

        for(Comment c : comments){

            try {
                //Test comment for profanity
                boolean containsProfanity = false;
                for (String s : profanity_list) {
                    if (c.getMessage().contains(s)) {
                        System.out.println("Profanity detected");
                        publishComment(c.getId(), "Profanity detected. Delete your comment.");

                        log(c, "Comment failed the profanity filter.");
                        containsProfanity = true;
                    }
                }
                if (containsProfanity) {
                    continue;
                }

                //Check whether current comment has more reacts than maxReacted
                if(c.getReactions()!=null && c.getReactions().getSummary()!=null){
                   Summary summary = c.getReactions().getSummary();
                   if(summary.getTotal_count()!=null && summary.getTotal_count()>maxReacted){
                       mostReacted = c;
                       maxReacted = summary.getTotal_count();
                   }
                }

            }catch (InterruptedException e){
                throw e;
            }catch (Throwable e){
                log(e,"Unanticipated error type thrown during getMostReacted");
            }
        }

        //ensure everything has been initialised etc and there is a comment which received the most reactions.
        if(mostReacted!=null){
            Summary summary = mostReacted.getReactions().getSummary();
            Integer total_count = summary.getTotal_count();
            return String.format("Congratulations to %s for having the top comment in the last post! (%s reaction%s)\n\n",mostReacted.getFrom().getName(),total_count,total_count==1?"":"s");
        }
        return null;
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
            errorMessage += String.format("Post: %s\nPost ID: %s\n",currentPostText,currentPostID);
        }catch (IOException ignored){
            //Not exactly critical that we get post text and ID for the error message
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
                currentPostID,
                currentPostText
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
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {

        //Load config data
        try {
            loadConfig();
            loadPostData();
        } catch (IOException e) {
            log(e,"IOException in CodeBot.main while trying to load config and post data from JSON");
            e.printStackTrace();
            return;
        }

        //CodeBot mode
        String mode;
        String message;

        //CLI args overwrite default mode and message
        if(args.length>0){ mode = args[0];
        }else{ mode = "Comment"; }

        if(args.length>1){ message = args[1];
        }else{ message = generatePost(); }

        try{
            switch (mode){

                //read and execute comments from the current post
                case "Comment" -> {
                    System.out.println("Comment mode");

                    if (currentPostID != null) {
                        Comment[] commentData = requestComments(currentPostID);
                        executeComments(commentData);
                    }else{
                        log("Comment mode failure","currentPostID was null.");
                    }
                }

                //Upload a new post
                case "Post" -> {
                    String topCommentMessage = null;

                    //First reply to remaining comments on current post,
                    //and get the top reacted comment.
                    System.out.println("Comment mode");

                    if (currentPostID != null) {
                        Comment[] commentData = requestComments(currentPostID);
                        executeComments(commentData);
                        topCommentMessage = getMostReacted(commentData);
                    }else{
                        log("Comment mode failure","currentPostID was null.");
                    }

                    //Upload post
                    System.out.println("Post mode");
                    String res = publishPost(
                            (topCommentMessage==null?"":topCommentMessage)+message,
                            false,
                            false);
                    if(res!=null){System.out.println(res);}
                }

                //Handling the post which takes programming challenge submissions
                case "CommentSubmissions" -> {
                    System.out.println("Comment Submissions mode");
                    if(!currentSubmissionsID.equals("")) {
                        Comment[] commentData = requestComments(currentSubmissionsID);
                        loadSubmissionComments(commentData);
                    }
                }

                //Create a new monitored post to ask for submissions
                case "PostSubmissions" -> {
                    System.out.println("Post Submissions mode");
                    String res = publishPost(
                            "CodeBot is now accepting challenge submissions! Please comment your coding challenge ideas below! Thanks [^_^]"
                            ,true
                            ,true
                    );
                    if(res!=null){System.out.println(res);}
                }

            }
        } catch (InterruptedException e) {
            log(e,"InterruptedException in CodeBot.main, mode: "+mode);
        }

    }

}
