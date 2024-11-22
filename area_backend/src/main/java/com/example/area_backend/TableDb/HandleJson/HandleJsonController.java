package com.example.area_backend.TableDb.HandleJson;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.Optional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import com.example.area_backend.TableDb.EnumServices;
import com.example.area_backend.TableDb.Tokens.TokenController;

@RestController
@RequestMapping("/handlejson")
@Validated
public class HandleJsonController {

    Optional<HandleJsonTable> existingHandleJsonTable;
    List<HandleJsonTable> listHandleJson;
    @Autowired
    private HandleJsonService handleJsonService;
    @Autowired
    private TokenController tockensController;

    @GetMapping("/")
    public ResponseEntity<?> getAllHandleJson(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId
    )
    {
        if (this.tockensController.isUserAuthorized(access_token, userId) < 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        return ResponseEntity.ok().body(this.handleJsonService.getAllHandleJson());
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @PostMapping("/create")
    public ResponseEntity<?> addHandleJson(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestBody HandleJsonTable handleJson
    )
    {
        if (this.tockensController.isUserAuthorized(access_token, userId) < 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        if (handleJson == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : New value to create action in body is required.");
        }
        this.existingHandleJsonTable = this.handleJsonService.createHandleJson(handleJson);
        if (!this.existingHandleJsonTable.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Impossible to save id = " + handleJson.getId() + ".");
        }
        return ResponseEntity.ok().body(this.existingHandleJsonTable.get());
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/actions")
    public ResponseEntity<?> getAllActionsHandleJson(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId
    )
    {
        if (this.tockensController.isUserAuthorized(access_token, userId) < 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        this.listHandleJson  = this.handleJsonService.getAllByTypeByService("Action", EnumServices.DISCORD, 0);
        return ResponseEntity.ok().body(this.listHandleJson);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/reactions")
    public ResponseEntity<?> getAllReactionsHandleJson(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId
    )
    {
        if (this.tockensController.isUserAuthorized(access_token, userId) < 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        this.listHandleJson  = this.handleJsonService.getAllByTypeByService("Reaction", EnumServices.DISCORD, 0);
        return ResponseEntity.ok().body(this.listHandleJson);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/service")
    public ResponseEntity<?> getAllByService(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestHeader(value="Service", required=false, defaultValue="null") String requestService
    )
    {
        if (requestService == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Service cannot be null.");
        }
        if (this.tockensController.isUserAuthorized(access_token, userId) < 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        EnumServices service;
        try {
            service = EnumServices.valueOf(requestService);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : This service = \"" + requestService + "\" doesn't exist.");
        }
        this.listHandleJson  = this.handleJsonService.getAllByTypeByService(null, service, 1);
        return ResponseEntity.ok().body(this.listHandleJson);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/service/actions")
    public ResponseEntity<?> getAllActionsByService(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestHeader(value="Service", required=false, defaultValue="null") String requestService
    )
    {
        if (requestService == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Service cannot be null.");
        }
        if (this.tockensController.isUserAuthorized(access_token, userId) < 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        EnumServices service;
        try {
            service = EnumServices.valueOf(requestService);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : This service = \"" + requestService + "\" doesn't exist.");
        }
        this.listHandleJson  = this.handleJsonService.getAllByTypeByService("Action", service, 1);
        return ResponseEntity.ok().body(this.listHandleJson);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/service/reactions")
    public ResponseEntity<?> getAllReactionsByService(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId,
        @RequestHeader(value="Service", required=false, defaultValue="null") String requestService
    )
    {
        if (requestService == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Service cannot be null.");
        }
        if (this.tockensController.isUserAuthorized(access_token, userId) < 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        EnumServices service;
        try {
            service = EnumServices.valueOf(requestService);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : This service = \"" + requestService + "\" doesn't exist.");
        }
        this.listHandleJson  = this.handleJsonService.getAllByTypeByService("Reaction", service, 1);
        return ResponseEntity.ok().body(this.listHandleJson);
    }

}
