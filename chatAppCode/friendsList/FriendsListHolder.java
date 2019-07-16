package com.example.myfriends.friendsList;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myfriends.R;

public class FriendsListHolder {
    ImageView profilImageView;
    TextView nameView;
    TextView stateMessageView;
    public FriendsListHolder(View root){
        profilImageView=(ImageView)root.findViewById(R.id.img);
        nameView=(TextView)root.findViewById(R.id.name);
        stateMessageView=(TextView)root.findViewById(R.id.stateMessage);
    }
}
