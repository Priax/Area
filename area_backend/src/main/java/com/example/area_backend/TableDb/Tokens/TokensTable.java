package com.example.area_backend.TableDb.Tokens;

import com.example.area_backend.Jwt;
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
@Table(name = "Tokens")
public class TokensTable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "userId")
    private UsersTable usersTable;
    @Column(columnDefinition = "Text")
    private String accessToken;

    public TokensTable() {}

    public TokensTable(UsersTable usersTable, String accessToken)
    {
        this.id = null;
        this.usersTable = usersTable;
        this.accessToken = accessToken;
    }

    public TokensTable(String accessToken, Long userId)
    {
        this.id = null;
        this.usersTable = new UsersTable(userId);
        this.accessToken = accessToken;
    }

    public Long getId()
    {
        return (this.id);
    }

    public void setID(Long newId)
    {
        this.id = newId;
    }

    public void setUserId(UsersTable newUsersTable)
    {
        this.usersTable = newUsersTable;
    }

    public UsersTable getUserTable()
    {
        return (this.usersTable);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void createToken(Jwt jwt)
    {
        try {
            this.accessToken = jwt.generateToken(this.usersTable.getId());
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
