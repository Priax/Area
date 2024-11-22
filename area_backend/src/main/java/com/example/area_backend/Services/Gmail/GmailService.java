package com.example.area_backend.Services.Gmail;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.area_backend.Handler;
import com.example.area_backend.TableDb.AccountApi.AccountApiService;
import com.example.area_backend.TableDb.Users.UsersRepo;
import com.example.area_backend.TableDb.Users.UsersTable;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Profile;


@RestController
@Validated
@Service
public class GmailService extends Handler
{
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Arrays.asList(
        GmailScopes.GMAIL_READONLY,
        GmailScopes.GMAIL_MODIFY,
        GmailScopes.GMAIL_LABELS,
        GmailScopes.GMAIL_SETTINGS_BASIC,
        GmailScopes.GMAIL_SETTINGS_SHARING,
        GmailScopes.GMAIL_SEND,
        GmailScopes.GMAIL_ADDONS_CURRENT_MESSAGE_READONLY,
        GmailScopes.GMAIL_METADATA
    );
    private static final String CREDENTIALS_FILE_PATH = "/client_secret.json";
    private static final String APPLICATION_NAME = "AREA_Gmail";
    private final GoogleAuthorizationCodeFlow flow;
    private String redirectUri;
    private GoogleClientSecrets clientSecrets;
    private NetHttpTransport httpTransport;
    private final AccountApiService apiService;
    private final UsersRepo usersRepo;

    public GmailService(AccountApiService apiService, UsersRepo usersRepo)
    {
        try {
            this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        } catch (IOException | GeneralSecurityException e) {
            System.err.println("Error with google: " + e);
        }
        this.flow = this.getFlow();
        this.apiService = apiService;
        this.usersRepo = usersRepo;
    }


    private GoogleAuthorizationCodeFlow getFlow()
    {
        if (this.httpTransport == null) {
            System.err.println("Http Transport is null");
            return (null);
        }
        InputStream in = GmailService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            System.err.println("Resource not found: " + CREDENTIALS_FILE_PATH);
            return (null);
        }
        try {
            this.clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
            GoogleAuthorizationCodeFlow nativeflow = new GoogleAuthorizationCodeFlow.Builder(
                this.httpTransport, JSON_FACTORY, this.clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(CREDENTIALS_FILE_PATH)))
                .setAccessType("offline")
                .build();
            this.redirectUri = this.clientSecrets.getDetails().getRedirectUris().get(1);
            return (nativeflow);
        } catch (IOException e) {
            System.err.println("Error getting Google Authorization Code Flow: " + e);
        }
        return (null);
    }

    public String getAuthorizationUrl(String userId, String emailId)
    {
        if (this.flow == null) {
            System.err.println("Flow is null");
            return (null);
        }
        String concatString = userId + ":" + emailId;
        return this.flow.newAuthorizationUrl()
                .setRedirectUri(this.redirectUri)
                .setState(concatString)
                .build();
    }

    public void handleAuthorizationCode(String userID, String emailId, String authCode)
    {
        if (this.flow == null) {
            System.err.println("Flow is null");
            return;
        }
        try {
            GoogleTokenResponse tokenResponse = this.flow.newTokenRequest(authCode)
                    .setRedirectUri(this.redirectUri)
                    .execute();
            this.flow.createAndStoreCredential(tokenResponse, emailId);
            // GmailHolder.getGmailListener().addGmailAccount(this.getGmailService(userId));
        } catch (IOException e) {
            System.err.println("Error when Create And Store Credential: " + e);
        }
        Gmail gmailUser = this.getGmailService(emailId);
        Profile profile;
        try {
            profile = gmailUser.users().getProfile("me").execute();
        } catch (IOException e) {
            System.err.println("Error when get gmail class: " + e);
            return;
        }
        Optional<UsersTable> optionalUsersTable = this.usersRepo.findById(Long.valueOf(userID));
        if (!optionalUsersTable.isPresent()) {
            System.err.println("User :" + userID + " not found in database");
            return;
        }
        apiService.loginWithGmail(emailId, profile.getEmailAddress(), optionalUsersTable.get());
        Handler.gmailService = this;
    }

    public Gmail getGmailService(String userId)
    {
        if (this.flow == null) {
            System.err.println("Flow is null");
            return (null);
        }
        Credential credential;
        try {
            credential = this.flow.loadCredential(userId);
        } catch (IOException e) {
            System.err.println("Error when load Credential: " + e);
            return (null);
        }
        if (credential == null) {
            System.err.println("No credentials found for user: " + userId);
            return (null);
        }
        return new Gmail.Builder(this.httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public boolean isUserAuthorized(String userId)
    {
        if (this.flow == null) {
            System.err.println("Flow is null");
            return (false);
        }
        try {
            Object tmp = this.flow.loadCredential(userId);
            return (tmp != null);
        } catch (IOException e) {
            System.err.println("Erro with Load Cradential: " + e);
            return (false);
        }
    }

    public boolean logoutGmailAccount(String emailId, String userId)
    {
        if (this.flow == null) {
            System.err.println("Flow is null");
            return false;
        }
        try {
            Credential credential = this.flow.loadCredential(emailId);
            if (credential == null) {
                System.err.println("No credentials found for user: " + emailId);
                return false;
            }
            String accessToken = credential.getAccessToken();
            if (accessToken != null) {
                try {
                    HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
                    GenericUrl revokeUrl = new GenericUrl("https://accounts.google.com/o/oauth2/revoke?token=" + accessToken);
                    HttpRequest request = transport.createRequestFactory().buildGetRequest(revokeUrl);
                    request.execute();
                } catch (GeneralSecurityException | IOException e) {
                    System.err.println("Error revoking access token: " + e);
                }
            }
            this.flow.getCredentialDataStore().delete(emailId);
            Optional<UsersTable> optionalUsersTable = this.usersRepo.findById(Long.valueOf(userId));
            if (optionalUsersTable.isPresent()) {
                UsersTable user = optionalUsersTable.get();
                this.apiService.logoutWithGmail(emailId, user);
            }
            System.out.println("UserID: " + emailId + " was succefully removed from database");
            return (true);
        } catch (IOException e) {
            System.err.println("Error during Gmail logout: " + e);
            return (false);
        }
    }


    @GetMapping("gmailOauth/callback")
    public ResponseEntity<String> handleGmailCallback(@RequestParam("state") String credential, @RequestParam("code") String code)
    {
        String[] stateParts = credential.split(":");
        this.handleAuthorizationCode(stateParts[0], stateParts[1], code);
        return ResponseEntity.ok("You can close this window");
    }
}
