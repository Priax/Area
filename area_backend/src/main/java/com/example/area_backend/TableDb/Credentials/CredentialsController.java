package com.example.area_backend.TableDb.Credentials;

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

import com.example.area_backend.TableDb.Tokens.TokenController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/credentials")
@Validated
public class CredentialsController
{
    Optional<CredentialsTable> existingCredentialTable;
    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private TokenController tockensController;

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/")
    public ResponseEntity<?> getAllCredentials(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId
    )
    {
        if (this.tockensController.isUserAuthorized(access_token, userId) <= 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        return ResponseEntity.ok().body(this.credentialsService.getAllCredentials());
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/id")
    public ResponseEntity<?> getCredentialById(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestHeader(value="X-Request-ID",required=false, defaultValue="null") String requestId
    )
    {
        if (this.tockensController.isUserAuthorized(access_token, userId) < 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        if (requestId == null || requestId.equals("null")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Value in header 'X-Request-ID' is required.");
        }
        Long id;
        try {
            id = Long.valueOf(requestId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : ID = " + requestId + " is not integer type.");
        }
        this.existingCredentialTable = this.credentialsService.getCredentialById(id);
        if (!this.existingCredentialTable.isPresent()) {
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).body("Error : ID = " + id + " not found");
        }
        return ResponseEntity.ok().body(this.existingCredentialTable.get());
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @PostMapping("/save")
    public ResponseEntity<?> saveCredential(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestBody CredentialsTable credential
    )
    {
        if (this.tockensController.isUserAuthorized(access_token, userId) < 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        if (credential == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : New value to create credential in body is required.");
        }
        this.existingCredentialTable = this.credentialsService.saveCredential(credential);
        if (!this.existingCredentialTable.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Impossible to save id = " + credential.getId() + ".");
        }
        return ResponseEntity.ok().body(this.existingCredentialTable.get());
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @PutMapping("/update")
    public ResponseEntity<?> updateCredential(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestBody CredentialsTable credential)
    {
        Long userLongId;
        try {
            userLongId = Long.valueOf(userId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : ID = " + userId + " is not integer type.");
        }
        int roleNbr = this.tockensController.isUserAuthorized(access_token, userLongId);
        if (roleNbr == 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        if (credential == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Value to update credential in body is required.");
        }
        if (roleNbr == 1 && !credential.getUser().getId().equals(userLongId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with UserId = \"" + userId + "\" cannot change data from not himself.");
        }
        this.existingCredentialTable = this.credentialsService.updateCredential(credential);
        if (this.existingCredentialTable.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Id = " + credential.getId() + " not found.");
        }
        return ResponseEntity.ok().body(this.existingCredentialTable.get());
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteEmployeeById(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestHeader(value="X-Request-ID", required=false, defaultValue="null") String requestId
    )
    {
        if (this.tockensController.isUserAuthorized(access_token, userId) <= 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        if (requestId == null || requestId.equals("null")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Value in header 'X-Request-ID' is required.");
        }
        Long id;
        try {
            id = Long.valueOf(requestId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : ID = " + requestId + " is not integer type.");
        }
        this.credentialsService.deleteCredentialById(id);
        return ResponseEntity.ok().body("Deleted employee successfully");
    }
}
