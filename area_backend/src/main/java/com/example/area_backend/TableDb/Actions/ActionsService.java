package com.example.area_backend.TableDb.Actions;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActionsService
{
    private ActionsTable actionsTable;
    private final ActionsRepo actionsRepo;
    private Optional<ActionsTable> existingActionTable;

    @Autowired
    public ActionsService(ActionsRepo actionsRepo)
    {
        this.actionsRepo = actionsRepo;
    }

    public List<ActionsTable> getAllActions()
    {
        return this.actionsRepo.findAll();
    }

    public Optional<ActionsTable> getActionById(String requestId)
    {
        Long id;
        try {
            id = Long.valueOf(requestId);
        } catch (NumberFormatException e) {
            return Optional.ofNullable(null);
        }
        this.existingActionTable = this.actionsRepo.findById(id);
        if(this.existingActionTable.isPresent()){
            return this.existingActionTable;
        }
        System.err.println("Id: " + requestId +  " doesn't exist");
        return Optional.ofNullable(null);
    }

    public List<ActionsTable> getAllActionByUser(Long id)
    {
        List<ActionsTable> actionsList;
        List<ActionsTable> resultActionList = new ArrayList<>();
        actionsList = this.actionsRepo.findAll();
        int sizeList = actionsList.size();
        for (int i = 0; i < sizeList; i++) {
            actionsTable = actionsList.get(i);
            if (actionsTable.getUserTable().getId().equals(id)) {
                resultActionList.add(actionsTable);
            }
        }
        return (resultActionList);
    }

    public Optional<ActionsTable> saveAction(ActionsTable action)
    {
        try {
            this.actionsTable = this.actionsRepo.save(action);
        } catch (Exception e) {
            System.err.println(e);
            return Optional.ofNullable(null);
        }

        System.out.println("Action with id: " + this.actionsTable.getId() +  " saved successfully");
        return Optional.of(this.actionsTable);
    }

    public Optional<ActionsTable> updateAction(ActionsTable action)
    {
        try {
            this.existingActionTable = this.actionsRepo.findById(action.getId());
            if (!this.existingActionTable.isPresent()) {
                System.err.println("Action with id: " + action.getId() +  " not found");
                return Optional.ofNullable(null);
            }
            this.actionsTable = this.actionsRepo.save(action);
        } catch (Exception e) {
            System.err.println(e);
            return Optional.ofNullable(null);
        }
        System.out.println("Action with id: " + this.actionsTable.getId() +  " updated successfully");
        return Optional.of(this.actionsTable);
    }

    public void deleteActionById(Long id)
    {
        try {
            this.actionsRepo.deleteById(id);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
