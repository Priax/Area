package com.example.area_backend.Auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.example.area_backend.TableDb.AccountApi.AccountApiService;
import com.example.area_backend.Tuple;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController
{
    @Autowired
    private Login login;

    @Autowired
    private SignUp signUp;
    @Autowired
    private AccountApiService accountApiService;

    @CrossOrigin(origins = "http://localhost:8081")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Auth auth)
    {
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : New value to create credential in body is required.");
        }
        Tuple<Long, String> return_token = login.loginUser(auth.getEmail(), auth.getPassword());
        if (return_token.getLeft() == -1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Email and/or password incorrect");
        }
        if (return_token.getLeft() == -2) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Something went wrong");
        }
        return ResponseEntity.ok().body(return_token);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody Auth auth)
    {
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : New value to create user in body is required.");
        }
        Tuple<Long, String> return_token = signUp.signUp(auth);
        if (return_token.getLeft() == -1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to Create User : One or more value are empty or wrong type.");
        }
        if (return_token.getLeft() == -2) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to Create User : User with email \"" + auth.getEmail() + "\" is already exist");
        }
        return ResponseEntity.ok().body(return_token);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @PostMapping("/logingoogle")
    public ResponseEntity<?> logingoogle(@RequestBody LoginGoogle loginGoogle)
    {
        if (loginGoogle == null || loginGoogle.getEmail() == null || loginGoogle.getName() == null
            || loginGoogle.getSurname() == null
            || loginGoogle.getToken() == null || loginGoogle.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to Create User : One or more value are empty or wrong type.");
        }
        Tuple<Long, String> return_token = accountApiService.loginWithGoogle(loginGoogle);
        if (return_token.getLeft() == -1)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Email are not verified.");
        if (return_token.getLeft() == -2)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Problem with the token.");
        if (return_token.getLeft() == -5)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Problem with the api google.");
        return ResponseEntity.ok().body(return_token);
    }
}
