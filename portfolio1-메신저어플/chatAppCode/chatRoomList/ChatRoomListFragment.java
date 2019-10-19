package com.example.myfriends.chatRoomList;

//import android.app.ListFragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myfriends.managerPackage.NetworkManager;
import com.example.myfriends.R;
import com.example.myfriends.chat.ChatActivity;

import java.util.ArrayList;

//import static com.example.myfriends.MainActivity.socket;

public class ChatRoomListFragment extends Fragment {
    ArrayList<ChatRoomListItemVO> datas;
    ChatRoomListAdapter chatRoomListAdapter;

    ListView listView;
    String chatRoomId;
    String content;
    String nicName;
    String userEmail;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        LinearLayout layout=(LinearLayout) inflater.inflate(R.layout.chatroom_list_fragment,container,false);
        listView=(ListView)layout.findViewById(R.id.chatRoomListView);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                makeDialog(position);
                return true;
            }
        });
        datas=getArguments().getParcelableArrayList("data");
        nicName=getArguments().getString("nicName");
        userEmail=getArguments().getString("userEmail");
        chatRoomListAdapter=new ChatRoomListAdapter(getContext(),R.layout.chatroom_list_item,datas);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getContext(), ChatActivity.class);
                intent.putExtra("nicName",nicName);
                intent.putExtra("chatRoomID",datas.get(position).getChatRoomId());
                startActivity(intent);
            }
        });
        listView.setAdapter(chatRoomListAdapter);

        return layout;
    }
    public void chatRoomListRefresh(){
        chatRoomListAdapter.notifyDataSetChanged();
    }

    public ArrayList<ChatRoomListItemVO> getDatas() {
        return datas;
    }

    public void makeDialog(final int position){
        AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
        CharSequence info[]=new CharSequence[]{"나가기"};
        alert.setTitle(datas.get(position).getNames().get(0));
        alert.setItems(info, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(),"채팅방에서 나감",Toast.LENGTH_LONG).show();
                NetworkManager.getInstance().chatRoomLeave(datas.get(position).getChatRoomId(),userEmail,nicName);
                datas.remove(position);
                chatRoomListAdapter.notifyDataSetChanged();
            }
        });
        alert.create();
        alert.show();
    }
    @Override
    public void onStart(){
        super.onStart();
        chatRoomListAdapter.notifyDataSetChanged();
    }
}
