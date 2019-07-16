package com.example.myfriends.realmVO;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class ChatRoomRealmVO extends RealmObject {
    @PrimaryKey
    private String chatRoomID;

    private int peopleCnt;
    private String date;
    private String peopleNames;

    public String getChatRoomID() {
        return chatRoomID;
    }

    public void setChatRoomID(String chatRoomID) {
        this.chatRoomID = chatRoomID;
    }

    public int getPeopleCnt() {
        return peopleCnt;
    }

    public void setPeopleCnt(int peopleCnt) {
        this.peopleCnt = peopleCnt;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPeopleNames() {
        return peopleNames;
    }

    public void setPeopleNames(String peopleNames) {
        this.peopleNames = peopleNames;
    }
}
