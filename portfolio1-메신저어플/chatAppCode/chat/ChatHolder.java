package com.example.myfriends.chat;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myfriends.R;

public class ChatHolder {
    LinearLayout chatLayout;
    ImageView imageView;
    TextView nameView;
    TextView contentView;
    TextView dateView;
    TextView senderDate;
    View leftView;
    View rightView;
    public ChatHolder(View root){
        chatLayout=(LinearLayout)root.findViewById(R.id.chatLayout);
        imageView=(ImageView)root.findViewById(R.id.img);
        nameView=(TextView)root.findViewById(R.id.name);
        contentView=(TextView)root.findViewById(R.id.content);
        dateView =(TextView)root.findViewById(R.id.date);
        senderDate=(TextView)root.findViewById(R.id.senderDate);
        leftView=(View)root.findViewById(R.id.leftView);
        rightView=(View)root.findViewById(R.id.rightView);
    }
}
