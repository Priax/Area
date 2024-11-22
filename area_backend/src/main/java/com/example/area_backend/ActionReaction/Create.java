package com.example.area_backend.ActionReaction;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.area_backend.TableDb.Actions.ActionsRepo;
import com.example.area_backend.TableDb.Actions.ActionsTable;
import com.example.area_backend.TableDb.Reactions.ReactionsRepo;
import com.example.area_backend.TableDb.Reactions.ReactionsTable;
import com.example.area_backend.TableDb.Users.UsersRepo;
import com.example.area_backend.TableDb.Users.UsersTable;
import com.example.area_backend.Tuple;

@Component
public class Create {

    @Autowired
    private UsersRepo usersRepo;
    @Autowired
    private ActionsRepo actionsRepo;
    @Autowired
    private ReactionsRepo reactionsRepo;

    private boolean checkData(ActionReaction actionReaction, List<ReactionsTable> reactionsList)
    {
        if (actionReaction.getActionValues() == null || actionReaction.getDate() == null
        || actionReaction.getServiceAction() == null || reactionsList == null) {
            return (false);
        }
        try {
            LocalDate.parse(actionReaction.getDate());
        } catch (DateTimeParseException e) {
            return (false);
        }
        Long userLongId;
        try {
            userLongId = Long.valueOf(actionReaction.getUserId());
        } catch (NumberFormatException e) {
            return (false);
        }
        Optional<UsersTable> allUsers = usersRepo.findById(userLongId);
        return (allUsers.isPresent());
    }

    public Tuple<Long, Long> create(ActionReaction actionReaction)
    {
        List<ReactionsTable> reactionsList = actionReaction.getReactionsList();
        int size;

        if (!this.checkData(actionReaction, reactionsList)) {
            return (new Tuple<>((long)-1, null));
        }
        size = reactionsList.size();
        Long userLongId = Long.valueOf(actionReaction.getUserId());
        ActionsTable newAction = new ActionsTable(
            null, usersRepo.getReferenceById(userLongId), actionReaction.getServiceAction(),
            actionReaction.getActionValues(),
            actionReaction.getActionName(),
            LocalDate.parse(actionReaction.getDate())
        );
        ActionsTable resultAction = this.actionsRepo.save(newAction);
        ReactionsTable resultReaction = null;
        for (int i = 0; i < size; i++) {
            ReactionsTable newReaction = new ReactionsTable(
                null, resultAction, reactionsList.get(i).getOrderReactions(), reactionsList.get(i).getWait(),
                reactionsList.get(i).getService(), reactionsList.get(i).getValues(), reactionsList.get(i).getDate()
            );
            resultReaction = this.reactionsRepo.save(newReaction);
        }
        return (new Tuple<>(resultAction.getId(), resultReaction == null ? null : resultReaction.getId()));
    }
}
