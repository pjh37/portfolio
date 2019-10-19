package com.example.myfriends.chatRoomList;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.myfriends.R;
public class ChatRoomListHolder {
    ImageView profilImageView;
    TextView nameView;
    TextView lastReceivedMessage;
    TextView peopleCount;
    TextView date;
    public ChatRoomListHolder(View root){
        profilImageView=(ImageView)root.findViewById(R.id.img);
        nameView=(TextView)root.findViewById(R.id.name);
        lastReceivedMessage=(TextView)root.findViewById(R.id.lastReceivedMessage);
        peopleCount=(TextView)root.findViewById(R.id.peopleCount);
        date=(TextView)root.findViewById(R.id.date);
    }
}
