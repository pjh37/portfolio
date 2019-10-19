package com.example.myfriends.chatRoomList;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.myfriends.friendsList.FriendsListItemVO;

public class ChatRoomListItemVO implements Parcelable {
    private String chatRoomId;
    private Bitmap profilImage;
    private String name;
    private String lastReceivedMessage;
    private String peopleCount;
    private String date;
    public ChatRoomListItemVO(){}
    public ChatRoomListItemVO(Parcel src){
        this.chatRoomId=src.readString();
        this.name=src.readString();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        dest.writeString(name);
        dest.writeString(lastReceivedMessage);
        dest.writeString(peopleCount);
        dest.writeString(date);
    }
}
