package com.example.area_backend.TableDb.AccountApi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.area_backend.Auth.Login;
import com.example.area_backend.Auth.LoginGoogle;
import com.example.area_backend.Constants;
import com.example.area_backend.Jwt;
import com.example.area_backend.MainFunctions;
import com.example.area_backend.ParserJson;
import com.example.area_backend.TableDb.EnumRoles;
import com.example.area_backend.TableDb.EnumServices;
import com.example.area_backend.TableDb.Tokens.TokensRepo;
import com.example.area_backend.TableDb.Tokens.TokensTable;
import com.example.area_backend.TableDb.Users.UsersRepo;
import com.example.area_backend.TableDb.Users.UsersTable;
import com.example.area_backend.Tuple;


@Service
public class AccountApiService {

    private final AccountApiRepo accountApiRepo;
    @Autowired
    private UsersRepo userRepo;
    @Autowired
    private Login login;
    @Autowired
    private TokensRepo tokenRepo;
    @Autowired
    private Jwt jwt;
    private final Constants constants = new Constants();
    private final ParserJson parserJson;
    private final MainFunctions function;

    @Autowired
    public AccountApiService(AccountApiRepo accountApiRepo)
    {
        this.accountApiRepo = accountApiRepo;
        this.parserJson = new ParserJson();
        this.function = new MainFunctions();
    }

    public AccountApiService()
    {
        this.accountApiRepo = null;
        this.parserJson = new ParserJson();
        this.function = new MainFunctions();
    }

    public List<AccountApiTable> getAllAccountApi()
    {
        return this.accountApiRepo.findAll();
    }

    public Tuple<Long, String> loginWithGoogle(LoginGoogle loginGoogle)
    {
        if (loginGoogle.getVerifiedEmail() == false){
            return (new Tuple<>((long)-1, null));
        }
        try {
            URL url = new URL("https://oauth2.googleapis.com/tokeninfo?access_token=" + loginGoogle.getToken());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader responseMessage = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = responseMessage.readLine()) != null) {
                    response.append(inputLine);
                }
                responseMessage.close();
                JSONObject jsonResponse = new JSONObject(response.toString());
                if (!loginGoogle.getEmail().equals(jsonResponse.getString("email"))
                    || !loginGoogle.getId().equals(jsonResponse.getString("sub"))) {
                    return new Tuple<>((long) -2, null);
                }
            } else {
                return new Tuple<>((long) -2, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Tuple<>((long) -5, null);
        }
        UsersTable resultUser;
        if (login.isUserSignUp(loginGoogle.getEmail()) == false) {
            UsersTable newUser = new UsersTable(
                null, loginGoogle.getName(), loginGoogle.getSurname(),
                loginGoogle.getEmail(), null,
                null, null, EnumRoles.USER
            );
            resultUser = this.userRepo.save(newUser);
        } else {
            resultUser = userRepo.findByEmail(loginGoogle.getEmail()).get();
        }
        String new_access_token = jwt.generateToken(resultUser.getId());
        tokenRepo.save(new TokensTable(resultUser, new_access_token));
        return (new Tuple<>(resultUser.getId(), new_access_token));
    }

    public Tuple<Long, String> loginWithThreads(String accessToken, String threadsID, String userID)
    {
        if (accessToken == null || threadsID == null) {
            return (new Tuple<>((long) -3, null));
        }
        Long id;
        try {
            id = Long.valueOf(userID);
        } catch (NumberFormatException e) {
            return (new Tuple<>((long) -1, null));
        }
        Optional<UsersTable> userTest = userRepo.findById(id);
        if (!userTest.isPresent()) {
            return (new Tuple<>((long) -2, null));
        }
        UsersTable resultUser = userTest.get();
        AccountApiTable resultAccount;
        Optional<AccountApiTable> checkAccount = accountApiRepo.findByUsersTable(resultUser);
        if (!checkAccount.isPresent()){
            AccountApiTable newAccount = new AccountApiTable(
                null, resultUser,
                ("{\"threads_id\": \"" + threadsID + "\", \"threads_access_token\": \"" + accessToken + "\"}")
            );
            this.accountApiRepo.save(newAccount);
        } else {
            resultAccount = checkAccount.get();
            JSONObject credential = new ParserJson().parseToJson(resultAccount.getCredentials());
            if (credential.has("threads_id")){
                credential.remove("threads_id");
            }
            credential.put("threads_id", threadsID);
            if (credential.has("threads_access_token")){
                credential.remove("threads_access_token");
            }
            credential.put("threads_access_token", accessToken);
            String credentialStr = new ParserJson().parseToString(credential);
            resultAccount.setCredentials(credentialStr);
            this.accountApiRepo.save(resultAccount);
        }
        return (new Tuple<>((long) 0, "Api added"));
    }

    public void deleteAccountApiByID(UsersTable tableUser)
    {
        Optional<AccountApiTable> tableAccount = this.accountApiRepo.findByUsersTable(tableUser);
        if (!tableAccount.isPresent())
            return;
        this.accountApiRepo.delete(tableAccount.get());
    }

    public Tuple<String, String> getCredentialFromUser(EnumServices service, UsersTable user, String id, String realId)
    {
        Optional<AccountApiTable> optionalAccountApiTable = this.accountApiRepo.findByUsersTable(user);
        if (optionalAccountApiTable.isEmpty()) {
            System.err.println("Account api table for user = " + user.getId() + " not found.");
            return null;
        }

        AccountApiTable accountApiTable = optionalAccountApiTable.get();
        JSONObject credentialUser = this.parserJson.parseToJson(accountApiTable.getCredentials());
        List<Tuple<String, String>> allEmailAssigned;
        Object value;
        switch (service) {
            case GMAIL -> {
                if (credentialUser.has(this.constants.ACCOUNT_API_CREDENTIAL_KEY_GMAIL)) {
                    value = credentialUser.get(this.constants.ACCOUNT_API_CREDENTIAL_KEY_GMAIL);
                } else {
                    value = null;
                }
            }
            case SPOTIFY -> {
                if (credentialUser.has(this.constants.ACCOUNT_API_CREDENTIAL_KEY_SPOTIFY)) {
                    value = credentialUser.get(this.constants.ACCOUNT_API_CREDENTIAL_KEY_SPOTIFY);
                } else {
                    value = null;
                }
            }
            default -> {
                System.err.println("Service not monitored");
                return (null);
            }
        }
        allEmailAssigned = this.function.getListOfTupleFromObject(value);
        if (allEmailAssigned == null) {
            System.err.println("Failed to convert Object to List<Tuple>");
            return null;
        }
        for (Tuple<String, String> elm : allEmailAssigned) {
            if (elm.getLeft().equals(id) && elm.getRight().equals(realId)) {
                return (elm);
            }
        }
        return (null);
    }

    public boolean loginWithGmail(String gmailId, String realGmailId, UsersTable user)
    {
        AccountApiTable accountApiTable;
        JSONObject credentialUser;
        Optional<AccountApiTable> optionalAccountApiTable = this.accountApiRepo.findByUsersTable(user);
        if (optionalAccountApiTable.isEmpty()) {
            accountApiTable = new AccountApiTable(null, user, null);
            credentialUser = new JSONObject();
        } else {
            accountApiTable = optionalAccountApiTable.get();
            credentialUser = this.parserJson.parseToJson(accountApiTable.getCredentials());

        }
        List<Tuple<String, String>> allEmailAssigned;
        if (credentialUser.has(this.constants.ACCOUNT_API_CREDENTIAL_KEY_GMAIL)) {
            Object value = credentialUser.get(this.constants.ACCOUNT_API_CREDENTIAL_KEY_GMAIL);
            allEmailAssigned = this.function.getListOfTupleFromObject(value);
            if (allEmailAssigned == null) {
                System.err.println("Failed to convert Object to List<Tuple>");
                return false;
            }
            if (this.isEmailAlreadyAssigned(allEmailAssigned, gmailId, realGmailId)) {
                    System.out.println("ID = " + gmailId + ", Email = " + realGmailId + " already exist in credentials.");
                    return false;
                }
        } else {
            allEmailAssigned = new ArrayList<>();
        }
        return saveNewEmailCredentials(accountApiTable, credentialUser, allEmailAssigned, gmailId, realGmailId);
    }

 public boolean logoutWithGmail(String userId, UsersTable user)
    {
        AccountApiTable accountApiTable;
        JSONObject credentialUser;
        Optional<AccountApiTable> optionalAccountApiTable = this.accountApiRepo.findByUsersTable(user);
        if (!optionalAccountApiTable.isPresent()) {
            System.err.println("User not found in database");
            return (false);
        }
        accountApiTable = optionalAccountApiTable.get();
        credentialUser = this.parserJson.parseToJson(accountApiTable.getCredentials());
        if (!credentialUser.has(this.constants.ACCOUNT_API_CREDENTIAL_KEY_GMAIL)) {
            System.out.println("User not connected to Gmail");
            return (false);
        }
        Object value = credentialUser.get(this.constants.ACCOUNT_API_CREDENTIAL_KEY_GMAIL);
        List<Tuple<String, String>> allEmailAssigned = this.function.getListOfTupleFromObject(value);
        if (allEmailAssigned == null) {
            System.err.println("Failed to convert Object to List<Tuple>");
            return (false);
        }
        int size = allEmailAssigned.size();
        boolean isSup = false;
        for (int i = 0; i < size; i++) {
            if (allEmailAssigned.get(i).getLeft().equals(userId)) {
                allEmailAssigned.remove(i);
                isSup = true;
                break;
            }
        }
        if (!isSup) {
            System.err.println("Account with userID: " + userId + " not found in database.");
            return (false);
        }
        credentialUser.put(this.constants.ACCOUNT_API_CREDENTIAL_KEY_GMAIL, this.function.transformListOfTupleToListOfString(allEmailAssigned));
        accountApiTable.setCredentials(this.parserJson.parseToString(credentialUser));
        this.accountApiRepo.save(accountApiTable);
        return (true);
    }


    public boolean loginWithSpotify(String userId, UsersTable userTable)
    {
        Optional<AccountApiTable> optionalAccountApiTable = this.accountApiRepo.findByUsersTable(userTable);
        AccountApiTable accountApiTable;
        JSONObject credentialUser;
        if (!optionalAccountApiTable.isEmpty()) {
            accountApiTable = optionalAccountApiTable.get();
            credentialUser = this.parserJson.parseToJson(accountApiTable.getCredentials());
        } else {
            accountApiTable = new AccountApiTable(null, userTable, null);
            credentialUser = new JSONObject();
        }

        if (credentialUser.has(this.constants.ACCOUNT_API_CREDENTIAL_KEY_SPOTIFY)) {
            System.err.println("Account api table for user already saved");
            return false;
        }
        credentialUser.put(this.constants.ACCOUNT_API_CREDENTIAL_KEY_SPOTIFY, this.function.getStringFromTuple(new Tuple<>(userTable.getId().toString(), userId)));
        accountApiTable.setCredentials(this.parserJson.parseToString(credentialUser));
        this.accountApiRepo.save(accountApiTable);
        return (true);
    }

    public boolean logoutWithSpotify(String userId, UsersTable userTable)
    {
        AccountApiTable accountApiTable;
        JSONObject credentialUser;
        Optional<AccountApiTable> optionalAccountApiTable = this.accountApiRepo.findByUsersTable(userTable);
        if (!optionalAccountApiTable.isPresent()) {
            System.err.println("User not found in database");
            return (false);
        }
        accountApiTable = optionalAccountApiTable.get();
        credentialUser = this.parserJson.parseToJson(accountApiTable.getCredentials());
        if (!credentialUser.has(this.constants.ACCOUNT_API_CREDENTIAL_KEY_SPOTIFY)) {
            System.out.println("User not connected to Gmail");
            return (false);
        }
        String value = credentialUser.getString(this.constants.ACCOUNT_API_CREDENTIAL_KEY_SPOTIFY);
        Tuple<String, String> accountAssigned = this.function.getTupleFromString(value);
        if (accountAssigned == null) {
            System.err.println("Failed to convert String to Tuple");
            return (false);
        }
        if (!accountAssigned.getLeft().equals(userId)) {
            System.err.println("Account with userID: " + userId + " not found in database.");
            return (false);
        }
        credentialUser.remove(this.constants.ACCOUNT_API_CREDENTIAL_KEY_SPOTIFY);
        accountApiTable.setCredentials(this.parserJson.parseToString(credentialUser));
        this.accountApiRepo.save(accountApiTable);
        return (true);
    }

    public Tuple<Long, String> logoutWithThreads(String userID)
    {
        Long id;
        try {
            id = Long.valueOf(userID);
        } catch (NumberFormatException e) {
            return (new Tuple<>((long) -1, null));
        }
        Optional<UsersTable> userTest = userRepo.findById(id);
        if (!userTest.isPresent()) {
            return (new Tuple<>((long) -2, null));
        }
        UsersTable resultUser = userTest.get();
        Optional<AccountApiTable> checkAccount = accountApiRepo.findByUsersTable(resultUser);
        if (!checkAccount.isPresent()){
            return (new Tuple<>((long) -3, null));
        }
        AccountApiTable resultAccount = checkAccount.get();
        JSONObject credential = new ParserJson().parseToJson(resultAccount.getCredentials());
        credential.remove("threads_id");
        credential.remove("threads_access_token");
        String credentialStr = new ParserJson().parseToString(credential);
        resultAccount.setCredentials(credentialStr);
        this.accountApiRepo.save(resultAccount);
        return (new Tuple<>((long) 0, null));
    }

    private boolean isEmailAlreadyAssigned(
        List<Tuple<String, String>> emails,
        String gmailId,
        String realGmailId
    ) {
        return emails.stream()
            .anyMatch(tuple -> tuple.getRight().equals(gmailId) || tuple.getRight().equals(realGmailId));
    }

    private boolean saveNewEmailCredentials(AccountApiTable accountApiTable,
                                          JSONObject credentialUser,
                                          List<Tuple<String, String>> allEmailAssigned,
                                          String gmailId,
                                          String realGmailId) {
        try {
            allEmailAssigned.add(new Tuple<>(gmailId, realGmailId));
            credentialUser.put(this.constants.ACCOUNT_API_CREDENTIAL_KEY_GMAIL, this.function.transformListOfTupleToListOfString(allEmailAssigned));
            accountApiTable.setCredentials(this.parserJson.parseToString(credentialUser));
            this.accountApiRepo.save(accountApiTable);
            return (true);
        } catch (JSONException e) {
            System.err.println("Error saving new email credentials: " + e.getMessage());
            return (false);
        }
    }
}
