package com.example.myfriends.friendsList;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myfriends.R;

public class profile_image_Activity extends AppCompatActivity {
    String nicName;
    String url="http://192.168.35.42:8006/files";
    ImageView original_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image);
        original_image=(ImageView)findViewById(R.id.original_image);
        Intent intent=getIntent();
        nicName=intent.getStringExtra("nicName");
        Glide.with(this).load(url+"/profile/"+nicName+".png").
                diskCacheStrategy(DiskCacheStrategy.NONE).
                skipMemoryCache(true).
                into(original_image);
    }
}
