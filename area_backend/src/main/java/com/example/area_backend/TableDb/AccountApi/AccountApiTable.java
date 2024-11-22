package com.example.area_backend.TableDb.AccountApi;

import com.example.area_backend.TableDb.Users.UsersTable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "AccountApi")
public class AccountApiTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UsersTable usersTable;
    @Column(columnDefinition = "TEXT")
    private String credential;

    public AccountApiTable() {}

    public AccountApiTable(
        Long id, UsersTable usersTable, String credential
    )
    {
        this.id = id;
        this.usersTable = usersTable;
        this.credential = credential;
    }

    public Long getId()
    {
        return (this.id);
    }

    public void setId(Long newId)
    {
        this.id = newId;
    }

    public UsersTable getUsersTable()
    {
        return (this.usersTable);
    }

    public void setUsersTable(UsersTable newUsersTable)
    {
        this.usersTable = newUsersTable;
    }

    public String getCredentials()
    {
        return (this.credential);
    }

    public void setCredentials(String newCredentials)
    {
        this.credential = newCredentials;
    }
}
