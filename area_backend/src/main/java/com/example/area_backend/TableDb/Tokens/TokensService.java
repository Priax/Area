package com.example.area_backend.TableDb.Tokens;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.area_backend.Jwt;
import com.example.area_backend.TableDb.EnumRoles;
import com.example.area_backend.TableDb.Users.UsersTable;

@Service
public class TokensService
{
    private TokensTable tokensTable;
    private Optional<TokensTable> existingTokens;
    @Autowired
    private Jwt jwt;
    @Autowired
    private TokensRepo tokensRepo;

    public List<TokensTable> getAllTokens()
    {
        return this.tokensRepo.findAll();
    }

    public Optional<TokensTable> getTokenByToken(String access_token)
    {
        this.existingTokens = this.tokensRepo.findByAccessToken(access_token);
        if(this.existingTokens.isPresent()){
            return this.existingTokens;
        }
        System.err.println("Token: " + access_token +  " doesn't exist");
        return Optional.ofNullable(null);
    }

    public Optional<TokensTable> saveToken(TokensTable token)
    {
        UsersTable usersTable = token.getUserTable();
        if (usersTable == null || usersTable.getId() == null)
            return Optional.ofNullable(null);
        try {
            token.createToken(jwt);
            this.tokensTable = this.tokensRepo.save(token);
        } catch (Exception e) {
            System.err.println(e);
            return Optional.ofNullable(null);
        }

        System.out.println("Token with id: " + this.tokensTable.getId() +  " saved successfully");
        return Optional.of(this.tokensTable);
    }

    public Optional<TokensTable> updateToken(TokensTable token)
    {
        try {
            this.existingTokens = this.tokensRepo.findById(token.getId());
            if (!this.existingTokens.isPresent()) {
                System.err.println("Token with id: " + token.getId() +  " not found");
                return Optional.ofNullable(null);
            }
            this.tokensTable = this.tokensRepo.save(token);
        } catch (Exception e) {
            System.err.println(e);
            return Optional.ofNullable(null);
        }
        System.out.println("Token with id: " + this.tokensTable.getId() +  " updated successfully");
        return Optional.of(this.tokensTable);
    }

    public void deleteTokenByToken(String access_token)
    {
        try {
            this.tokensRepo.deleteByAccessToken(access_token);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void deleteTokenById(Long id)
    {
        try {
            this.tokensRepo.deleteById(id);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void deleteTokenByUserTable(UsersTable userTable)
    {
        try{
            Optional<List<TokensTable>> tableToken = this.tokensRepo.findByUsersTable(userTable);
            if (!tableToken.isPresent()){
                return;
            }
            List<TokensTable> listTableToken = tableToken.get();
            int size = listTableToken.size();
            for (int i = 0; i < size; i++){
                deleteTokenById(listTableToken.get(i).getId());
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public int isUserAuthorized(TokensTable token)
    {
        String access_token = token.getAccessToken();
        UsersTable usersTable = token.getUserTable();
        if (usersTable == null || usersTable.getId() == null) {
            return (0);
        }
        this.existingTokens = this.getTokenByToken(access_token);
        if (!this.existingTokens.isPresent()) {
            return (0);
        }
        if (!this.jwt.isTokenValid(access_token, usersTable.getId())) {
            return (0);
        }
        if (this.existingTokens.get().getUserTable().getRole().equals(EnumRoles.ADMIN)) {
            return (2);
        }
        return (1);
    }

    public boolean isTokenExpired(TokensTable token)
    {
        return (this.jwt.isTokenExpired(token.getAccessToken()));
    }
}
