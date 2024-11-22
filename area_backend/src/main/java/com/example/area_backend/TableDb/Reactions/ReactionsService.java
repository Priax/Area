package com.example.area_backend.TableDb.Reactions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.area_backend.TableDb.Actions.ActionsTable;

@Service
public class ReactionsService
{
    private final ReactionsRepo reactionsRepo;
    private ReactionsTable reactionsTable;
    private Optional<ReactionsTable> existingReactionTable;

    @Autowired
    public ReactionsService(ReactionsRepo reactionsRepo)
    {
        this.reactionsRepo = reactionsRepo;
    }

    public List<ReactionsTable> getAllReactions()
    {
        return this.reactionsRepo.findAll();
    }

    public Optional<ReactionsTable> getReactionById(String requestId)
    {
        Long id;
        try {
            id = Long.valueOf(requestId);
        } catch (NumberFormatException e) {
            return Optional.ofNullable(null);
        }
        this.existingReactionTable = this.reactionsRepo.findById(id);
        if(this.existingReactionTable.isPresent()){
            return this.existingReactionTable;
        }
        System.err.println("Id: " + requestId +  " doesn't exist");
        return Optional.ofNullable(null);
    }

    public List<ReactionsTable> getAllReactionByUser(Long id)
    {
        List<ReactionsTable> reactionsList;
        List<ReactionsTable> resultReactionList = new ArrayList<>();
        reactionsList = this.reactionsRepo.findAll();
        int sizeList = reactionsList.size();
        for (int i = 0; i < sizeList; i++) {
            reactionsTable = reactionsList.get(i);
            if (reactionsTable.getActionTable().getUserTable().getId().equals(id)) {
                resultReactionList.add(reactionsTable);
            }
        }
        return (resultReactionList);
    }

    public List<ReactionsTable> getReactionsByActionId(ActionsTable action)
    {
        List<ReactionsTable> resultReactionList = new ArrayList<>();
        resultReactionList = this.reactionsRepo.findByActionTable(action);
        return (resultReactionList);
    }

    public Optional<ReactionsTable> saveReaction(ReactionsTable reaction)
    {
        try {
            this.reactionsTable = this.reactionsRepo.save(reaction);
        } catch (Exception e) {
            System.err.println(e);
            return Optional.ofNullable(null);
        }

        System.out.println("Reaction with id: " + this.reactionsTable.getId() +  " saved successfully");
        return Optional.of(this.reactionsTable);
    }

    public Optional<ReactionsTable> updateReaction(ReactionsTable reaction)
    {
        try {
            this.existingReactionTable = this.reactionsRepo.findById(reaction.getId());
            if (!this.existingReactionTable.isPresent()) {
                System.err.println("Reaction with id: " + reaction.getId() +  " not found");
                return Optional.ofNullable(null);
            }
            this.reactionsTable = this.reactionsRepo.save(reaction);
        } catch (Exception e) {
            System.err.println(e);
            return Optional.ofNullable(null);
        }
        System.out.println("Reaction with id: " + this.reactionsTable.getId() +  " updated successfully");
        return Optional.of(this.reactionsTable);
    }

    public void deleteReactionById(Long id)
    {
        try {
            this.reactionsRepo.deleteById(id);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
