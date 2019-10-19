package com.example.myfriends.chatRoomList;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.myfriends.friendsList.FriendsListItemVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatRoomListItemVO implements Parcelable {
    private String chatRoomId;
    private Bitmap profilImage;
    private List<String> names;
    private String lastReceivedMessage;
    private String peopleCount;
    private String date;
    public ChatRoomListItemVO(){
        this.lastReceivedMessage="";
        this.peopleCount="0";
        this.date=getTime();
    }
    public ChatRoomListItemVO(Parcel src){
        this.chatRoomId=src.readString();
        names=src.createStringArrayList();
        this.lastReceivedMessage=src.readString();
        this.peopleCount=src.readString();
        this.date=src.readString();
    }
    public String getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(String peopleCount) {
        this.peopleCount = peopleCount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public Bitmap getProfilImage() {
        return profilImage;
    }

    public void setProfilImage(Bitmap profilImage) {
        this.profilImage = profilImage;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public String getLastReceivedMessage() {
        return lastReceivedMessage;
    }

    public void setLastReceivedMessage(String lastReceivedMessage) {
        this.lastReceivedMessage = lastReceivedMessage;
    }

    public static final Parcelable.Creator CREATOR=new Parcelable.Creator(){
        @Override
        public ChatRoomListItemVO createFromParcel(Parcel source) {
            return new ChatRoomListItemVO(source);
        }

        @Override
        public ChatRoomListItemVO[] newArray(int size) {
            return new ChatRoomListItemVO[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chatRoomId);
        dest.writeStringList(names);
        dest.writeString(lastReceivedMessage);
        dest.writeString(peopleCount);
        dest.writeString(date);
    }
    public String getTime(){
        SimpleDateFormat sdf=new SimpleDateFormat("MM월dd일");
        long now=System.currentTimeMillis();
        Date date=new Date(now);
        return sdf.format(date);
    }
}
