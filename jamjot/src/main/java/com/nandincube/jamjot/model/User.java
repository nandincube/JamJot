package com.nandincube.jamjot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name="user")
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

    public User() {
    }
    
    public String getUserID(){
        return userID;
    }

    public String getDisplayName(){
        return displayName;
    }

    public void setUserID(String userID){
        this.userID = userID;
    }

    public void setDisplayName(String displayName){
        this.displayName = displayName;
    }

}