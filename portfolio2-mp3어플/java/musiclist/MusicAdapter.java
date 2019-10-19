package com.example.playmusic.musiclist;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.playmusic.R;
import com.example.playmusic.servicePackage.MusicService;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicHolder> {
    private  final Uri externalUri=Uri.parse("content://media/external/audio/albumart");
    private ArrayList<MusicVO> musicList;
    private Context context;
    public MusicAdapter(Context context,ArrayList<MusicVO> musicList){
        this.context=context;
        this.musicList=musicList;
    }
    @NonNull
    @Override
    public MusicHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.music_list_view,viewGroup,false);
        return new MusicHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicHolder musicHolder,  int i) {
        Uri albumUri= ContentUris.withAppendedId(externalUri,musicList.get(i).getmAlbumId());
        Glide.with(context).load(albumUri).error(R.drawable.ic_album_default_img).
                diskCacheStrategy(DiskCacheStrategy.NONE).
                into(musicHolder.mAlbumImage);
        final String title=musicList.get(i).getmTitle();
        final String subTitle=musicList.get(i).getmArtist();
        final String strAlbumUri=String.valueOf(albumUri);
        musicHolder.mTitle.setText(title);
        musicHolder.mSubTitle.setText(subTitle);
        musicHolder.mDuration.setText(DateFormat.format("mm:ss",musicList.get(i).getmDuration()));
        final long audioID=musicList.get(i).getmAudioId();
        final String endTime=DateFormat.format("mm:ss",musicList.get(i).getmDuration())+"";
        final int currentPosition=i;
        musicHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent=new Intent(BroadcastActions.PLAY);
                intent.putExtra("title",title);
                intent.putExtra("subTitle",subTitle);
                intent.putExtra("albumUri",strAlbumUri);
                Log.v("브로드캐스트로보낸정보",title);
                Log.v("브로드캐스트로보낸정보",subTitle);
                Log.v("브로드캐스트로보낸정보",strAlbumUri);
                context.sendBroadcast(intent);
                MusicApplication.getInstance().getServiceInterface().play(currentPosition);
                MusicApplication.getInstance().getServiceInterface().setCurrentPosition(currentPosition);
                /*
                Intent intent=new Intent(context,MusicService.class);
                intent.putExtra("audioID",audioID);
                intent.putExtra("endTime",endTime);
                intent.putExtra("mode","start");
                context.startService(intent);
                */
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }
    public void addItem(MusicVO musicVO){
        musicList.add(musicVO);
    }
    public void updataList(){
        this.notifyDataSetChanged();
    }
}
