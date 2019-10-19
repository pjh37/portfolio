package com.example.myfriends.chat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.myfriends.MainActivity;
import com.example.myfriends.R;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<ChatVO> chatData;
    private LayoutInflater inflater;

    String url="http://192.168.35.42:8006";

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
        LinearLayout chat_item_layout=chatHolder.chat_item_layout;
        ImageView imageView=chatHolder.imageView;
        ImageView send_image=chatHolder.send_image;
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
            nameView.setText("");
            chatLayout.setGravity(Gravity.RIGHT);
            if(!chatVO.getContent().equals("")){
                contentView.setVisibility(View.VISIBLE);
                contentView.setText(chatVO.getContent());
                contentView.setBackgroundResource(R.drawable.inbox2);
            }else{
                contentView.setVisibility(View.GONE);
                contentView.setText("");
            }
            if(chatVO.getFileType()!=AttachedFileType.MESSAGE){
                Log.v("getFileUrl",chatVO.getName());
                send_image.setVisibility(View.VISIBLE);
                if(chatVO.getFileType()==AttachedFileType.CAMERA||
                chatVO.getFileType()==AttachedFileType.ALBUM) {
                    Glide.with(context).asBitmap().load(chatVO.getFileUrl()).
                            override(500,700).
                            centerInside().
                            skipMemoryCache(false).
                            into(new BitmapImageViewTarget(send_image) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    super.setResource(resource);
                                }
                            });
                }else if(chatVO.getFileType()==AttachedFileType.FILE){
                    Log.v("파일전송로그","파일전송로그");
                    Glide.with(context).asBitmap().load(R.drawable.download_button).
                            diskCacheStrategy(DiskCacheStrategy.NONE).
                            skipMemoryCache(true).
                            into(new BitmapImageViewTarget(send_image) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    super.setResource(resource);
                                }
                            });
                    contentView.setLinkTextColor(Color.BLUE);
                    send_image.setVisibility(View.VISIBLE);
                    contentView.setVisibility(View.VISIBLE);
                    contentView.setText(chatVO.getContent());
                }
            }else if(chatVO.getFileType()==AttachedFileType.MESSAGE){
                send_image.setVisibility(View.GONE);
            }
            senderDate.setVisibility(View.VISIBLE);
            senderDate.setText(chatVO.getDate());
            dateView.setVisibility(View.GONE);
            leftView.setVisibility(View.GONE);
            rightView.setVisibility(View.GONE);
        }else if(chatVO.getType()==1){
            imageView.setVisibility(View.VISIBLE);
            Glide.with(context).load(Uri.parse(url+"/files/profile/"+chatVO.getName()+".png")).diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);
            nameView.setText(chatVO.getName());
            chatLayout.setGravity(Gravity.LEFT);
            if(!chatVO.getContent().equals("")){
                contentView.setVisibility(View.VISIBLE);
                contentView.setText(chatVO.getContent());
                contentView.setBackgroundResource(R.drawable.outbox2);
            }else{
                contentView.setVisibility(View.GONE);
                contentView.setText("");
            }
            if(chatVO.getFileType()!=AttachedFileType.MESSAGE){
                Log.v("getFileUrl",chatVO.getName());
                send_image.setVisibility(View.VISIBLE);
                if(chatVO.getFileType()==AttachedFileType.CAMERA||
                        chatVO.getFileType()==AttachedFileType.ALBUM){
                    Glide.with(context).asBitmap().load(chatVO.getFileUrl()).
                            diskCacheStrategy(DiskCacheStrategy.NONE).
                            into(new BitmapImageViewTarget(send_image){
                                @Override
                                protected void setResource(Bitmap resource) {
                                    super.setResource(resource);
                                }
                            });
                }else if(chatVO.getFileType()==AttachedFileType.FILE){
                    send_image.setVisibility(View.VISIBLE);
                    contentView.setVisibility(View.VISIBLE);
                    send_image.setImageResource(R.drawable.download_button);
                    contentView.setText(chatVO.getContent());
                    chat_item_layout.setBackgroundResource(R.drawable.outbox2);
                }
            }else{
                send_image.setVisibility(View.GONE);
            }
            dateView.setVisibility(View.VISIBLE);
            senderDate.setVisibility(View.GONE);
            dateView.setText(chatVO.getDate());
            leftView.setVisibility(View.GONE);
            rightView.setVisibility(View.GONE);
        }
        return convertView;
    }
    private static final BitmapFactory.Options options = new BitmapFactory.Options();

}
