package com.example.area_backend.TableDb.HandleJson;

import com.example.area_backend.TableDb.EnumServices;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;

@Entity
@Table(name = "HandleJson")
public class HandleJsonTable
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "Integer")
    private EnumServices service;
    private String type;
    private String title;
    @Column(columnDefinition = "Text")
    private String value;
    @Column(columnDefinition = "Text")
    private String description;
    @Column(columnDefinition = "Text")
    private String variables;

    public HandleJsonTable() {}

    public HandleJsonTable (
        Long id, EnumServices service, String type,
        String title, String description, String value, String variables
    )
    {
        this.id = id;
        this.service = service;
        this.type = type;
        this.title = title;
        this.description = description;
        this.value = value;
        this.variables = variables;
    }

    public Long getId()
    {
        return (this.id);
    }

    public void setId(Long newId)
    {
        this.id = newId;
    }

    public EnumServices getService()
    {
        return (this.service);
    }

    public void setService(EnumServices newServices)
    {
        this.service = newServices;
    }

    public String getType()
    {
        return (this.type);
    }

    public void setType(String newType)
    {
        this.type = newType;
    }

    public String getTitle()
    {
        return (this.title);
    }

    public void setTitle(String newTitle)
    {
        this.title = newTitle;
    }

    public String getDescription()
    {
        return (this.description);
    }
    public void setDescription(String newDescription)
    {
        this.description = newDescription;
    }

    public String getValue()
    {
        return (this.value);
    }

    public void setValue(String newValue)
    {
        this.value = newValue;
    }

    public String getVariables()
    {
        return (this.variables);
    }

    public void setVarables(String newVariables)
    {
        this.variables = newVariables;
    }
}
