package com.example.area_backend.Services.Discord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.area_backend.HandlingReaction;
import com.example.area_backend.TableDb.Actions.ActionsService;
import com.example.area_backend.TableDb.Reactions.ReactionsService;

import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

@Component
public class Discord
{
    @Value("${spring.services.discord.botToken}")
	private String bot_token;

    @Autowired
    private ActionsService actionsService;
    @Autowired
    private ReactionsService reactionsService;
    @Autowired
    private HandlingReaction handlingReaction;

    public Discord() {}

    @PostConstruct
    public void initBot()
    {
        JDABuilder.createLight(bot_token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
            .setChunkingFilter(ChunkingFilter.ALL)
            .enableIntents(GatewayIntent.GUILD_MEMBERS)
            .setMemberCachePolicy(MemberCachePolicy.ALL)
            .addEventListeners(new ListenerDiscord(actionsService, reactionsService, handlingReaction))
            .addEventListeners(new ReadyBot())
            .build();
    }
}
