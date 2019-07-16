package com.example.myfriends.inviteList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myfriends.MainActivity;
import com.example.myfriends.R;
import com.example.myfriends.chat.ChatActivity;
import com.example.myfriends.friendsList.FriendsListItemVO;

import java.util.ArrayList;
import java.util.List;

public class InviteActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<InviteListVO> datas;
    private InviteListAdapter inviteListAdapter;
    private ArrayList<FriendsListItemVO> friendsListItemVOS;
    private MenuItem menuItem;
    private int selectedFriends;
    private boolean optionCheck;
    private String userEmail;
    private String nicName;
    private String groupKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        datas=new ArrayList<>();
        getSupportActionBar().setTitle("대화상대 초대");
        Intent intent=getIntent();
        friendsListItemVOS=intent.getParcelableArrayListExtra("data");
        nicName=intent.getStringExtra("nicName");
        userEmail=intent.getStringExtra("userEmail");
        for(int i=0;i<friendsListItemVOS.size();i++){
            InviteListVO inviteListVO=new InviteListVO();
            Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.custom_item1);
            inviteListVO.setProfilImage(bitmap);
            inviteListVO.setName(friendsListItemVOS.get(i).getName());
            inviteListVO.setChecked(false);
            datas.add(inviteListVO);
        }
        registerReceiver(chatRoomCreated,new IntentFilter("com.example.CHAT_ROOM_CREATE_RECEIVE_ACTION"));

        selectedFriends=0;
        optionCheck=false;
        listView=(ListView)findViewById(R.id.inviteListView);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


        inviteListAdapter=new InviteListAdapter(this,R.layout.invite_item_list,datas);
        listView.setAdapter(inviteListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),datas.get(position).getName(),Toast.LENGTH_LONG).show();
                optionCheck=false;
                if(datas.get(position).getChecked()){
                    datas.get(position).setChecked(false);
                    datas.get(position).getSelectChk().setChecked(false);
                }else{
                    datas.get(position).setChecked(true);
                    datas.get(position).getSelectChk().setChecked(true);
                }
                for(int i=0;i<datas.size();i++){
                    if(datas.get(i).getChecked()){
                        optionCheck=true;
                        break;
                    }
                }
                if(optionCheck){
                    optionCheck=true;
                    invalidateOptionsMenu();
                }else{
                    optionCheck=false;
                    invalidateOptionsMenu();
                }

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.invite_complete,menu);
        menuItem=menu.findItem(R.id.check);
        if(optionCheck){
            menuItem.setEnabled(true);

        }else{
            menuItem.setEnabled(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId()==R.id.check){
            //채팅방생성, 체크된 유저들 초대, 채팅방으로 이동
            ArrayList<String> nicNames=new ArrayList<>();
            groupKey=userEmail+"/"+Long.toString(System.currentTimeMillis());

            Intent intent =new Intent("com.example.CHAT_ROOM_CREATE_REQUEST_ACTION");
            for(int i=0;i<datas.size();i++){
                if(datas.get(i).getSelectChk().isChecked()){
                    nicNames.add(datas.get(i).getName());
                }
            }
            intent.putExtra("userEmail",userEmail);
            intent.putStringArrayListExtra("nicNames",nicNames);
            intent.putExtra("groupKey",groupKey);
            sendBroadcast(intent);

        }
        return super.onContextItemSelected(menuItem);
    }

    BroadcastReceiver chatRoomCreated=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent mainIntent=new Intent(getApplicationContext(), ChatActivity.class);
            mainIntent.putExtra("userEmail",userEmail);
            mainIntent.putExtra("nicName",nicName);
            mainIntent.putExtra("groupKey",groupKey);
            startActivity(mainIntent);
        }
    };


    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(chatRoomCreated);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        setResult(RESULT_OK,intent);
        finish();
    }
}
