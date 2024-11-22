package com.example.area_backend.Auth;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.area_backend.Hash;
import com.example.area_backend.Jwt;
import com.example.area_backend.TableDb.Credentials.CredentialsRepo;
import com.example.area_backend.TableDb.Credentials.CredentialsTable;
import com.example.area_backend.TableDb.Tokens.TokensRepo;
import com.example.area_backend.TableDb.Tokens.TokensTable;
import com.example.area_backend.TableDb.Users.UsersRepo;
import com.example.area_backend.TableDb.Users.UsersTable;
import com.example.area_backend.Tuple;

@Component
public class Login
{
    @Autowired
    private UsersRepo userRepo;
    @Autowired
    private CredentialsRepo credentialsRepo;
    @Autowired
    private Jwt jwt;
    @Autowired
    private TokensRepo tokenRepo;

    public boolean isUserLogin(String userId)
    {
        Long userLongId;
        try {
            userLongId = Long.valueOf(userId);
        } catch (NumberFormatException e) {
            return (false);
        }
        Optional<UsersTable> user = userRepo.findById(userLongId);
        if (!user.isPresent()) {
            return (false);
        }
        Optional<List<TokensTable>> allTokens = tokenRepo.findByUsersTable(user.get());
        if (!allTokens.isPresent()) {
            return (false);
        }
        for (TokensTable token : allTokens.get()) {
            if (!jwt.isTokenExpired(token.getAccessToken())) {
                return (true);
            }
        }
        return (false);
    }

    public boolean isUserLogin(Long userId)
    {
        Optional<UsersTable> user = userRepo.findById(userId);
        if (!user.isPresent()) {
            return (false);
        }
        Optional<List<TokensTable>> allTokens = tokenRepo.findByUsersTable(user.get());
        if (!allTokens.isPresent()) {
            return (false);
        }
        for (TokensTable token : allTokens.get()) {
            if (!jwt.isTokenExpired(token.getAccessToken())) {
                return (true);
            }
        }
        return (false);
    }

    public boolean isUserSignUp(String email)
    {
        Optional<UsersTable> allUsers = userRepo.findByEmail(email);
        return (allUsers.isPresent());
    }

    public Tuple<Long, String> loginUser(String email, String password)
    {
        Optional<UsersTable> user = userRepo.findByEmail(email);
        if (!user.isPresent()) {
            return (new Tuple<>((long)-1, null));
        }
        Long userId = user.get().getId();
        Optional<CredentialsTable> cdt = credentialsRepo.findByUsersTable(user.get());
        if (!cdt.isPresent() || !cdt.get().getEmail().equals(email)) {
            return (new Tuple<>((long)-1, null));
        }
        Hash hash = new Hash();
        try {
            if (hash.isSame(password, cdt.get().getPassword(), cdt.get().getSalt())) {
                String new_access_token = jwt.generateToken(userId);
                tokenRepo.save(new TokensTable(user.get(), new_access_token));
                return (new Tuple<>(user.get().getId(), new_access_token));
            }
        } catch (NoSuchAlgorithmException e) {
            return (new Tuple<>((long)-2, null));
        }
        return (new Tuple<>((long)-2, null));
    }
}
