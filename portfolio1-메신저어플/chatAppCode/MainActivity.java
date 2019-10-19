package com.example.myfriends;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.view.View;
import io.socket.client.Socket;
import io.socket.client.IO;
import io.socket.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Handler;
import android.os.Looper;

import com.example.myfriends.chat.ChatVO;
import com.example.myfriends.chatRoomList.ChatRoomListAdapter;
import com.example.myfriends.chatRoomList.ChatRoomListFragment;
import com.example.myfriends.chatRoomList.ChatRoomListItemVO;
import com.example.myfriends.friendsList.FriendsListFragment;
import com.example.myfriends.friendsList.FriendsListItemVO;
import com.example.myfriends.inviteList.InviteActivity;
import com.example.myfriends.inviteList.InviteListVO;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    SearchView searchView;
    public BottomNavigationView bottomNavigationItemView;
    FragmentManager manager;
    FriendsListFragment friendsListFragment;
    ChatRoomListFragment chatRoomListFragment;
    public ArrayList<FriendsListItemVO> friendsListData;
    public ArrayList<ChatRoomListItemVO> chatRoomListData;
    public boolean friendsBtn=true;
    public boolean chatBtn=false;
    public boolean optionBtn=false;
    public MenuItem actionBarItem;
    public static String userEmail;
    public static String nicName;
    //public static Socket socket;
    //private String url="http://192.168.35.34:8006";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("친구목록");
        registerReceiver(chatRoomListUpdate,new IntentFilter("com.example.CHAT_ROOM_LIST_UPDATE_ACTION"));
        /*
        registerReceiver(friendsList,new IntentFilter("com.example.FRIENDS_LIST_RECEIVE_ACTION"));
        registerReceiver(chatRoomList,new IntentFilter("com.example.MAIN_ACTIVITY_CHAT_ROOM_LIST_ACTION"));
        Intent intent =new Intent("com.example.FRIENDS_LIST_ACTION");
        sendBroadcast(intent);
        */
        Intent intent=getIntent();
        friendsListData=intent.getParcelableArrayListExtra("friendsListData");
        chatRoomListData=intent.getParcelableArrayListExtra("chatRoomListData");
        userEmail=intent.getStringExtra("userEmail");
        nicName=intent.getStringExtra("nicName");
        friendsListFragment=new FriendsListFragment();
        chatRoomListFragment=new ChatRoomListFragment();
        setFragment(0);

        bottomNavigationItemView=(BottomNavigationView)findViewById(R.id.bottomNavigation);
        bottomNavigationItemView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.friendMenu:
                        invalidateOptionsMenu();
                        friendsBtn=true;
                        chatBtn=false;
                        optionBtn=false;
                        setFragment(0);
                        return true;

                    case R.id.chatRoomMenu:
                        invalidateOptionsMenu();
                        friendsBtn=false;
                        chatBtn=true;
                        optionBtn=false;
                        setFragment(1);
                        return true;

                    case R.id.optionMenu:
                        getSupportActionBar().setTitle("옵션");
                        friendsBtn=false;
                        chatBtn=false;
                        optionBtn=true;
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        if(friendsBtn){
            inflater.inflate(R.menu.friends_option_menu,menu);
        }else if(chatBtn){
            inflater.inflate(R.menu.chatroom_option_menu,menu);
        }


        MenuItem menuItem=menu.findItem(R.id.searchViewMenu);

        menu.add(0,0,0,"편집");
        menu.add(0,1,0,"친구관리");
        menu.add(0,2,0,"전체설정");

        searchView=(SearchView)menuItem.getActionView();
        searchView.setQueryHint("검색");
        searchView.setOnQueryTextListener(queryTextListener);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        if(friendsBtn){
            MenuItem menuItem=menu.findItem(1);
            menuItem.setTitle("친구관리");

        }else if(chatBtn){
            MenuItem menuItem=menu.findItem(1);
            menuItem.setTitle("정렬");
        }else{

        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId()==R.id.friendsPlusOption){

        }else if(menuItem.getItemId()==R.id.inviteOption){
            Intent intent=new Intent(this,InviteActivity.class);
            intent.putExtra("userEmail",userEmail);
            intent.putExtra("nicName",nicName);
            intent.putParcelableArrayListExtra("data",friendsListData);
            startActivityForResult(intent,3000);
        }
        return super.onContextItemSelected(menuItem);
    }
    private SearchView.OnQueryTextListener queryTextListener=new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            searchView.setQuery("",false);
            searchView.setIconified(true);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

    @Override
    public void onClick(View v) {

    }
    public void setFragment(int n){
        manager=getSupportFragmentManager();
        FragmentTransaction ft=manager.beginTransaction();
        Bundle bundle;
        switch(n){
            case 0:
                getSupportActionBar().setTitle("친구목록");
                bundle=new Bundle();
                bundle.putParcelableArrayList("data",friendsListData);
                friendsListFragment.setArguments(bundle);
                ft.replace(R.id.main_frame,friendsListFragment);
                ft.commit();
                break;
            case 1:
                getSupportActionBar().setTitle("채팅");
                bundle=new Bundle();
                bundle.putString("nicName",nicName);
                bundle.putParcelableArrayList("data",chatRoomListData);
                chatRoomListFragment.setArguments(bundle);
                ft.replace(R.id.main_frame,chatRoomListFragment);
                ft.commit();

                break;
        }
    }

    /*
    BroadcastReceiver friendsList=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            data=intent.getParcelableArrayListExtra("data");
            //setFragment(0);
            Log.v("argumentCount",data.get(0).getStateMessage());
        }
    };
    BroadcastReceiver chatRoomList=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
    */
    BroadcastReceiver chatRoomListUpdate=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ChatRoomListItemVO chatRoomListItemVO=new ChatRoomListItemVO();
            chatRoomListItemVO.setChatRoomId(intent.getStringExtra("groupKey"));
            chatRoomListData.add(chatRoomListItemVO);
            Log.v("chatRoomListUpdate","채팅룸 추가");
        }
    };
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==RESULT_OK){
            setFragment(1);
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(chatRoomListUpdate);
        //unregisterReceiver(chatRoomList);
    }
}
