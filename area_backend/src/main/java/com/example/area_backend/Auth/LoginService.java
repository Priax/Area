package com.example.area_backend.Auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.example.area_backend.Handler;
import com.example.area_backend.TableDb.AccountApi.AccountApiService;
import com.example.area_backend.TableDb.Tokens.TokenController;
import com.example.area_backend.Tuple;

@RestController
@RequestMapping("/auth/services")
@Validated
public class LoginService extends Handler
{
    @Autowired
    private TokenController tockensController;

    @Autowired
    private final AccountApiService accountApiService;

    @Autowired
    public LoginService(AccountApiService newAccountApiService)
    {
        accountApiService = newAccountApiService;
    }

    public LoginService()
    {
        accountApiService = null;
    }

    @Value("${spring.services.threads.applicationId}")
	private String appId;

    @CrossOrigin(origins = "http://localhost:8081")
    @PostMapping("/login/gmail")
    public ResponseEntity<?> loginWithGmail
    (
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestBody String email
    )
    {
        if (this.tockensController.isUserAuthorized(access_token, userId) < 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        if (Handler.gmailService.isUserAuthorized(email)) {
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("User Already Saved");
        }
        return ResponseEntity.ok().body(Handler.gmailService.getAuthorizationUrl(userId, email));
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @DeleteMapping("/logout/gmail")
    public ResponseEntity<?> logoutWithGmail
    (
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestBody String email
    )
    {
        if (this.tockensController.isUserAuthorized(access_token, userId) < 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        if (!Handler.gmailService.isUserAuthorized(email)) {
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("User with email: " + email + " not saved in flow of gmail");
        }
        if (Handler.gmailService.logoutGmailAccount(email, userId)) {
            return ResponseEntity.ok().body("UserID: " + email + " was succefully removed from database");
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Error : UserID: " + email + " not removed from database");
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @PostMapping("/login/spotify")
    public ResponseEntity<?> loginWithSpotify(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId
    )
    {
        if (this.tockensController.isUserAuthorized(access_token, userId) < 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        if (Handler.userSpotify.isAccountMonitored(userId)) {
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("User Already Saved");
        }
        return ResponseEntity.ok().body(Handler.userSpotify.getAuthorizationUri(userId));
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @DeleteMapping("/logout/spotify")
    public ResponseEntity<?> logoutWithSpotify
    (
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId
    )
    {
        if (this.tockensController.isUserAuthorized(access_token, userId) < 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        if (Handler.userSpotify.deleteUserAccount(userId)) {
            return ResponseEntity.ok().body("UserID: " + userId + " was succefully removed from database");
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Error : UserID: " + userId + " not removed from database");
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @PostMapping("/login/threads")
    public ResponseEntity<?> getAuthorisationCode(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId
    )
    {
        if (this.tockensController.isUserAuthorized(access_token, userId) <= 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        String redirectUri = "https://promptly-normal-maggot.ngrok-free.app/threads/request";
        String connectUrl = "https://threads.net/oauth/authorize" +
           "?client_id=" + appId +
           "&redirect_uri=" + redirectUri +
           "&scope=threads_basic,threads_read_replies,threads_manage_insights,threads_manage_replies,threads_content_publish" +
           "&response_type=code" +
           "&state=" + userId;
        return ResponseEntity.ok().body(connectUrl);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @DeleteMapping("/logout/threads")
    public ResponseEntity<?> logoutWithThreads
    (
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId
    )
    {
        if (this.tockensController.isUserAuthorized(access_token, userId) < 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        Tuple<Long, String> result = this.accountApiService.logoutWithThreads(userId);
        if (result.getLeft().equals((long) 0)) {
            return ResponseEntity.ok().body("UserID: " + userId + " was succefully removed from database.");
        }
        if (result.getLeft().equals((long) -1)){
            ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Error : UserID: " + userId + " is not a Integer.");
        }
        if (result.getLeft().equals((long) -2)){
            ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Error : UserID: " + userId + " is not an User.");
        }
        if (result.getLeft().equals((long) -3)){
            ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Error : UserID: " + userId + " has not a account api.");
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Error : UserID: " + userId + " not removed from database.");
    }
}