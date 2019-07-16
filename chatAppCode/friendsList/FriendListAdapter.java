package com.example.myfriends.friendsList;

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

public class FriendListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<FriendsListItemVO> friendsListData;
    private LayoutInflater inflater;
    public FriendListAdapter(Context context,int talkList,ArrayList<FriendsListItemVO> list) {
        this.context = context;
        this.layout=talkList;
        this.friendsListData=list;
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public void add(FriendsListItemVO friendsListItemVO){
        friendsListData.add(friendsListItemVO);
    }
    @Override
    public int getCount() {
        return friendsListData.size();
    }

    @Override
    public Object getItem(int position) {
        return friendsListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(layout,parent,false);
            FriendsListHolder friendsListHolder=new FriendsListHolder(convertView);
            convertView.setTag(friendsListHolder);
        }
        FriendsListHolder friendsListHolder=(FriendsListHolder)convertView.getTag();
        ImageView profilImageView=friendsListHolder.profilImageView;
        TextView nameView=friendsListHolder.nameView;
        TextView stateMessageView=friendsListHolder.stateMessageView;
        final FriendsListItemVO friendsListItemVO=friendsListData.get(position);
        profilImageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.custom_item,null));

        nameView.setText(friendsListItemVO.getName());
        stateMessageView.setText(friendsListItemVO.getStateMessage());
        return convertView;
    }
}
