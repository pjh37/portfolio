package com.example.myfriends.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import io.realm.Realm;
import io.socket.emitter.Emitter;
import com.example.myfriends.R;
import com.example.myfriends.realmVO.ChatRealmVO;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//import static com.example.myfriends.MainActivity.socket;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener{
    private String nicName;
    private String chatRoomId;
    private String content;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        registerReceiver(receiver,new IntentFilter("com.example.RECEIVE_ACTION"));

        sendBtn=(Button)findViewById(R.id.sendBtn);
       sendBtn.setOnClickListener(this);
        intent=getIntent();
        nicName=intent.getStringExtra("nicName");
        chatRoomId=intent.getStringExtra("chatRoomId");
        txtContent =(TextView)findViewById(R.id.content);
        txtSendMessage=(TextView)findViewById(R.id.sendMessage);
        chatData=new ArrayList<>();
        chatListView=(ListView)findViewById(R.id.chatListView);
        chatAdapter=new ChatAdapter(this,R.layout.chatlistview_item,chatData);
        chatListView.setAdapter(chatAdapter);
        chatAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                chatListView.setSelection(chatAdapter.getCount());
            }
        });
        /*
        if(socket.connected()){
            socket.on("message",onReceiveMessage);
        }
        */
    }
    BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type=intent.getIntExtra("type",1);
            String nicName=intent.getStringExtra("nicName");
            String content=intent.getStringExtra("content");
            ChatVO chatVO=new ChatVO();
            chatVO.setType(type);
            Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.custom_item);//버전 낮은거에서는 메모리 초과오류
            chatVO.setBimage(bitmap);
            chatVO.setContent(content);
            chatVO.setName(nicName);
            chatVO.setDate(getTime());
            chatAdapter.add(chatVO);
            chatAdapter.notifyDataSetChanged();

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
            sendMessage();
        }
    }
    public void sendMessage(){
        if(txtSendMessage.getText().toString().equals("")){return;}
        JSONObject jObject=new JSONObject();
        try{
            Intent intent=new Intent("com.example.SEND_ACTION");
            intent.putExtra("type",0);
            intent.putExtra("chatRoomID",chatRoomId);
            intent.putExtra("nicName",nicName);
            intent.putExtra("content",txtSendMessage.getText().toString());
            intent.putExtra("date",getTime());
            sendBroadcast(intent);
            ChatVO chatVO=new ChatVO();
            chatVO.setType(0);
            //Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.ic_person_add_black_24dp);
            //chatVO.setBimage(bitmap);
            chatVO.setContent(txtSendMessage.getText().toString());
            //chatVO.setName("익명");
            chatVO.setDate(getTime());
            chatAdapter.add(chatVO);
            chatAdapter.notifyDataSetChanged();
            Log.v("V",txtSendMessage.getText().toString());
            txtSendMessage.setText("");
            //jObject.put("chatRoomId",roomId);
            //jObject.put("content", txtContent.getText().toString());
            //socket.emit("message",jObject);


        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
