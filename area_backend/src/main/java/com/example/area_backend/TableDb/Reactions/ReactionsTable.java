package com.example.area_backend.TableDb.Reactions;

import java.time.LocalDate;

import com.example.area_backend.TableDb.Actions.ActionsTable;
import com.example.area_backend.TableDb.EnumServices;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Reactions")
public class ReactionsTable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "action_id")
    private ActionsTable actionTable;
    private Integer orderReactions;
    private Integer wait;
    @Column(columnDefinition = "Integer")
    private EnumServices service;
    //@Column(columnDefinition = "json")
    @Column(columnDefinition = "TEXT")
    private String values;
    private LocalDate date;

    public ReactionsTable() {}

    public ReactionsTable(
        Long id, ActionsTable actionsTable, Integer orderReaction,
        Integer wait, EnumServices service, String values, LocalDate date
    )
    {
        this.id = id;
        this.actionTable = actionsTable;
        this.orderReactions = orderReaction;
        this.wait = wait;
        this.service = service;
        this.values = values;
        this.date = date;
    }

    public Long getId()
    {
        return (this.id);
    }

    public void setId(Long newId)
    {
        this.id = newId;
    }

    public ActionsTable getActionTable()
    {
        return (this.actionTable);
    }

    public void setActionTable(ActionsTable newActionTable)
    {
        this.actionTable = newActionTable;
    }

    public Integer getOrderReactions()
    {
        return (this.orderReactions);
    }

    public void setOrderReactions(Integer newOrderReactions)
    {
        this.orderReactions = newOrderReactions;
    }

    public Integer getWait()
    {
        return (this.wait);
    }

    public void setWait(Integer newWait)
    {
        this.wait = newWait;
    }

    public EnumServices getService()
    {
        return (this.service);
    }

    public void setService(EnumServices newService)
    {
        this.service = newService;
    }

    public String getValues()
    {
        return (this.values);
    }

    public void setValues(String newValues)
    {
        this.values = newValues;
    }

    public LocalDate getDate()
    {
        return (this.date);
    }

    public void setDate(LocalDate newDate)
    {
        this.date = newDate;
    }
}
