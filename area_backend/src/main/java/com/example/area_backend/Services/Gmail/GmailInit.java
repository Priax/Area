package com.example.area_backend.Services.Gmail;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import com.example.area_backend.Constants;
import com.example.area_backend.Handler;
import com.example.area_backend.MainFunctions;
import com.example.area_backend.ParserJson;
import com.example.area_backend.TableDb.AccountApi.AccountApiService;
import com.example.area_backend.TableDb.AccountApi.AccountApiTable;
import com.example.area_backend.TableDb.Actions.ActionsService;
import com.example.area_backend.TableDb.Reactions.ReactionsService;
import com.example.area_backend.TableDb.Users.UsersRepo;
import com.example.area_backend.Tuple;

import jakarta.annotation.PostConstruct;

@Component
@EnableAsync
public class GmailInit extends Handler
{
    @Autowired
    private final AccountApiService apiService;

    @Autowired
    private final UsersRepo usersRepo;

    @Autowired
    private final ActionsService actionsService;

    @Autowired
    private final ReactionsService reactionsService;

    private final MainFunctions functions;

    @Autowired
    public GmailInit(AccountApiService apiService, ActionsService actionsService, ReactionsService reactionsService, UsersRepo usersRepo)
    {
        this.apiService = apiService;
        this.usersRepo = usersRepo;
        Handler.gmailService = new GmailService(apiService, this.usersRepo);
        this.functions = new MainFunctions();
        this.actionsService = actionsService;
        this.reactionsService = reactionsService;
        Handler.gmailListener = new GmailListener(/*"area-oauth-438008", "notification_sub_gmail", new GmailAction(actionsService, reactionsService)*/);
    }

    public GmailInit()
    {
        this.functions = new MainFunctions();
        this.apiService = null;
        this.actionsService = null;
        this.reactionsService = null;
        this.usersRepo = null;
        Handler.gmailService = new GmailService(this.apiService, this.usersRepo);
        Handler.gmailListener = new GmailListener(/*"area-oauth-438008", "notification_sub_gmail", new GmailAction(null, null)*/);
    }

    @PostConstruct
    @Async
    public void initGmailApi()
    {
        List<AccountApiTable> allApiTable = this.apiService.getAllAccountApi();
        ParserJson parserJson = new ParserJson();
        Constants globalVariable = new Constants();
        for (AccountApiTable apiTable : allApiTable) {
            JSONObject credential = parserJson.parseToJson(apiTable.getCredentials());
            if (credential.has(globalVariable.ACCOUNT_API_CREDENTIAL_KEY_GMAIL)) {
                List<Tuple<String, String>> allAccountFromUser = this.functions.getListOfTupleFromObject(credential.get(globalVariable.ACCOUNT_API_CREDENTIAL_KEY_GMAIL));
                if (allAccountFromUser == null) {
                    continue;
                }
                for (Tuple<String, String> account : allAccountFromUser) {
                    if (!Handler.gmailService.isUserAuthorized(account.getRight())) {
                        String authUrl = Handler.gmailService.getAuthorizationUrl(apiTable.getUsersTable().getId().toString(), account.getLeft());
                        System.out.println("Click on this url: " + authUrl);
                    }
                }
            }
            // else {
            //     try {
            //         this.gmailListener.addGmailAccount(gmailService.getGmailService(apiTable.getEmail()));
            //     } catch (Exception e) {
            //         System.err.println("Failed to add email: \"" + apiTable.getEmail() + "\" in Gmail Listener: " + e);
            //     }
            // }
        }
    }
}
