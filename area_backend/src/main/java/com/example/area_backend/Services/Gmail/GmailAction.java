// package com.example.area_backend.Services.Gmail;

// import java.util.List;
// import java.util.Map;

// import org.apache.commons.collections4.map.HashedMap;
// import org.json.JSONException;
// import org.json.JSONObject;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;

// import com.example.area_backend.HandlingReaction;
// import com.example.area_backend.ParserJson;
// import com.example.area_backend.TableDb.Actions.ActionsService;
// import com.example.area_backend.TableDb.Actions.ActionsTable;
// import com.example.area_backend.TableDb.EnumServices;
// import com.example.area_backend.TableDb.Reactions.ReactionsService;
// import com.example.area_backend.TableDb.Reactions.ReactionsTable;
// import com.google.api.services.gmail.Gmail;


// @Component
// public class GmailAction implements GmailListener.MessageHandler
// {
//     @Autowired
//     private final ActionsService actionsService;
//     @Autowired
//     private final ReactionsService reactionsService;
//     private final HandlingReaction handlingReaction;
//     private final ParserJson parserJson;

//     @Autowired
//     public GmailAction(ActionsService actionsService, ReactionsService reactionsService)
//     {
//         this.actionsService = actionsService;
//         this.reactionsService = reactionsService;
//         this.handlingReaction = new HandlingReaction();
//         this.parserJson = new ParserJson();
//     }

//     @Override
//     public void onNewEmail(Gmail service, String email, String messageId, String from, String subject)
//     {
//         System.out.println("\n\nJ'ai recu un message provenant de " + email + " venant de " + from);
//         List<ActionsTable> allActions = this.actionsService.getAllActions();
//         Map<String, Object> dataMap = new HashedMap<>();
//         Map<String, String> arguments = new HashedMap<>();
//         arguments.put("$From", email);
//         arguments.put("$To", from);
//         arguments.put("$Subject", subject);
//         arguments.put("$MessageID", messageId);
//         dataMap.put("Service Gmail", service);
//         dataMap.put("Arguments", arguments);
//         for (ActionsTable action : allActions) {
//             if (action.getService().equals(EnumServices.GMAIL)) {
//                 JSONObject jsonValues = this.parserJson.parseToJson(action.getValues());
//                 if (jsonValues == null) {
//                     continue;
//                 }
//                 if (!this.checkArgument(jsonValues, from, subject)) {
//                     continue;
//                 }
//                 List<ReactionsTable> all_reaction = this.reactionsService.getReactionsByActionId(action);
//                 this.handlingReaction.doReaction(all_reaction, dataMap);
//             }
//         }
//     }

//     private boolean checkArgument(JSONObject jsonValues, String From, String Subject)
//     {
//         String from;
//         String subject;
//         String actionType;
//         try {
//             actionType = jsonValues.getString("Action");
//             from = jsonValues.getString("From");
//             subject = jsonValues.getString("Subject");
//         } catch (JSONException e) {
//             return (false);
//         }
//         if (!actionType.equals("Received Email")) {
//             return (false);
//         }
//         if (!from.isEmpty() && !from.equals(From)) {
//             return (false);
//         }
//         if (!subject.isEmpty() && !subject.equals(Subject)) {
//             return (false);
//         }
//         return (true);
//     }
// }
