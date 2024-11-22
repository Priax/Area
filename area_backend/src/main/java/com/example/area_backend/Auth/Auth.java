package com.example.area_backend.Auth;

import com.example.area_backend.TableDb.EnumRoles;

public class Auth
{
    private String email;
    private String password;
    private String dateOfBirth;
    private String gender;
    private String name;
    private String surname;
    private String phoneNumber;
    private EnumRoles role;

    public Auth() {}

    public Auth(
        String email, String password, String dateOfBirth, String gender,
        String name, String surname, String phoneNumber, EnumRoles role
    )
    {
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
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
    public String getPassword()
    {
        return (this.password);
    }
    public void setPassword(String newPassword)
    {
        this.password = newPassword;
    }
    public String getDateOfBirth()
    {
        return (this.dateOfBirth);
    }
    public void setDateOfBirth(String newDateOfBirth)
    {
        this.dateOfBirth = newDateOfBirth;
    }
    public String getGender()
    {
        return (this.gender);
    }
    public void setGender(String newGender)
    {
        this.gender = newGender;
    }
    public String getName()
    {
        return (this.name);
    }
    public void setName(String newName)
    {
        this.name = newName;
    }
    public String getSurname()
    {
        return (this.surname);
    }
    public void setSurname(String newSurname)
    {
        this.surname = newSurname;
    }
    public String getPhoneNumber()
    {
        return (this.phoneNumber);
    }
    public void setPhoneNumber(String newPhoneNumber)
    {
        this.phoneNumber = newPhoneNumber;
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
