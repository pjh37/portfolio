package com.example.playmusic.musiclist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.playmusic.R;
import com.example.playmusic.servicePackage.MusicService;

import java.util.ArrayList;

public class MusicPlayActivity extends AppCompatActivity implements View.OnClickListener{
    SeekBar seekBar;
    TextView currentTime;
    TextView endTime;
    ImageButton playOrPauseBtn;
    ImageButton previousBtn;
    ImageButton nextBtn;
    String audioID;

    boolean isPlaying;
    SeekbarUpdate seekbarUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        Intent intent=getIntent();
        audioID=String.valueOf(intent.getLongExtra("audioID",0));
        currentTime=(TextView)findViewById(R.id.currentTime);
        endTime=(TextView)findViewById(R.id.endTime);
        endTime.setText(intent.getStringExtra("endTime"));
        playOrPauseBtn=(ImageButton)findViewById(R.id.playOrPause);


        seekbarUpdate=new SeekbarUpdate();
        registerReceiver(receiver,new IntentFilter("com.example.PLAY_TO_ACTIVITY"));
    }

    @Override
    public void onClick(View view) {
        if(view==playOrPauseBtn){
            Intent intent=new Intent("com.example.PLAY_TO_SERVICE");
            intent.putExtra("mode","start");
            sendBroadcast(intent);
        }
    }

    BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String mode=intent.getStringExtra("mode");
            if(mode!=null){
                if(mode.equals("start")){
                    int duration=intent.getIntExtra("duration",0);
                    isPlaying=true;
                    seekBar.setMax(duration);
                    seekbarUpdate.start();
                }else if(mode.equals("pause")){
                    isPlaying=false;
                }else if(mode.equals("restart")){

                }
            }
        }
    };
    private class SeekbarUpdate extends Thread{
        int time=0;
        @Override
        public void run(){
            while(isPlaying){
                try{
                    Thread.sleep(1000);
                    currentTime.setText(DateFormat.format("mm:ss",System.currentTimeMillis()));
                    seekBar.setProgress(time);
                    time++;

                }catch (Exception e){
                    Log.e("progress update",e.getMessage());
                }
            }
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(receiver);
        System.gc();
        finish();
    }
}
