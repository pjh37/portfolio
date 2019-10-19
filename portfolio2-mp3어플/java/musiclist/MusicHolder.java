package com.example.playmusic.musiclist;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.playmusic.R;

public class MusicHolder extends RecyclerView.ViewHolder{
    public ImageView mAlbumImage;
    public TextView mTitle;
    public TextView mSubTitle;
    public TextView mDuration;
    public MusicHolder(@NonNull View itemView) {
        super(itemView);
        mAlbumImage=(ImageView)itemView.findViewById(R.id.albumImage);
        mTitle=(TextView)itemView.findViewById(R.id.Title);
        mSubTitle=(TextView)itemView.findViewById(R.id.subTitle);
        mDuration=(TextView)itemView.findViewById(R.id.duration);
        itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

            }
        });
    }
}

