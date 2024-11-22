package com.example.area_backend.Services.Spotify;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.area_backend.Constants;
import com.example.area_backend.Handler;
import com.example.area_backend.MainFunctions;
import com.example.area_backend.ParserJson;
import com.example.area_backend.TableDb.AccountApi.AccountApiService;
import com.example.area_backend.TableDb.AccountApi.AccountApiTable;
import com.example.area_backend.Tuple;

import jakarta.annotation.PostConstruct;

@Component
public class InitSpotify extends Handler
{
    @Autowired
    private final UserSpotify userSpotifyClass;

    @Autowired
    private final AccountApiService accountApiService;

    private final ParserJson parserJson;
    private final Constants globalVariable = new Constants();
    private final MainFunctions functions;

    @Autowired
    public InitSpotify(UserSpotify newUserSpotify, AccountApiService accountApiService)
    {
        this.functions = new MainFunctions();
        this.userSpotifyClass = newUserSpotify;
        this.accountApiService = accountApiService;
        this.parserJson = new ParserJson();
        Handler.userSpotify = this.userSpotifyClass;
    }

    @PostConstruct
    public void iniSpotify()
    {
        List<AccountApiTable> allAccountApi = this.accountApiService.getAllAccountApi();
        for (AccountApiTable accountApi : allAccountApi) {
            JSONObject credential = this.parserJson.parseToJson(accountApi.getCredentials());
            if (credential.has(globalVariable.ACCOUNT_API_CREDENTIAL_KEY_SPOTIFY)) {
                String userCredetialSpotify = credential.getString(globalVariable.ACCOUNT_API_CREDENTIAL_KEY_SPOTIFY);
                Tuple<String,String> tuple = this.functions.getTupleFromString(userCredetialSpotify);
                if (tuple == null) {
                    continue;
                }
                String authUrl = userSpotify.getAuthorizationUri(tuple.getLeft());
                System.out.println("Click on this url: " + authUrl);
            }
        }
    }
}