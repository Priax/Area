package com.example.area_backend.TableDb.Actions;

import java.time.LocalDate;

import com.example.area_backend.TableDb.EnumServices;
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
@Table(name = "Actions")
public class ActionsTable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UsersTable usersTable;
    @Column(columnDefinition = "Integer")
    private EnumServices service;
    //@Column(columnDefinition = "json")
    @Column(columnDefinition = "TEXT")
    private String values;
    private String actionName;
    private LocalDate date;

    public ActionsTable(
        Long id, UsersTable usersTable, EnumServices service,
        String values, String actionName, LocalDate date
    )
    {
        this.id = id;
        this.usersTable = usersTable;
        this.service = service;
        this.values = values;
        this.actionName = actionName;
        this.date = date;
    }

    public ActionsTable() {}

    public void setId(Long newId)
    {
        this.id = newId;
    }

    public Long getId()
    {
        return (this.id);
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

    public void setActionName(String newActionName)
    {
        this.actionName = newActionName;
    }

    public String getActionName()
    {
        return (this.actionName);
    }

    public LocalDate getDate()
    {
        return (this.date);
    }

    public UsersTable getUserTable()
    {
        return (this.usersTable);
    }
}
