package com.example.tpc;

public class User {
    public String name,roll,email,isAdmin,plink;

    public User(){}

    public User(String name, String roll, String email, String isAdmin, String plink){
        this.name = name;
        this.roll = roll;
        this.email = email;
        this.isAdmin = isAdmin;
        this.plink = plink;
    }
}
