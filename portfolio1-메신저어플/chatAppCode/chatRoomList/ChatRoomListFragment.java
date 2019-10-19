package com.example.myfriends.chatRoomList;

//import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.support.v4.app.ListFragment;
import android.widget.TextView;

import com.example.myfriends.R;
import com.example.myfriends.chat.ChatActivity;
import com.example.myfriends.chat.ChatAdapter;
import com.example.myfriends.chat.ChatVO;

import org.json.JSONObject;

import io.socket.emitter.Emitter;
import java.util.ArrayList;

//import static com.example.myfriends.MainActivity.socket;

public class ChatRoomListFragment extends ListFragment {
    ArrayList<ChatRoomListItemVO> datas;
    ChatRoomListAdapter chatRoomListAdapter;
    String chatRoomId;
    String content;
    String nicName;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        nicName=getArguments().getString("nicName");
        datas=getArguments().getParcelableArrayList("data");
        chatRoomListAdapter=new ChatRoomListAdapter(getContext(), R.layout.chatroom_list_item,datas);
        for(int i=0;i<datas.size();i++){
            ChatRoomListItemVO chatRoomListItemVO=datas.get(i);
            chatRoomListItemVO.setName("테스터"+i);
            chatRoomListItemVO.setLastReceivedMessage("테스트"+i+ "채팅방입니다~~");
            Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.custom_item1);
            chatRoomListItemVO.setProfilImage(bitmap);
            chatRoomListItemVO.setDate("7월12일");
            chatRoomListItemVO.setPeopleCount("0");
        }
        setListAdapter(chatRoomListAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent=new Intent(getContext(), ChatActivity.class);
        intent.putExtra("nicName",nicName);
        intent.putExtra("chatRoomId",datas.get(position).getChatRoomId());
        startActivity(intent);
    }

}
