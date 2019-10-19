package com.example.myfriends.servicePackage;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import io.realm.Realm;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONObject;

import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.myfriends.R;
import com.example.myfriends.chat.ChatActivity;
import com.example.myfriends.chat.ChatDTO;
import com.example.myfriends.chatRoomList.ChatRoomListItemVO;
import com.example.myfriends.chatRoomList.ChatroomInfo;
import com.example.myfriends.dbHelperPackage.SQLiteDBHelper;
import com.example.myfriends.friendsList.FriendsListItemVO;
import com.example.myfriends.managerPackage.AppLifeManager;
import com.example.myfriends.managerPackage.NetworkManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ChatService extends Service {
    Socket socket;
    String url="http://192.168.35.42:8006";
    String email="";
    Realm realm;
    AppLifeManager appControl;
    Notification notification;
    private SQLiteDBHelper sqLiteDBHelper;

    public ChatService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.v("chatService","chatService에서 onStartCommand작동!!");

       return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //sqLiteDBHelper=new SQLiteDBHelper(this);
        sqLiteDBHelper=new SQLiteDBHelper(this);

        appControl=new AppLifeManager();

        try{
            Log.v("chatService","server connect!!");
            NetworkManager.getInstance().connect();
            socket=NetworkManager.getInstance().getSocket();
            socket.on(Socket.EVENT_CONNECT,onConnected);
            socket.on(Socket.EVENT_DISCONNECT,onReconnect);
            socket.on("message",onMessageReceive);
            socket.on("friendsListResponse",onFriendsListResponse);
            socket.on("chatRoomListResponse",onChatRoomListResponse);
            socket.on("chatRoomCreated",onChatRoomCreated);
            socket.on("loginComplete",onLoginComplete);
            socket.on("chatRoomListUpdate",onChatRoomListUpdate);
            socket.on("friendsPlusResponse",onFriendsPlusResponse);


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
    private Emitter.Listener onReconnect=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.v("disconnect","서버연결끊어짐");
            socket.connect();
            socket.emit("reconnect",email);
        }
    };
    private Emitter.Listener onLoginComplete=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
                JSONObject jsonObject=(JSONObject)args[0];
                Intent intent =new Intent("com.example.LOGIN_COMPLETE_ACTION");
                if(jsonObject.getString("type").equals("joined")){
                    intent.putExtra("type",jsonObject.getString("type"));
                    intent.putExtra("nicName",jsonObject.getString("nicName"));
                }else{
                    intent.putExtra("type",jsonObject.getString("type"));
                }

                Log.v("test",jsonObject.getString("type"));
                Log.v("test", jsonObject.getString("nicName"));
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
                String fileUrl=jsonObject.getString("url");
                int fileType=jsonObject.getInt("fileType");
                Intent intent =new Intent("com.example.RECEIVE_ACTION");
                intent.putExtra("type",1);
                intent.putExtra("chatRoomID",chatRoomId);
                intent.putExtra("nicName",nicName);
                intent.putExtra("content",content);
                intent.putExtra("url",fileUrl);
                intent.putExtra("fileType",fileType);
                sendBroadcast(intent);
                ChatDTO chatDTO=new ChatDTO();
                chatDTO.setChatRoomID(chatRoomId);
                chatDTO.setClientID(nicName);
                chatDTO.setMessage(content);
                chatDTO.setMessageType(1);
                chatDTO.setFileUrl(fileUrl);
                chatDTO.setFileType(fileType);
                chatDTO.setDate(getTime());
                sqLiteDBHelper.chatInsert(chatDTO);
                if(AppLifeManager.getInstance().getAppStatus()== AppLifeManager.AppStatus.BACKGROUND){
                    notification(nicName,content,chatRoomId);
                }
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
                Log.v("test","friendslist length : "+jsonArray.length());
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject tempObject=jsonArray.getJSONObject(i);
                    FriendsListItemVO friendsListItemVO=new FriendsListItemVO();
                    Log.v("test",tempObject.getString("nicName"));
                    Log.v("test",tempObject.getString("stateMessage"));
                    friendsListItemVO.setName(tempObject.getString("nicName"));
                    friendsListItemVO.setStateMessage(tempObject.getString("stateMessage"));
                    datas.add(friendsListItemVO);
                }
                Intent intent =new Intent("com.example.FRIENDS_LIST_RECEIVE_ACTION");
                intent.putExtra("data",datas);
                sendBroadcast(intent);
                /*
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
                */
            }catch (Exception error){
                error.printStackTrace();
            }

        }
    };
    private Emitter.Listener onFriendsPlusResponse=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject jsonObject=(JSONObject)args[0];
            try{
                Intent intent =new Intent("com.example.FRIENDS_PLUS_RESULT_ACTION");
                intent.putExtra("result",jsonObject.getBoolean("result"));
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
                Log.v("onChatRoomListResponse","도착함");
                JSONArray jsonArray=(JSONArray)args[0];
                ArrayList<ChatRoomListItemVO> datas=new ArrayList<>();
                if(jsonArray.length()==0){
                    Log.v("onChatRoomListResponse","jsonArray.length()==0");
                    Intent intent =new Intent("com.example.CHAT_ROOM_LIST_RECEIVE_ACTION");
                    intent.putExtra("data",datas);
                    sendBroadcast(intent);
                }else{
                    HashMap<String,ChatroomInfo> chatroominfo=new HashMap<>();

                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject tempObject=jsonArray.getJSONObject(i);
                        if(!chatroominfo.containsKey(tempObject.getString("chatRoomID"))){
                            chatroominfo.put(tempObject.getString("chatRoomID"),new ChatroomInfo());
                        }
                        if(chatroominfo.get(tempObject.getString("chatRoomID")).getChatRoomID().equals("")){
                            chatroominfo.get(tempObject.getString("chatRoomID")).setChatRoomID(tempObject.getString("chatRoomID"));
                        }
                        chatroominfo.get(tempObject.getString("chatRoomID")).getNames().add(tempObject.getString("name"));
                    }

                    Set<String> chatroomid= chatroominfo.keySet();
                    Iterator<String> iterator=chatroomid.iterator();
                    while(iterator.hasNext()){
                        ChatRoomListItemVO chatRoomListItemVO=new ChatRoomListItemVO();
                        String key=iterator.next();
                        chatRoomListItemVO.setChatRoomId(chatroominfo.get(key).getChatRoomID());
                        chatRoomListItemVO.setNames(chatroominfo.get(key).getNames());
                        Log.v("onChatRoomListResponse",chatroominfo.get(key).getChatRoomID());
                        datas.add(chatRoomListItemVO);
                    }
                    Intent intent =new Intent("com.example.CHAT_ROOM_LIST_RECEIVE_ACTION");
                    intent.putExtra("data",datas);
                    sendBroadcast(intent);
                }

                /*
                ArrayList<ChatRoomListItemVO> datas=new ArrayList<>();
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject tempObject=jsonArray.getJSONObject(i);
                    ChatRoomListItemVO chatRoomListItemVO=new ChatRoomListItemVO();
                    chatRoomListItemVO.setChatRoomId(tempObject.getString("chatRoomID"));
                    List<String> list=new ArrayList<>();


                    String[] userList=tempObject.getString("roomUsers").replaceAll("\\[","")
                            .replaceAll("\\]","").split(",");
                    for (String id:userList) {
                        list.add(id);
                    }

                    chatRoomListItemVO.setNames(list);
                    chatRoomListItemVO.setPeopleCount(String.valueOf(userList.length));
                    datas.add(chatRoomListItemVO);
                }
                */


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
                ArrayList<String> list=new ArrayList<>();
                String[] userList=jsonObject.getString("roomUsers").replaceAll("\\[","")
                        .replaceAll("\\]","").split(",");
                for (String id:userList) {
                    list.add(id);
                }
                intent.putStringArrayListExtra("nicNames",list);
                sendBroadcast(intent);
            }catch (Exception error){
                error.printStackTrace();
            }

        }
    };
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public String getTime(){
        SimpleDateFormat sdf=new SimpleDateFormat("hh:mm");
        long now=System.currentTimeMillis();
        Date date=new Date(now);
        return sdf.format(date);
    }
    public void notification(final String nicName,final String content,final String groupKey){
        NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder=null;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            notificationManager.createNotificationChannel((new NotificationChannel("default","기본채널",NotificationManager.IMPORTANCE_DEFAULT)));
            builder=new NotificationCompat.Builder(this,"default");
        }else{
            builder=new NotificationCompat.Builder(this);
        }
        builder.setSmallIcon(R.mipmap.ic_launcher_umr_round);
        builder.setContentTitle(nicName);
        builder.setContentText(content);
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        Intent nintent=new Intent(this, ChatActivity.class);
        nintent.putExtra("chatRoomID",groupKey);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,10,nintent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        notificationManager.notify(100,builder.build());
    }
    @Override
    public void onDestroy(){
        Log.v("chatService","chatService에서 onDestroy작동!!");
        System.gc();
    }
}
