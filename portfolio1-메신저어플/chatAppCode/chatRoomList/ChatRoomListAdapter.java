package com.example.myfriends.chatRoomList;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myfriends.R;

import java.util.ArrayList;

public class ChatRoomListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<ChatRoomListItemVO> chatRoomListData;
    private LayoutInflater inflater;


    public ChatRoomListAdapter(Context context,int talkList,ArrayList<ChatRoomListItemVO> list) {
        this.context = context;
        this.layout=talkList;
        this.chatRoomListData=list;
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public void add(ChatRoomListItemVO chatRoomListItemVO){
        chatRoomListData.add(chatRoomListItemVO);
    }
    @Override
    public int getCount() {
        return chatRoomListData.size();
    }

    @Override
    public Object getItem(int position) {
        return chatRoomListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(layout,parent,false);
            ChatRoomListHolder chatRoomListHolder=new ChatRoomListHolder(convertView);
            convertView.setTag(chatRoomListHolder);
        }
        ChatRoomListHolder chatRoomListHolder=(ChatRoomListHolder)convertView.getTag();
        ImageView profilImageView=chatRoomListHolder.profilImageView;
        TextView nameView=chatRoomListHolder.nameView;
        TextView lastReceivedMessage=chatRoomListHolder.lastReceivedMessage;
        TextView peopleCount=chatRoomListHolder.peopleCount;
        TextView date=chatRoomListHolder.date;
        final ChatRoomListItemVO chatRoomListItemVO=chatRoomListData.get(position);
        profilImageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.custom_item,null));
        nameView.setText(chatRoomListItemVO.getName());

        lastReceivedMessage.setText(chatRoomListItemVO.getLastReceivedMessage());
        peopleCount.setText(chatRoomListItemVO.getPeopleCount());
        date.setText(chatRoomListItemVO.getDate());
        return convertView;
    }
}
