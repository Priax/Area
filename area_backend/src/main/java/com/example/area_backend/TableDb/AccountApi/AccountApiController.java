package com.example.area_backend.TableDb.AccountApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.example.area_backend.TableDb.Tokens.TokenController;

@RestController
@RequestMapping("/accountapi")
@Validated
public class AccountApiController {

    @Autowired
    private AccountApiService accountApiService;
    @Autowired
    private TokenController tockensController;

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/")
    public ResponseEntity<?> getAllAccountApi(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId
    )
    {
        if (this.tockensController.isUserAuthorized(access_token, userId) <= 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        return ResponseEntity.ok().body(this.accountApiService.getAllAccountApi());
    }
}
