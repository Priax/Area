package com.example.area_backend.TableDb.Results;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.area_backend.TableDb.Users.UsersTable;

@Service
public class ResultsService
{
    @Autowired
    ResultsRepo resultsRepo;
    private ResultsTable resultsTable;
    private Optional<ResultsTable> existingReactionTable;

    public List<ResultsTable> getAllResults()
    {
        return this.resultsRepo.findAll();
    }

    public Optional<ResultsTable> getResultById(String requestId)
    {
        Long id;
        try {
            id = Long.valueOf(requestId);
        } catch (NumberFormatException e) {
            return Optional.ofNullable(null);
        }
        this.existingReactionTable = this.resultsRepo.findById(id);
        if(this.existingReactionTable.isPresent()){
            return this.existingReactionTable;
        }
        System.err.println("Id: " + requestId +  " doesn't exist");
        return Optional.ofNullable(null);
    }

    public List<ResultsTable> getAllResultByUser(Long id)
    {
        List<ResultsTable> resultsList;
        List<ResultsTable> resultResultsList = new ArrayList<>();
        resultsList = this.resultsRepo.findAll();
        int sizeList = resultsList.size();
        for (int i = 0; i < sizeList; i++) {
            resultsTable = resultsList.get(i);
            if (resultsTable.getUserTable().getId().equals(id)) {
                resultResultsList.add(resultsTable);
            }
        }
        return (resultResultsList);
    }

    public Optional<ResultsTable> saveResult(ResultsTable reaction)
    {
        try {
            this.resultsTable = this.resultsRepo.save(reaction);
        } catch (Exception e) {
            System.err.println(e);
            return Optional.ofNullable(null);
        }

        System.out.println("Result with id: " + this.resultsTable.getId() +  " saved successfully");
        return Optional.of(this.resultsTable);
    }

    public Optional<ResultsTable> updateResult(ResultsTable reaction)
    {
        try {
            this.existingReactionTable = this.resultsRepo.findById(reaction.getId());
            if (!this.existingReactionTable.isPresent()) {
                System.err.println("Result with id: " + reaction.getId() +  " not found");
                return Optional.ofNullable(null);
            }
            this.resultsTable = this.resultsRepo.save(reaction);
        } catch (Exception e) {
            System.err.println(e);
            return Optional.ofNullable(null);
        }
        System.out.println("Result with id: " + this.resultsTable.getId() +  " updated successfully");
        return Optional.of(this.resultsTable);
    }

    public void deleteResultById(Long id)
    {
        try {
            this.resultsRepo.deleteById(id);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void deleteResultByUsersTable(UsersTable userTable)
    {
        try {
            this.resultsRepo.deleteByUserTable(userTable);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
