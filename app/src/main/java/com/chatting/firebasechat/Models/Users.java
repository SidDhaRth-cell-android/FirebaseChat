package com.chatting.firebasechat.Models;

public class Users {
    String id;
    String mail;
    String password;
    String status;
    String username;


    public Users() {
    }

    public Users(String id, String username, String mail, String password, String status) {
        this.id = id;
        this.username = username;
        this.mail = mail;
        this.password = password;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
