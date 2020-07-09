package com.study.wechattag.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Group implements Serializable, Parcelable {
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.groupName);
        dest.writeString(this.groupId);
        dest.writeString(this.userId);
        dest.writeString(this.groupCount);
    }

    public Group() {
    }

    protected Group(Parcel in) {
        this.groupName = in.readString();
        this.groupId = in.readString();
        this.userId = in.readString();
        this.groupCount = in.readString();
    }

    public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel source) {
            return new Group(source);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };
}
