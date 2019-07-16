package com.example.myfriends.friendsList;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import java.io.Serializable;

public class FriendsListItemVO implements Parcelable {
    private Bitmap profilImage;
    private String name;
    private String stateMessage;
    public FriendsListItemVO(){}
    public FriendsListItemVO(Parcel src){
        this.name=src.readString();
        this.stateMessage=src.readString();
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

    public String getStateMessage() {
        return stateMessage;
    }

    public void setStateMessage(String stateMessage) {
        this.stateMessage = stateMessage;
    }
    public static final Parcelable.Creator CREATOR=new Parcelable.Creator(){
        @Override
        public FriendsListItemVO createFromParcel(Parcel source) {
            return new FriendsListItemVO(source);
        }

        @Override
        public FriendsListItemVO[] newArray(int size) {
            return new FriendsListItemVO[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(stateMessage);
    }
}
