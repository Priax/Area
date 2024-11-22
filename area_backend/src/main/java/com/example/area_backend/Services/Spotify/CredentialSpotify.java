package com.example.area_backend.Services.Spotify;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

@Component
public class CredentialSpotify
{
    @Value("${spring.services.spotify.clientId}")
    private String clientId;
    @Value("${spring.services.spotify.clientSecret}")
    private String clientSecret;
    private SpotifyApi spotifyApi;
    private AuthorizationCodeUriRequest authorizationCodeUriRequest;
    private static final URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:8080/spotifyoauth/callback");
    private static final String SCOPES = String.join(",", Arrays.asList("user-read-playback-state", "user-modify-playback-state", "user-read-currently-playing", "playlist-read-private", "playlist-modify-private", "playlist-modify-public", "user-read-email", "user-library-read"));
    private final CompletableFuture<Boolean> isUserAuthorized;

    public CredentialSpotify()
    {
        this.isUserAuthorized = new CompletableFuture<>();
    }

    public void buildSpotifyApi()
    {
        this.spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(redirectUri)
                .build();
    }

    public void setAccessTokenAndRefreshToken(String code)
    {
        AuthorizationCodeRequest authorizationCodeRequest = this.spotifyApi.authorizationCode(code).build();
        try {
            AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
            this.spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            this.spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
            this.isUserAuthorized.complete(true);
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.err.println("Error: " + e.getMessage());
            this.isUserAuthorized.complete(false);
        }
    }

    public CompletableFuture<SpotifyApi> getSpotifyApiAsync()
    {
        return isUserAuthorized.thenApplyAsync(authorized -> {
            if (authorized) {
                return this.spotifyApi;
            } else {
                throw new IllegalStateException("User not authorized.");
            }
        });
    }

    public String getAuthorizationUri(String userId)
    {
        this.setAuthorizationCodeUri(userId);
        return this.authorizationCodeUriRequest.execute().toString();
    }

    private void setAuthorizationCodeUri(String userId)
    {
        this.authorizationCodeUriRequest = this.spotifyApi.authorizationCodeUri()
            .state(userId)
            .scope(SCOPES)
            .show_dialog(true)
            .build();
    }
}