package com.example.myfriends.managerPackage;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.myfriends.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.client.IO;
import io.socket.client.Url;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkManager extends Application {
    Socket socket;
    String url="http://192.168.35.42:8006";
    static int NOTIFICATION_ID=100;
    public NetworkManager(){

    }
    //singleton
    public static class NetworkManagerHolder{
        public static final NetworkManager networkManager=new NetworkManager();
    }
    public static NetworkManager getInstance(){
        return NetworkManagerHolder.networkManager;
    }
    public void connect(){
        try{
            socket=IO.socket(url);
            socket.connect();
            Log.v("NetworkManager","socket connect ");
        }catch (Exception error){
            Log.v("NetworkManager","error.printStackTrace()");
            error.printStackTrace();
        }
    }
    public Socket getSocket(){
        return socket;
    }

    public void messageSend(String chatRoomId,String nicName,String content,String url,int fileType,String date){
        JSONObject jObject=new JSONObject();
        try{
            jObject.put("chatRoomID",chatRoomId);
            jObject.put("nicName", nicName);
            jObject.put("content", content);
            jObject.put("url", url);
            jObject.put("fileType",fileType);
            jObject.put("date", date);
            socket.emit("message",jObject);
            Log.v("messageSend","메세지 전송함!!!");
        }catch (Exception error){
            error.printStackTrace();
        }
    }
    public void join(String email,String password,String nicName){
        JSONObject jObject=new JSONObject();
        try{
            Log.v("socket","join emit 실행");
            jObject.put("email",email);
            jObject.put("password",password);
            jObject.put("nicName",nicName);
            socket.emit("joinRequest",jObject);
        }catch (Exception error){
            error.printStackTrace();
        }
    }
    public void login(String email){
        try{
            socket.emit("loginRequest",email);
        }catch(Exception error){
            error.printStackTrace();
        }
    }
    public void friendsList(String email){
        try{
            socket.emit("friendsListRequest",email);
        }catch(Exception error){
            error.printStackTrace();
        }
    }
    public void chatRoomList(String email,String nicName){
        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("type","chatRoomList");
            jsonObject.put("userEmail",email);
            jsonObject.put("nicName",nicName);
            socket.emit("chatRoomRequest",jsonObject);
        }catch (Exception error){
            error.printStackTrace();
        }
    }
    public void chatRoomCreate(String email, ArrayList<String> nicNames,String chatRoomID){
        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("type","chatRoomCreate");
            jsonObject.put("nicNames",nicNames);
            jsonObject.put("groupKey",chatRoomID);
            jsonObject.put("userEmail",email);
            socket.emit("chatRoomRequest",jsonObject);
        }catch (Exception error){
            error.printStackTrace();
        }
    }
    public void chatRoomLeave(String chatRoomID,String email,String nicName){
        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("type","userLeave");
            jsonObject.put("groupKey",chatRoomID);
            jsonObject.put("userEmail",email);
            jsonObject.put("nicName",nicName);
            socket.emit("chatRoomRequest",jsonObject);
        }catch (Exception error){
            error.printStackTrace();
        }
    }
    public void friendsPlus(String email,String nicName,String myNicName){
        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("userEmail",email);
            jsonObject.put("nicName",nicName);
            jsonObject.put("myNicName",myNicName);
            socket.emit("friendsPlusRequest",jsonObject);
        }catch (Exception error){
            error.printStackTrace();
        }
    }
    public void friendsDelete(String email,String nicName){
        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("userEmail",email);
            jsonObject.put("nicName",nicName);
            socket.emit("friendsDelete",jsonObject);
        }catch (Exception error){
            error.printStackTrace();
        }
    }

    public void downloadRequest(Context context,String fileurl){
         new DownloaderTask(context).execute(fileurl);
    }
    private class DownloaderTask extends AsyncTask<String,Integer,String> {
        int fileSize;
        String path;
        String fileName;
        File file;
        Context context;
        NotificationManager notificationManager;
        NotificationCompat.Builder builder;

        DownloaderTask(Context context){
            this.context=context;
            path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/MyFriends";
            fileName="";
            file=null;
            fileSize=0;
            notificationManager=(NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                notificationManager.createNotificationChannel((new NotificationChannel("default","기본채널",NotificationManager.IMPORTANCE_DEFAULT)));
                builder=new NotificationCompat.Builder(context,"default");
            }else{
                builder=new NotificationCompat.Builder(context);
            }


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(context,"다운로드중...",Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... strings) {
            final int bufferSize=1024*1024*4;
            OkHttpClient httpClient=new OkHttpClient();
            RequestBody requestFileExtension=new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("fileExtensionRequest",strings[0].substring(url.length()))
                    .build();
            RequestBody requestbody=new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("downloadRequest",strings[0].substring(url.length()))
                    .build();
            Request request=new Request.Builder()
                    .url(url)
                    .header("Content-Type", "multipart/form-data")
                    .post(requestbody)
                    .build();
            Request fileRequest=new Request.Builder()
                    .url(url)
                    .header("Content-Type", "multipart/form-data")
                    .post(requestFileExtension)
                    .build();
            try{
                Response response=httpClient.newCall(fileRequest).execute();
                JSONObject jsonObject=new JSONObject(response.body().string());
                Log.v("파일정보","extension : " +jsonObject.getString("fileExtension")+"  "+"name : "+jsonObject.getString("fileName")+" fileSize : "+jsonObject.getInt("fileSize"));
                fileName=jsonObject.getString("fileName");
                fileSize=jsonObject.getInt("fileSize");
                file=new File(path);
                if(!file.isDirectory()){
                    file.mkdir();
                }
                file=new File(path+"/"+fileName);
                if(!file.isFile()){
                    file.createNewFile();
                }
                httpClient.newCall(request).enqueue(new Callback(){
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        InputStream in=response.body().byteStream();
                        OutputStream out=new FileOutputStream(path+"/"+fileName);
                        try{
                            byte[] buffer=new byte[bufferSize];
                            int readBufferSize=0;
                            int progressbarSize=0;
                            while((readBufferSize=in.read(buffer))!=-1){
                                progressbarSize+=readBufferSize;
                                out.write(buffer,0,readBufferSize);
                                builder.setAutoCancel(false);
                                builder.setSmallIcon(R.mipmap.ic_launcher_umr_round);
                                builder.setOngoing(true);
                                builder.setProgress(fileSize,progressbarSize,false);
                                notificationManager.notify(NOTIFICATION_ID,builder.build());
                            }
                            notificationManager.cancel(NOTIFICATION_ID);
                            out.flush();
                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
                            if(in!=null){
                                in.close();
                            }
                            if(out!=null){
                                out.close();
                            }
                        }
                    }
                });
            }catch (Exception e){}


            return null;
        }
    }
}
