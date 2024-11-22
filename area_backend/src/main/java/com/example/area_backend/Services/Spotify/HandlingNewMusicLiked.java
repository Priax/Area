package com.example.area_backend.Services.Spotify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.michaelthelin.spotify.model_objects.specification.SavedTrack;

public class HandlingNewMusicLiked
{
    private final Map<String, List<String>> musicLikedPerUser;

    public HandlingNewMusicLiked()
    {
        this.musicLikedPerUser = new HashMap<>();
    }

    public void addUser(String userId)
    {
        if (!this.isAccountMonitored(userId)) {
            this.musicLikedPerUser.put(userId, new ArrayList<>());
        }
    }

    public void deleteUser(String userId)
    {
        if (this.isAccountMonitored(userId)) {
            this.musicLikedPerUser.remove(userId);
        }
    }

    public void deleteAllUser()
    {
        this.musicLikedPerUser.clear();
    }

    public boolean isAccountMonitored(String userId)
    {
        return (this.musicLikedPerUser.containsKey(userId));
    }

    public List<String> isNewMusicLikedForUser(String userId, SavedTrack[] musicsId)
    {
        List<String> newMusicId = new ArrayList<>();
        if (!this.isAccountMonitored(userId)) {
            return (null);
        }
        List<String> allMusicIdSaved = this.musicLikedPerUser.get(userId);
        List<String> MusicIdToSaved = new ArrayList<>();
        int size = musicsId.length;
        for (int i = 0; i < size; i++) {
            String musicId = musicsId[i].getTrack().getId();
            if (!allMusicIdSaved.contains(musicId)) {
                newMusicId.add(musicId);
            }
            MusicIdToSaved.add(musicId);
        }
        if (newMusicId.isEmpty()) {
            return (null);
        }
        this.musicLikedPerUser.replace(userId, MusicIdToSaved);
        return (newMusicId);
    }

    public void setMusicAlreadyLiked(String userId, SavedTrack[] musicsId)
    {
        List<String> MusicIdToSaved = new ArrayList<>();
        if (!this.isAccountMonitored(userId)) {
            return;
        }
        int size = musicsId.length;
        for (int i = 0; i < size; i++) {
            String musicId = musicsId[i].getTrack().getId();
            MusicIdToSaved.add(musicId);
        }
        this.musicLikedPerUser.put(userId, MusicIdToSaved);
    }
}
