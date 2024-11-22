package com.example.area_backend.Services.Discord;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.area_backend.Constants;
import com.example.area_backend.HandlingReaction;
import com.example.area_backend.ParserJson;
import com.example.area_backend.TableDb.Actions.ActionsService;
import com.example.area_backend.TableDb.Actions.ActionsTable;
import com.example.area_backend.TableDb.Reactions.ReactionsService;
import com.example.area_backend.TableDb.Reactions.ReactionsTable;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideUpdateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ListenerDiscord extends ListenerAdapter
{
    private final ActionsService actionsService;
    private final ReactionsService reactionsService;
    private final HandlingReaction handlingReaction;
    private final ParserJson parserJson;
    private final Constants globalVariable = new Constants();

    public ListenerDiscord(ActionsService actionsService, ReactionsService reactionsService, HandlingReaction handlingReaction)
    {
        this.actionsService = actionsService;
        this.reactionsService = reactionsService;
        this.handlingReaction = handlingReaction;
        this.parserJson = new ParserJson();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (event.getAuthor().isBot()) {
            return;
        }
        List<ActionsTable> allActions = this.actionsService.getAllActions();
        Map<String, Object> dataMap = new HashedMap<>();
        Map<String, String> arguments = new HashedMap<>();
        arguments.put(this.globalVariable.KEY_VARIABLE_DISCORD_ACTION_RECEIVED_MESSAGE_MESSAGE, event.getMessage().getContentRaw());
        arguments.put(this.globalVariable.KEY_VARIABLE_DISCORD_ACTION_RECEIVED_MESSAGE_AUTHOR, event.getAuthor().getName());
        dataMap.put("Discord React Message", event);
        dataMap.put(this.globalVariable.KEY_VARIABLE_ARGUMENTS, arguments);
        for (ActionsTable action : allActions) {
            String actionValues = action.getValues();
            JSONObject jsonValues = this.parserJson.parseToJson(actionValues);
            if (jsonValues == null) {
                continue;
            }
            if (!this.checkArgument(jsonValues, event)) {
                continue;
            }
            List<ReactionsTable> all_reaction = this.reactionsService.getReactionsByActionId(action);
            this.handlingReaction.doReaction(all_reaction, dataMap);
        }
    }

    private boolean checkArgument(JSONObject jsonValues, MessageReceivedEvent event)
    {
        String actionType;
        String message;
        String destination;
        String from;
        try {
            actionType = jsonValues.getString("Action");
            message = jsonValues.getString("Message");
            destination = jsonValues.getString("Destination");
            from = jsonValues.getString("From");
        } catch (JSONException e) {
            return (false);
        }
        if (!actionType.equals("Received Message")) {
            return (false);
        }
        if (!message.isEmpty() && !message.equals(event.getMessage().getContentRaw())) {
            return (false);
        }
        if (!destination.isEmpty() && (!destination.equals(event.getChannel().getName()) && !destination.equals(event.getGuild().getName()))) {
            return (false);
        }
        if (!from.isEmpty() && !from.equals(event.getAuthor().getName())) {
            return (false);
        }
        return (true);
    }

    @Override
    public void onPermissionOverrideUpdate(PermissionOverrideUpdateEvent event)
    {
        List<ActionsTable> allActions = this.actionsService.getAllActions();
        Map<String, Object> dataMap = new HashedMap<>();
        Map<String, String> arguments = new HashedMap<>();
        Member memberUser = event.getMember();
        if (memberUser != null) {
            arguments.put(this.globalVariable.KEY_VARIABLE_DISCORD_ACTION_PERM_CHANGED_USER, memberUser.getUser().getName());
        } else {
            arguments.put(this.globalVariable.KEY_VARIABLE_DISCORD_ACTION_PERM_CHANGED_USER, null);
        }
        arguments.put(this.globalVariable.KEY_VARIABLE_DISCORD_ACTION_PERM_CHANGED_GUILD, event.getGuild().getName());
        arguments.put(this.globalVariable.KEY_VARIABLE_DISCORD_ACTION_PERM_CHANGED_CHANNEL, event.getChannel().getName());
        dataMap.put("Discord Change Perm", event);
        dataMap.put(this.globalVariable.KEY_VARIABLE_ARGUMENTS, arguments);
        for (ActionsTable action : allActions) {
            String actionValues = action.getValues();
            String user;
            String actiontype;
            JSONObject jsonValues = this.parserJson.parseToJson(actionValues);
            if (jsonValues == null) {
                continue;
            }
            try {
                actiontype = jsonValues.getString("Action");
                user = jsonValues.getString("User");
            } catch (JSONException e) {
                continue;
            }
            if (!actiontype.equals("Change Perms")) {
                continue;
            }
            Member userMember = event.getMember();
            if (userMember == null) {
                continue;
            }
            if (!user.isEmpty() && !user.equals(userMember.getUser().getName())) {
                continue;
            }
            List<ReactionsTable> all_reaction = this.reactionsService.getReactionsByActionId(action);
            this.handlingReaction.doReaction(all_reaction, dataMap);
        }
    }

    @Override
    public void onChannelCreate(ChannelCreateEvent event)
    {
        List<ActionsTable> allActions = this.actionsService.getAllActions();
        Map<String, Object> dataMap = new HashedMap<>();
        Map<String, String> arguments = new HashedMap<>();
        arguments.put(this.globalVariable.KEY_VARIABLE_DISCORD_ACTION_CREATE_CHANNEL_NAME, event.getChannel().getName());
        arguments.put(this.globalVariable.KEY_VARIABLE_DISCORD_ACTION_CREATE_CHANNEL_WHERE, event.getGuild().getName());
        dataMap.put("Discord Channel Create", event);
        dataMap.put(this.globalVariable.KEY_VARIABLE_ARGUMENTS, arguments);
        for (ActionsTable action : allActions) {
            String actionValues = action.getValues();
            String name;
            String actiontype;
            JSONObject jsonValues = this.parserJson.parseToJson(actionValues);
            if (jsonValues == null) {
                continue;
            }
            try {
                actiontype = jsonValues.getString("Action");
                name = jsonValues.getString("Name");
            } catch (JSONException e) {
                continue;
            }
            if (!actiontype.equals("Create Channel")) {
                continue;
            }
            if (!name.isEmpty() && !name.equals(event.getChannel().getName())) {
                continue;
            }
            List<ReactionsTable> all_reaction = this.reactionsService.getReactionsByActionId(action);
            this.handlingReaction.doReaction(all_reaction, dataMap);
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event)
    {
        List<ActionsTable> allActions = this.actionsService.getAllActions();
        Map<String, Object> dataMap = new HashedMap<>();
        Map<String, String> arguments = new HashedMap<>();
        arguments.put(this.globalVariable.KEY_VARIABLE_DISCORD_ACTION_USER_JOIN_NAME, event.getUser().getName());
        arguments.put(this.globalVariable.KEY_VARIABLE_DISCORD_ACTION_USER_JOIN_WHERE, event.getGuild().getName());
        dataMap.put("Discord Member Join", event);
        dataMap.put(this.globalVariable.KEY_VARIABLE_ARGUMENTS, arguments);
        for (ActionsTable action : allActions) {
            String actionValues = action.getValues();
            String actiontype;
            JSONObject jsonValues = this.parserJson.parseToJson(actionValues);
            if (jsonValues == null) {
                continue;
            }
            try {
                actiontype = jsonValues.getString("Action");
            } catch (JSONException e) {
                continue;
            }
            if (!actiontype.equals("User Join")) {
                continue;
            }
            List<ReactionsTable> all_reaction = this.reactionsService.getReactionsByActionId(action);
            this.handlingReaction.doReaction(all_reaction, dataMap);
        }
    }
}
