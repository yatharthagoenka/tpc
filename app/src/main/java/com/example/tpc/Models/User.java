package com.example.tpc.Models;

public class User {
    public String name,roll,email,isAdmin;

    public User(){}

    public User(String name, String roll, String email, String isAdmin){
        this.name = name;
        this.roll = roll;
        this.email = email;
        this.isAdmin = isAdmin;
    }
}
