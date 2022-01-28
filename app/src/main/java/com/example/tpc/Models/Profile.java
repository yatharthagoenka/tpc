package com.example.tpc.Models;

public class Profile {
    public String name,roll,email,isAdmin,github,linkedin,instagram;

    public Profile(){}

    public Profile(String name, String roll, String email, String isAdmin, String github, String linkedin, String instagram){
        this.name = name;
        this.roll = roll;
        this.email = email;
        this.isAdmin = isAdmin;
        this.github = github;
        this.linkedin = linkedin;
        this.instagram = instagram;
    }


}
