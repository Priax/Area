package com.example.area_backend.Services.Gmail;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.area_backend.Constants;
import com.example.area_backend.HandleArgument;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.Profile;

public class GmailReactions
{
    private final HandleArgument handleArgument;
    private final Constants gloabalVariable = new Constants();

    public GmailReactions()
    {
        this.handleArgument = new HandleArgument();
    }

    public Optional<Map<String, String>> sendMessage(Gmail service, JSONObject jsonValues, Map<String, String> arguments)
    {
        String to;
        String subject;
        String text;
        String from = this.getEmailFromService(service);
        if (from == null) {
            return (Optional.ofNullable(null));
        }
        try {
            to = jsonValues.getString("To");
            subject = jsonValues.getString("Subject");
            text = jsonValues.getString("Text");
        } catch (JSONException e) {
            System.err.println("Key From or To or Subject or Text not in values: " + e);
            return (Optional.ofNullable(null));
        }
        String[] newData = this.handleArgument.fillWithArgument(arguments, new String[]{to, subject, text});
        to = newData[0];
        subject = newData[1];
        text = newData[2];
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        try {
            email.setFrom(new InternetAddress(from));
            email.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
            email.setSubject(subject);
            email.setText(text);
        } catch (MessagingException e) {
            System.err.println("Error when trying to get adresse: " + e);
            return (Optional.ofNullable(null));
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            email.writeTo(buffer);
        } catch (IOException | MessagingException e) {
            System.err.println("Error when wrapping message: " + e);
            return (Optional.ofNullable(null));
        }
        byte[] rawMessageByte = buffer.toByteArray();
        String encodedMessage = Base64.encodeBase64URLSafeString(rawMessageByte);
        Message message = new Message();
        message.setRaw(encodedMessage);
        try {
            message = service.users().messages().send("me", message).execute();
        } catch (IOException e) {
            System.err.println("Erreur when trying to send message: " + e);
            return (Optional.ofNullable(null));
        }
        arguments = this.handleArgument.fillOutWithArg(arguments, new String[]{
            this.gloabalVariable.KEY_VARIABLE_GMAIL_REACTION_SEND_EMAIL_FROM,
            this.gloabalVariable.KEY_VARIABLE_GMAIL_REACTION_SEND_EMAIL_TO,
            this.gloabalVariable.KEY_VARIABLE_GMAIL_REACTION_SEND_EMAIL_SUBJECT,
            this.gloabalVariable.KEY_VARIABLE_GMAIL_REACTION_SEND_EMAIL_TEXT,
            this.gloabalVariable.KEY_VARIABLE_GMAIL_REACTION_SEND_EMAIL_EMAIL_ID},
            new String[]{from, to, subject, text, message.getId()});
        return (Optional.of(arguments));
    }

    public Optional<Map<String, String>> transfertMessage(Gmail service, JSONObject jsonValues, Map<String, String> arguments)
    {
        String to;
        String messageId;
        Message message;
        MimeMessage forwardMessage;
        MimeMessage mimeMessage;
        ByteArrayOutputStream buffer;
        String subject;
        Object object;
        String from = this.getEmailFromService(service);
        if (from == null) {
            return (Optional.ofNullable(null));
        }
        try {
            to = jsonValues.getString("To");
            messageId = jsonValues.getString("MessageID");
        } catch (JSONException e) {
            System.err.println("Key User or MessageId not in values: " + e);
            return (Optional.ofNullable(null));
        }
        String[] newData = this.handleArgument.fillWithArgument(arguments, new String[]{to, messageId});
        to = newData[0];
        messageId = newData[1];
        try {
            message = service.users().messages().get("me", messageId)
                .setFormat("raw")
                .execute();
        } catch (IOException e) {
            System.err.println("Error when get message: " + e);
            return (Optional.ofNullable(null));
        }
        byte[] emailBytes = Base64.decodeBase64(message.getRaw());
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        try {
            mimeMessage = new MimeMessage(session, new java.io.ByteArrayInputStream(emailBytes));
            forwardMessage = new MimeMessage(session);
            forwardMessage.setFrom(new InternetAddress(from));
            forwardMessage.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
            subject = mimeMessage.getSubject();
            object = mimeMessage.getContent();
            forwardMessage.setSubject("Fwd: " + subject);
            forwardMessage.setContent(object, mimeMessage.getContentType());

        } catch (MessagingException | IOException e) {
            System.err.println("Error when write email: " + e);
            return (Optional.ofNullable(null));
        }
        try {
            buffer = new ByteArrayOutputStream();
            forwardMessage.writeTo(buffer);
        } catch (MessagingException | IOException e) {
            System.err.println("Error when hash message: " + e);
            return (Optional.ofNullable(null));
        }
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);

        Message finalMessage = new Message();
        finalMessage.setRaw(encodedEmail);
        try {
            service.users().messages().send("me", finalMessage).execute();
        } catch (IOException e) {
            System.err.println("Error sending message: " + e);
            return (Optional.ofNullable(null));
        }
        arguments = this.handleArgument.fillOutWithArg(arguments, new String[]{
            this.gloabalVariable.KEY_VARIABLE_GMAIL_REACTION_FORWARD_EMAIL_FROM,
            this.gloabalVariable.KEY_VARIABLE_GMAIL_REACTION_FORWARD_EMAIL_TO,
            this.gloabalVariable.KEY_VARIABLE_GMAIL_REACTION_FORWARD_EMAIL_SUBJECT,
            this.gloabalVariable.KEY_VARIABLE_GMAIL_REACTION_FORWARD_EMAIL_TEXT,
            this.gloabalVariable.KEY_VARIABLE_GMAIL_REACTION_FORWARD_EMAIL_ID},
            new String[]{from, to, subject, object.toString(), message.getId()});
        return (Optional.of(arguments));
    }

    private String getEmailFromService(Gmail service)
    {
        Profile profile;
        try {
            profile = service.users().getProfile("me").execute();
        } catch (IOException e) {
            System.err.println("Error trying to get email from user: " + e);
            return (null);
        }
        return profile.getEmailAddress();
    }
}
