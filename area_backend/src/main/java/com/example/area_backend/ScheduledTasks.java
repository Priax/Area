package com.example.area_backend;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.area_backend.TableDb.Tokens.TokensService;
import com.example.area_backend.TableDb.Tokens.TokensTable;

@EnableScheduling
@Component
public class ScheduledTasks
{
    @Autowired
    private TokensService tokensService;

    @Scheduled(fixedRateString = "${spring.security.jwt.expirationTime}")
    public void removeTokenExpired()
    {
        System.out.println("Beginning of process to delete tokens which are expired");
        List<TokensTable> allTokens = tokensService.getAllTokens();
        for (TokensTable token : allTokens) {
            if (tokensService.isTokenExpired(token)) {
                System.out.println("Token: \"" + token.getAccessToken() + "\" belong to user ID: \"" + token.getUserTable().getId() + "\" deleted.");
                tokensService.deleteTokenById(token.getId());
            }
        }
    }
}
