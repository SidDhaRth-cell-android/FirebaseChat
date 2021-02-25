package com.chatting.firebasechat.Models;

import java.util.List;

public class GroupNameModel {
    String groupId;
    String groupTitle;
    String cratedBy;
    String timestamp;


    public GroupNameModel() {
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public String getCratedBy() {
        return cratedBy;
    }

    public void setCratedBy(String cratedBy) {
        this.cratedBy = cratedBy;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public GroupNameModel(String groupId, String groupTitle, String cratedBy, String timestamp) {
        this.groupId = groupId;
        this.groupTitle = groupTitle;
        this.cratedBy = cratedBy;
        this.timestamp = timestamp;
    }
}
