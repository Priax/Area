package com.example.area_backend.Services.Threads;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;

import com.example.area_backend.HandleArgument;
import com.example.area_backend.ParserJson;

public class ThreadsReactions {

    private final HandleArgument handleArgument;

    public ThreadsReactions()
    {
        handleArgument = new HandleArgument();
    }

    private Optional<Map<String, String>> publishPost(JSONObject credential, JSONObject jsonValues, Map<String, String> arguments, String message, String links)
    {
        int responseCode = 0;
        String publishUrl = "https://graph.threads.net/v1.0/" + credential.getString("threads_id") + "/threads_publish";
        try {
            String data = "?creation_id=" + jsonValues.getString("id") +
                          "&access_token=" + credential.getString("threads_access_token");
            URL url = new URL(publishUrl + data);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println(responseCode);
                arguments = this.handleArgument.fillOutWithArg(arguments, new String[]{
                    "$ThreadPostMessage",
                    "$ThreadPostLinks"
                }, new String[]{message, links});
                return (Optional.of(arguments));
            }
        } catch (Exception e) {
            //e.printStackTrace();
            return (Optional.ofNullable(null));
        }
        System.out.println(responseCode);
        return (Optional.ofNullable(null));
    }

    public Optional<Map<String, String>> createPost(JSONObject credential, JSONObject jsonValues, Map<String, String> arguments)
    {
        if (!credential.has("threads_id") || !credential.has("threads_access_token")){
            return (Optional.ofNullable(null));
        }
        String postUrl = "https://graph.threads.net/v1.0/" + credential.getString("threads_id") + "/threads";
        int responseCode = 0;
        String message = jsonValues.getString("Message");
        String links = jsonValues.getString("Links");
        String media = jsonValues.getString("Media");
        HttpURLConnection connection = null;
        String[] newData = this.handleArgument.fillWithArgument(arguments, new String[]{message, links});
        message = newData[0];
        links = newData[1];
        if (!media.equals("TEXT") && !media.equals("IMAGE")
            && !media.equals("VIDEO")) {
            System.out.println("This media doesn't exist.");
            return (Optional.ofNullable(null));
        }
        if (media.equals("VIDEO") || media.equals("IMAGE")){
            if (links.equals("") || links.equals(null)){
                System.out.println("Parameter Links is required when Media equal IMAGE or VIDEO");
                return (Optional.ofNullable(null));
            }
        }
        if (media.equals("TEXT") && message.equals("")
            || media.equals("TEXT") && message.equals(null)) {
            System.out.println("Parameter Message is required when Media equal TEXT");
            return (Optional.ofNullable(null));
        }
        try {
            String data = "?media_type=" + media +
                          "&text=" + message +
                          "&access_token=" + credential.getString("threads_access_token");
            if (media.equals("IMAGE")) {
                data = data + "&image_url=" + links;
            }
            if (media.equals("VIDEO")) {
                data = data + "&video_url=" + links;
            }
            data = data.replaceAll(" ", "%20");
            URL url = new URL(postUrl + data);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            responseCode = connection.getResponseCode();
            BufferedReader responseMessage = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = responseMessage.readLine()) != null) {
                response.append(inputLine);
            }
            responseMessage.close();
            String infosResponse = response.toString();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                JSONObject infosThreads = new ParserJson().parseToJson(infosResponse);
                return (publishPost(credential, infosThreads, arguments, message, links));
            }
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("You are limited at 250 posts per 24 hours. Text posts are limited to 500 characters. Image File Size: 8 MB maximum. PNG or JPEG only. Video are limited to 5min and size to 1GB.");
            return (Optional.ofNullable(null));
        }
        System.out.println(responseCode);
        return (Optional.ofNullable(null));
    }

}