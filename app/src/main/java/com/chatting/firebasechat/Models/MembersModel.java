package com.chatting.firebasechat.Models;

public class MembersModel {
    String userId;
    String userName;

    public MembersModel(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public MembersModel() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}


