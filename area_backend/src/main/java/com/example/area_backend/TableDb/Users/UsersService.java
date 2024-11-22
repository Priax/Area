package com.example.area_backend.TableDb.Users;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.area_backend.ActionReaction.Delete;
import com.example.area_backend.TableDb.AccountApi.AccountApiService;
import com.example.area_backend.TableDb.Credentials.CredentialsService;
import com.example.area_backend.TableDb.Results.ResultsService;
import com.example.area_backend.TableDb.Tokens.TokensService;

@Service
public class UsersService
{
    private final UsersRepo usersRepo;
    private UsersTable usersTable;
    private Optional<UsersTable> existingUserTable;
    @Autowired
    private CredentialsService credentialsService;
    @Autowired
    private Delete deleteAREA;
    @Autowired
    private AccountApiService accountApiService;
    @Autowired
    private TokensService tokensService;
    @Autowired
    private ResultsService resultsService;

    @Autowired
    public UsersService(UsersRepo usersRepo)
    {
        this.usersRepo = usersRepo;
    }

    public List<UsersTable> getAllUsers()
    {
        return this.usersRepo.findAll();
    }

    public Optional<UsersTable> getUserById(Long requestId)
    {
        this.existingUserTable = this.usersRepo.findById(requestId);
        return (this.existingUserTable);
    }

    public Optional<UsersTable> saveUser(UsersTable user)
    {
        try {
            this.usersTable = this.usersRepo.save(user);
        } catch (Exception e) {
            System.err.println(e);
            return Optional.ofNullable(null);
        }

        System.out.println("User with id: " + this.usersTable.getId() +  " saved successfully");
        return Optional.of(this.usersTable);
    }

    public Optional<UsersTable> updateUser(UsersTable user)
    {
        try {
            this.existingUserTable = this.usersRepo.findById(user.getId());
            if (!this.existingUserTable.isPresent()) {
                System.err.println("User with id: " + user.getId() +  " not found");
                return Optional.ofNullable(null);
            }
            this.usersTable = this.usersRepo.save(user);
        } catch (Exception e) {
            System.err.println(e);
            return Optional.ofNullable(null);
        }
        System.out.println("User with id: " + this.usersTable.getId() +  " updated successfully");
        return Optional.of(this.usersTable);
    }

    public void deleteUserById(Long id)
    {
        this.usersTable = this.usersRepo.findById(id).get();
        try {
            credentialsService.deleteCredentialById(id);
            deleteAREA.deleteAllActionReactionUser(id);
            accountApiService.deleteAccountApiByID(this.usersTable);
            resultsService.deleteResultByUsersTable(this.usersTable);
            tokensService.deleteTokenByUserTable(this.usersTable);
            this.usersRepo.deleteById(id);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
