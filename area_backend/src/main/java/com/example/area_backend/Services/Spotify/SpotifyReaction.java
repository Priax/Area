package com.example.area_backend.Services.Spotify;

import java.util.Map;
import java.util.Optional;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.area_backend.Constants;
import com.example.area_backend.HandleArgument;
import com.example.area_backend.Handler;
import com.example.area_backend.TableDb.AccountApi.AccountApiRepo;

@Component
public class SpotifyReaction extends Handler
{
    private final HandleArgument handleArgument;

    private final Constants globalVariable = new Constants();

    public SpotifyReaction()
    {
        this.handleArgument = new HandleArgument();
    }

    @Autowired
    public SpotifyReaction(AccountApiRepo apiRepo)
    {
        this.handleArgument = new HandleArgument();
    }

    public Optional<Map<String,String>> addItemToPlaylist(String userId, JSONObject jsonValues, Map<String, String> arguments)
    {
        String playlistId;
        String urisString;
        String positionString;
        int position;
        try {
            playlistId = jsonValues.getString("PlaylistID");
            urisString = jsonValues.getString("URIs");
            positionString = jsonValues.getString("Position");
        } catch (JSONException e) {
            System.err.println("Key Name or Description or IsPublic or IsCollaborative not in values: " + e);
            return (Optional.ofNullable(null));
        }
        String[] newData = this.handleArgument.fillWithArgument(arguments, new String[]{playlistId, urisString, positionString});
        playlistId = newData[0];
        urisString = newData[1];
        positionString = newData[2];
        position = this.convertStringToInt(positionString);
        if (position == -2) {
            System.err.println("Position is not a integer");
            return (Optional.ofNullable(null));
        }
        String[] uris = this.convertStringToList(urisString);
        String idAdd = Handler.userSpotify.addItemToPlaylist(userId, playlistId, uris, position);
        if (idAdd == null) {
            System.err.println("Failed to Add Musics in Playlist");
            return (Optional.ofNullable(null));
        }
        arguments = this.handleArgument.fillOutWithArg(arguments, new String[]{
            this.globalVariable.KEY_VARIABLE_SPOTIFY_REACTION_ADD_MUSIC_USER,
            this.globalVariable.KEY_VARIABLE_SPOTIFY_REACTION_ADD_MUSIC_ID,
            this.globalVariable.KEY_VARIABLE_SPOTIFY_REACTION_ADD_MUSIC_REAL_USER_ID},
            new String[]{userId, idAdd, Handler.userSpotify.getRealUserId(userId)});
        return (Optional.of(arguments));
    }

    public Optional<Map<String,String>> createPlaylist(String userId, JSONObject jsonValues, Map<String, String> arguments)
    {
        String nameOfThePlaylist;
        String description;
        String isPublicString;
        boolean isPublic;
        String isCollaborativeString;
        boolean isCollaborative;
        String EvenIsExistString;
        boolean EvenIsExist;
        try {
            nameOfThePlaylist = jsonValues.getString("Name");
            description = jsonValues.getString("Description");
            isPublicString = jsonValues.getString("IsPublic");
            isCollaborativeString = jsonValues.getString("IsCollaborative");
            EvenIsExistString = jsonValues.getString("EvenIsExist");
        } catch (JSONException e) {
            System.err.println("Key Name or Description or IsPublic or IsCollaborative not in values: " + e);
            return (Optional.ofNullable(null));
        }
        String[] newData = this.handleArgument.fillWithArgument(arguments, new String[]{nameOfThePlaylist, description, isPublicString, isCollaborativeString, EvenIsExistString});
        nameOfThePlaylist = newData[0];
        description = newData[1];
        isPublicString = newData[2];
        isCollaborativeString = newData[3];
        EvenIsExistString = newData[4];
        isPublic = this.convertStringToBool(isPublicString);
        isCollaborative = this.convertStringToBool(isCollaborativeString);
        EvenIsExist = this.convertStringToBool(EvenIsExistString);
        String idPlaylist = Handler.userSpotify.createPlaylist(userId, nameOfThePlaylist, description, isPublic, isCollaborative, EvenIsExist);
        if (idPlaylist == null) {
            System.err.println("Failed to Create Playlist");
            return (Optional.ofNullable(null));
        }
        arguments = this.handleArgument.fillOutWithArg(arguments, new String[]{
            this.globalVariable.KEY_VARIABLE_SPOTIFY_REACTION_CREATE_PLAYLIST_USER,
            this.globalVariable.KEY_VARIABLE_SPOTIFY_REACTION_CREATE_PLAYLIST_ID,
            this.globalVariable.KEY_VARIABLE_SPOTIFY_REACTION_CREATE_PLAYLIST_REAL_USER_ID},
            new String[]{userId, idPlaylist, Handler.userSpotify.getRealUserId(userId)});
        return (Optional.of(arguments));
    }

    private int convertStringToInt(String newInteger)
    {
        if (newInteger.isBlank()) {
            return (-1);
        }
        Integer integer;
        try {
            integer = Integer.decode(newInteger);
        } catch (NumberFormatException e) {
            return (-2);
        }
        return (integer);
    }

    private String[] convertStringToList(String newList)
    {
        return newList
            .replaceAll("^\\[|]$", "")
            .split("\\s*,\\s*");
    }

    private boolean convertStringToBool(String newBoolean)
    {
        return (
            newBoolean.equals("1") ||
            newBoolean.equals("true") ||
            newBoolean.equals("True") ||
            newBoolean.equals("Vrai") ||
            newBoolean.equals("vrai") ||
            newBoolean.equals("Oui") ||
            newBoolean.equals("oui")
        );
    }
}
