package com.example.area_backend.TableDb.Users;

import java.time.LocalDate;

import com.example.area_backend.TableDb.EnumRoles;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Users")
public class UsersTable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "Text")
    private String name;
    @Column(columnDefinition = "Text")
    private String surname;
    @Column(columnDefinition = "Text")
    private String email;
    private LocalDate date_of_birth;
    private String gender;
    private String phone_number;
    @Column(columnDefinition = "Integer")
    private EnumRoles role;

    public UsersTable() {}

    public UsersTable(Long id)
    {
        this.id = id;
        this.name = null;
        this.surname = null;
        this.email = null;
        this.date_of_birth = null;
        this.gender = null;
        this.phone_number = null;
        this.role = EnumRoles.USER;
    }

    public UsersTable(
        Long id, String name, String surname, String email,
        LocalDate date_of_birth, String gender, String phone_number,
        EnumRoles role)
    {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.date_of_birth = date_of_birth;
        this.gender = gender;
        this.phone_number = phone_number;
        if (role == null)
            this.role = EnumRoles.USER;
        else
            this.role = role;
    }

    public String getEmail()
    {
        return (this.email);
    }

    public void setEmail(String newEmail)
    {
        this.email = newEmail;
    }

    public Long getId()
    {
        return (this.id);
    }

    public void setId(Long newId)
    {
        this.id = newId;
    }

    public String getSurname()
    {
        return (this.surname);
    }

    public void setSurname(String newSurname)
    {
        this.surname = newSurname;
    }

    public String getName()
    {
        return (this.name);
    }

    public void setName(String newName)
    {
        this.name = newName;
    }

    public LocalDate getDateOfBirth()
    {
        return (this.date_of_birth);
    }

    public void setDateOfBirth(LocalDate newDateOfBirth)
    {
        this.date_of_birth = newDateOfBirth;
    }

    public String getGender()
    {
        return (this.gender);
    }

    public void setGender(String newGender)
    {
        this.gender = newGender;
    }

    public String getPhoneNumber()
    {
        return (this.phone_number);
    }

    public void setPhoneNumber(String newPhoneNumber)
    {
        this.phone_number = newPhoneNumber;
    }

    public EnumRoles getRole()
    {
        return (this.role);
    }

    public void setRole(EnumRoles newRole)
    {
        this.role = newRole;
    }
}
