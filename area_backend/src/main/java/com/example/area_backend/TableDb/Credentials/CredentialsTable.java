package com.example.area_backend.TableDb.Credentials;

import java.security.NoSuchAlgorithmException;

import com.example.area_backend.Hash;
import com.example.area_backend.TableDb.Users.UsersTable;
import com.example.area_backend.Tuple;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Credentials")
public class CredentialsTable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id")
    private UsersTable usersTable;
    @Column(columnDefinition = "Text")
    private String email;
    @Column(columnDefinition = "Text")
    private String password;
    @Column(columnDefinition = "Text")
    private String salt;

    public CredentialsTable()
    {
    }

    public CredentialsTable(Long id, String email, String password)
    {
        if (id != null)
            this.id = id;
        this.email = email;
        Hash hash = new Hash();
        Tuple<String, String> hashPasswordSalt;
        try {
            hashPasswordSalt = hash.hash(password);
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e);
            return;
        }
        this.password = hashPasswordSalt.getLeft();
        this.salt = hashPasswordSalt.getRight();
    }

    public CredentialsTable(String email, String password, UsersTable usersTable)
    {
        this.id = null;
        this.email = email;
        Hash hash = new Hash();
        Tuple<String, String> hashPasswordSalt;
        try {
            hashPasswordSalt = hash.hash(password);
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e);
            return;
        }
        this.password = hashPasswordSalt.getLeft();
        this.salt = hashPasswordSalt.getRight();
        this.usersTable = usersTable;
    }

    public void setId(Long newId)
    {
        this.id = newId;
    }

    public Long getId()
    {
        return (this.id);
    }

    public void setEmail(String newEmail)
    {
        this.email = newEmail;
    }

    public String getEmail()
    {
        return (this.email);
    }

    public void setPassword(String newPassword)
    {
        Hash hash = new Hash();
        Tuple<String, String> hashPasswordSalt;
        try {
            hashPasswordSalt = hash.hash(newPassword);
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e);
            return;
        }
        this.password = hashPasswordSalt.getLeft();
        this.salt = hashPasswordSalt.getRight();
    }

    public String getPassword()
    {
        return (this.password);
    }

    public String getSalt()
    {
        return (this.salt);
    }

    public void setUser(UsersTable newUserTables)
    {
        this.usersTable = newUserTables;
    }

    public UsersTable getUser()
    {
        return (this.usersTable);
    }
}
