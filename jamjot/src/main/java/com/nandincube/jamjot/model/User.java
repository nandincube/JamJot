package com.nandincube.jamjot.model;

import java.util.ArrayList;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="users")
public class User {
    @Id
    @Column(name = "user_id")
    private String userID;

    @Column(name = "display_name", nullable = false, length =30)
    private String displayName;

    public User(String userID, String displayName){
        this.userID = userID;
        this.displayName = displayName;
    }
    
    public String getUserID(){
        return userID;
    }

    public String getDisplayName(){
        return displayName;
    }

}