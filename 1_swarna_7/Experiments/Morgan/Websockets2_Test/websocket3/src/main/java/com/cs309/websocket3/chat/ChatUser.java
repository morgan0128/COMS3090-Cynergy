package com.cs309.websocket3.chat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.Optional;


@Entity
public class ChatUser {

    @Id
    private String username;

    private String password;

    public ChatUser(){

    }

    public ChatUser(String u, String p){
        this.username = u;
        this.password = p;
    }


    public String getUsername(){
        return this.username;
    }

    public String getPassword(){
        return this.password;
    }




//    public void changePassword();
//
//    public void changeUsername();

}
