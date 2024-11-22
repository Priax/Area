package com.example.area_backend.Services.Gmail;

// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Map;
// import java.util.concurrent.ConcurrentHashMap;
// import java.util.concurrent.TimeUnit;
// import java.util.concurrent.TimeoutException;

// import com.google.api.gax.core.InstantiatingExecutorProvider;
// import com.google.api.services.gmail.Gmail;
// import com.google.api.services.gmail.model.Profile;
// import com.google.api.services.gmail.model.WatchRequest;
// import com.google.cloud.pubsub.v1.MessageReceiver;
// import com.google.cloud.pubsub.v1.Subscriber;
// import com.google.gson.Gson;
// import com.google.gson.JsonObject;
// import com.google.pubsub.v1.ProjectSubscriptionName;

// import jakarta.annotation.PreDestroy;

public class GmailListener
{
//     private final String projectId;
//     private final String subscriptionId;
//     private final Map<String, Gmail> gmailServices;
//     private final Map<String, Subscriber> subscribers;
//     private final MessageHandler messageHandler;

//     public interface MessageHandler
//     {
//         void onNewEmail(Gmail service, String email, String messageId, String from, String subject);
//     }

//     public GmailListener(String projectId, String subscriptionId, MessageHandler messageHandler)
//     {
//         this.projectId = projectId;
//         this.subscriptionId = subscriptionId;
//         this.messageHandler = messageHandler;
//         this.gmailServices = new ConcurrentHashMap<>();
//         this.subscribers = new ConcurrentHashMap<>();
//     }

//     public void addGmailAccount(Gmail newGmailService) throws Exception
//     {
//         String email = this.getEmailFromService(newGmailService);
//         if (email == null) {
//             throw new Exception("Email not found in service");
//         }
//         this.gmailServices.put(email, newGmailService);

//         WatchRequest watchRequest = new WatchRequest()
//             .setTopicName("projects/" + projectId + "/topics/gmail-notifications")
//             .setLabelIds(Arrays.asList("INBOX"));

//         newGmailService.users().watch("me", watchRequest).execute();
//         setupSubscriberForAccount(email);
//     }

//     public void removeGmailAccount(String email)
//     {
//         try {
//             if (this.gmailServices.containsKey(email)) {
//                 this.gmailServices.get(email).users().stop("me").execute();
//             }
//             stopSubscriberForAccount(email);
//             gmailServices.remove(email);
//         } catch (IOException e) {
//             System.err.println("Erreur lors de la suppression du compte " + email + ": " + e.getMessage());
//         }
//     }

//     private void setupSubscriberForAccount(String email)
//     {
//         ProjectSubscriptionName subscriptionName =
//         ProjectSubscriptionName.of(projectId, subscriptionId + "-" + email.replace("@", "-").replace(".", "-"));

//         MessageReceiver receiver = (message, consumer) -> {
//             try {
//                 String data = message.getData().toStringUtf8();
//                 JsonObject notification = new Gson().fromJson(data, JsonObject.class);

//                 String emailId = notification.get("emailId").getAsString();
//                 Gmail gmailService = this.gmailServices.get(email);

//                 if (gmailService != null) {
//                     com.google.api.services.gmail.model.Message gmailMessage =
//                         gmailService.users().messages()
//                             .get("me", emailId)
//                             .setFormat("metadata")
//                             .setMetadataHeaders(Arrays.asList("From", "Subject"))
//                             .execute();

//                     String from = gmailMessage.getPayload().getHeaders().stream()
//                         .filter(header -> header.getName().equals("From"))
//                         .findFirst()
//                         .map(header -> header.getValue())
//                         .orElse("Unknown");

//                     String subject = gmailMessage.getPayload().getHeaders().stream()
//                         .filter(header -> header.getName().equals("Subject"))
//                         .findFirst()
//                         .map(header -> header.getValue())
//                         .orElse("(no subject)");

//                     messageHandler.onNewEmail(gmailService, email, emailId, from, subject);
//                 }
//                 consumer.ack();
//             } catch (IOException e) {
//                 System.err.println("Erreur lors du traitement du message pour " + email + ": " + e.getMessage());
//                 consumer.nack();
//             }
//         };
//         Subscriber subscriber = Subscriber.newBuilder(subscriptionName, receiver)
//             .setExecutorProvider(InstantiatingExecutorProvider.newBuilder()
//                 .setExecutorThreadCount(1)
//                     .build())
//             .build();
//         subscriber.startAsync().awaitRunning();
//         subscribers.put(email, subscriber);
//         System.out.println("Écoute démarrée pour le compte " + email);
//     }

//     private void stopSubscriberForAccount(String email)
//     {
//         Subscriber subscriber = subscribers.get(email);
//         if (subscriber != null) {
//             try {
//                 subscriber.stopAsync().awaitTerminated(30, TimeUnit.SECONDS);
//                 subscribers.remove(email);
//             } catch (TimeoutException e) {
//                 System.err.println("Timeout lors de l'arrêt du subscriber pour " + email);
//             }
//         }
//     }

//     @PreDestroy
//     public void stopAll()
//     {
//         for (String email : new ArrayList<>(gmailServices.keySet())) {
//             removeGmailAccount(email);
//         }
//     }

//     public List<String> getMonitoredAccounts()
//     {
//         return new ArrayList<>(gmailServices.keySet());
//     }

//     private String getEmailFromService(Gmail service)
//     {
//         Profile profile;
//         try {
//             profile = service.users().getProfile("me").execute();
//         } catch (IOException e) {
//             System.err.println("Error trying to get email from user: " + e);
//             return (null);
//         }
//         return profile.getEmailAddress();
//     }
}
