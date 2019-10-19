package com.example.playmusic.servicePackage;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;

import com.example.playmusic.musiclist.BroadcastActions;
import com.example.playmusic.musiclist.MusicVO;
import com.example.playmusic.musiclist.NotificationPlayer;

import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener{
    private final IBinder mBinder=new MusicServiceBinder();
    private ArrayList<MusicVO> mAudioInfo =new ArrayList<>();
    private Uri musicUri;
    private MediaPlayer mMediaPlayer;
    private boolean isPrepared;
    private int mCurrentPosition;
    private int mCurrentPlayPosition;
    private NotificationPlayer mNotificationPlayer;
    public class MusicServiceBinder extends Binder{
        public MusicService getService(){
            return MusicService.this;
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(receiver,new IntentFilter("com.example.PLAY_TO_SERVICE"));
        mNotificationPlayer=new NotificationPlayer(this);
        Log.v("서비스정보","서비스생성");
        mMediaPlayer=new MediaPlayer();
        mMediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.v("서비스정보","서비스에서 prepare listerner 작동" );
                isPrepared=true;
                mediaPlayer.start();
                updateUI();
                updateNotificationPlayer();
                //sendBroadcast(new Intent(BroadcastActions.PLAY));
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null){
            String action=intent.getAction();
            if(action.equals(BroadcastActions.TOGGLE_PLAY)){
                if(isPlaying()){
                    pause();
                }else{
                    play();
                }
            }else if(action.equals(BroadcastActions.PREVIOUS)){
                    previous();
            }else if(action.equals(BroadcastActions.NEXT)){
                    next();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
    BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String mode=intent.getStringExtra("mode");
            if(mode!=null){
                if(mode.equals("start")){
                    //start();
                }else if(mode.equals("pause")){
                    //pause();
                }else if(mode.equals("restart")){
                    //restart();
                }
            }
        }
    };
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void setPlayerList(ArrayList<MusicVO> list){
        Log.v("서비스정보","MusicVO 사이즈 : "+list.size());
        mAudioInfo.clear();
        mAudioInfo.addAll(list);
        Log.v("서비스정보","mAudioInfo 사이즈 : "+mAudioInfo.size());
    }
    public MusicVO getPlayInfo(){
        return mAudioInfo.get(mCurrentPosition);
    }
    public void prepare(){
        try{
            Uri audioUri=Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    String.valueOf(mAudioInfo.get(mCurrentPosition).getmAudioId()));
            Log.v("서비스오디오아이디",audioUri+"");
            mMediaPlayer.setDataSource(getApplicationContext(),audioUri);
            mMediaPlayer.prepareAsync();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void stop(){
        mMediaPlayer.stop();
        mMediaPlayer.reset();
    }
    public void play(int currentPosition){
        Log.v("서비스정보","서비스에서 play(int currentPosition)버튼 눌림 : "+currentPosition );
        this.mCurrentPosition=currentPosition;
        stop();
        prepare();
    }
    public void play(){
        if(isPrepared){
            mMediaPlayer.start();
            mMediaPlayer.seekTo(mCurrentPlayPosition);
            updateNotificationPlayer();
        }
    }
    public void pause(){
        mCurrentPlayPosition=mMediaPlayer.getCurrentPosition();
        mMediaPlayer.pause();
        updateNotificationPlayer();
    }
    public void previous(){
        if(mCurrentPosition>0){
            mCurrentPosition--;
        }else{
            mCurrentPosition=mAudioInfo.size()+1;
        }

        play(mCurrentPosition);
    }
    public void next(){
        if(mAudioInfo.size()-1>mCurrentPosition){
            mCurrentPosition++;
        }else{
            mCurrentPosition=0;
        }
        play(mCurrentPosition);
    }
    public void setCurrentPosition(int currentPosition){
        mCurrentPosition=currentPosition;
    }
    public int getCurrentPosition(){
        return mCurrentPosition;
    }
    public boolean isPlaying(){
        return mMediaPlayer.isPlaying();
    }
    public  MediaPlayer getMediaPlayer(){
        return mMediaPlayer;
    }
    private void updateNotificationPlayer(){
        if(mNotificationPlayer!=null){
            mNotificationPlayer.updateNotificationPlayer();
        }
    }
    private void removeNotificationPlayer(){
        if(mNotificationPlayer!=null){
            mNotificationPlayer.removeNotificationPlayer();
        }
    }
    public void updateUI(){
        Uri externalUri=Uri.parse("content://media/external/audio/albumart");
        Uri albumUri= ContentUris.withAppendedId(externalUri,mAudioInfo.get(mCurrentPosition).getmAlbumId());

        Intent intent=new Intent(BroadcastActions.PLAY);
        intent.putExtra("title",mAudioInfo.get(mCurrentPosition).getmTitle());
        intent.putExtra("subTitle",mAudioInfo.get(mCurrentPosition).getmArtist());
        intent.putExtra("albumUri",String.valueOf(albumUri));
        sendBroadcast(intent);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mMediaPlayer!=null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer=null;
        }
        unregisterReceiver(receiver);
        System.gc();
    }
}
