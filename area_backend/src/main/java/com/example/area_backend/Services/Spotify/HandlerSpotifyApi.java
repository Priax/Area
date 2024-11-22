package com.example.area_backend.Services.Spotify;

import java.io.IOException;
import java.time.Instant;

import org.apache.hc.core5.http.ParseException;

import com.example.area_backend.Tuple;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;

public class HandlerSpotifyApi
{
    private final SpotifyApi spotifyApi;
    private final String userID;
    private Instant tokenExpirationTime;

    public HandlerSpotifyApi(SpotifyApi newSpotifyApi, String userID)
    {
        this.spotifyApi = newSpotifyApi;
        this.tokenExpirationTime = Instant.now().plusSeconds(3600);
        this.userID = userID;
    }

    public Tuple<String, SpotifyApi> getSpotifyApi()
    {
        return (new Tuple<>(userID, this.spotifyApi));
    }

    public String getRealUserId()
    {
        return (this.userID);
    }

    public void setUserAuthorized()
    {
        if (Instant.now().isAfter(this.tokenExpirationTime)) {
            try {
                AuthorizationCodeCredentials newCredentials = this.spotifyApi.authorizationCodeRefresh().build().execute();
                this.spotifyApi.setAccessToken(newCredentials.getAccessToken());
                this.tokenExpirationTime = Instant.now().plusSeconds(newCredentials.getExpiresIn());
                System.out.println("The access token has been updated successfully!");
            } catch (IOException | SpotifyWebApiException | ParseException e) {
                System.err.println("Error when trying to refresh token: " + e);
            }
        }
    }

    public boolean isUserStillAuthorized()
    {
        return (!Instant.now().isAfter(this.tokenExpirationTime));
    }
}