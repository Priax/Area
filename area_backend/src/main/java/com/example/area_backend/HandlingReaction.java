package com.example.area_backend;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.area_backend.Services.Discord.DiscordReactions;
import com.example.area_backend.Services.Gmail.GmailReactions;
import com.example.area_backend.Services.Riot.LeagueService;
import com.example.area_backend.Services.Riot.LeagueService.Account;
import com.example.area_backend.Services.Riot.LeagueService.LeagueEntry;
import com.example.area_backend.Services.Riot.LeagueService.MatchDetail;
import com.example.area_backend.Services.Riot.LeagueService.Summoner;
import com.example.area_backend.Services.Spotify.SpotifyReaction;
import com.example.area_backend.Services.Threads.ThreadsReactions;
import com.example.area_backend.Services.osu.OsuService;
import com.example.area_backend.Services.osu.OsuService.Beatmap;
import com.example.area_backend.Services.osu.OsuService.Beatmapset;
import com.example.area_backend.Services.osu.OsuService.BeatmapsetData;
import com.example.area_backend.Services.osu.OsuService.Level;
import com.example.area_backend.Services.osu.OsuService.Statistics;
import com.example.area_backend.Services.osu.OsuService.User;
import com.example.area_backend.TableDb.AccountApi.AccountApiRepo;
import com.example.area_backend.TableDb.AccountApi.AccountApiTable;
import com.example.area_backend.TableDb.Reactions.ReactionsTable;

@Component
public class HandlingReaction extends Handler
{
    @Autowired
    private final AccountApiRepo apiRepo;

    @Autowired
    private final SpotifyReaction spotifyReaction;

    private final ParserJson parserJson;

    @Autowired
    private final OsuService osuService;

    @Autowired
    private final LeagueService leagueService;

    private final Constants globalVariable = new Constants();

    private final MainFunctions functions;

    @Autowired
    public HandlingReaction(AccountApiRepo newApiRepo, OsuService osuService, LeagueService leagueService, SpotifyReaction spotifyReaction)
    {
        this.apiRepo = newApiRepo;
        this.osuService = osuService;
        this.leagueService = leagueService;
        this.spotifyReaction = spotifyReaction;
        this.parserJson = new ParserJson();
        this.functions = new MainFunctions();
    }

    public HandlingReaction()
    {
        this.apiRepo = null;
        this.osuService = null;
        this.leagueService = null;
        this.parserJson = new ParserJson();
        this.functions = new MainFunctions();
        this.spotifyReaction = new SpotifyReaction();
    }

    private Map<String, String> doReactionGmail(ReactionsTable reaction, Map<String, String> arguments)
    {
        Optional<AccountApiTable> apiTable = this.apiRepo.findByUsersTable(reaction.getActionTable().getUserTable());
        if (!apiTable.isPresent()) {
            System.err.println("User Email not found in database");
            return arguments;
        }
        String mainReaction;
        Object listOfAccount;
        List<String> listStringOfAccount;
        JSONObject jsonCredential = this.parserJson.parseToJson(apiTable.get().getCredentials());
        if (jsonCredential == null || !jsonCredential.has(globalVariable.ACCOUNT_API_CREDENTIAL_KEY_GMAIL)) {
            System.err.println("User Credentials not found in database");
            return arguments;
        }
        Object credentials = jsonCredential.get(globalVariable.ACCOUNT_API_CREDENTIAL_KEY_GMAIL);
        List<Tuple<String, String>> userGmailId = this.functions.getListOfTupleFromObject(credentials);
        if (userGmailId == null) {
            System.err.println("Failed to convert Object to List of Tuple");
            return arguments;
        }
        JSONObject jsonValues;
        GmailReactions gmailReactions = new GmailReactions();
        Optional<Map<String, String>> maybeArgument;
        jsonValues = this.parserJson.parseToJson(reaction.getValues());
        if (jsonValues == null) {
            return arguments;
        }
        try {
            mainReaction = jsonValues.getString("Reaction");
            listOfAccount = jsonValues.get("Accounts");
        } catch (JSONException e) {
            System.err.println("No key \"Reaction\" or \"Accounts\"");
            return arguments;
        }
        listStringOfAccount = this.functions.getListOfStringFromObject(listOfAccount);
        if (listStringOfAccount == null) {
            System.err.println("Failed to parse Object into List of string");
            return arguments;
        }
        for (Tuple<String, String> accounts : userGmailId) {
            if (!listStringOfAccount.contains(accounts.getLeft())) {
                continue;
            }
            if (!Handler.gmailService.isUserAuthorized(accounts.getLeft())) {
                System.err.println("User with email adress " + accounts.getLeft() + " not authorized");
                return arguments;
            }
            switch (mainReaction) {
                case "Send Email"-> {
                    maybeArgument = gmailReactions.sendMessage(Handler.gmailService.getGmailService(accounts.getLeft()), jsonValues, arguments);
                    if (!maybeArgument.isPresent()) {
                        System.err.println("Failed to do reaction Send Email");
                    } else {
                        arguments = maybeArgument.get();
                    }
                }
                case "Forward Email" -> {
                    maybeArgument = gmailReactions.transfertMessage(Handler.gmailService.getGmailService(accounts.getLeft()), jsonValues, arguments);
                    if (!maybeArgument.isPresent()) {
                        System.err.println("Failed to do reaction Forward Email");
                    } else {
                        arguments = maybeArgument.get();
                    }
                }
                default -> {
                    System.err.println("Reaction " + mainReaction + "doesn't exist.");
                }
            }
        }
        return (arguments);
    }

    public Map<String, String> doReactionSpotify(ReactionsTable reaction, Map<String, Object> dataMap, Map<String, String> arguments)
    {
        Optional<AccountApiTable> apiTable = this.apiRepo.findByUsersTable(reaction.getActionTable().getUserTable());
        if (!apiTable.isPresent()) {
            System.err.println("User Credentials not found in database");
            return arguments;
        }
        JSONObject jsonCredentials = this.parserJson.parseToJson(apiTable.get().getCredentials());
        if (jsonCredentials == null || !jsonCredentials.has(globalVariable.ACCOUNT_API_CREDENTIAL_KEY_SPOTIFY)) {
            System.err.println("User Credentials not found in database");
            return arguments;
        }
        String userId = jsonCredentials.getString(globalVariable.ACCOUNT_API_CREDENTIAL_KEY_SPOTIFY);
        Tuple<String, String> allUserId = this.functions.getTupleFromString(userId);
        String mainReaction;
        JSONObject jsonValues;
        Optional<Map<String, String>> maybeArgument;
        jsonValues = this.parserJson.parseToJson(reaction.getValues());
        if (jsonValues == null) {
            return arguments;
        }
        try {
            mainReaction = jsonValues.getString("Reaction");
        } catch (JSONException e) {
            System.err.println("No key \"Reaction\"");
            return arguments;
        }
        switch (mainReaction) {
            case "Create Playlist"-> {
                maybeArgument = spotifyReaction.createPlaylist(allUserId.getLeft(), jsonValues, arguments);
                if (!maybeArgument.isPresent()) {
                    System.err.println("Failed to do reaction Create Playlist");
                } else {
                    arguments = maybeArgument.get();
                }
            }
            case "Add Music In Playlist" -> {
                maybeArgument = spotifyReaction.addItemToPlaylist(allUserId.getLeft(), jsonValues, arguments);
                if (!maybeArgument.isPresent()) {
                    System.err.println("Failed to do reaction Add Music In Playlist");
                } else {
                    arguments = maybeArgument.get();
                }
            }
            default -> {
                System.err.println("Reaction " + mainReaction + "doesn't exist.");
            }
        }
        return (arguments);
    }

    private Map<String, String> doReactionDiscord(ReactionsTable reaction, Map<String, Object> dataMap, Map<String, String> arguments)
    {
        String mainReaction;
        JSONObject jsonValues;
        DiscordReactions discordReaction = new DiscordReactions();
        Optional<Map<String, String>> maybeArgument;
        jsonValues = this.parserJson.parseToJson(reaction.getValues());
        if (jsonValues == null) {
            return arguments;
        }
        try {
            mainReaction = jsonValues.getString("Reaction");
        } catch (JSONException e) {
            System.err.println("No key \"Reaction\"");
            return arguments;
        }
        switch (mainReaction) {
            case "Send Message"-> {
                maybeArgument = discordReaction.sendMessage(jsonValues, arguments);
                if (!maybeArgument.isPresent()) {
                    System.err.println("Failed to do reaction Send Message");
                } else {
                    arguments = maybeArgument.get();
                }
            }
            case "React Message" -> {
                maybeArgument = discordReaction.reactMessage(jsonValues, dataMap, arguments);
                if (!maybeArgument.isPresent()) {
                    System.err.println("Failed to do reaction React Message");
                } else {
                    arguments = maybeArgument.get();
                }
            }
            case "Ban" -> {
                maybeArgument = discordReaction.ban(jsonValues, arguments);
                if (!maybeArgument.isPresent()) {
                    System.err.println("Failed to do reaction Ban");
                } else {
                    arguments = maybeArgument.get();
                }
            }
            case "Create Channel" -> {
                maybeArgument = discordReaction.createChannel(jsonValues, arguments);
                if (!maybeArgument.isPresent()) {
                    System.err.println("Failed to do reaction Create Channel");
                } else {
                    arguments = maybeArgument.get();
                }
            }
            default -> {
                System.err.println("Reaction " + mainReaction + "doesn't exist.");
            }
        }
        return (arguments);
    }

    private Map<String, String> convertOsuToMap(Object maybeArgument, Map<String, String> arguments) {
        if (maybeArgument instanceof User user) {
            arguments.put("$username", user.username);
            arguments.put("$id", String.valueOf(user.id));
            arguments.put("$countryCode", user.countryCode);

            Statistics stats = user.statistics;
            arguments.put("$globalRank", String.valueOf(stats.globalRank));
            arguments.put("$countryRank", String.valueOf(stats.countryRank));
            arguments.put("$pp", String.valueOf(stats.pp));
            arguments.put("$hitAccuracy", String.valueOf(stats.hitAccuracy));

            Level level = stats.level;
            arguments.put("$levelCurrent", String.valueOf(level.current));
            arguments.put("$levelProgress", String.valueOf(level.progress));
        } else if (maybeArgument instanceof Beatmapset beatmapset) {
            arguments.put("$beatmapId", String.valueOf(beatmapset.beatmapId));
            arguments.put("$count", String.valueOf(beatmapset.count));

            if (beatmapset.beatmapsetData != null) {
                BeatmapsetData data = beatmapset.beatmapsetData;
                arguments.put("$title", data.title);
                arguments.put("$artist", data.artist);
                arguments.put("$creator", data.creator);
                arguments.put("$status", data.status);
                arguments.put("$playCount", data.play_count);
            }
        } else {
            System.err.println("Unsupported object type for conversion to Map<String, String>.");
        }
        return arguments;
    }

    private Map<String, String> doReactionOsu(ReactionsTable reaction, Map<String, String> arguments) {
        String mainReaction;
        JSONObject jsonValues;
        Object maybeArgument;

        jsonValues = this.parserJson.parseToJson(reaction.getValues());
        if (jsonValues == null) {
            return arguments;
        }
        try {
            mainReaction = jsonValues.getString("Reaction");
        } catch (JSONException e) {
            System.err.println("No key \"Reaction\"");
            return arguments;
        }
        switch (mainReaction) {
            case "Get Osu User" -> {
                try {
                    maybeArgument = osuService.getUserData(jsonValues.getString("Id"));
                    arguments = convertOsuToMap(maybeArgument, arguments);
                } catch (Exception e) {
                    System.err.println("Failed to retrieve OSU user data: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "Get User Beatmaps" -> {
                try {
                    String userId = jsonValues.getString("Id");
                    String type = jsonValues.optString("type", "most_played");
                    int limit = jsonValues.optInt("limit", 20);
                    int offset = jsonValues.optInt("offset", 1);

                    List<Beatmapset> beatmapsets = osuService.getUserBeatmaps(Long.parseLong(userId), type, limit, offset);

                    Map<String, String> userBeatmapsMap = new HashMap<>();

                    JSONArray beatmapsetsArray = new JSONArray();

                    for (Beatmapset beatmapset : beatmapsets) {
                        JSONObject beatmapsetObject = new JSONObject();
                        beatmapsetObject.put("$count", beatmapset.count);

                        BeatmapsetData data = beatmapset.beatmapsetData;
                        beatmapsetObject.put("$title", data.title);
                        beatmapsetObject.put("$artist", data.artist);
                        beatmapsetObject.put("$artist_unicode", data.artistUnicode);
                        beatmapsetObject.put("$play_count", data.play_count);
                        beatmapsetObject.put("$status", data.status);
                        beatmapsetObject.put("$creator", data.creator);
                        beatmapsetObject.put("$id", data.id);

                        JSONArray beatmapsArray = new JSONArray();
                        for (Beatmap beatmap : beatmapset.beatmaps) {
                            JSONObject beatmapObject = new JSONObject();
                            beatmapObject.put("$difficulty_rating", beatmap.difficultyRating);
                            beatmapObject.put("$id", beatmap.id);
                            beatmapObject.put("$mode", beatmap.mode);
                            beatmapObject.put("$status", beatmap.status);
                            beatmapObject.put("$total_length", beatmap.totalLength);
                            beatmapObject.put("$user_id", beatmap.userId);
                            beatmapObject.put("$version", beatmap.version);
                            beatmapsArray.put(beatmapObject);
                        }
                        beatmapsetObject.put("$beatmaps", beatmapsArray);
                        beatmapsetsArray.put(beatmapsetObject);
                    }
                    userBeatmapsMap.put("$beatmapsets", beatmapsetsArray.toString());
                    arguments.putAll(userBeatmapsMap);

                } catch (Exception e) {
                    System.err.println("Failed to retrieve OSU user beatmaps: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            default -> {
                System.err.println("Reaction " + mainReaction + " doesn't exist.");
            }
        }
        System.err.println(arguments);
        return arguments;
    }

    public static Map<String, String> convertToMap(Object obj) {
        Map<String, String> map = new HashMap<>();
        if (obj == null) return map;

        Class<?> objClass = obj.getClass();

        for (Field field : objClass.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);

                if (value instanceof List) {
                    JSONArray jsonArray = new JSONArray();
                    List<?> list = (List<?>) value;
                    for (Object listItem : list) {
                        jsonArray.put(convertToMap(listItem));
                    }
                    map.put("$" + field.getName(), jsonArray.toString());
                } else if (value != null) {
                    map.put("$" + field.getName(), value.toString());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return map;
    }

    public static Map<String, String> convertToMap(Object obj, String prefix) {
        Map<String, String> map = new HashMap<>();
        if (obj == null) return map;

        Class<?> objClass = obj.getClass();

        for (Field field : objClass.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value != null) {
                    map.put("$" + prefix + "_" + field.getName(), value.toString());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static Map<String, String> convertMatchDetailToMap(MatchDetail matchDetail) {
        Map<String, String> map = new HashMap<>();
        if (matchDetail == null) return map;

        map.put("$matchId", matchDetail.matchId);
        map.put("$gameMode", matchDetail.gameMode);
        map.put("$gameType", matchDetail.gameType);
        map.put("$gameVersion", matchDetail.gameVersion);
        map.put("$gameCreation", String.valueOf(matchDetail.gameCreation));
        map.put("$gameDuration", String.valueOf(matchDetail.gameDuration));
        map.put("$endOfGameResult", matchDetail.endOfGameResult);

        String participantIds = String.join(",", matchDetail.participantIds);
        map.put("$participantIds", participantIds);

        JSONArray participantsArray = new JSONArray();
        for (MatchDetail.Participant participant : matchDetail.participants) {
            participantsArray.put(convertParticipantToMap(participant));
        }
        map.put("$participants", participantsArray.toString());

        return map;
    }

    private static JSONObject convertParticipantToMap(MatchDetail.Participant participant) {
        JSONObject jsonObject = new JSONObject();
        if (participant == null) return jsonObject;

        jsonObject.put("$riotIdGameName", participant.riotIdGameName);
        jsonObject.put("$riotIdTagline", participant.riotIdTagline);
        jsonObject.put("$teamPosition", participant.teamPosition);
        jsonObject.put("$championName", participant.championName);
        jsonObject.put("$champLevel", participant.champLevel);
        jsonObject.put("$kills", participant.kills);
        jsonObject.put("$deaths", participant.deaths);
        jsonObject.put("$assists", participant.assists);
        jsonObject.put("$goldEarned", participant.goldEarned);
        jsonObject.put("$win", participant.win);
        jsonObject.put("$totalDamageDealtToChampions", participant.totalDamageDealtToChampions);
        jsonObject.put("$totalDamageTaken", participant.totalDamageTaken);
        jsonObject.put("$teamId", participant.teamId);

        return jsonObject;
    }

    private Map<String, String> doReactionRiot(ReactionsTable reaction, Map<String, String> arguments) {
        String mainReaction;
        JSONObject jsonValues;
        Map<String, String> maybeArgument;

        jsonValues = this.parserJson.parseToJson(reaction.getValues());
        if (jsonValues == null) {
            return arguments;
        }
        try {
            mainReaction = jsonValues.getString("Reaction");
        } catch (JSONException e) {
            System.err.println("No key \"Reaction\"");
            return arguments;
        }
        switch (mainReaction) {
            case "Get League User" -> {
                try {
                    Account account = leagueService.getAccount(jsonValues.getString("gameName"), jsonValues.getString("tagLine"));
                    maybeArgument = convertToMap(account);
                    arguments = maybeArgument;
                } catch (Exception e) {
                    System.err.println("Failed to retrieve League User data: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "Get Summoner" -> {
                try {
                    Summoner summoner = leagueService.getSummoner(jsonValues.getString("puuid"));
                    maybeArgument = convertToMap(summoner);
                    arguments = maybeArgument;
                } catch (Exception e) {
                    System.err.println("Failed to retrieve League Summoner data: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "Get Player Match Data" -> {
                try {
                    MatchDetail matchDetail = leagueService.getMatchDetails(jsonValues.getString("match_id"));
                    maybeArgument = convertMatchDetailToMap(matchDetail);
                    arguments = maybeArgument;
                } catch (Exception e) {
                    System.err.println("Failed to retrieve Match Details: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "Get Player Matches" -> {
                try {
                    List<String> matchIds = leagueService.getMatchList(jsonValues.getString("puuid"));
                    String matchIdsJson = new JSONArray(matchIds).toString();
                    arguments.put("$matchId", matchIdsJson);
                } catch (Exception e) {
                    System.err.println("Failed to retrieve User Matches: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "Get Player Rank" -> {
                try {
                    List<LeagueEntry> leagueEntries = leagueService.getLeagueEntries(jsonValues.getString("summoner_id"));
                    List<Map<String, String>> entriesList = new ArrayList<>();

                    for (LeagueEntry entry : leagueEntries) {
                        Map<String, String> entryMap = convertToMap(entry);
                        entriesList.add(entryMap);
                    }
                    String leagueEntriesJson = new JSONArray(entriesList).toString();
                    arguments.put("$leagueEntries", leagueEntriesJson);

                } catch (Exception e) {
                    System.err.println("Failed to retrieve Summoner Details: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            default -> {
                System.err.println("Reaction " + mainReaction + " doesn't exist.");
            }
        }
        System.err.println("Arguments: " + arguments);
        return arguments;
    }

    private Map<String, String> doReactionThreads(ReactionsTable reaction, Map<String, Object> dataMap, Map<String, String> arguments)
    {
        Optional<AccountApiTable> apiTable = this.apiRepo.findByUsersTable(reaction.getActionTable().getUserTable());
        if (!apiTable.isPresent()) {
            System.err.println("User not found in database");
            return arguments;
        }
        String mainReaction;
        JSONObject jsonValues;
        ThreadsReactions threadsReactions = new ThreadsReactions();
        Optional<Map<String, String>> maybeArgument;
        jsonValues = this.parserJson.parseToJson(reaction.getValues());
        JSONObject credentials = this.parserJson.parseToJson(apiTable.get().getCredentials());
        try {
            mainReaction = jsonValues.getString("Reaction");
        } catch (JSONException e) {
            System.err.println("No key \"Reaction\"");
            return arguments;
        }
        if (!credentials.has("threads_id") || !credentials.has("threads_access_token")){
            System.err.println("Error with AccountApi : No key for threads");
            return arguments;
        }
        switch (mainReaction) {
            case "Create Post"-> {
                maybeArgument = threadsReactions.createPost(credentials, jsonValues, arguments);
                if (!maybeArgument.isPresent()) {
                    System.err.println("Failed to do reaction Create Post");
                } else {
                    arguments = maybeArgument.get();
                }
            }
            default -> {
                System.err.println("Reaction " + mainReaction + "doesn't exist.");
            }
        }
        return (arguments);
    }

    @SuppressWarnings("unchecked")
    public void doReaction(List<ReactionsTable> allReactions, Map<String, Object> dataMap)
    {
        Map<String, String> arguments = null;
        if (dataMap.containsKey("Arguments") && dataMap.get("Arguments") instanceof Map argument) {
            arguments = argument;
        }

        List<ReactionsTable> sortedReactions = allReactions.stream()
        .sorted(Comparator.comparingInt(ReactionsTable::getOrderReactions))
        .collect(Collectors.toList());

        for (ReactionsTable reaction : sortedReactions) {
            switch (reaction.getService()) {
                case DISCORD -> {
                    arguments = this.doReactionDiscord(reaction, dataMap, arguments);
                }
                case GMAIL -> {
                    arguments = this.doReactionGmail(reaction, arguments);
                }
                case OSU -> {
                    arguments = this.doReactionOsu(reaction, arguments);
                }
                case RIOT -> {
                    arguments = this.doReactionRiot(reaction, arguments);
                }
                case SPOTIFY -> {
                    arguments = this.doReactionSpotify(reaction, dataMap, arguments);
                }
                case THREADS -> {
                    arguments = this.doReactionThreads(reaction, dataMap, arguments);
                }
                default -> {
                }
            }
        }
    }
}
