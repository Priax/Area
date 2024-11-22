package com.example.area_backend;

public class Constants
{
    public final String KEY_VARIABLE_ARGUMENTS = "Arguments";
    public final String ACCOUNT_API_CREDENTIAL_KEY_GMAIL = "GMAIL Emails";
    public final String ACCOUNT_API_CREDENTIAL_KEY_SPOTIFY = "Spotify User ID";
    public final String ACCOUNT_API_CREDENTIAL_KEY_THREADS = "threads_id";

    // Discord - Messages
    public final String KEY_VARIABLE_DISCORD_REACTION_SEND_MESSAGE_MESSSAGE = "$MessageSent";
    public final String KEY_VARIABLE_DISCORD_REACTION_SEND_MESSAGE_WHERE = "$DestinationMessage";

    // Discord - Reactions
    public final String KEY_VARIABLE_DISCORD_REACTION_REACT_MESSAGE_MESSAGE = "$MessageReacted";
    public final String KEY_VARIABLE_DISCORD_REACTION_REACT_MESSAGE_EMOJI = "$EmojiReacted";

    // Discord - Ban
    public final String KEY_VARIABLE_DISCORD_REACTION_BAN_USER_USERID = "$BannedUser";
    public final String KEY_VARIABLE_DISCORD_REACTION_BAN_USER_WHERE = "$OriginUserBanned";
    public final String KEY_VARIABLE_DISCORD_REACTION_BAN_USER_WHEREID = "$OriginIDUserBanned";

    // Discord - Create Channel
    public final String KEY_VARIABLE_DISCORD_REACTION_CREATE_CHANNEL_NAME = "$NameNewChannel";
    public final String KEY_VARIABLE_DISCORD_REACTION_CREATE_CHANNEL_FROM = "$OriginNewChannel";

    // Discord - Received Message
    public final String KEY_VARIABLE_DISCORD_ACTION_RECEIVED_MESSAGE_MESSAGE = "$MessageReceived";
    public final String KEY_VARIABLE_DISCORD_ACTION_RECEIVED_MESSAGE_AUTHOR = "$MessageAuthor";

    // Discord - Change Permissions
    public final String KEY_VARIABLE_DISCORD_ACTION_PERM_CHANGED_USER = "$DiscordUserPermChanged";
    public final String KEY_VARIABLE_DISCORD_ACTION_PERM_CHANGED_GUILD = "$DiscordGuildPermChanged";
    public final String KEY_VARIABLE_DISCORD_ACTION_PERM_CHANGED_CHANNEL = "$DiscordChannelPermChanged";

    // Discord - Create Channel
    public final String KEY_VARIABLE_DISCORD_ACTION_CREATE_CHANNEL_NAME = "$ChannelName";
    public final String KEY_VARIABLE_DISCORD_ACTION_CREATE_CHANNEL_WHERE = "$ChannelLocalisation";

    // Discord - User Join
    public final String KEY_VARIABLE_DISCORD_ACTION_USER_JOIN_NAME = "$NewUser";
    public final String KEY_VARIABLE_DISCORD_ACTION_USER_JOIN_WHERE = "$JoinServerName";

    // Gmail - Send message
    public final String KEY_VARIABLE_GMAIL_REACTION_SEND_EMAIL_FROM = "$EmailFrom";
    public final String KEY_VARIABLE_GMAIL_REACTION_SEND_EMAIL_TO = "$EmailTo";
    public final String KEY_VARIABLE_GMAIL_REACTION_SEND_EMAIL_SUBJECT = "$EmailSubject";
    public final String KEY_VARIABLE_GMAIL_REACTION_SEND_EMAIL_TEXT = "$EmailText";
    public final String KEY_VARIABLE_GMAIL_REACTION_SEND_EMAIL_EMAIL_ID = "$EmailID";

    // Gmail - Forward message
    public final String KEY_VARIABLE_GMAIL_REACTION_FORWARD_EMAIL_FROM = "$ForwardFrom";
    public final String KEY_VARIABLE_GMAIL_REACTION_FORWARD_EMAIL_TO = "$ForwardTo";
    public final String KEY_VARIABLE_GMAIL_REACTION_FORWARD_EMAIL_SUBJECT = "$ForwardSubject";
    public final String KEY_VARIABLE_GMAIL_REACTION_FORWARD_EMAIL_TEXT = "$ForwardText";
    public final String KEY_VARIABLE_GMAIL_REACTION_FORWARD_EMAIL_ID = "$ForwardID";

    // Spotify - Add music
    public final String KEY_VARIABLE_SPOTIFY_REACTION_ADD_MUSIC_USER = "$AddMusicsUser";
    public final String KEY_VARIABLE_SPOTIFY_REACTION_ADD_MUSIC_REAL_USER_ID = "$AddMusicsUserID";
    public final String KEY_VARIABLE_SPOTIFY_REACTION_ADD_MUSIC_ID = "$TrackIds";

    // Spotify - Create Playlist
    public final String KEY_VARIABLE_SPOTIFY_REACTION_CREATE_PLAYLIST_USER = "$PlaylistUser";
    public final String KEY_VARIABLE_SPOTIFY_REACTION_CREATE_PLAYLIST_REAL_USER_ID = "$PlaylistUserID";
    public final String KEY_VARIABLE_SPOTIFY_REACTION_CREATE_PLAYLIST_ID = "$PlaylistId";

    // Spotify - Music Liked
    public final String KEY_VARIABLE_SPOTIFY_ACTION_LIKED_MUSIC_USER = "$LikeUser";
    public final String KEY_VARIABLE_SPOTIFY_ACTION_LIKED_MUSIC_REAL_USER_ID = "$LikeUserID";
    public final String KEY_VARIABLE_SPOTIFY_ACTION_LIKED_MUSIC_ID = "$LikedTracks";
}