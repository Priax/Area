package com.example.area_backend;

import org.springframework.stereotype.Component;

import com.example.area_backend.Services.Gmail.GmailListener;
import com.example.area_backend.Services.Gmail.GmailService;
import com.example.area_backend.Services.Spotify.UserSpotify;

import net.dv8tion.jda.api.JDA;

@Component
public abstract class Handler
{
    protected static JDA discordJda;
    public static GmailService gmailService;
    protected static GmailListener gmailListener;
    protected static UserSpotify userSpotify;

    public static void setDiscordJda(JDA newDiscordJda)
    {
        Handler.discordJda = newDiscordJda;
    }
}
