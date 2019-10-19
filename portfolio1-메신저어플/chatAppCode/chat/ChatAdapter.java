package com.example.myfriends.chat;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myfriends.R;

import java.util.ArrayList;

public class ChatAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<ChatVO> chatData;
    private LayoutInflater inflater;


    public ChatAdapter(Context context,int talkList,ArrayList<ChatVO> list) {
        this.context = context;
        this.layout=talkList;
        this.chatData=list;
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public void add(ChatVO chatVO){
        chatData.add(chatVO);
    }
    @Override
    public int getCount() {
        return chatData.size();
    }

    @Override
    public Object getItem(int position) {
        return chatData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(layout,parent,false);
            ChatHolder chatHolder=new ChatHolder(convertView);
            convertView.setTag(chatHolder);
        }
        ChatHolder chatHolder=(ChatHolder)convertView.getTag();
        LinearLayout chatLayout=chatHolder.chatLayout;
        ImageView imageView=chatHolder.imageView;
        TextView nameView=chatHolder.nameView;
        TextView contentView=chatHolder.contentView;
        TextView dateView=chatHolder.dateView;
        TextView senderDate=chatHolder.senderDate;
        View leftView=chatHolder.leftView;
        View rightView=chatHolder.rightView;
        final ChatVO chatVO=chatData.get(position);
        //0은 전송 1은 수신
        if(chatVO.getType()==0){
            imageView.setVisibility(View.GONE);
            nameView.setText(chatVO.getName());
            chatLayout.setGravity(Gravity.RIGHT);
            contentView.setText(chatVO.getContent());
            contentView.setBackgroundResource(R.drawable.outbox2);
            senderDate.setVisibility(View.VISIBLE);
            senderDate.setText(chatVO.getDate());
            dateView.setVisibility(View.GONE);
            leftView.setVisibility(View.GONE);
            rightView.setVisibility(View.GONE);
        }else if(chatVO.getType()==1){
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.custom_item,null));
            nameView.setText(chatVO.getName());
            chatLayout.setGravity(Gravity.LEFT);
            contentView.setText(chatVO.getContent());
            contentView.setBackgroundResource(R.drawable.inbox2);
            dateView.setVisibility(View.VISIBLE);
            senderDate.setVisibility(View.GONE);
            dateView.setText(chatVO.getDate());
            leftView.setVisibility(View.GONE);
            rightView.setVisibility(View.GONE);
        }



        return convertView;
    }
}
