package com.example.myfriends.chat;

import android.graphics.Bitmap;
import android.view.View;

public class ChatVO {
    private int type;
    private Bitmap bimage;
    private String name;
    private String content;
    private String date;
    private String senderDate;
    private String fileUrl;
    private int fileType;
    private View leftView;
    private View rightView;

    public String getSenderDate() {
        return senderDate;
    }

    public void setSenderDate(String senderDate) {
        this.senderDate = senderDate;
    }

    public View getLeftView() {
        return leftView;
    }

    public void setLeftView(View leftView) {
        this.leftView = leftView;
    }

    public View getRightView() {
        return rightView;
    }

    public void setRightView(View rightView) {
        this.rightView = rightView;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Bitmap getBimage() {
        return bimage;
    }

    public void setBimage(Bitmap bimage) {
        this.bimage = bimage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }
}
