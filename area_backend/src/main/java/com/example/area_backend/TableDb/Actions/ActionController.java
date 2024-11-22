package com.example.area_backend.TableDb.Actions;

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
import org.springframework.web.bind.annotation.CrossOrigin;

import com.example.area_backend.TableDb.Tokens.TokenController;

@RestController
@RequestMapping("/actions")
@Validated
public class ActionController {

    Optional<ActionsTable> existingActionTable;
    List<ActionsTable> actionsList;
    @Autowired
    private ActionsService actionService;
    @Autowired
    private TokenController tockensController;

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/")
    public ResponseEntity<?> getAllActions(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId
    )
    {
        if (this.tockensController.isUserAuthorized(access_token, userId) < 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        return ResponseEntity.ok().body(this.actionService.getAllActions());
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/action")
    public ResponseEntity<?> getActionById(
        @RequestHeader("X-Request-ID") String requestId,
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestBody ActionsTable action
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
        if (this.tockensController.isUserAuthorized(access_token, userId) == 1 && !userLongId.equals(action.getUserTable().getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with UserId = \"" + userId + "\" is unauthorized to access at this action.");
        }
        this.existingActionTable = this.actionService.getActionById(requestId);
        if (!this.existingActionTable.isPresent()) {
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).body("Error : Action = " + requestId + " not found");
        }
        return ResponseEntity.ok().body(this.existingActionTable.get());
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/user")
    public ResponseEntity<?> getAllActionsByUser(
        @RequestHeader("X-Request-ID") String requestId,
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId
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
        Long requestLongId;
        try {
            requestLongId = Long.valueOf(requestId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : ID = " + userId + " is not integer type.");
        }
        if (this.tockensController.isUserAuthorized(access_token, userId) == 1 && !userLongId.equals(requestLongId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with UserId = \"" + userId + "\" is unauthorized to access other user's actions.");
        }
        this.actionsList = this.actionService.getAllActionByUser(requestLongId);
        return ResponseEntity.ok().body(this.actionsList);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @PostMapping("/save")
    public ResponseEntity<?> saveAction(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestBody ActionsTable actions
    )
    {
        if (this.tockensController.isUserAuthorized(access_token, userId) < 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        if (actions == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : New value to create action in body is required.");
        }
        this.existingActionTable = this.actionService.saveAction(actions);
        if (!this.existingActionTable.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Impossible to save id = " + actions.getId() + ".");
        }
        return ResponseEntity.ok().body(this.existingActionTable.get());
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @PutMapping("/update")
    public ResponseEntity<?> updateAction(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestBody ActionsTable action)
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
        if (action == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Value to update action in body is required.");
        }
        if (roleNbr == 1 && !action.getId().equals(userLongId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with UserId = \"" + userId + "\" cannot change data from not himself.");
        }
        this.existingActionTable = this.actionService.updateAction(action);
        if (this.existingActionTable.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Id = " + action.getId() + " not found.");
        }
        return ResponseEntity.ok().body(this.existingActionTable.get());
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteActionById(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestBody ActionsTable action
    )
    {
        if (this.tockensController.isUserAuthorized(access_token, userId) < 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        if (action == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Parameter action is required.");
        }
        Long id;
        try {
            id = Long.valueOf(userId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : ID = " + action.getUserTable().getId() + " is not integer type.");
        }
        if (this.tockensController.isUserAuthorized(access_token, userId) == 1 && !id.equals(action.getUserTable().getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with UserID cannot delete action of another user");
        }
        this.actionService.deleteActionById(id);
        return ResponseEntity.ok().body("Deleted action successfully");
    }
}
