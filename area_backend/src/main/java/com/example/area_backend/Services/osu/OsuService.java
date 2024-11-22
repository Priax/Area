package com.example.area_backend.Services.osu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OsuService {
    private static final String BASE_URL = "https://osu.ppy.sh/api/v2/";
    private static final String APP_ID = "32002";
    // private static final String CLIENT_ID = "27370985";

    private final RestTemplate restTemplate;
    private final String accessToken;

    @Autowired
    public OsuService(@Value("${OSU_CLIENT_SECRET}") String clientSecret) throws Exception {
        this.restTemplate = new RestTemplate();
        this.accessToken = getAccessToken(clientSecret);
    }

    public String getAccessToken(String clientSecret) throws Exception {
        Map<String, String> formParams = new HashMap<>();
        formParams.put("client_id", APP_ID);
        formParams.put("client_secret", clientSecret);
        formParams.put("grant_type", "client_credentials");
        formParams.put("scope", "public");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(formParams, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://osu.ppy.sh/oauth/token", requestEntity, String.class
        );

        JSONObject responseBody = new JSONObject(response.getBody());
        return responseBody.getString("access_token");
    }

    public User getUserData(String userId) throws Exception {
        String endpoint = BASE_URL + "users/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, entity, String.class
        );

        JSONObject jsonResponse = new JSONObject(response.getBody());
        return new User(
                jsonResponse.getString("username"),
                jsonResponse.getLong("id"),
                jsonResponse.getString("country_code"),
                jsonResponse.getJSONObject("statistics")
        );
    }

    public List<Beatmapset> getUserBeatmaps(long userId, String type, int limit, int offset) throws Exception {
        String endpoint = BASE_URL + "users/" + userId + "/beatmapsets/" + type + "?limit=" + limit + "&offset=" + offset;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.GET, entity, String.class);
            // System.err.println(response.getStatusCode());

            if (!response.getStatusCode().is2xxSuccessful()) {
                System.err.println("Failed to fetch beatmaps: " + response.getStatusCode() + " - " + response.getBody());
                throw new RuntimeException("Failed to fetch beatmaps. HTTP Status: " + response.getStatusCode());
            }
            JSONArray beatmapsetsJsonArray = new JSONArray(response.getBody());
            // System.err.println(beatmapsetsJsonArray);

            List<Beatmapset> beatmapsets = new ArrayList<>();

            for (int i = 0; i < beatmapsetsJsonArray.length(); i++) {
                JSONObject beatmapsetJson = beatmapsetsJsonArray.getJSONObject(i);
                // System.err.println("beatmapset: " + beatmapsetJson);

                // System.err.println("JSON Beatmapset Array: " + beatmapsetJson.names());
                Beatmapset beatmapset = new Beatmapset();
                beatmapset.count = beatmapsetJson.getLong("count");

                JSONObject beatmapsetDataJson = beatmapsetJson.getJSONObject("beatmapset");
                // System.err.println("JSON beatmapsetData Array: " + beatmapsetDataJson.names());
                BeatmapsetData beatmapsetData = new BeatmapsetData();
                beatmapsetData.title = beatmapsetDataJson.optString("title", "Unknown Title");
                beatmapsetData.artist = beatmapsetDataJson.optString("artist", "Unknown Artist");
                beatmapsetData.artistUnicode = beatmapsetDataJson.optString("artist_unicode", "Unknown");
                beatmapsetData.play_count = beatmapsetDataJson.optString("play_count", "Unknown");
                beatmapsetData.status = beatmapsetDataJson.optString("status", "Unknown");
                beatmapsetData.creator = beatmapsetDataJson.optString("creator", "Unknown Creator");
                beatmapsetData.id = beatmapsetDataJson.getLong("id");
                beatmapset.beatmapsetData = beatmapsetData;

                JSONObject beatmapsJson = beatmapsetJson.optJSONObject("beatmap");
                List<Beatmap> beatmaps = new ArrayList<>();
                if (beatmapsJson != null) {
                    Beatmap beatmap = new Beatmap();
                    beatmap.beatmapsetId = beatmapsJson.getLong("beatmapset_id");
                    beatmap.difficultyRating = beatmapsJson.getDouble("difficulty_rating");
                    beatmap.id = beatmapsJson.getLong("id");
                    beatmap.mode = beatmapsJson.getString("mode");
                    beatmap.status = beatmapsJson.getString("status");
                    beatmap.totalLength = beatmapsJson.getLong("total_length");
                    beatmap.userId = beatmapsJson.getLong("user_id");
                    beatmap.version = beatmapsJson.getString("version");
                    beatmaps.add(beatmap);
                } else {
                    System.err.println("No beatmaps found for user ID: " + userId);
                }
                beatmapset.beatmaps = beatmaps;
                beatmapsets.add(beatmapset);
            }

            return beatmapsets;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("HTTP Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new RuntimeException("HTTP Error occurred: " + e.getMessage(), e);
        } catch (JSONException e) {
            System.err.println("JSON Parsing Error: " + e.getMessage());
            throw new RuntimeException("Error parsing the JSON response.", e);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            throw e;
        }
    }

    public static class User {
        public String username;
        public long id;
        public String countryCode;
        public Statistics statistics;

        public User(String username, long id, String countryCode, JSONObject statisticsJson) {
            this.username = username;
            this.id = id;
            this.countryCode = countryCode;
            this.statistics = new Statistics(statisticsJson);
        }

        @Override
        public String toString() {
            return String.format("Username: %s\nID: %d\nCountry Code: %s\nStatistics:\n%s",
                    username, id, countryCode, statistics);
        }
    }

    public static class Statistics {
        public Level level;
        public long globalRank;
        public long countryRank;
        public double pp;
        public double hitAccuracy;

        public Statistics(JSONObject json) {
            this.level = new Level(json.getJSONObject("level"));
            this.globalRank = json.getLong("global_rank");
            this.countryRank = json.getLong("country_rank");
            this.pp = json.getDouble("pp");
            this.hitAccuracy = json.getDouble("hit_accuracy");
        }

        @Override
        public String toString() {
            return String.format("Level: %d (Progress: %d%%), Global Rank: %d, Country Rank: %d, PP: %.2f, Hit Accuracy: %.2f%%",
                    level.current, level.progress, globalRank, countryRank, pp, hitAccuracy);
        }
    }

    public static class Level {
        public int current;
        public int progress;

        public Level(JSONObject json) {
            this.current = json.getInt("current");
            this.progress = json.getInt("progress");
        }
    }

    public static class Beatmapset {
        public long beatmapId;
        public long count;
        public List<Beatmap> beatmaps;
        public BeatmapsetData beatmapsetData;
    }

    public static class Beatmap {
        public long id;
        public long beatmapsetId;
        public String status;
        public long totalLength;
        public long userId;
        public String version;
        public String mode;
        public double difficultyRating;
    }

    public static class BeatmapsetData {
        public long id;
        public String title;
        public String artist;
        public String artistUnicode;
        public String creator;
        public String status;
        public String play_count;
    }
}
