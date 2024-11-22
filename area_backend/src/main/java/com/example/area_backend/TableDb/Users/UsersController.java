package com.example.area_backend.TableDb.Users;

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
@RequestMapping("/users")
@Validated
public class UsersController {
    Optional<UsersTable> existingUserTable;
    @Autowired
    private UsersService usersService;

    @Autowired
    private TokenController tockensController;
    
    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/")
    public ResponseEntity<?> getAllUsers(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId
    )
    {
        if (this.tockensController.isUserAuthorized(access_token, userId) < 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        return ResponseEntity.ok().body(this.usersService.getAllUsers());
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/user")
    public ResponseEntity<?> getUserById(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestHeader(value="RequestID", required=false, defaultValue="null") String requestId
    )
    {
        if (requestId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Value in header 'X-Request-ID' is required.");
        }
        if (this.tockensController.isUserAuthorized(access_token, userId) < 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        Long userLongId;
        try {
            userLongId = Long.valueOf(requestId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : ID = " + requestId + " is not integer type.");
        }
        this.existingUserTable = this.usersService.getUserById(userLongId);
        if (!this.existingUserTable.isPresent()) {
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).body("Error : User = " + userLongId + " not found");
        }
        return ResponseEntity.ok().body(this.existingUserTable.get());
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @PostMapping("/save")
    public ResponseEntity<?> saveUser(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestBody UsersTable user
    )
    {
        if (this.tockensController.isUserAuthorized(access_token, userId) < 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : New value to create user in body is required.");
        }
        this.existingUserTable = this.usersService.saveUser(user);
        if (!this.existingUserTable.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Impossible to save id = " + user.getId() + ".");
        }
        return ResponseEntity.ok().body(this.existingUserTable.get());
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestBody UsersTable user
    )
    {
        Long userLongId;
        try {
            userLongId = Long.valueOf(userId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : ID = " + userId + " is not integer type.");
        }
        int roleNbr = this.tockensController.isUserAuthorized(access_token, userId);
        if (roleNbr == 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Value to update reation in body is required.");
        }
        if (roleNbr == 1 && !user.getId().equals(userLongId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with UserId = \"" + userId + "\" cannot change data from not himself.");
        }
        this.existingUserTable = this.usersService.updateUser(user);
        if (!this.existingUserTable.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Id = " + user.getId() + " not found.");
        }
        return ResponseEntity.ok().body(this.existingUserTable.get());
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteReactionById(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestHeader(value="X-Request-ID", required=false, defaultValue="null") String requestId
    )
    {
        if (requestId == null || requestId.equals("null")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Value in header 'X-Request-ID' is required.");
        }
        if (this.tockensController.isUserAuthorized(access_token, userId) <= 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        if (this.tockensController.isUserAuthorized(access_token, userId) == 1 && userId != requestId) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with UserId = \"" + userId + "\" is unauthorized to delete other users.");
        }
        Long id;
        try {
            id = Long.valueOf(requestId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : ID = " + requestId + " is not integer type.");
        }
        this.usersService.deleteUserById(id);
        return ResponseEntity.ok().body("Deleted user successfully");
    }
}
