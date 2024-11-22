package com.example.area_backend.Services.Threads;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.area_backend.ParserJson;
import com.example.area_backend.Tuple;
import com.example.area_backend.TableDb.AccountApi.AccountApiService;
import com.example.area_backend.TableDb.Tokens.TokenController;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


@Component
@RestController
@RequestMapping("/threads")
@Validated
public class Threads {

    @Value("${spring.services.threads.applicationId}")
	private String appId;

    @Value("${spring.services.threads.secretKey}")
	private String secretKey;

    @Autowired
    private TokenController tockensController;
    @Autowired
    private AccountApiService accountApiService;

    private String exchangeToken(String access_token)
    {
        String tokenUrl = "https://graph.threads.net/access_token";
        try {
            String data = "?grant_type=th_exchange_token" +
                          "&client_secret=" + secretKey +
                          "&access_token=" + access_token;
            URL url = new URL(tokenUrl + data);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader responseMessage = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = responseMessage.readLine()) != null) {
                    response.append(inputLine);
                }
                responseMessage.close();
                String infosResponse = response.toString();
                JSONObject reponseInfosJson = new ParserJson().parseToJson(infosResponse);
                return (reponseInfosJson.getString("access_token"));
            }
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
        return null;
    }

    @GetMapping("/request")
    public ResponseEntity<?> getCodeForAccessToken(
        @RequestParam("code") String code,
        @RequestParam("state") String userID
    )
    {
        String redirectUri = "https://promptly-normal-maggot.ngrok-free.app/threads/request";
        String tokenUrl = "https://graph.threads.net/oauth/access_token";
        try {
            URL url = new URL(tokenUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            String data = "client_id=" + appId +
                          "&client_secret=" + secretKey +
                          "&grant_type=authorization_code" +
                          "&redirect_uri=" + redirectUri +
                          "&code=" + code;
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = data.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            int responseCode = connection.getResponseCode();
            System.out.println(responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader responseMessage = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = responseMessage.readLine()) != null) {
                    response.append(inputLine);
                }
                responseMessage.close();
                String infosResponse = response.toString();
                JSONObject infosThreads = new ParserJson().parseToJson(infosResponse);
                String newToken = exchangeToken(infosThreads.getString("access_token"));
                Tuple<Long, String> error;
                if (!newToken.equals(null)){
                    error = accountApiService.loginWithThreads(newToken,
                    Long.toString(infosThreads.getLong("user_id")), userID);
                } else {
                    error = accountApiService.loginWithThreads(infosThreads.getString("access_token"),
                    Long.toString(infosThreads.getLong("user_id")), userID);
                }
                if (error.getLeft().equals((long) -1)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : ID = " + userID + " is not integer type.");
                }
                if (error.getLeft().equals((long) -2)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Any user with ID = " + userID + " not found.");
                }
                if (error.getLeft().equals((long) -3)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : One or more parameters are invalids.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("You can close this window.");
    }

}
