package com.example.area_backend.TableDb.Tokens;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/tokens")
@Validated
public class TokenController
{
    Optional<TokensTable> existingTokenTable;
    @Autowired
    private TokensService tokensService;

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/")
    public ResponseEntity<?> getAllTokens(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId
    )
    {
        if (this.isUserAuthorized(access_token, userId) <= 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        return ResponseEntity.ok().body(this.tokensService.getAllTokens());
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/token")
    public ResponseEntity<?> getTokenByToken(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestHeader(value="X-Request-AccessToken", required=false, defaultValue="null") String requestToken
    )
    {
        if (this.isUserAuthorized(access_token, userId) <= 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        if (requestToken == null || requestToken.equals("null")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Value in header 'X-Request-AccessToken' is required.");
        }
        this.existingTokenTable = this.tokensService.getTokenByToken(requestToken);
        if (!this.existingTokenTable.isPresent()) {
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).body("Error : Token = " + requestToken + " not found");
        }
        return ResponseEntity.ok().body(this.existingTokenTable.get());
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @PostMapping("/save")
    public ResponseEntity<?> saveToken(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestBody TokensTable token
    )
    {
        if (this.isUserAuthorized(access_token, userId) <= 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        if (token == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : New value to create token in body is required.");
        }
        this.existingTokenTable = this.tokensService.saveToken(token);
        if (!this.existingTokenTable.isPresent()) {
            if (token.getUserTable() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Bad formating request.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Impossible to save id = " + token.getUserTable().getId() + ".");
            }
        }
        return ResponseEntity.ok().body(this.existingTokenTable.get());
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @PutMapping("/update")
    public ResponseEntity<?> updateToken(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestBody TokensTable token
    )
    {
        if (this.isUserAuthorized(access_token, userId) <= 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        if (token == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Value to update credential in body is required.");
        }
        this.existingTokenTable = this.tokensService.updateToken(token);
        if (this.existingTokenTable.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Id = " + token.getId() + " not found.");
        }
        return ResponseEntity.ok().body(this.existingTokenTable.get());
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteTokenByToken(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestHeader("X-Request-AccessToken") String requestToken
    )
    {
        if (this.isUserAuthorized(access_token, userId) <= 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        if (requestToken == null || requestToken.equals("null")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Value in header 'X-Request-AccessToken' is required.");
        }
        this.tokensService.deleteTokenByToken(access_token);
        return ResponseEntity.ok().body("Deleted token successfully");
    }

    public boolean isTokenExistInTable(String access_token)
    {
        if (access_token == null) {
            return false;
        }
        this.existingTokenTable = this.tokensService.getTokenByToken(access_token);
        return this.existingTokenTable.isPresent();
    }

    public int isUserAuthorized(String access_token, String userId)
    {
        if (access_token == null || access_token.equals("null") || userId == null || userId.equals("null")) {
            return (0);
        }
        String token = access_token.replace("Bearer ", "");
        if (!this.isTokenExistInTable(token))
            return 0;
        Long userLongId;
        try {
            userLongId = Long.valueOf(userId);
        } catch (NumberFormatException e) {
            return 0;
        }
        return (this.tokensService.isUserAuthorized(new TokensTable(token, userLongId)));
    }

    public int isUserAuthorized(String access_token, Long userId)
    {
        if (access_token == null || access_token.equals("null") || userId == null) {
            return (0);
        }
        String token = access_token.replace("Bearer ", "");
        if (this.isTokenExistInTable(token))
            return 0;
        return (this.tokensService.isUserAuthorized(new TokensTable(token, userId)));
    }
}
