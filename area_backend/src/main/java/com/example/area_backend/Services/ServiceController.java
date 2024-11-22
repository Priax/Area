package com.example.area_backend.Services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.example.area_backend.Constants;
import com.example.area_backend.MainFunctions;
import com.example.area_backend.ParserJson;
import com.example.area_backend.TableDb.AccountApi.AccountApiService;
import com.example.area_backend.TableDb.AccountApi.AccountApiTable;
import com.example.area_backend.TableDb.EnumServices;
import com.example.area_backend.TableDb.Tokens.TokenController;
import com.example.area_backend.Tuple;

@RestController
@RequestMapping("/services/info")
@Validated
public class ServiceController
{
    @Autowired
    private final TokenController tockensController;

    @Autowired
    private final AccountApiService apiService;

    private final ParserJson parserJson;

    private final Constants globalVariable = new Constants();

    private final MainFunctions functions = new MainFunctions();

    @Autowired
    public ServiceController(TokenController tockensController, AccountApiService apiService)
    {
        this.tockensController = tockensController;
        this.apiService = apiService;
        this.parserJson = new ParserJson();
    }

    public ServiceController()
    {
        this.tockensController = new TokenController();
        this.apiService = new AccountApiService();
        this.parserJson = new ParserJson();
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/")
    public ResponseEntity<?> getAllServicesByUserAndService(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestHeader(value="Service", required=false, defaultValue="null") String service
    )
    {
        if (this.tockensController.isUserAuthorized(access_token, userId) < 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        if (service == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Service missing in request");
        }
        EnumServices enumServices;
        try {
            enumServices = EnumServices.valueOf(service);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Service. Expeted: \"" + Arrays.toString(EnumServices.values()) + "\", but got: \"" + service + "\".");
        }
        Object return_value = this.getServicesConnectedByUser(userId, enumServices);
        if (return_value == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Service: " + enumServices + " for user: " + userId + " not found.");
        }
        return ResponseEntity.ok().body(return_value);
    }

    private Object getServicesConnectedByUser(String userId, EnumServices enumServices)
    {
        switch (enumServices) {
            case GMAIL -> {
                return (this.getServicesGmailByUser(userId));
            }
            case SPOTIFY -> {
                return (this.getServicesSpotifyByUser(userId));
            }
            case THREADS -> {
                return (this.getServicesThreadsByUser(userId));
            }
            default -> {
                return (null);
            }
        }
    }

    private List<Tuple<String, String>> getServicesGmailByUser(String userId)
    {
        List<AccountApiTable> allAccountApi = this.apiService.getAllAccountApi();
        AccountApiTable finalAccountApi = null;
        for (AccountApiTable accountApi : allAccountApi) {
            if (accountApi.getUsersTable().getId().equals(Long.valueOf(userId))) {
                finalAccountApi = accountApi;
                break;
            }
        }
        if (finalAccountApi == null) {
            return (null);
        }
        JSONObject credential = this.parserJson.parseToJson(finalAccountApi.getCredentials());
        if (credential == null) {
            System.err.println("Faild to parse JsonObject");
            return (null);
        }
        Object accountGmail;
        try {
            accountGmail = credential.get(this.globalVariable.ACCOUNT_API_CREDENTIAL_KEY_GMAIL);
        } catch (JSONException e) {
            System.err.println("Faild to parse JsonObject");
            return (null);
        }
        List<Tuple<String, String>> allAccountGmail = this.functions.getListOfTupleFromObject(accountGmail);
        if (allAccountGmail == null) {
            System.err.println("Faild to parse Object to List of tuple");
            return (null);
        }
        return (allAccountGmail);
    }

    private String getServicesSpotifyByUser(String userId)
    {
        List<AccountApiTable> allAccountApi = this.apiService.getAllAccountApi();
        AccountApiTable finalAccountApi = null;
        for (AccountApiTable accountApi : allAccountApi) {
            if (accountApi.getUsersTable().getId().equals(Long.valueOf(userId))) {
                finalAccountApi = accountApi;
                break;
            }
        }
        if (finalAccountApi == null) {
            return (null);
        }
        JSONObject credential = this.parserJson.parseToJson(finalAccountApi.getCredentials());
        if (credential == null) {
            System.err.println("Faild to parse JsonObject");
            return (null);
        }
        String accountGmail;
        try {
            accountGmail = credential.getString(this.globalVariable.ACCOUNT_API_CREDENTIAL_KEY_SPOTIFY);
        } catch (JSONException e) {
            System.err.println("Faild to parse JsonObject");
            return (null);
        }
        Tuple<String, String> allAccountGmail = this.functions.getTupleFromString(accountGmail);
        if (allAccountGmail == null) {
            System.err.println("Faild to parse Object to Tuple");
            return (null);
        }
        return (allAccountGmail.getRight());
    }

    private String getServicesThreadsByUser(String userId)
    {
        List<AccountApiTable> allAccountApi = this.apiService.getAllAccountApi();
        AccountApiTable finalAccountApi = null;
        for (AccountApiTable accountApi : allAccountApi) {
            if (accountApi.getUsersTable().getId().equals(Long.valueOf(userId))) {
                finalAccountApi = accountApi;
                break;
            }
        }
        if (finalAccountApi == null) {
            return (null);
        }
        JSONObject credential = this.parserJson.parseToJson(finalAccountApi.getCredentials());
        if (credential == null) {
            System.err.println("Failed to parse JsonObject");
            return (null);
        }
        if (!credential.has("threads_id") || !credential.has("threads_access_token")){
            return (null);
        }
        String accountThreads;
        try {
            accountThreads = credential.getString(this.globalVariable.ACCOUNT_API_CREDENTIAL_KEY_THREADS);
        } catch (JSONException e) {
            System.err.println("Failed to parse JsonObject");
            return (null);
        }
        String publishUrl = "https://graph.threads.net/v1.0/" + accountThreads;
        try {
            String data = "?fields=username" +
                          "&access_token=" + credential.getString("threads_access_token");
            URL url = new URL(publishUrl + data);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            System.out.println(publishUrl + data);
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
                JSONObject usernameUser = this.parserJson.parseToJson(infosResponse);
                return (usernameUser.getString("username"));
            }
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Error with threads api.");
        }
        System.out.println("Error with threads api.");
        return (null);
    }
}