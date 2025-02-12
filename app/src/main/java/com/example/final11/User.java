package com.example.final11;

public class User {
    private String email;
    private String password;
    private int userType; // 0 for student 1 for admin

    // empty constructor
    public User() {
    }

    // add user data to firebase
    public User(String email, String password, int userType) {
        this.email = email;
        this.password = password;
        this.userType = userType;
    }


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

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }
}
