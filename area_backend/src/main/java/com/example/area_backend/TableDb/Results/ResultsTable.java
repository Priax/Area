package com.example.area_backend.TableDb.Results;

import java.time.LocalDate;

import com.example.area_backend.TableDb.Actions.ActionsTable;
import com.example.area_backend.TableDb.EnumServices;
import com.example.area_backend.TableDb.Reactions.ReactionsTable;
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
@Table(name = "Results")
public class ResultsTable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UsersTable userTable;
    @ManyToOne
    @JoinColumn(name = "action_id")
    private ActionsTable actionTable;
    @ManyToOne
    @JoinColumn(name = "reaction_id")
    private ReactionsTable reactionsTable;
    @Column(columnDefinition = "Integer")
    private EnumServices service;
    @Column(columnDefinition = "json")
    private String values;
    private LocalDate date;

    public void setId(Long newId)
    {
        this.id = newId;
    }

    public Long getId()
    {
        return (this.id);
    }

    public void setUserTable(UsersTable newUserTable)
    {
        this.userTable = newUserTable;
    }

    public UsersTable getUserTable()
    {
        return (this.userTable);
    }

    public void setActionTable(ActionsTable newActionTable)
    {
        this.actionTable = newActionTable;
    }

    public ActionsTable getActionTable()
    {
        return (this.actionTable);
    }

    public void setReactionsTable(ReactionsTable newReactionTable)
    {
        this.reactionsTable = newReactionTable;
    }

    public ReactionsTable getReactionsTable()
    {
        return (this.reactionsTable);
    }

    public void setService(EnumServices newService)
    {
        this.service = newService;
    }

    public EnumServices getService()
    {
        return (this.service);
    }

    public void setValues(String newValues)
    {
        this.values = newValues;
    }

    public String getValues()
    {
        return (this.values);
    }

    public void setDate(LocalDate newDate)
    {
        this.date = newDate;
    }

    public LocalDate getDate()
    {
        return (this.date);
    }
}
