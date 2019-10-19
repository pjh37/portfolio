package com.example.playmusic.musiclist;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.example.playmusic.servicePackage.MusicService;

import java.util.ArrayList;

public class MusicServiceInterface {
    private ServiceConnection mServiceConnection;
    private MusicService mService;
    public MusicServiceInterface(final Context context){
        mServiceConnection=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service) {
                Log.v("서비스정보","바인드서비스 연결");
                mService=((MusicService.MusicServiceBinder)service).getService();
                context.sendBroadcast(new Intent(BroadcastActions.CONNECTED));
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mServiceConnection=null;
                mService=null;
            }
        };
        context.bindService(new Intent(context,MusicService.class),
                mServiceConnection,Context.BIND_AUTO_CREATE);
    }
    public void setPlayList(ArrayList<MusicVO> list){
        if(mService!=null){
            Log.v("서비스정보","MusicVO 사이즈 : "+list.size());
            mService.setPlayerList(list);
        }
    }
    public void play(int position){
        if(mService!=null) {
            Log.v("서비스정보","play 기능 : "+position);
            mService.play(position);
        }
    }
    public void play(){
        if(mService!=null) {
            mService.play();
        }
    }
    public void pause(){
        if(mService!=null) {
            mService.pause();
        }
    }
    public void previous(){
        if(mService!=null) {
            mService.previous();
        }
    }
    public void next(){
        if(mService!=null) {
            mService.next();
        }
    }
    public int getCurrentPosition(){
        if(mService!=null) {
            return mService.getCurrentPosition();
        }
        return 0;
    }
    public void setCurrentPosition(int currentPosition){
        if(mService!=null) {
            mService.setCurrentPosition(currentPosition);
        }
    }
    public boolean isPlaying(){
        if(mService!=null){
            return mService.isPlaying();
        }
        return false;
    }
    public void togglePlay(){
        if(mService!=null){
            if(isPlaying()){
                mService.pause();
            }else{
                mService.play();
            }
        }

    }
}
