package com.example.area_backend.Services.Discord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.area_backend.Constants;
import com.example.area_backend.HandleArgument;
import com.example.area_backend.Handler;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.channel.concrete.TextChannelManager;

public class DiscordReactions extends Handler
{
    private final HandleArgument handleArgument;
    private final Constants gloabalVariable = new Constants();

    public DiscordReactions()
    {
        this.handleArgument = new HandleArgument();
    }

    private String checkHasDescriminator(String verif)
    {
        String regex = "^(?!.*#\\d{4}$).*";
        if (verif.matches(regex)) {
            return (verif.concat("#0000"));
        } else {
            return (verif);
        }
    }

    public Optional<Map<String, String>> sendMessage(JSONObject jsonValues, Map<String, String> arguments) {
        String message;
        String destination;
        try {
            message = jsonValues.getString("Message");
            destination = jsonValues.getString("Destination");
        } catch (JSONException e) {
            System.err.println("Key " + (jsonValues.has("Message") ? "Destination" : "Message") + " doesn't exist");
            return Optional.empty();
        }

        String[] newData = this.handleArgument.fillWithArgument(arguments, new String[]{message, destination}, new boolean[]{false, true});
        message = newData[0];
        destination = newData[1];

        List<String> messageParts = splitMessage(message);

        if (destination.startsWith("@")) {
            destination = destination.replace("@", "");
            destination = this.checkHasDescriminator(destination);
            User user = this.discordJda.getUserByTag(destination);
            if (user == null) {
                System.err.println("User " + destination + " doesn't exist");
                return Optional.empty();
            }
            user.openPrivateChannel().queue((channel) -> {
                for (String part : messageParts) {
                    channel.sendMessage(part).queue();
                }
            });
            arguments = this.handleArgument.fillOutWithArg(arguments, new String[]{
                this.gloabalVariable.KEY_VARIABLE_DISCORD_REACTION_SEND_MESSAGE_MESSSAGE,
                this.gloabalVariable.KEY_VARIABLE_DISCORD_REACTION_SEND_MESSAGE_WHERE
            }, new String[]{message, user.getName()});
            return Optional.of(arguments);
        }

        if (destination.startsWith("#")) {
            destination = destination.replace("#", "");
            Long channelId = (long) -1;
            List<Guild> allGuilds = this.discordJda.getGuilds();
            for (Guild guild : allGuilds) {
                for (GuildChannel channel : guild.getChannels()) {
                    if (destination.equals(channel.getName())) {
                        channelId = channel.getIdLong();
                        break;
                    }
                }
            }
            if (channelId < 0) {
                System.err.println("Channel " + destination + " doesn't exist");
                return Optional.empty();
            }
            TextChannel textChannel = this.discordJda.getChannelById(TextChannel.class, channelId);
            if (textChannel == null) {
                System.err.println("Channel " + destination + " doesn't exist");
                return Optional.empty();
            }
            for (String part : messageParts) {
                textChannel.sendMessage(part).queue();
            }
            arguments = this.handleArgument.fillOutWithArg(arguments, new String[]{
                this.gloabalVariable.KEY_VARIABLE_DISCORD_REACTION_SEND_MESSAGE_MESSSAGE,
                this.gloabalVariable.KEY_VARIABLE_DISCORD_REACTION_SEND_MESSAGE_WHERE
            }, new String[]{message, textChannel.getName()});
        }
        return Optional.of(arguments);
    }

    private List<String> splitMessage(String message) {
        List<String> parts = new ArrayList<>();
        int messageLength = message.length();
        for (int i = 0; i < messageLength; i += 2000) {
            parts.add(message.substring(i, Math.min(messageLength, i + 2000)));
        }
        return parts;
    }

    public Optional<Map<String, String>> reactMessage(JSONObject jsonValues, Map<String, Object> dataMap, Map<String, String> arguments)
    {
        MessageReceivedEvent event = null;
        if (dataMap.containsKey("Discord React Message")) {
            if (dataMap.get("Discord React Message") instanceof MessageReceivedEvent discordEvent) {
                event = discordEvent;
            }
        }
        if (event == null) {
            System.err.println("Object not instance of MessageReceivedEvent");
            return (Optional.ofNullable(null));
        }
        String message;
        String emojiSend;
        try {
            message = jsonValues.getString("Message");
            emojiSend = jsonValues.getString("Emoji");
        } catch (JSONException e) {
            System.err.println("Key Message or Emoji not in values: " + e);
            return (Optional.ofNullable(null));
        }
        String[] newData = this.handleArgument.fillWithArgument(arguments, new String[]{message, emojiSend}, new boolean[]{false, false});
        message = newData[0];
        emojiSend = newData[1];
        String messageRaw = event.getMessage().getContentRaw();
        try {
            if (message.isEmpty() || messageRaw.equals(message)) {
                event.getMessage().addReaction(Emoji.fromUnicode(emojiSend)).queue();
            }
            arguments = this.handleArgument.fillOutWithArg(arguments, new String[]{
                this.gloabalVariable.KEY_VARIABLE_DISCORD_REACTION_REACT_MESSAGE_MESSAGE,
                this.gloabalVariable.KEY_VARIABLE_DISCORD_REACTION_REACT_MESSAGE_EMOJI
            }, new String[]{messageRaw, emojiSend});
            return (Optional.of(arguments));
        } catch (IllegalArgumentException e) {
            System.err.println("Emoji not found: " + e);
            return (Optional.ofNullable(null));
        }
    }

    public Optional<Map<String, String>> ban(JSONObject jsonValues, Map<String, String> arguments)
    {
        String user;
        String where;
        Long idGuild = (long)-1;
        Long idChannel = (long)-1;
        try {
            user = jsonValues.getString("User");
            where = jsonValues.getString("Where");
        } catch (JSONException e) {
            System.err.println("Key User or Where or Temps not in values: " + e);
            return (Optional.ofNullable(null));
        }
        String[] newData = this.handleArgument.fillWithArgument(arguments, new String[]{user, where}, new boolean[]{false, false});
        user = newData[0];
        where = newData[1];
        List<Guild> allGuilds = this.discordJda.getGuilds();
        int sizeList = allGuilds.size();
        for (int itr = 0; (idGuild == -1 || idChannel == -1) && itr < sizeList; itr++) {
            Guild guild = allGuilds.get(itr);
            if (where.equals(guild.getName())) {
                idGuild = guild.getIdLong();
                break;
            }
            for (GuildChannel channel : guild.getChannels()) {
                if (where.equals(channel.getName())) {
                    idChannel = channel.getIdLong();
                    break;
                }
            }
        }
        if (idChannel == -1 && idGuild == -1) {
            System.err.println("Channel or Guild: " + where + ", doesn't exist");
            return (Optional.ofNullable(null));
        }
        if (idChannel != -1) {
            TextChannel finalChannel = this.discordJda.getChannelById(TextChannel.class, idChannel);
            if (finalChannel == null) {
                System.err.println("Channel: " + where + ", doesn't exist");
                return (Optional.ofNullable(null));
            }
            User userToBan = null;
            for (Member member : finalChannel.getMembers()) {
                if (user.equals(member.getUser().getName())) {
                    userToBan = member.getUser();
                    break;
                }
            }
            if (userToBan == null) {
                System.err.println("User: " + user + ", doesn't exist");
                return (Optional.ofNullable(null));
            }
            TextChannelManager manager = finalChannel.getManager();
            manager.putMemberPermissionOverride(userToBan.getIdLong(),(long)0 , Permission.ALL_CHANNEL_PERMISSIONS).queue();
        } else {
            Guild finalGuild = this.discordJda.getGuildById(idGuild);
            if (finalGuild == null) {
                System.err.println("Guild: " + where + ", doesn't exist");
                return (Optional.ofNullable(null));
            }
            User userToBan = null;
            finalGuild.loadMembers().onSuccess(callback -> {});
            for (Member member : finalGuild.getMembers()) {
                if (user.equals(member.getUser().getName())) {
                    userToBan = member.getUser();
                    break;
                }
            }
            if (userToBan == null) {
                System.err.println("User: " + user + ", doesn't exist");
                return (Optional.ofNullable(null));
            }
            finalGuild.ban(userToBan, 0, TimeUnit.SECONDS).queue();
        }
        arguments = this.handleArgument.fillOutWithArg(arguments, new String[]{
            this.gloabalVariable.KEY_VARIABLE_DISCORD_REACTION_BAN_USER_USERID,
            this.gloabalVariable.KEY_VARIABLE_DISCORD_REACTION_BAN_USER_WHERE,
            this.gloabalVariable.KEY_VARIABLE_DISCORD_REACTION_BAN_USER_WHEREID},
            new String[]{user, where, idChannel == -1 ? idGuild.toString() : idChannel.toString()});
        return (Optional.of(arguments));
    }

    public Optional<Map<String, String>> createChannel(JSONObject jsonValues, Map<String, String> arguments)
    {
        String name;
        String from;
        try {
            name = jsonValues.getString("Name");
            from = jsonValues.getString("From");
        } catch (JSONException e) {
            System.err.println("Key Name or From not in values: " + e);
            return (Optional.ofNullable(null));
        }
        String[] newData = this.handleArgument.fillWithArgument(arguments, new String[]{name, from}, new boolean[]{false, false});
        name = newData[0];
        from = newData[1];
        for (Guild guild : this.discordJda.getGuilds()) {
            if (from.equals(guild.getName())) {
                if (!this.chechAlreadyChannelExist(name, guild.getChannels())) {
                    guild.createTextChannel(name).queue();
                }
                arguments = this.handleArgument.fillOutWithArg(arguments, new String[]{
                    this.gloabalVariable.KEY_VARIABLE_DISCORD_REACTION_CREATE_CHANNEL_NAME,
                    this.gloabalVariable.KEY_VARIABLE_DISCORD_REACTION_CREATE_CHANNEL_FROM},
                    new String[]{name, from});
                return (Optional.of(arguments));
            }
        }
        System.err.println("Guild named " + from + ", doesn't not exist");
        return (Optional.ofNullable(null));
    }

    private boolean chechAlreadyChannelExist(String name, List<GuildChannel> element)
    {
        for (GuildChannel channel : element) {
            if (!name.isEmpty() && channel.getName().equals(name)) {
                return (true);
            }
        }
        return (false);
    }
}
