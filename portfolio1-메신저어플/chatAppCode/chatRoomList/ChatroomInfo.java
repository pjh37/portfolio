package com.example.myfriends.chatRoomList;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ChatroomInfo implements Parcelable {
    String chatRoomID;
    ArrayList<String> names;
    public ChatroomInfo(){
        chatRoomID="";
        names=new ArrayList<>();
    }
    public ChatroomInfo(Parcel src){
        this.chatRoomID=src.readString();
        this.names=src.createStringArrayList();
    }
    public String getChatRoomID() {
        return chatRoomID;
    }

    public void setChatRoomID(String chatRoomID) {
        this.chatRoomID = chatRoomID;
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public void setNames(ArrayList<String> names) {
        this.names = names;
    }
    public static final Parcelable.Creator CREATOR=new Parcelable.Creator(){
        @Override
        public ChatroomInfo createFromParcel(Parcel source) {
            return new ChatroomInfo(source);
        }

        @Override
        public ChatroomInfo[] newArray(int size) {
            return new ChatroomInfo[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chatRoomID);
        dest.writeStringList(names);
    }
}
