package com.chatting.firebasechat.Models;

public class MessageModel {

    String message;
    String receiverId;
    String senderId;
    String time;
    boolean isseen;


    public MessageModel(String message, String receiverId, String senderId, String time, boolean isseen) {
        this.message = message;
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.time = time;
        this.isseen = isseen;
    }

    public String getTime() {
        return time;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public MessageModel() {
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
