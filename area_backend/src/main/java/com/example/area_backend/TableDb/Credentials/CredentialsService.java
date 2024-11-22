package com.example.area_backend.TableDb.Credentials;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CredentialsService
{
    private CredentialsTable credentialsTable;
    private Optional<CredentialsTable> existingCredential;
    @Autowired
    private CredentialsRepo credentialsRepo;

    public List<CredentialsTable> getAllCredentials()
    {
        return this.credentialsRepo.findAll();
    }

    public Optional<CredentialsTable> getCredentialById(Long id)
    {
        this.existingCredential = this.credentialsRepo.findById(id);
        if (this.existingCredential.isPresent()){
            return this.existingCredential;
        }
        System.err.println("Credential with id: " + id +  " doesn't exist");
        return Optional.ofNullable(null);
    }

    public Optional<CredentialsTable> saveCredential(CredentialsTable credential)
    {
        try {
            this.credentialsTable = this.credentialsRepo.save(credential);
        } catch (Exception e) {
            System.err.println(e);
            return Optional.ofNullable(null);
        }

        System.out.println("Credential with id: " + this.credentialsTable.getId() +  " saved successfully");
        return Optional.of(this.credentialsTable);
    }

    public Optional<CredentialsTable> updateCredential(CredentialsTable credential)
    {
        try {
            this.existingCredential = this.credentialsRepo.findById(credential.getId());
            if (!this.existingCredential.isPresent()) {
                System.err.println("Credential with id: " + credential.getId() +  " not found");
                return Optional.ofNullable(null);
            }
            this.credentialsTable = this.credentialsRepo.save(credential);
        } catch (Exception e) {
            System.err.println(e);
            return Optional.ofNullable(null);
        }
        System.out.println("Credential with id: " + this.credentialsTable.getId() +  " updated successfully");
        return Optional.of(this.credentialsTable);
    }

    public void deleteCredentialById(Long id)
    {
        try {
            this.credentialsRepo.deleteById(id);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
