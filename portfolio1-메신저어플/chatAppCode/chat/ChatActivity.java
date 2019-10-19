package com.example.myfriends.chat;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myfriends.managerPackage.AppLifeManager;
import com.example.myfriends.BuildConfig;
import com.example.myfriends.managerPackage.NetworkManager;
import com.example.myfriends.R;
import com.example.myfriends.dbHelperPackage.SQLiteDBHelper;
import com.example.myfriends.fileList.FileFindActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.Response;
import okio.Okio;
//import static com.example.myfriends.MainActivity.socket;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener{
    private static final BitmapFactory.Options options = new BitmapFactory.Options();
    private int scale=0;
    private String url="http://192.168.35.42:8006";
    private String uploadFilePath="";
    private String getTotalPath="";
    private Uri photoURI=null;
    private String absolutePath="";
    private String nicName;
    private String chatRoomId;
    private String fileinfo="";
    private ChatAdapter chatAdapter;
    private ListView chatListView;
    private ArrayList<ChatVO> chatData;
    private long now;
    private Date date;
    private SimpleDateFormat sdf=new SimpleDateFormat("hh:mm");
    private TextView txtContent;
    private TextView txtSendMessage;
    private Intent intent;
    private Button sendBtn;
    private Button fileSendBtn;
    private SQLiteDBHelper sqLiteDBHelper;
    private ArrayList<ChatDTO> chatDTOS;
    private TableLayout sendMenus;
    private ImageView camera;
    private ImageView album;
    private ImageView video;
    private ImageView file;
    private boolean sendMenusToggle=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        registerReceiver(receiver,new IntentFilter("com.example.RECEIVE_ACTION"));
        intent=getIntent();
        chatRoomId=intent.getStringExtra("chatRoomID");
        sqLiteDBHelper=new SQLiteDBHelper(this);
        chatDTOS=new ArrayList<>();
        chatData=new ArrayList<>();
        chatListView=(ListView)findViewById(R.id.chatListView);
        chatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(chatData.get(position).getFileType()==AttachedFileType.CAMERA||
                        chatData.get(position).getFileType()==AttachedFileType.ALBUM){
                    Intent intent=new Intent(getApplicationContext(),chat_image_manage_Activity.class);
                    intent.putExtra("url",chatData.get(position).getFileUrl());
                    startActivity(intent);
                }
                if(chatData.get(position).getFileType()==AttachedFileType.FILE){
                    NetworkManager.getInstance().downloadRequest(getApplicationContext(),chatData.get(position).getFileUrl());
                }

            }
        });
        chatAdapter=new ChatAdapter(this,R.layout.chatlistview_item,chatData);
        chatListView.setAdapter(chatAdapter);

        sendBtn=(Button)findViewById(R.id.sendBtn);
        fileSendBtn=(Button)findViewById(R.id.fileSendBtn);
        sendMenus=(TableLayout)findViewById(R.id.sendMenus);
        camera=(ImageView)findViewById(R.id.camera);
        album=(ImageView)findViewById(R.id.album);
        video=(ImageView)findViewById(R.id.video);
        file=(ImageView)findViewById(R.id.file);
        sendBtn.setOnClickListener(this);
        fileSendBtn.setOnClickListener(this);
        camera.setOnClickListener(this);
        album.setOnClickListener(this);
        video.setOnClickListener(this);
        file.setOnClickListener(this);
        sendMenus.setVisibility(View.GONE);
        nicName=intent.getStringExtra("nicName");


        txtContent =(TextView)findViewById(R.id.content);
        txtSendMessage=(TextView)findViewById(R.id.sendMessage);

        chatAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                chatListView.setSelection(chatAdapter.getCount());
            }
        });

        new ChatListPreLoad().execute();
        Log.v("chatDTOS.size()",chatDTOS.size()+"");

    }
    BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(chatRoomId.equals(intent.getStringExtra("chatRoomID"))){
                int type=intent.getIntExtra("type",1);
                String nicName=intent.getStringExtra("nicName");
                String content=intent.getStringExtra("content");
                String fileUrl=intent.getStringExtra("url");
                ChatVO chatVO=new ChatVO();
                chatVO.setType(type);
                chatVO.setContent(content);
                chatVO.setName(nicName);
                chatVO.setFileUrl(fileUrl);
                chatVO.setDate(getTime());
                chatAdapter.add(chatVO);
                chatAdapter.notifyDataSetChanged();
            }
        }
    };
    public String getTime(){
        now=System.currentTimeMillis();
        date=new Date(now);
        return sdf.format(date);
    }
    @Override
    public void onClick(View v) {
        if(v==sendBtn) {
            sendMessage(false,AttachedFileType.MESSAGE);
        }else if(v==fileSendBtn){
            if(!sendMenusToggle){
                sendMenus.setVisibility(View.VISIBLE);
                sendMenusToggle=true;
            }else{
                sendMenus.setVisibility(View.GONE);
                sendMenusToggle=false;
            }
        }else if(v==camera){

            photoURI= FileProvider.getUriForFile(getApplicationContext(),
                    BuildConfig.APPLICATION_ID+".provider",createTempImageFile());
            Log.v("absolutePath","photoURI : "+photoURI.getPath());
            Intent camera_intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            camera_intent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
            startActivityForResult(camera_intent, AttachedFileType.CAMERA);

        }else if(v==album){
            Intent album_intent=new Intent(Intent.ACTION_GET_CONTENT);
            album_intent.setDataAndTypeAndNormalize(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
            //album_intent.putExtra(album_intent.EXTRA_ALLOW_MULTIPLE,true);
            startActivityForResult(album_intent, AttachedFileType.ALBUM);
        }else if(v==video){
            Intent video_intent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            video_intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT,1024*1024*10);
            //video_intent.putExtra(MediaStore.EXTRA_OUTPUT,)
            startActivityForResult(video_intent,AttachedFileType.VIDEO);

        }else if(v==file){
            Intent file_intent=new Intent(getApplicationContext(), FileFindActivity.class);
            startActivityForResult(file_intent,AttachedFileType.FILE);
        }
    }
    public File createTempImageFile(){
        File filePath=null;
        try{
            String dirPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/myfriends/";
            String imageFileName="img-"+System.currentTimeMillis();
            //absolutePath=dirPath+imageFileName;
            Log.v("absolutePath","createTempImageFile : "+dirPath+imageFileName);
            File dir=new File(dirPath);
            if(!dir.exists()){
                dir.mkdir();
            }
            filePath=File.createTempFile(imageFileName,".jpg",dir);
            if(!filePath.exists()){
                filePath.createNewFile();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return filePath;
    }
    public void sendMessage(boolean isAttachedFileExist,int type){
        if(txtSendMessage.getText().toString().equals("")){
            if(!isAttachedFileExist){
                return;
            }
        }
        String content=txtSendMessage.getText().toString();
        NetworkManager.getInstance().messageSend(
                chatRoomId,
                nicName,
                content,
                uploadFilePath,
                type,
                getTime());
        ChatVO chatVO=new ChatVO();
        chatVO.setType(0);
        chatVO.setName(nicName);
        chatVO.setFileType(type);
        Log.v("uploadfilepath",uploadFilePath);
        if(isAttachedFileExist){ chatVO.setFileUrl(getTotalPath); }
        else{
            chatVO.setFileUrl("");
        }
        if(content.equals(""))chatVO.setContent("");
        else chatVO.setContent(content);

        if(type==AttachedFileType.FILE){
            chatVO.setContent(fileinfo);
        }


        chatVO.setDate(getTime());
        chatAdapter.add(chatVO);
        chatAdapter.notifyDataSetChanged();
        ChatDTO chatDTO=new ChatDTO();
        chatDTO.setChatRoomID(chatRoomId);
        chatDTO.setClientID(nicName);
        chatDTO.setFileType(type);
        if(content.equals(""))chatDTO.setMessage("");
        else chatDTO.setMessage(content);
        chatDTO.setMessageType(0);
        if(isAttachedFileExist){ chatDTO.setFileUrl(getTotalPath); }
        else{
            chatDTO.setFileUrl("");
        }
        if(type==AttachedFileType.FILE){
            chatDTO.setMessage(fileinfo);
        }
        chatDTO.setDate(getTime());
        sqLiteDBHelper.chatInsert(chatDTO);
        txtSendMessage.setText("");
        if(isAttachedFileExist){
            uploadFilePath="";
            getTotalPath="";
            fileinfo="";
        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==AttachedFileType.CAMERA&&resultCode==RESULT_OK){
            Toast.makeText(getApplicationContext(),"변경완료",Toast.LENGTH_LONG).show();
            //String absolutePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/myfriends/"+photoURI.getLastPathSegment();
            Log.v("absolutePath","onActivityResult : "+absolutePath);
            new fileSendToServerTask().execute(photoURI.toString(),String.valueOf(AttachedFileType.CAMERA));
        }else if(requestCode==AttachedFileType.ALBUM&&resultCode==RESULT_OK){
            Toast.makeText(getApplicationContext(),"변경완료",Toast.LENGTH_LONG).show();
            new fileSendToServerTask().execute(data.getDataString(),String.valueOf(AttachedFileType.ALBUM));
        }else if(requestCode==AttachedFileType.VIDEO&&resultCode==RESULT_OK){
            Toast.makeText(getApplicationContext(),"변경완료",Toast.LENGTH_LONG).show();
        }
        else if(requestCode==AttachedFileType.FILE&&resultCode==RESULT_OK){
            //Toast.makeText(getApplicationContext(),"변경완료",Toast.LENGTH_LONG).show();

            Toast.makeText(getApplicationContext(),data.getStringExtra("filePath"),Toast.LENGTH_LONG).show();

            new fileSendToServerTask().execute(data.getStringExtra("filePath"),String.valueOf(AttachedFileType.FILE));
        }
    }
    public String getImagePath(Uri uri){
        Log.v("절대경로","uri : "+uri.toString());
        String filePath=null;
        if(pathType(uri)){
            String docId=DocumentsContract.getDocumentId(uri);
            String[] split=docId.split(":");
            String type=split[0];
            Uri contentUri=null;
            if("image".equals(type)){
                contentUri=MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            }
            String selection=MediaStore.Images.Media._ID+"=?";
            String[] selectionargs=new String[]{split[1]};
            String columm="_data";
            String[] proj={columm};
            Cursor cursor=getContentResolver().query(contentUri,proj,selection,selectionargs,null);

            if(cursor!=null&&cursor.moveToFirst()){
                int column_index=cursor.getColumnIndexOrThrow(columm);
                filePath=cursor.getString(column_index);
            }
            Log.v("절대경로","도큐먼트식 : "+filePath);
            cursor.close();
            return filePath;
        }else{
            String selection=MediaStore.Images.Media._ID+"=?";
            String[] selectionargs=new String[]{uri.getLastPathSegment()};
            String columm="_data";
            String[] proj={columm};
            Cursor cursor=getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,proj,selection,selectionargs,null);
            if(cursor!=null&&cursor.moveToFirst()){
                int column_index=cursor.getColumnIndexOrThrow(columm);
                filePath=cursor.getString(column_index);
            }
            Log.v("절대경로","세그먼트 : "+filePath);
            cursor.close();
            return filePath;
        }

    }
    public boolean pathType(Uri uri){
            if(uri.toString().contains("com.android.providers")){
                return true;//도큐먼트식
            }
            return false;//세그먼트식
    }


    public Bitmap imagePreprocess(Uri uri){
        try{
            Log.v("uriinput","uri들어옴");
            InputStream in=getContentResolver().openInputStream(uri);
            options.inJustDecodeBounds=true;
            BitmapFactory.decodeStream(in,null,options);
            if (options.outHeight > 600 || options.outWidth > 300) {
                scale = (int) Math.pow(2, (int) Math.round(Math.log(300 / (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
            }
            options.inSampleSize = scale;
            options.inJustDecodeBounds = false;
            in=getContentResolver().openInputStream(uri);
            final Bitmap bitmap=BitmapFactory.decodeStream(in,null,options);
            in.close();
            return bitmap;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //new version file path function

    private class ChatListPreLoad extends  AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... voids) {
            chatDTOS=sqLiteDBHelper.getChatList(chatRoomId);
            for(int i=0;i<chatDTOS.size();i++){
                ChatVO chatVO=new ChatVO();
                chatVO.setType(chatDTOS.get(i).getMessageType());
                chatVO.setContent(chatDTOS.get(i).getMessage());
                chatVO.setName(chatDTOS.get(i).getClientID());
                chatVO.setFileUrl(chatDTOS.get(i).getFileUrl());Log.v("chaturi",chatDTOS.get(i).getFileUrl());
                chatVO.setFileType(chatDTOS.get(i).getFileType());
                chatVO.setDate(chatDTOS.get(i).getDate());
                chatAdapter.add(chatVO);
                Log.v("background","채팅 백그라운드작동");
            }
            
            return null;
        }
        @Override
        protected void onPostExecute(String content) {
            super.onPostExecute(content);
            chatAdapter.notifyDataSetChanged();
            //chatListView.setSelection(chatAdapter.getCount());
            chatListView.smoothScrollToPosition(chatAdapter.getCount());
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }
    private class fileSendToServerTask extends AsyncTask<String,Void,String>
    {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Log.v("contenturi","파일전송 비동기 시작");

                File sourceFile=null;
                int type=Integer.parseInt(params[1]);

                MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
                if(type==AttachedFileType.CAMERA){
                    Uri contentUri=Uri.parse(params[0]);
                    absolutePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/Myfriends/"+contentUri.getLastPathSegment();
                    sourceFile=new File(absolutePath);
                }else if(type==AttachedFileType.ALBUM) {
                    Uri contentUri=Uri.parse(params[0]);
                    Log.v("엘범",getImagePath(contentUri));
                    sourceFile = new File(getImagePath(contentUri));
                }else if(type==AttachedFileType.FILE){
                    sourceFile = new File(params[0]);
                }

                String extension=MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(sourceFile).toString());
                String mimeType=mimeTypeMap.getMimeTypeFromExtension(extension);
                Log.v("카메라",sourceFile.getName()+"  :  "+extension+"  :  "+mimeType);
                uploadFilePath=uploadPath();
                getTotalPath=url+uploadFilePath+"/"+sourceFile.getName();
                switch (type) {
                    case AttachedFileType.CAMERA:
                        sendToServer(sourceFile,mimeType,AttachedFileType.CAMERA);
                        break;
                    case AttachedFileType.ALBUM:
                        sendToServer(sourceFile,mimeType,AttachedFileType.ALBUM);
                        break;
                    case AttachedFileType.VIDEO:

                        break;
                    case AttachedFileType.FILE:
                        sendToServer(sourceFile,mimeType,AttachedFileType.FILE);
                        fileinfo=sourceFile.getName()+"\n";

                        if(sourceFile.length()<1024*1024){
                            fileinfo+=(sourceFile.length()/1024+"KB");
                        }else if(sourceFile.length()<1024*1024*1024){
                            fileinfo+=(sourceFile.length()/(1024*1024)+"MB");
                        }
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }
    public void sendToServer(File sourceFile,String mimeType,final int type){
        RequestBody requestbody=new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("savePath",uploadFilePath)
                .addFormDataPart("fileName",sourceFile.getName())
                .addFormDataPart("MIMETYPE",mimeType)
                .addFormDataPart(
                        "data",sourceFile.getName(),
                        RequestBody.create(sourceFile, MediaType.parse(mimeType))
                )
                .build();
        Request request=new Request.Builder()
                .url(url+"/uploadFile")
                .header("Content-Type", "multipart/form-data")
                .post(requestbody)
                .build();

        OkHttpClient client=new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.v("contenturi","response 발생");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sendMessage(true,type);
                            }
                        });
                    }
                }).start();
            }
        });
    }
    public String uploadPath(){
        int year=Calendar.getInstance().get(Calendar.YEAR);
        int month=Calendar.getInstance().get(Calendar.MONTH)+1;
        int day=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        StringBuffer path=new StringBuffer();
        path.append("/uploadFile/").append(year).append("/").append(month).
                append("/").append(day).
                append("/").append(chatRoomId);

        return path.toString();
    }
    @Override
    public void onStart(){
        super.onStart();
        AppLifeManager.getInstance().onActivityStarted(this);
        sendMenus.setVisibility(View.GONE);
        sendMenusToggle=false;

    }
    @Override
    public void onStop(){
        super.onStop();
        AppLifeManager.getInstance().onActivityStopped(this);
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onResume(){
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("chatActivity","destory 호출");
        init();
    }
    public void init(){
        unregisterReceiver(receiver);
        sqLiteDBHelper.close();
        nicName=null;
        chatRoomId=null;
        chatAdapter=null;
        chatListView=null;
        chatData=null;
        now=0;
        date=null;
        txtContent=null;
        txtSendMessage=null;
        intent=null;
        sendBtn=null;
        chatDTOS=null;
        System.gc();
        finish();
    }

}
//앨범,카메라,파일,위치정보


