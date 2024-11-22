package com.example.area_backend.TableDb.Reactions;

import java.util.List;
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

import com.example.area_backend.TableDb.Actions.ActionsRepo;
import com.example.area_backend.TableDb.Actions.ActionsTable;
import com.example.area_backend.TableDb.Tokens.TokenController;

import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/reactions")
@Validated
public class ReactionController {
    Optional<ReactionsTable> existingReactionTable;
    List<ReactionsTable> reactionsList;
    @Autowired
    private ReactionsService reactionsService;
    @Autowired
    private ActionsRepo actionsRepo;
    @Autowired
    private TokenController tockensController;

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/")
    public ResponseEntity<?> getAllReactions(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId
    )
    {
        if (this.tockensController.isUserAuthorized(access_token, userId) < 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        return ResponseEntity.ok().body(this.reactionsService.getAllReactions());
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/reaction")
    public ResponseEntity<?> getReactionById(
        @RequestHeader("RequestID") String requestId,
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestBody ReactionsTable reaction
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
        if (this.tockensController.isUserAuthorized(access_token, userId) == 1 && !reaction.getActionTable().getUserTable().getId().equals(userLongId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with UserId = \"" + userId + "\" is unauthorized to access at this reaction.");
        }
        this.existingReactionTable = this.reactionsService.getReactionById(requestId);
        if (!this.existingReactionTable.isPresent()) {
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).body("Error : Reaction = " + requestId + " not found");
        }
        return ResponseEntity.ok().body(this.existingReactionTable.get());
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/user")
    public ResponseEntity<?> getAllReactionsByUser(
        @RequestHeader("RequestID") String requestId,
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestBody ReactionsTable reaction
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
        if (this.tockensController.isUserAuthorized(access_token, userId) == 1 && !reaction.getActionTable().getUserTable().getId().equals(userLongId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with UserId = \"" + userId + "\" is unauthorized to access other user's reaction.");
        }
        this.reactionsList = this.reactionsService.getAllReactionByUser(userLongId);
        return ResponseEntity.ok().body(this.reactionsList);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/action")
    public ResponseEntity<?> getAllReactionsByActionId(
        @RequestHeader("RequestID") String actionId,
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId
    )
    {
        List<ReactionsTable> reactionList;
        if (actionId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Value in header 'X-Request-ID' is required.");
        }
        if (this.tockensController.isUserAuthorized(access_token, userId) < 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        Long actionLongId;
        try {
            actionLongId = Long.valueOf(actionId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Action ID = " + actionId + " is not integer type.");
        }
        Optional<ActionsTable> action = this.actionsRepo.findById(actionLongId);
        if (!action.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Action ID = " + actionId + " doesn't exist.");
        }
        Long userLongId;
        try {
            userLongId = Long.valueOf(userId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : ID = " + userId + " is not integer type.");
        }
        if (this.tockensController.isUserAuthorized(access_token, userId) == 1 && !userLongId.equals(action.get().getUserTable().getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with UserId = \"" + userId + "\" is unauthorized to access other user's actions.");
        }
        reactionList = this.reactionsService.getReactionsByActionId(action.get());
        return ResponseEntity.ok().body(reactionList);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @PostMapping("/save")
    public ResponseEntity<?> saveReaction(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestBody ReactionsTable reaction
    )
    {
        if (this.tockensController.isUserAuthorized(access_token, userId) < 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        if (reaction == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : New value to create reaction in body is required.");
        }
        this.existingReactionTable = this.reactionsService.saveReaction(reaction);
        if (!this.existingReactionTable.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Impossible to save id = " + reaction.getId() + ".");
        }
        return ResponseEntity.ok().body(this.existingReactionTable.get());
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @PutMapping("/update")
    public ResponseEntity<?> updateReaction(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="ReactionID", required=false, defaultValue="null") String userId,
        @RequestBody ReactionsTable reaction)
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
        if (reaction == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Value to update reation in body is required.");
        }
        if (roleNbr == 1 && !reaction.getId().equals(userLongId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with UserId = \"" + userId + "\" cannot change data from not himself.");
        }
        this.existingReactionTable = this.reactionsService.updateReaction(reaction);
        if (this.existingReactionTable.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Id = " + reaction.getId() + " not found.");
        }
        return ResponseEntity.ok().body(this.existingReactionTable.get());
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteReactionById(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestBody ReactionsTable reaction
    )
    {
        if (this.tockensController.isUserAuthorized(access_token, userId) < 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        if (reaction == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Parameter reaction is required.");
        }
        Long id;
        try {
            id = Long.valueOf(userId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : ID = " + userId + " is not integer type.");
        }
        if (this.tockensController.isUserAuthorized(access_token, userId) == 1 && !reaction.getActionTable().getUserTable().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with UserID cannot delete reaction of another user");
        }
        this.reactionsService.deleteReactionById(id);
        return ResponseEntity.ok().body("Deleted reaction successfully");
    }
}
