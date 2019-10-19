package com.example.myfriends.friendsList;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.UriLoader;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.myfriends.MainActivity;
import com.example.myfriends.R;
import com.example.myfriends.dbHelperPackage.ProfileDBHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.Url;
import okhttp3.internal.cache.DiskLruCache;

import static android.os.Environment.isExternalStorageRemovable;

public class profileActivity extends AppCompatActivity implements View.OnClickListener{
    Bitmap profileBitmap;
    String nicName;
    String stateMessage;
    String url="http://192.168.35.42:8006/files";
    RequestQueue queue;
    int scale=0;
    int type;
    static final int PROFILE_IMAGE_REQUEST_CODE =1;
    static final int BACKGROUND_PROFILE_IMAGE_REQUEST_CODE =2;
    private static final BitmapFactory.Options options = new BitmapFactory.Options();
    boolean isSettingClick;
    boolean isCheckEnable;
    TextView txtNicName;
    TextView txtStateMessage;
    TextView txtStateMessageLength;
    EditText editStateMessage;
    ImageView setting_image;
    ImageView profile_image;
    ImageView background_image;
    ImageView editFiliter_image;
    ImageView edit_profile_image;
    ImageView edit_background_profile_image;
    ProfileDBHelper profileDBHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        isSettingClick=false;
        isCheckEnable=false;
        profileDBHelper=new ProfileDBHelper(this);
        txtNicName=(TextView)findViewById(R.id.name);
        txtStateMessage=(TextView)findViewById(R.id.stateMessage);
        txtStateMessageLength=(TextView)findViewById(R.id.stateMessageLength);
        setting_image=(ImageView)findViewById(R.id.setting);
        profile_image=(ImageView)findViewById(R.id.myProfileImage);
        background_image=(ImageView)findViewById(R.id.BackgroundImage);
        editFiliter_image=(ImageView)findViewById(R.id.editBackgroundImageFilter);
        edit_profile_image=(ImageView)findViewById(R.id.edit_profile_image);
        edit_background_profile_image=(ImageView)findViewById(R.id.edit_background_profile_image);
        editStateMessage=(EditText) findViewById(R.id.editStateMessage);
        queue= Volley.newRequestQueue(this);

        Intent intent=getIntent();
        nicName=intent.getStringExtra("nicName");
        stateMessage=intent.getStringExtra("stateMessage");
        type=intent.getIntExtra("type",-1);
        if(type==1){
            setting_image.setVisibility(View.GONE);
        }
        txtNicName.setText(nicName);
        txtStateMessage.setText(stateMessage);
        //Glide.with(this).load("http://192.168.35.34:8006/files/profile/박지효.png").into(profile_image);
        //url+"/profile/"+nicName+".png"
        //Glide.with(this).load(profileDBHelper.getBackgroundProfileImage(nicName)).into(background_image);
        Log.v("nicName",nicName);
        Glide.with(this).load(url+"/profile/"+nicName+".png").diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(profile_image);
        Glide.with(this).load(url+"/profileBackground/"+nicName+".png").diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(background_image);



        setting_image.setOnClickListener(this);
        profile_image.setOnClickListener(this);
        txtStateMessage.setOnClickListener(this);
        edit_background_profile_image.setOnClickListener(this);
        editStateMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtStateMessageLength.setText(editStateMessage.length()+"/60");
            }

            @Override
            public void afterTextChanged(Editable s) {
                txtStateMessageLength.setText(editStateMessage.length()+"/60");

            }
        });
    }

    @Override
    public void onClick(View v) {
        if(setting_image==v){
            if(type==0){
                setting_image.setVisibility(View.GONE);
                isSettingClick = true;
                isCheckEnable = true;
                invalidateOptionsMenu();
                edit_profile_image.setVisibility(View.VISIBLE);
                edit_background_profile_image.setVisibility(View.VISIBLE);
                txtStateMessage.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                txtStateMessage.setText("상태 메세지를 입력해주세요");
            }
        }else if(profile_image==v&&!isSettingClick){
            Intent intent=new Intent(this,profile_image_Activity.class);
            intent.putExtra("nicName",nicName);
            startActivity(intent);
        } else if(profile_image==v&&isSettingClick){
            //갤러리로 이동
            Intent intent=new Intent();
            intent.setDataAndTypeAndNormalize(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PROFILE_IMAGE_REQUEST_CODE);
        }else if(txtStateMessage==v){
            editFiliter_image.setVisibility(View.VISIBLE);
            editStateMessage.setVisibility(View.VISIBLE);
            txtStateMessageLength.setVisibility(View.VISIBLE);
            InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            editStateMessage.requestFocus();
            inputMethodManager.showSoftInput(editStateMessage,0);
        }else if(edit_background_profile_image==v){
            Intent intent=new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, BACKGROUND_PROFILE_IMAGE_REQUEST_CODE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        if(requestCode== PROFILE_IMAGE_REQUEST_CODE &&resultCode==RESULT_OK){
            ImageSendRequest("profile",imagePreprocess(PROFILE_IMAGE_REQUEST_CODE,nicName,data.getDataString(),data.getData()));
        }else if(requestCode==BACKGROUND_PROFILE_IMAGE_REQUEST_CODE&&resultCode==RESULT_OK){
            ImageSendRequest("profileBackground",imagePreprocess(BACKGROUND_PROFILE_IMAGE_REQUEST_CODE,nicName,data.getDataString(),data.getData()));
        }
    }
    public Bitmap imagePreprocess(int type,String nicName,String path,Uri uri){
        try{
            if(type==PROFILE_IMAGE_REQUEST_CODE){
                profileDBHelper.updateProfileImage(nicName,path);
                InputStream in=getContentResolver().openInputStream(uri);
                options.inJustDecodeBounds=true;
                BitmapFactory.decodeStream(in,null,options);
                if (options.outHeight > 100 || options.outWidth > 100) {
                    scale = (int) Math.pow(2, (int) Math.round(Math.log(100 / (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
                }
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                in=getContentResolver().openInputStream(uri);
                final Bitmap bitmap=BitmapFactory.decodeStream(in,null,options);
                return bitmap;
            }else{
                profileDBHelper.updateBackgroundProfileImage(nicName,path);
                InputStream in=getContentResolver().openInputStream(uri);
                options.inJustDecodeBounds=true;
                BitmapFactory.decodeStream(in,null,options);
                if (options.outHeight > 800 || options.outWidth > 600) {
                    scale = (int) Math.pow(2, (int) Math.round(Math.log(600 / (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
                }
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                in=getContentResolver().openInputStream(uri);
                final Bitmap bitmap=BitmapFactory.decodeStream(in,null,options);
                return bitmap;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public void ImageSendRequest(final String path,final Bitmap bitmap){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url+"/"+path, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    Toast.makeText(getApplicationContext(),"변경완료",Toast.LENGTH_LONG).show();
                    if(path.equals("profile")){
                        Glide.with(getApplicationContext()).load(url+"/"+path+"/"+nicName+".png").
                                diskCacheStrategy(DiskCacheStrategy.NONE).
                                skipMemoryCache(true).
                                into(profile_image);
                    }else{
                        Glide.with(getApplicationContext()).load(url+"/"+path+"/"+nicName+".png").
                                diskCacheStrategy(DiskCacheStrategy.NONE).
                                skipMemoryCache(true).
                                into(background_image);
                    }

                    Log.v("profile","변경완료");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("profile","변경에러");
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.v("profile","getParams()");
                Map<String,String> params=new HashMap<>();
                params.put("type",path);
                params.put("name",nicName);
                params.put("image",imageToString(bitmap));
                return params;
            }
        };
        Log.v("profile","프로필이미지 선택함");
        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        queue.add(stringRequest);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.profile_activity_menu,menu);
        MenuItem menuItem=menu.findItem(R.id.check);
        if(isCheckEnable){
            menuItem.setVisible(true);
        }else{
            menuItem.setVisible(false);
        }

        if(isSettingClick){
            menuItem.setEnabled(true);

        }else{
            menuItem.setEnabled(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId()==R.id.check){
            editFiliter_image.setVisibility(View.GONE);
            editStateMessage.setVisibility(View.GONE);
            txtStateMessageLength.setVisibility(View.GONE);
            edit_profile_image.setVisibility(View.GONE);
            edit_background_profile_image.setVisibility(View.GONE);
            setting_image.setVisibility(View.VISIBLE);
            InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(editStateMessage.getWindowToken(),0);
            txtStateMessage.setText(editStateMessage.getText().toString());
            SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("myStateMessage",editStateMessage.getText().toString());
            editor.commit();
            isCheckEnable=false;
            isSettingClick=false;
            txtStateMessage.setPaintFlags(Paint.DEV_KERN_TEXT_FLAG);
            invalidateOptionsMenu();
        }

        return super.onContextItemSelected(menuItem);
    }

    public String imageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] imgBytes=byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes,Base64.DEFAULT);
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.v("onResume","이미지 받기전");
        Glide.with(this).load(url+"/profile/"+nicName+".png").
                diskCacheStrategy(DiskCacheStrategy.NONE).
                skipMemoryCache(true).
                into(profile_image);
        Glide.with(this).load(url+"/profileBackground/"+nicName+".png").
                diskCacheStrategy(DiskCacheStrategy.NONE).
                skipMemoryCache(true).
                into(background_image);
        Log.v("onResume","이미지 받은후");
    }




    @Override
    public void onDestroy(){
        super.onDestroy();
        System.gc();
        finish();
    }
}
