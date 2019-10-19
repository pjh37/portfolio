package com.example.myfriends.chat;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.myfriends.R;
import com.example.myfriends.managerPackage.NetworkManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class chat_image_manage_Activity extends AppCompatActivity implements View.OnClickListener{
    int type;
    String fileUrl="";
    String url="http://192.168.35.42:8006";
    ImageView download_button;
    ImageView chat_image;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_image_manage);
        Intent intent=getIntent();
        download_button=(ImageView)findViewById(R.id.download_button);
        download_button.setOnClickListener(this);
        chat_image=(ImageView)findViewById(R.id.chat_image);
        fileUrl=intent.getStringExtra("url");
        Glide.with(this).asBitmap().load(Uri.parse(fileUrl)).
                diskCacheStrategy(DiskCacheStrategy.NONE).
                into(new BitmapImageViewTarget(chat_image){
                    @Override
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);
                        bitmap=resource;
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if(v==download_button){
            Toast.makeText(this,"저장되었습니다",Toast.LENGTH_SHORT).show();
            NetworkManager.getInstance().downloadRequest(getApplicationContext(),fileUrl);
            //new DownloaderTask().execute(fileUrl);
        }
    }
    public void saveBitmapToFile(final Bitmap bitmap){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String ex_storage_path= Environment.getExternalStorageDirectory().getAbsolutePath();
                String folder="/Myfriends";
                String fileName=fileUrl.substring(fileUrl.lastIndexOf("/")+1);
                String savePath=ex_storage_path+folder;
                try{
                    MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,fileName,null);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"
                            + Environment.getExternalStorageDirectory())));
                }catch (Exception e){
                    e.printStackTrace();
                }

                //Toast.makeText(getApplicationContext(),"다운로드완료",Toast.LENGTH_SHORT).show();
                /*
                File file=null;
                FileOutputStream out=null;

                try{
                    file=new File(savePath);
                    if(!file.isDirectory()){
                        file.mkdirs();
                    }
                    out=new FileOutputStream(savePath+fileName);
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
                    Toast.makeText(getApplicationContext(),"다운로드완료",Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    try {
                        out.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                */
            }
        }).start();
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        finish();
        System.gc();
    }
}
