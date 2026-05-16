package com.project.back_end.DTO;

public class Login {

    // 1. 'email' field:
    //    - Represents the user's email address used for login.
    private String email;

    // 2. 'password' field:
    //    - Represents the user's password used for authentication.
    private String password;

    // 3. Default Constructor
    public Login() {
    }

    // 4. Parameterized Constructor
    public Login(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // 5. Getters and Setters

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}