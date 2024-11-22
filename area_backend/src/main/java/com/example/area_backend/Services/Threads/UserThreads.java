package com.example.area_backend.Services.Threads;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.map.HashedMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.area_backend.Constants;
import com.example.area_backend.HandlingReaction;
import com.example.area_backend.ParserJson;
import com.example.area_backend.TableDb.EnumServices;
import com.example.area_backend.TableDb.AccountApi.AccountApiRepo;
import com.example.area_backend.TableDb.AccountApi.AccountApiTable;
import com.example.area_backend.TableDb.Actions.ActionsService;
import com.example.area_backend.TableDb.Actions.ActionsTable;
import com.example.area_backend.TableDb.Reactions.ReactionsService;
import com.example.area_backend.TableDb.Reactions.ReactionsTable;

@Component
@EnableScheduling
@EnableAsync
public class UserThreads {

    @Autowired
    private final AccountApiRepo apiRepo;

    @Autowired
    private final ActionsService actionsService;

    private final HandlingReaction handlingReaction;
    private final ReactionsService reactionsService;
    private final ParserJson parserJson;
    private final Constants globalVariable = new Constants();
    private Map<Long, Boolean> executed = new HashedMap<>();


    @Autowired
    public UserThreads(AccountApiRepo newApiRepo, ActionsService newActionsService, HandlingReaction handlingReaction,
        ReactionsService reactionsService)
    {
        this.apiRepo = newApiRepo;
        this.actionsService = newActionsService;
        this.handlingReaction = handlingReaction;
        this.reactionsService = reactionsService;
        this.parserJson = new ParserJson();
    }

    public UserThreads()
    {
        this.apiRepo = null;
        this.actionsService = null;
        this.handlingReaction = null;
        this.reactionsService = null;
        this.parserJson = new ParserJson();
    }

    private void checkFollowersActions(ActionsTable actionsTable)
    {
        Optional<AccountApiTable> apiTable = this.apiRepo.findByUsersTable(actionsTable.getUserTable());
        if (!apiTable.isPresent()) {
            return;
        }
        JSONObject credentials = this.parserJson.parseToJson(apiTable.get().getCredentials());
        if (!credentials.has("threads_id") || !credentials.has("threads_access_token")){
            return;
        }
        JSONObject valueAction = this.parserJson.parseToJson(actionsTable.getValues());
        String followerReach = valueAction.getString("Followers Number");
        int responseCode = 0;
        String publishUrl = "https://graph.threads.net/v1.0/" + credentials.getString("threads_id") + "/threads_insights";
        try {
            String data = "?metric=followers_count" +
                          "&access_token=" + credentials.getString("threads_access_token");
            URL url = new URL(publishUrl + data);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader responseMessage = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = responseMessage.readLine()) != null) {
                    response.append(inputLine);
                }
                responseMessage.close();
                String infosResponse = response.toString();
                JSONObject reponseInfosJson = this.parserJson.parseToJson(infosResponse);
                JSONArray dataArray = reponseInfosJson.getJSONArray("data");
                JSONObject dataObject = dataArray.getJSONObject(0);
                int followers_count = dataObject.getJSONObject("total_value").getInt("value");
                if (followers_count < Integer.parseInt(followerReach)){
                    executed.put(actionsTable.getId(), false);
                    return;
                }
                if (executed.get(actionsTable.getId()).equals(true)){
                    return;
                }
                List<ReactionsTable> listReactions = reactionsService.getReactionsByActionId(actionsTable);
                Map<String, Object> dataMap = new HashedMap<>();
                Map<String, String> arguments = new HashedMap<>();
                arguments.put("$ThreadFollowersNumberReach", followerReach);
                dataMap.put(this.globalVariable.KEY_VARIABLE_ARGUMENTS, arguments);
                this.handlingReaction.doReaction(listReactions, dataMap);
                executed.put(actionsTable.getId(), true);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error with Threads api (check token and user id).");
            return;
        }
        System.out.println("Error with Threads api (check token and user id).");
        System.out.println(responseCode);
    }

    @Async
    @Scheduled(fixedRate = 60000)
    public void checkNewFollower()
    {
        System.out.println("Je check les followers");
        List<ActionsTable> listActionTable = this.actionsService.getAllActions();
        int size = listActionTable.size();
        for (int i = 0; i < size; i++){
            JSONObject actionValue = this.parserJson.parseToJson(listActionTable.get(i).getValues());
            if (listActionTable.get(i).getService().equals(EnumServices.THREADS) && actionValue.getString("Action").equals("Reach Followers")){
                if (!executed.containsKey(listActionTable.get(i).getId())){
                    executed.put(listActionTable.get(i).getId(), false);
                }
                checkFollowersActions(listActionTable.get(i));
            }
        }
    }

}