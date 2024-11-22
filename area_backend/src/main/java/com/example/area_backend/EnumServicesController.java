package com.example.area_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.area_backend.TableDb.EnumServices;
import com.example.area_backend.TableDb.Tokens.TokenController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/services")
@Validated
public class EnumServicesController
{
    @Autowired
    private TokenController tockensController;

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/")
    public ResponseEntity<?> getAllServices(
        @RequestHeader(value="Authorization", required=false, defaultValue="null") String access_token,
        @RequestHeader(value="UserID", required=false, defaultValue="null") String userId
    )
    {
        if (this.tockensController.isUserAuthorized(access_token, userId) < 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error : User with Token = \"" + access_token + "\" and UserId = \"" + userId + "\" is unauthorized.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(EnumServices.values());
    }
}
