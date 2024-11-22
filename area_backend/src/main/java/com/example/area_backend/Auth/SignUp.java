package com.example.area_backend.Auth;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.area_backend.Jwt;
import com.example.area_backend.TableDb.Credentials.CredentialsRepo;
import com.example.area_backend.TableDb.Credentials.CredentialsTable;
import com.example.area_backend.TableDb.EnumRoles;
import com.example.area_backend.TableDb.Tokens.TokensRepo;
import com.example.area_backend.TableDb.Tokens.TokensTable;
import com.example.area_backend.TableDb.Users.UsersRepo;
import com.example.area_backend.TableDb.Users.UsersTable;
import com.example.area_backend.Tuple;


@Component
public class SignUp
{
    @Autowired
    private UsersRepo userRepo;
    @Autowired
    private CredentialsRepo credentialsRepo;
    @Autowired
    private Jwt jwt;
    @Autowired
    private TokensRepo tokenRepo;

    public boolean isUserSignUp(String email)
    {
        Optional<UsersTable> allUsers = userRepo.findByEmail(email);
        return (allUsers.isPresent());
    }

    private boolean checkData(Auth auth)
    {
        if (auth.getDateOfBirth() == null || auth.getEmail() == null || auth.getGender() == null
        || auth.getName() == null || auth.getPassword() == null || auth.getPhoneNumber() == null
        || auth.getSurname() == null) {
            return (false);
        }
        try {
            LocalDate.parse(auth.getDateOfBirth());
        } catch (DateTimeParseException e) {
            return (false);
        }
        String emailRegex = "^[^@]+@[^@]+\\.[^@]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(auth.getEmail());
        if (!matcher.matches()) {
            return (false);
        }
        if (auth.getPassword().length() < 5) {
            return (false);
        }
        return (true);
    }

    public Tuple<Long, String> signUp(Auth auth)
    {
        if (!this.checkData(auth)) {
            return (new Tuple<>((long)-1, null));
        }
        if (this.isUserSignUp(auth.getEmail())) {
            return (new Tuple<>((long)-2, null));
        }
        UsersTable newUser = new UsersTable(
            null, auth.getName(), auth.getSurname(), auth.getEmail(), LocalDate.parse(auth.getDateOfBirth()),
            auth.getGender(), auth.getPhoneNumber(), EnumRoles.USER
        );
        UsersTable resultUser = this.userRepo.save(newUser);
        CredentialsTable newCredential = new CredentialsTable(auth.getEmail(), auth.getPassword(), resultUser);
        this.credentialsRepo.save(newCredential);
        TokensTable newToken = new TokensTable(newUser, jwt.generateToken(resultUser.getId()));
        this.tokenRepo.save(newToken);
        return (new Tuple<>(newUser.getId(), newToken.getAccessToken()));
    }
}
