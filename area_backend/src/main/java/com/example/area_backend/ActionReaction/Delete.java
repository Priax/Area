package com.example.area_backend.ActionReaction;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.area_backend.TableDb.Actions.ActionsRepo;
import com.example.area_backend.TableDb.Actions.ActionsTable;
import com.example.area_backend.TableDb.Reactions.ReactionsRepo;
import com.example.area_backend.TableDb.Reactions.ReactionsService;
import com.example.area_backend.TableDb.Reactions.ReactionsTable;


@Component
public class Delete {

    @Autowired
    private ActionsRepo actionsRepo;
    @Autowired
    private ReactionsRepo reactionsRepo;
    @Autowired
    private ReactionsService reactionsService;

    public void deleteAllActionReactionUser(Long userId)
    {
        List<ActionsTable> listActions = actionsRepo.findAll();
        int size = listActions.size();
        for (int i = 0; i < size; i++){
            if (listActions.get(i).getUserTable().getId().equals(userId)){
                deleteActionReaction(listActions.get(i), userId);
            }
        }
    }

    public void deleteActionReaction(
        ActionsTable actionsTable,
        Long userID
    )
    {
        List<ReactionsTable> listReaction = reactionsRepo.findByActionTable(actionsTable);
        int size = listReaction.size();
        for (int i = 0; i < size; i++){
            reactionsService.deleteReactionById(listReaction.get(i).getId());
        }
        actionsRepo.delete(actionsTable);
    }
}
