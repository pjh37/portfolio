package com.example.myfriends.inviteList;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myfriends.R;

public class InviteListHolder {
    ImageView profilImageView;
    TextView txtName;
    CheckBox selectChk;
    InviteListHolder(View root){
        profilImageView=(ImageView)root.findViewById(R.id.img);
        txtName=(TextView)root.findViewById(R.id.name);
        selectChk=(CheckBox)root.findViewById(R.id.selectChk);
    }
}
