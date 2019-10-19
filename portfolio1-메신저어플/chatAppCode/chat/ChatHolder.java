package com.example.myfriends.chat;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myfriends.R;

public class ChatHolder {
    LinearLayout chatLayout;
    LinearLayout chat_item_layout;
    ImageView imageView;
    ImageView send_image;
    TextView nameView;
    TextView contentView;
    TextView dateView;
    TextView senderDate;
    View leftView;
    View rightView;
    public ChatHolder(View root){
        chatLayout=(LinearLayout)root.findViewById(R.id.chatLayout);
        chat_item_layout=(LinearLayout)root.findViewById(R.id.chat_item_layout);
        imageView=(ImageView)root.findViewById(R.id.img);
        send_image=(ImageView)root.findViewById(R.id.send_image);
        nameView=(TextView)root.findViewById(R.id.name);
        contentView=(TextView)root.findViewById(R.id.content);
        dateView =(TextView)root.findViewById(R.id.date);
        senderDate=(TextView)root.findViewById(R.id.senderDate);
        leftView=(View)root.findViewById(R.id.leftView);
        rightView=(View)root.findViewById(R.id.rightView);
    }
}
