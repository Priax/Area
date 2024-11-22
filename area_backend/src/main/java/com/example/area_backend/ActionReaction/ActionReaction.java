package com.example.area_backend.ActionReaction;

import java.util.List;

import com.example.area_backend.TableDb.EnumServices;
import com.example.area_backend.TableDb.Reactions.ReactionsTable;

public class ActionReaction {

    private String userId;
    private EnumServices serviceAction;
    private String actionValues;
    private String date;
    private String actionName;
    private List<ReactionsTable> reactionsList;

    public ActionReaction() {}

    public ActionReaction(
        String userId, EnumServices serviceAction, String actionValues, String date, String actionName,
        List<ReactionsTable> reactionsList
    )
    {
        this.userId = userId;
        this.serviceAction = serviceAction;
        this.actionValues = actionValues;
        this.date = date;
        this.actionName = actionName;
        this.reactionsList = reactionsList;
    }

    public String getUserId()
    {
        return (this.userId);
    }
    public void setUserId(String newUserId)
    {
        this.userId = newUserId;
    }
    public EnumServices getServiceAction()
    {
        return (this.serviceAction);
    }
    public void setServiceAction(EnumServices newServiceAction)
    {
        this.serviceAction = newServiceAction;
    }
    public String getActionValues()
    {
        return (this.actionValues);
    }
    public void setActionValues(String newActionValues)
    {
        this.actionValues = newActionValues;
    }
    public String getDate()
    {
        return (this.date);
    }
    public String getActionName()
    {
        return (this.actionName);
    }
    public void setActionName(String newActionName)
    {
        this.actionName = newActionName;
    }
    public void setDate(String newDate)
    {
        this.date = newDate;
    }
    public List<ReactionsTable> getReactionsList()
    {
        return this.reactionsList;
    }
    public void setReactionsList(List<ReactionsTable> newReactionsList)
    {
        this.reactionsList = newReactionsList;
    }
}
