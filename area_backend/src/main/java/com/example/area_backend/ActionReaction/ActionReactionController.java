package com.example.area_backend.ActionReaction;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.area_backend.TableDb.Actions.ActionsRepo;
import com.example.area_backend.TableDb.Actions.ActionsTable;
import com.example.area_backend.TableDb.Tokens.TokenController;
import com.example.area_backend.Tuple;

@RestController
@RequestMapping("/actionreaction")
@Validated
public class ActionReactionController {

    @Autowired
    private TokenController tockensController;
    @Autowired
    private Create create;
    @Autowired
    private ActionsRepo actionsRepo;
    @Autowired
    private Delete delete;

    @CrossOrigin(origins = "http://localhost:8081")
    @PostMapping("/create")
    public ResponseEntity<?> login(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestBody ActionReaction actionReaction
    )
    {
        if (actionReaction == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : New value to create action-reaction in body is required.");
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
        Long requestId;
        try {
            requestId = Long.valueOf(actionReaction.getUserId());
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : ID = " + actionReaction.getUserId() + " is not integer type.");
        }
        if (this.tockensController.isUserAuthorized(access_token, userId) < 1 && !userLongId.equals(requestId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : user with userID = " + userId + " cannot create an action-reaction or other user.");
        }
        Tuple<Long, Long> return_actionReaction = create.create(actionReaction);
        if (return_actionReaction.getLeft() == -1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Data in body is incorrect");
        }
        return ResponseEntity.ok().body(return_actionReaction);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteActionReaction(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestHeader(value="ActionId", required=false, defaultValue="null") String actionId
    )
    {
        if (actionId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : New value to create action-reaction in body is required.");
        }
        if (this.tockensController.isUserAuthorized(access_token, userId) < 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        Long requestAction;
        try {
            requestAction = Long.valueOf(actionId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Action ID = " + actionId + " is not integer type.");
        }
        Long idUser;
        try {
            idUser = Long.valueOf(userId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : User ID = " + userId + " is not integer type.");
        }
        Optional<ActionsTable> checkAction = actionsRepo.findById(requestAction);
        if (!checkAction.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Action Id : " + actionId + " doesn't exist.");
        }
        if (!checkAction.get().getUserTable().getId().equals(idUser)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : You can delete your tasks only.");
        }
        delete.deleteActionReaction(checkAction.get(), idUser);
        return ResponseEntity.ok().body("Delete ARea successfully.");
    }

}
