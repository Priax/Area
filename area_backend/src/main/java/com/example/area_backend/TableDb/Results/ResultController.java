package com.example.area_backend.TableDb.Results;

import java.util.Optional;
import java.util.List;

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
@RequestMapping("/results")
@Validated
public class ResultController {
    Optional<ResultsTable> existingReactionTable;
    List<ResultsTable> resultsList;
    @Autowired
    private ResultsService resultsService;

    @Autowired
    private TokenController tockensController;

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/")
    public ResponseEntity<?> getAllResults(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId
    )
    {
        if (this.tockensController.isUserAuthorized(access_token, userId) < 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        return ResponseEntity.ok().body(this.resultsService.getAllResults());
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/result")
    public ResponseEntity<?> getResultById(
        @RequestHeader("RequestID") String requestId,
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestBody ResultsTable result
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
            userLongId = Long.valueOf(userId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : ID = " + userId + " is not integer type.");
        }
        if (this.tockensController.isUserAuthorized(access_token, userId) == 1 && userLongId != result.getUserTable().getId()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with UserId = \"" + userId + "\" is unauthorized to access at this user.");
        }
        this.existingReactionTable = this.resultsService.getResultById(requestId);
        if (!this.existingReactionTable.isPresent()) {
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).body("Error : Result = " + requestId + " not found");
        }
        return ResponseEntity.ok().body(this.existingReactionTable.get());
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/user")
    public ResponseEntity<?> getAllResultsByUser(
        @RequestHeader("X-Request-ID") String requestId,
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestBody ResultsTable result
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
            userLongId = Long.valueOf(userId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : ID = " + userId + " is not integer type.");
        }
        if (this.tockensController.isUserAuthorized(access_token, userId) == 1 && userLongId != result.getUserTable().getId()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with UserId = \"" + userId + "\" is unauthorized to access other user's results.");
        }
        this.resultsList = this.resultsService.getAllResultByUser(userLongId);
        return ResponseEntity.ok().body(this.resultsList);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @PostMapping("/save")
    public ResponseEntity<?> saveReaction(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestBody ResultsTable result
    )
    {
        if (this.tockensController.isUserAuthorized(access_token, userId) < 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        if (result == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : New value to create result in body is required.");
        }
        this.existingReactionTable = this.resultsService.saveResult(result);
        if (!this.existingReactionTable.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Impossible to save id = " + result.getId() + ".");
        }
        return ResponseEntity.ok().body(this.existingReactionTable.get());
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @PutMapping("/update")
    public ResponseEntity<?> updateResult(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="ReactionID", required=false, defaultValue="null") String userId,
        @RequestBody ResultsTable result)
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
        if (result == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Value to update reation in body is required.");
        }
        if (roleNbr == 1 && !result.getId().equals(userLongId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with UserId = \"" + userId + "\" cannot change data from not himself.");
        }
        this.existingReactionTable = this.resultsService.updateResult(result);
        if (this.existingReactionTable.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Id = " + result.getId() + " not found.");
        }
        return ResponseEntity.ok().body(this.existingReactionTable.get());
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteResultById(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestBody ResultsTable result
    )
    {
        if (this.tockensController.isUserAuthorized(access_token, userId) < 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        if (result == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Parameter result is required.");
        }
        Long id;
        try {
            id = Long.valueOf(userId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : ID = " + userId + " is not integer type.");
        }
        if (this.tockensController.isUserAuthorized(access_token, userId) == 1 && id != result.getUserTable().getId()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with UserID cannot delete result of another user");
        }
        this.resultsService.deleteResultById(id);
        return ResponseEntity.ok().body("Deleted reaction successfully");
    }
}
