package com.study.wechattag.model;

import java.io.Serializable;

public class Group implements Serializable {
    public String groupName; //分组名
    public String groupId; //分组Id
    public String userId; //该分组所属用户的Id
    public String groupCount;//分组成员数

    public String getGroupCount() {
        return groupCount;
    }

    public void setGroupCount(String groupCount) {
        this.groupCount = groupCount;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
