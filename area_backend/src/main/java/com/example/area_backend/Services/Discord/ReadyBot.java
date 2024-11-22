package com.example.area_backend.Services.Discord;

import com.example.area_backend.Handler;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReadyBot extends ListenerAdapter
{
    public ReadyBot() {}

    @Override
    public void onReady(ReadyEvent event)
    {
        Handler.setDiscordJda(event.getJDA());
    }
}
