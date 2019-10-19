package com.example.myfriends.inviteList;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myfriends.R;
import com.example.myfriends.friendsList.FriendsListHolder;
import com.example.myfriends.friendsList.FriendsListItemVO;

import java.util.ArrayList;

public class InviteListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<InviteListVO> inviteListData;
    private LayoutInflater inflater;
    String url="http://192.168.35.42:8006/files";
    public InviteListAdapter(Context context,int talkList,ArrayList<InviteListVO> list) {
        this.context = context;
        this.layout=talkList;
        this.inviteListData=list;
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public void add(InviteListVO inviteListVo){
        inviteListData.add(inviteListVo);
    }
    @Override
    public int getCount() {
        return inviteListData.size();
    }

    @Override
    public Object getItem(int position) {
        return inviteListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(layout,parent,false);
            InviteListHolder inviteListHolder=new InviteListHolder(convertView);
            convertView.setTag(inviteListHolder);
        }
        InviteListHolder inviteListHolder=(InviteListHolder)convertView.getTag();
        ImageView profilImageView=inviteListHolder.profilImageView;
        TextView txtName=inviteListHolder.txtName;
        CheckBox selectChk=inviteListHolder.selectChk;
        final InviteListVO inviteListItemVO=inviteListData.get(position);
        profilImageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.custom_item1,null));

        Glide.with(context).load(url+"/profile/"+inviteListItemVO.getName()+".png").diskCacheStrategy(DiskCacheStrategy.NONE).into(profilImageView);
        txtName.setText(inviteListItemVO.getName());
        inviteListItemVO.setSelectChk(selectChk);
        selectChk.setChecked(false);
        selectChk.setClickable(false);
        selectChk.setFocusable(false);
        /*
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectChk.isChecked()){
                    selectChk.setChecked(false);
                }else{
                    selectChk.setChecked(true);
                }

            }
        });
        */
        return convertView;
    }
}
