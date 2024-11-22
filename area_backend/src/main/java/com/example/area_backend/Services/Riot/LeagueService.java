package com.example.area_backend.Services.Riot;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class LeagueService {

    private static final String BASE_URL = "https://euw1.api.riotgames.com/lol/";
    private static final String ACCOUNT_URL = "https://europe.api.riotgames.com/riot/";
    private final RestTemplate restTemplate;
    private final String apiKey;

    public LeagueService(@Value("${RIOT_API_KEY}") String apiKey) {
        this.restTemplate = new RestTemplate();
        this.apiKey = apiKey;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Riot-Token", apiKey);
        return headers;
    }

    public Account getAccount(String GameName, String TagLine) throws Exception {
        String endpoint = ACCOUNT_URL + "account/v1/accounts/by-riot-id/" + GameName + "/" + TagLine; // TagLine without the #
        HttpEntity<String> entity = new HttpEntity<>(getHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, entity, String.class
        );

        JSONObject json = new JSONObject(response.getBody());
        return new Account(json.getString("puuid"), json.getString("gameName"), json.getString("tagLine"));
    }

    public Summoner getSummoner(String summonerPuuid) throws Exception {
        String endpoint = BASE_URL + "summoner/v4/summoners/by-puuid/" + summonerPuuid;
        HttpEntity<String> entity = new HttpEntity<>(getHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, entity, String.class
        );

        JSONObject json = new JSONObject(response.getBody());
        return new Summoner(
                json.getString("id"), json.getString("accountId"), json.getString("puuid"),
                json.getInt("profileIconId"), json.getLong("revisionDate"), json.getInt("summonerLevel")
        );
    }

    public List<LeagueEntry> getLeagueEntries(String summonerId) throws Exception {
        String endpoint = BASE_URL + "league/v4/entries/by-summoner/" + summonerId;
        HttpEntity<String> entity = new HttpEntity<>(getHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, entity, String.class
        );

        JSONArray jsonArray = new JSONArray(response.getBody());
        List<LeagueEntry> leagueEntries = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            leagueEntries.add(new LeagueEntry(
                    json.getString("leagueId"), json.getString("queueType"),
                    json.getString("tier"), json.getString("rank"), json.getString("summonerId"),
                    json.getInt("leaguePoints"), json.getInt("wins"), json.getInt("losses"),
                    json.getBoolean("hotStreak")
            ));
        }
        return leagueEntries;
    }

    public List<String> getMatchList(String puuid) throws Exception {
        String endpoint = "https://europe.api.riotgames.com/lol/match/v5/matches/by-puuid/" + puuid + "/ids";
        HttpEntity<String> entity = new HttpEntity<>(getHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, entity, String.class
        );

        JSONArray jsonArray = new JSONArray(response.getBody());
        List<String> matches = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            matches.add(jsonArray.getString(i));
        }
        return matches;
    }

    public MatchDetail getMatchDetails(String matchId) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI uri = new URI("https://europe.api.riotgames.com/lol/match/v5/matches/" + matchId);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("X-Riot-Token", apiKey)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body());
                MatchDetail matchDetail = new MatchDetail(jsonResponse);
                return matchDetail;
            } else {
                System.err.println("Error fetching data");
                throw new RuntimeException("Failed to get match details: " + response.statusCode() + " - " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class Account {
        public String puuid;
        public String gameName;
        public String tagLine;

        public Account(String puuid, String gameName, String tagLine) {
            this.puuid = puuid;
            this.gameName = gameName;
            this.tagLine = tagLine;
        }
    }

    public static class Summoner {
        public String id;
        public String accountId;
        public String puuid;
        public int profileIconId;
        public long revisionDate;
        public int summonerLevel;

        public Summoner(String id, String accountId, String puuid, int profileIconId, long revisionDate, int summonerLevel) {
            this.id = id;
            this.accountId = accountId;
            this.puuid = puuid;
            this.profileIconId = profileIconId;
            this.revisionDate = revisionDate;
            this.summonerLevel = summonerLevel;
        }
    }

    public static class LeagueEntry {
        public String leagueId;
        public String queueType;
        public String tier;
        public String rank;
        public String summonerId;
        public int leaguePoints;
        public int wins;
        public int losses;
        public boolean hotStreak;

        public LeagueEntry(String leagueId, String queueType, String tier, String rank, String summonerId, int leaguePoints, int wins, int losses, boolean hotStreak) {
            this.leagueId = leagueId;
            this.queueType = queueType;
            this.tier = tier;
            this.rank = rank;
            this.summonerId = summonerId;
            this.leaguePoints = leaguePoints;
            this.wins = wins;
            this.losses = losses;
            this.hotStreak = hotStreak;
        }
    }

    public static class MatchDetail {
        public String matchId;
        public String gameMode;
        public String gameType;
        public String gameVersion;
        public List<String> participantIds;
        public long gameCreation;
        public int gameDuration;
        public List<Participant> participants;
        public String endOfGameResult;

        public MatchDetail(JSONObject json) {
            JSONObject metadata = json.getJSONObject("metadata");
            this.matchId = metadata.getString("matchId");

            JSONArray participantIdsJson = metadata.getJSONArray("participants");
            this.participantIds = new ArrayList<>();
            for (int i = 0; i < participantIdsJson.length(); i++) {
                this.participantIds.add(participantIdsJson.getString(i));
            }

            JSONObject info = json.getJSONObject("info");

            this.endOfGameResult = info.getString("endOfGameResult");
            this.gameCreation = info.getLong("gameCreation");
            this.gameDuration = info.getInt("gameDuration");
            this.gameMode = info.getString("gameMode");
            this.gameType = info.getString("gameType");
            this.gameVersion = info.getString("gameVersion");

            JSONArray participantsJson = info.getJSONArray("participants");
            this.participants = new ArrayList<>();
            for (int i = 0; i < participantsJson.length(); i++) {
                JSONObject participantJson = participantsJson.getJSONObject(i);
                this.participants.add(new Participant(participantJson));
            }
        }

        public static class Participant {
            public String riotIdGameName;
            public String riotIdTagline;
            public String teamPosition;
            public String championName;
            public int champLevel;
            public int kills;
            public int deaths;
            public int assists;
            public int goldEarned;
            public boolean win;
            public int totalDamageDealtToChampions;
            public int totalDamageTaken;
            public int teamId;

            public Participant(JSONObject json) {
                this.riotIdGameName = json.getString("riotIdGameName");
                this.riotIdTagline = json.getString("riotIdTagline");
                this.teamPosition = json.getString("teamPosition");
                this.championName = json.getString("championName");
                this.champLevel = json.getInt("champLevel");
                this.kills = json.getInt("kills");
                this.deaths = json.getInt("deaths");
                this.assists = json.getInt("assists");
                this.goldEarned = json.getInt("goldEarned");
                this.win = json.getBoolean("win");
                this.totalDamageDealtToChampions = json.getInt("totalDamageDealtToChampions");
                this.totalDamageTaken = json.getInt("totalDamageTaken");
                this.teamId = json.getInt("teamId");
            }
        }
    }
}
