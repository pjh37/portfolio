package com.example.myfriends.inviteList;

import android.graphics.Bitmap;
import android.widget.CheckBox;

public class InviteListVO {
    private Bitmap profilImage;
    private String name;
    private boolean isChecked;
    private CheckBox selectChk;
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

    public boolean getChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked=checked;
    }

    public CheckBox getSelectChk() {
        return selectChk;
    }

    public void setSelectChk(CheckBox selectChk) {
        this.selectChk = selectChk;
    }
}
