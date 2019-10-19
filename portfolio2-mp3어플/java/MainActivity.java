package com.example.playmusic;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.playmusic.musiclist.BroadcastActions;
import com.example.playmusic.musiclist.ItemDecoration;
import com.example.playmusic.musiclist.MusicAdapter;
import com.example.playmusic.musiclist.MusicApplication;
import com.example.playmusic.musiclist.MusicVO;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    RecyclerView recyclerView;
    ArrayList<MusicVO> mMusic;
    ArrayList<MusicVO> mMusicCopy;
    MusicAdapter musicAdapter;
    ImageView albumImage;
    TextView title;
    TextView subTitle;
    ImageButton previousBtn;
    ImageButton playOrPauseBtn;
    ImageButton nextBtn;
    boolean fileReadPermission;
    boolean fileWritePermission;
    boolean internetPermission;
    IntentFilter filter=new IntentFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        mMusic=new ArrayList<>();
        mMusicCopy=new ArrayList<>();
        musicAdapter=new MusicAdapter(this,mMusic);
        fileReadPermission=false;
        fileWritePermission=false;
        internetPermission=false;
        previousBtn=(ImageButton)findViewById(R.id.previous);
        playOrPauseBtn=(ImageButton)findViewById(R.id.playOrPause);
        nextBtn=(ImageButton)findViewById(R.id.next);
        albumImage=findViewById(R.id.bottom_albumImage);
        title=(TextView) findViewById(R.id.bottom_Title);
        subTitle=(TextView) findViewById(R.id.bottom_subTitle);
        previousBtn.setOnClickListener(this);
        playOrPauseBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new ItemDecoration());
        recyclerView.setAdapter(musicAdapter);
        filter.addAction(BroadcastActions.PLAY);
        filter.addAction(BroadcastActions.CONNECTED);
        registerReceiver(mBroadcastReceiver,filter);
        new MusicListTask().execute();
    }
     BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BroadcastActions.PLAY)){
                Log.v("서비스정보","곡 정보 : "+intent.getStringExtra("albumUri"));
                Log.v("서비스정보","곡 정보 : "+intent.getStringExtra("title"));
                Log.v("서비스정보","곡 정보 : "+intent.getStringExtra("subTitle"));
                Glide.with(context).load(Uri.parse(intent.getStringExtra("albumUri"))).error(R.drawable.ic_album_default_img).
                        diskCacheStrategy(DiskCacheStrategy.NONE).
                        into(albumImage);
                title.setText(intent.getStringExtra("title"));
                subTitle.setText(intent.getStringExtra("subTitle"));
                playOrPauseBtn.setImageResource(R.drawable.ic_pause_btn);
            }
            if(intent.getAction().equals(BroadcastActions.CONNECTED)){
                Log.v("서비스정보","MusicVO 사이즈 : (broadcast)"+mMusic.size());
                MusicApplication.getInstance().getServiceInterface().setPlayList(mMusic);
            }
        }
    };
    private class MusicListTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            musicAdapter.updataList();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getMusicList();
            return null;
        }
    }

    @Override
    public void onClick(View view) {
        if(view==previousBtn){
            MusicApplication.getInstance().getServiceInterface().previous();
        }else if(view==playOrPauseBtn){
            if(MusicApplication.getInstance().getServiceInterface().isPlaying()){
                playOrPauseBtn.setImageResource(R.drawable.ic_play_btn);
            }else{
                playOrPauseBtn.setImageResource(R.drawable.ic_pause_btn);
            }
            MusicApplication.getInstance().getServiceInterface().togglePlay();
        }else if(view==nextBtn){
            MusicApplication.getInstance().getServiceInterface().next();
        }
    }
    private void updateUI(){
        if(MusicApplication.getInstance().getServiceInterface().isPlaying()){
            playOrPauseBtn.setImageResource(R.drawable.ic_play_btn);

        }else{
            playOrPauseBtn.setImageResource(R.drawable.ic_pause_btn);
        }
    }
    public void checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        == PackageManager.PERMISSION_GRANTED){
            fileReadPermission=true;
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            fileWritePermission=true;
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED){
            internetPermission=true;
        }
        if(!fileReadPermission||!fileWritePermission||!internetPermission){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.INTERNET},100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[]permissions,int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        if(requestCode==100&&grantResults.length>0){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                fileReadPermission=true;
            }
            if(grantResults[1]==PackageManager.PERMISSION_GRANTED){
                fileWritePermission=true;
            }
            if(grantResults[2]==PackageManager.PERMISSION_GRANTED){
                internetPermission=true;
            }
        }
    }
    private void getMusicList(){
        String[] projection={
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA
        };
        Cursor cursor=getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,null,null,null);
        while(cursor.moveToNext()){
            MusicVO musicVO=new MusicVO();
            musicVO.setmAudioId(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
            musicVO.setmTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            musicVO.setmArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            musicVO.setmAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
            musicVO.setmAlbumId(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
            musicVO.setmDuration(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
            musicVO.setmDataPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
            mMusic.add(musicVO);
        }
        cursor.close();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        System.gc();
        finish();
    }
}
