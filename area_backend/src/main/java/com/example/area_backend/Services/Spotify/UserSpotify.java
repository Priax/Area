package com.example.area_backend.Services.Spotify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.hc.core5.http.ParseException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.area_backend.Constants;
import com.example.area_backend.HandlingReaction;
import com.example.area_backend.ParserJson;
import com.example.area_backend.TableDb.AccountApi.AccountApiService;
import com.example.area_backend.TableDb.Actions.ActionsService;
import com.example.area_backend.TableDb.Actions.ActionsTable;
import com.example.area_backend.TableDb.EnumServices;
import com.example.area_backend.TableDb.Reactions.ReactionsService;
import com.example.area_backend.TableDb.Reactions.ReactionsTable;
import com.example.area_backend.TableDb.Users.UsersRepo;
import com.example.area_backend.TableDb.Users.UsersTable;
import com.example.area_backend.Tuple;

import jakarta.annotation.PostConstruct;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.special.SnapshotResult;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.SavedTrack;
import se.michaelthelin.spotify.requests.data.library.GetUsersSavedTracksRequest;
import se.michaelthelin.spotify.requests.data.playlists.AddItemsToPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.CreatePlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetListOfUsersPlaylistsRequest;

@Component
@EnableScheduling
@EnableAsync
@RestController
public class UserSpotify
{
    @Autowired
    private CredentialSpotify credentialSpotify;

    private final HandlingNewMusicLiked userMusicLiked;

    private final Map<String, HandlerSpotifyApi> userMap;

    @Autowired
    private final ActionsService actionsService;

    @Autowired
    private final ReactionsService reactionsService;

    private final ParserJson parserJson;

    @Autowired
    private final HandlingReaction handlingReaction;

    @Autowired
    private final AccountApiService accountApiService;

    @Autowired
    private final UsersRepo userRepo;

    private final Constants gloabalVariable = new Constants();

    public UserSpotify()
    {
        this.userMap = new HashedMap<>();
        this.userMusicLiked = new HandlingNewMusicLiked();
        this.parserJson = new ParserJson();
        this.handlingReaction = new HandlingReaction();
        this.accountApiService = new AccountApiService();
        this.userRepo = null;
        this.actionsService = null;
        this.reactionsService = null;
    }

    @Autowired
    public UserSpotify(ActionsService actionsService, ReactionsService reactionsService, HandlingReaction handlingReaction, AccountApiService accountApiService, UsersRepo userRepo)
    {
        this.userMap = new HashedMap<>();
        this.userMusicLiked = new HandlingNewMusicLiked();
        this.parserJson = new ParserJson();
        this.handlingReaction = handlingReaction;
        this.actionsService = actionsService;
        this.reactionsService = reactionsService;
        this.accountApiService = accountApiService;
        this.userRepo = userRepo;
    }

    @PostConstruct
    public void iniSpotify()
    {
        if (this.credentialSpotify != null) {
            this.credentialSpotify.buildSpotifyApi();
        }
    }

    public List<String> getMonitoredAccounts()
    {
        return new ArrayList<>(this.userMap.keySet());
    }

    public boolean isAccountMonitored(String userId)
    {
        return (this.userMap.containsKey(userId));
    }

    public Tuple<String, SpotifyApi> getSpotifyApiFromUSer(String userId)
    {
        return (this.userMap.get(userId).getSpotifyApi());
    }

    public String getRealUserId(String userId)
    {
        if (!this.isAccountMonitored(userId)) {
            System.err.println("User : " + userId + " not monithorized");
            return (null);
        }
        return (this.userMap.get(userId).getRealUserId());
    }

    public String getAuthorizationUri(String userId)
    {
        return (this.credentialSpotify.getAuthorizationUri(userId));
    }

    private void checkMusicLiked(String userID)
    {
        HandlerSpotifyApi spotifyApiUser = this.userMap.get(userID);
            if (!spotifyApiUser.isUserStillAuthorized()) {
                spotifyApiUser.setUserAuthorized();
            }
            SpotifyApi apiUser = spotifyApiUser.getSpotifyApi().getRight();
            GetUsersSavedTracksRequest requestSavedTrack = apiUser.getUsersSavedTracks()
                .limit(50)
                .build();
            Paging<SavedTrack> savedTracks;
            try {
                savedTracks = requestSavedTrack.execute();
            } catch (IOException | SpotifyWebApiException | ParseException e) {
                System.err.println("Erreur dans spotify: " + e);
                return;
            }
            this.userMusicLiked.setMusicAlreadyLiked(userID, savedTracks.getItems());
    }

    private void addUserAccount(String userID)
    {
        this.credentialSpotify.getSpotifyApiAsync()
        .thenCompose(spotifyApi -> CompletableFuture.supplyAsync(() -> {
            String realUserId;
            try {
                realUserId = spotifyApi.getCurrentUsersProfile().build().execute().getId();
                this.userMap.put(userID, new HandlerSpotifyApi(spotifyApi, realUserId));
                this.userMusicLiked.addUser(userID);
                this.checkMusicLiked(userID);
            } catch (IOException | SpotifyWebApiException | ParseException e) {
                System.err.println("Erreur dans addUserAccount : " + e.getMessage());
                return null;
            }
            Optional<UsersTable> optionalUserTable = this.userRepo.findById(Long.valueOf(userID));
            if (!optionalUserTable.isPresent()) {
                System.err.println("Erreur dans addUserAccount : User not present in database");
                return null;
            }
            if (!this.accountApiService.loginWithSpotify(realUserId, optionalUserTable.get())) {
                System.out.println("User credential spotify already in database");
            }
            return userID;
        }))
        .thenAccept(userId -> System.out.println("Utilisateur " + userId + " ajouté avec succès"))
        .exceptionally(e -> {
            System.err.println("Erreur dans addUserAccount : " + e.getMessage());
            return null;
        });
    }

    public boolean deleteUserAccount(String userId)
    {
        if (!this.userMap.containsKey(userId)) {
            return (false);
        }
        this.userMap.remove(userId);
        this.userMusicLiked.deleteUser(userId);
        Optional<UsersTable> optionalUserTable = this.userRepo.findById(Long.valueOf(userId));
        if (!optionalUserTable.isPresent()) {
            System.err.println("Erreur dans addUserAccount : User not present in database");
            return (false);
        }
        if (!this.accountApiService.logoutWithSpotify(userId, optionalUserTable.get())) {
            return (false);
        }
        return (true);
    }

    public void deleteAllUserAccount()
    {
        this.userMap.clear();
        this.userMusicLiked.deleteAllUser();
    }

    private String getPlaylistIDIfExist(SpotifyApi spotifyApiUser, String realUserId, String nameOfThePlaylist)
    {
        GetListOfUsersPlaylistsRequest request = spotifyApiUser.getListOfUsersPlaylists(realUserId).build();
        Paging<PlaylistSimplified> playlistSaved;
        try {
            playlistSaved = request.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.err.println("Error when fetching playlist saved: " + e.getMessage());
            return (null);
        }
        PlaylistSimplified[] allPlaylistSaved = playlistSaved.getItems();
        int nbPlaylist = allPlaylistSaved.length;

        for (int i = 0; i < nbPlaylist; i++) {
            if (allPlaylistSaved[i].getName().equals(nameOfThePlaylist)) {
                return (allPlaylistSaved[i].getId());
            }
        }
        return (null);
    }

    public String createPlaylist(String userId, String nameOfThePlaylist, String description, boolean isPublic, boolean isCollaborative, boolean EvenIsExist)
    {
        if (!this.isAccountMonitored(userId)) {
            System.err.println("User : " + userId + " not monithorized");
            return (null);
        }
        HandlerSpotifyApi spotifyUser = this.userMap.get(userId);
        if (!spotifyUser.isUserStillAuthorized()) {
            spotifyUser.setUserAuthorized();
        }
        SpotifyApi spotifyApiUser = spotifyUser.getSpotifyApi().getRight();
        String realUserId = spotifyUser.getSpotifyApi().getLeft();
        if (!EvenIsExist) {
            String playlistIdAlreadyExist = this.getPlaylistIDIfExist(spotifyApiUser, realUserId, nameOfThePlaylist);
            if (playlistIdAlreadyExist != null) {
                return (playlistIdAlreadyExist);
            }
        }
        CreatePlaylistRequest createPlaylistRequest = spotifyApiUser.createPlaylist(realUserId, nameOfThePlaylist)
            .collaborative(isCollaborative)
            .public_(isPublic)
            .description(description)
            .build();
        Playlist newPlaylist;
        try {
            newPlaylist = createPlaylistRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.err.println("Failed to create Playlist for user " + userId + ": " + e);
            return (null);
        }
        System.out.println("Playlist: " + nameOfThePlaylist + " has been created succesfully by user = " + userId);
        return (newPlaylist.getId());
    }

    public String addItemToPlaylist(String userId, String playlistId, String[] uris, int position)
    {
        if (!this.isAccountMonitored(userId)) {
            System.err.println("User : " + userId + " not monithorized");
            return (null);
        }
        HandlerSpotifyApi spotifyUser = this.userMap.get(userId);
        if (!spotifyUser.isUserStillAuthorized()) {
            spotifyUser.setUserAuthorized();
        }
        uris = this.putAllLinkBeforeMusicId(uris);
        SpotifyApi spotifyApiUser = spotifyUser.getSpotifyApi().getRight();
        AddItemsToPlaylistRequest addItemRequest;
        SnapshotResult snapshotResult;
        if (position > 0) {
            addItemRequest = spotifyApiUser.addItemsToPlaylist(playlistId, uris)
               .position(position)
               .build();
        } else {
            addItemRequest = spotifyApiUser.addItemsToPlaylist(playlistId, uris)
               .build();
        }
        try {
            snapshotResult = addItemRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.err.println("Failed to add Item in Playlist for user " + userId + ": " + e);
            return (null);
        }
        return (snapshotResult.getSnapshotId());
    }

    private String[] putAllLinkBeforeMusicId(String[] uris)
    {
        int size = uris.length;

        for (int i = 0; i < size; i++) {
            uris[i] = "spotify:track:" + uris[i];
        }
        return (uris);
    }

    @Async
    @Scheduled(fixedRate = 60000)
    public void checkNewMusicLiked()
    {
        System.out.println("Je check les liked");
        List<String> allUser = this.getMonitoredAccounts();
        for (String user : allUser) {
            HandlerSpotifyApi spotifyApiUser = this.userMap.get(user);
            if (!spotifyApiUser.isUserStillAuthorized()) {
                spotifyApiUser.setUserAuthorized();
            }
            SpotifyApi apiUser = spotifyApiUser.getSpotifyApi().getRight();
            GetUsersSavedTracksRequest requestSavedTrack = apiUser.getUsersSavedTracks()
                .limit(50)
                .build();
            Paging<SavedTrack> savedTracks;
            try {
                savedTracks = requestSavedTrack.execute();
            } catch (IOException | SpotifyWebApiException | ParseException e) {
                System.err.println("Erreur dans spotify: " + e);
                continue;
            }
            List<String> newMusicId = this.userMusicLiked.isNewMusicLikedForUser(user, savedTracks.getItems());
            if (newMusicId == null) {
                continue;
            }
            System.out.println("J'ai detecter un like de music");
            this.doActionSpotify(apiUser, user, newMusicId);
        }
    }

    private void doActionSpotify(SpotifyApi apiUser, String user, List<String> musicId)
    {
        List<ActionsTable> allActions = this.actionsService.getAllActions();
        Map<String, Object> dataMap = new HashedMap<>();
        Map<String, String> arguments = new HashedMap<>();
        dataMap.put("Spotify Api", apiUser);
        arguments.put(this.gloabalVariable.KEY_VARIABLE_SPOTIFY_ACTION_LIKED_MUSIC_USER, user);
        arguments.put(this.gloabalVariable.KEY_VARIABLE_SPOTIFY_ACTION_LIKED_MUSIC_REAL_USER_ID, this.getRealUserId(user));
        arguments.put(this.gloabalVariable.KEY_VARIABLE_SPOTIFY_ACTION_LIKED_MUSIC_ID, musicId.toString());
        dataMap.put(this.gloabalVariable.KEY_VARIABLE_ARGUMENTS, arguments);
        for (ActionsTable action : allActions) {
            if (!action.getService().equals(EnumServices.SPOTIFY)) {
                continue;
            }
            String actionValues = action.getValues();
            JSONObject jsonValues = this.parserJson.parseToJson(actionValues);
            if (jsonValues == null) {
                continue;
            }
            if (!this.checkArgument(jsonValues)) {
                continue;
            }
            List<ReactionsTable> all_reaction = this.reactionsService.getReactionsByActionId(action);
            this.handlingReaction.doReaction(all_reaction, dataMap);
        }
    }

    private boolean checkArgument(JSONObject jsonValues)
    {
        String actionType;
        try {
            actionType = jsonValues.getString("Action");
        } catch (JSONException e) {
            return (false);
        }
        if (!actionType.equals("Liked a music")) {
            return (false);
        }
        return (true);
    }

    @GetMapping("/spotifyoauth/callback")
    public ResponseEntity<String> handleSpotifyCallback(@RequestParam("code") String code, @RequestParam("state") String credential)
    {
        this.credentialSpotify.setAccessTokenAndRefreshToken(code);
        this.addUserAccount(credential);
        return ResponseEntity.ok("You can close this window");
    }
}