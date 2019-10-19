package com.example.myfriends;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.socket.client.Socket;
import io.socket.client.IO;
import io.socket.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.example.myfriends.chatRoomList.ChatRoomListItemVO;
import com.example.myfriends.friendsList.FriendsListItemVO;
import com.example.myfriends.realmVO.ChatRealmVO;

import java.util.ArrayList;

public class ChatService extends Service {
    Socket socket;
    String url="http://192.168.35.34:8006";
    Realm realm;

    public ChatService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        registerReceiver(sender,new IntentFilter("com.example.SEND_ACTION"));
        registerReceiver(join,new IntentFilter("com.example.JOIN_ACTION"));
        registerReceiver(friendsList,new IntentFilter("com.example.FRIENDS_LIST_ACTION"));
        registerReceiver(loginRequest,new IntentFilter("com.example.LOGIN_REQUEST_ACTION"));
        registerReceiver(chatRoomCreate,new IntentFilter("com.example.CHAT_ROOM_CREATE_REQUEST_ACTION"));
        registerReceiver(chatRoomList,new IntentFilter("com.example.CHAT_ROOM_LIST_ACTION"));

        try{
            socket=IO.socket(url);
            socket.connect();
            socket.on(Socket.EVENT_CONNECT,onConnected);
            socket.on("message",onMessageReceive);
            socket.on("friendsListResponse",onFriendsListResponse);
            socket.on("chatRoomListResponse",onChatRoomListResponse);
            socket.on("chatRoomCreated",onChatRoomCreated);
            socket.on("loginComplete",onLoginComplete);
            socket.on("chatRoomListUpdate",onChatRoomListUpdate);
        }catch (Exception error){
            error.printStackTrace();
        }
    }
    private Emitter.Listener onConnected=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //socket.emit("connectComplete","jjjj1352");
        }
    };
    private Emitter.Listener onLoginComplete=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
                JSONObject jsonObject=(JSONObject)args[0];
                Intent intent =new Intent("com.example.LOGIN_COMPLETE_ACTION");
                intent.putExtra("nicName",jsonObject.getString("nicName"));
                sendBroadcast(intent);
            }catch (Exception error){
                error.printStackTrace();
            }

        }
    };
    private Emitter.Listener onMessageReceive=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
                JSONObject jsonObject=(JSONObject)args[0];
                String chatRoomId=jsonObject.getString("chatRoomID");
                String nicName=jsonObject.getString("nicName");
                String content=jsonObject.getString("content");
                //String date=jsonObject.getString("date");
                //chatInsert(chatRoomId,content,name,date,1);
                Intent intent =new Intent("com.example.RECEIVE_ACTION");
                intent.putExtra("type",1);
                intent.putExtra("nicName",nicName);
                intent.putExtra("content",content);
                sendBroadcast(intent);
                Log.v("onMessageReceive",nicName);
                Log.v("onMessageReceive","onMessageReceive"+content);
            }catch (Exception error){
                error.printStackTrace();
            }
        }
    };
    private Emitter.Listener onFriendsListResponse=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
                JSONArray jsonArray=(JSONArray)args[0];
                ArrayList<FriendsListItemVO> datas=new ArrayList<>();
                Log.v("onFriendsListResponse","도착함");
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject tempObject=jsonArray.getJSONObject(i);
                    FriendsListItemVO friendsListItemVO=new FriendsListItemVO();
                    friendsListItemVO.setName(tempObject.getString("nicName"));
                    friendsListItemVO.setStateMessage(tempObject.getString("stateMessage"));
                    //사진은 임시로 이렇게 넣음  나중에 수정할꺼임!!
                    Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.custom_item1);
                    friendsListItemVO.setProfilImage(bitmap);
                    datas.add(friendsListItemVO);
                }
                Intent intent =new Intent("com.example.FRIENDS_LIST_RECEIVE_ACTION");
                intent.putExtra("data",datas);
                sendBroadcast(intent);
            }catch (Exception error){
                error.printStackTrace();
            }
        }
    };
    private Emitter.Listener onChatRoomListResponse=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
                JSONArray jsonArray=(JSONArray)args[0];
                ArrayList<ChatRoomListItemVO> datas=new ArrayList<>();
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject tempObject=jsonArray.getJSONObject(i);
                    ChatRoomListItemVO chatRoomListItemVO=new ChatRoomListItemVO();
                    chatRoomListItemVO.setChatRoomId(tempObject.getString("chatRoomID"));
                    datas.add(chatRoomListItemVO);
                }
                Intent intent =new Intent("com.example.CHAT_ROOM_LIST_RECEIVE_ACTION");
                intent.putExtra("data",datas);
                sendBroadcast(intent);
                Log.v("FriendsListItemVO","도착함");
            }catch (Exception error){
                error.printStackTrace();
            }
        }
    };
    private Emitter.Listener onChatRoomCreated=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Intent intent=new Intent("com.example.CHAT_ROOM_CREATE_RECEIVE_ACTION");
            sendBroadcast(intent);
        }
    };
    private Emitter.Listener onChatRoomListUpdate=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject jsonObject=(JSONObject)args[0];
            try{
                Intent intent =new Intent("com.example.CHAT_ROOM_LIST_UPDATE_ACTION");
                intent.putExtra("type",jsonObject.getString("type"));
                intent.putExtra("groupKey",jsonObject.getString("groupKey"));
                sendBroadcast(intent);
            }catch (Exception error){
                error.printStackTrace();
            }

        }
    };
    BroadcastReceiver sender=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String chatRoomId=intent.getStringExtra("chatRoomID");
            String nicName=intent.getStringExtra("nicName");
            String content=intent.getStringExtra("content");
            String date=intent.getStringExtra("date");
            //chatInsert(chatRoomId,content,name,date,0);
            JSONObject jObject=new JSONObject();
            try{
                jObject.put("chatRoomID",chatRoomId);
                jObject.put("nicName", nicName);
                jObject.put("content", content);
                jObject.put("date", date);
                socket.emit("message",jObject);
            }catch (Exception error){
                error.printStackTrace();
            }
        }
    };
    BroadcastReceiver join=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            JSONObject jObject=new JSONObject();
            try{
                jObject.put("email",intent.getStringExtra("email"));
                jObject.put("password",intent.getStringExtra("password"));
                jObject.put("nicName",intent.getStringExtra("nicName"));
                socket.emit("joinRequest",jObject);
            }catch (Exception error){
                error.printStackTrace();
            }
        }
    };
    BroadcastReceiver loginRequest=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String email=intent.getStringExtra("email");
            socket.emit("loginRequest",email);
        }
    };
    BroadcastReceiver friendsList=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String email=intent.getStringExtra("email");
            socket.emit("friendsListRequest",email);
        }
    };
    BroadcastReceiver chatRoomList=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            JSONObject jsonObject=new JSONObject();
            try{
                jsonObject.put("type","chatRoomList");
                jsonObject.put("userEmail",intent.getStringExtra("email"));
                socket.emit("chatRoomRequest",jsonObject);
            }catch (Exception error){
                error.printStackTrace();
            }
        }
    };

    BroadcastReceiver chatRoomCreate=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            JSONObject jsonObject=new JSONObject();
            try{
                jsonObject.put("type","chatRoomCreate");
                jsonObject.put("nicNames",intent.getStringArrayListExtra("nicNames"));
                jsonObject.put("groupKey",intent.getStringExtra("groupKey"));
                jsonObject.put("userEmail",intent.getStringExtra("userEmail"));
                socket.emit("chatRoomRequest",jsonObject);
            }catch (Exception error){
                error.printStackTrace();
            }
            //로컬
        }
    };
    private void init(){

    }
    private long autoIncrement(){
        Number nextNumber=realm.where(ChatRealmVO.class).max("chatID");
        long chatID;
        if(nextNumber==null){
            chatID=0;
        }else{
            chatID=nextNumber.longValue()+1;
        }
        return chatID;
    }
    private void chatInsert(final String chatRoomID,final String content,final String name,final String date
            ,final int type){

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ChatRealmVO chatRealmVO=realm.createObject(ChatRealmVO.class);
                chatRealmVO.setChatID(autoIncrement());
                chatRealmVO.setChatRoomID("오픈채팅");
                chatRealmVO.setName(name);
                chatRealmVO.setContent(content);
                chatRealmVO.setDate(date);
                chatRealmVO.setType(type);
            }
        });

    }
    private RealmResults<ChatRealmVO> getChatList(String chatRoomID){
        return realm.where(ChatRealmVO.class).equalTo("chatRoomID",chatRoomID)
                                              .findAll();
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onDestroy(){
        unregisterReceiver(sender);
        unregisterReceiver(join);
        unregisterReceiver(friendsList);
        unregisterReceiver(chatRoomList);
        unregisterReceiver(chatRoomCreate);
        unregisterReceiver(loginRequest);
    }
}
