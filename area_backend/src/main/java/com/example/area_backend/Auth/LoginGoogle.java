package com.example.area_backend.Auth;

public class LoginGoogle {

    private String email;
    private String name;
    private String surname;
    private String id;
    private String token;
    private boolean verifiedEmail;

    public LoginGoogle() {}

    public LoginGoogle(
        String email, String name, String surname,
        String id, String token, boolean verifiedEmail
    )
    {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.id = id;
        this.token = token;
        this.verifiedEmail = verifiedEmail;
    }

    public String getEmail()
    {
        return (this.email);
    }

    public void setEmail(String newEmail)
    {
        this.email = newEmail;
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

    public String getId()
    {
        return (this.id);
    }

    public void setId(String newId)
    {
        this.id = newId;
    }

    public String getToken()
    {
        return (this.token);
    }

    public void setToken(String newToken)
    {
        this.token = newToken;
    }

    public boolean getVerifiedEmail()
    {
        return (this.verifiedEmail);
    }

    public void setVerifiedEmail(boolean newVerifiedEmail)
    {
        this.verifiedEmail = newVerifiedEmail;
    }
}
