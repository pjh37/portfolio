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
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.view.View;

import com.example.myfriends.chatRoomList.ChatRoomListFragment;
import com.example.myfriends.chatRoomList.ChatRoomListItemVO;
import com.example.myfriends.friendsList.FriendsListFragment;
import com.example.myfriends.friendsList.FriendsListItemVO;
import com.example.myfriends.friendsList.FriendsPlusActivity;
import com.example.myfriends.inviteList.InviteActivity;
import com.example.myfriends.managerPackage.AppLifeManager;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    SearchView searchView;
    public BottomNavigationView bottomNavigationItemView;
    FragmentManager manager;
    FriendsListFragment friendsListFragment;
    ChatRoomListFragment chatRoomListFragment;
    public ArrayList<FriendsListItemVO> friendsListData;
    public ArrayList<ChatRoomListItemVO> chatRoomListData;
    public HashMap<String,Integer> chatRoomListHash;
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
        registerReceiver(receiver,new IntentFilter("com.example.RECEIVE_ACTION"));
        registerReceiver(friendsListUpdate,new IntentFilter("com.example.FRIENDS_LIST_UPDATE"));

        Intent intent=getIntent();
        friendsListData=intent.getParcelableArrayListExtra("friendsListData");
        chatRoomListData=intent.getParcelableArrayListExtra("chatRoomListData");
        chatRoomListHash=(HashMap<String, Integer>) intent.getSerializableExtra("chatRooms");
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
            Intent intent =new Intent(this, FriendsPlusActivity.class);
            intent.putExtra("userEmail",userEmail);
            intent.putExtra("myNicName",nicName);
            startActivity(intent);
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
                bundle.putString("nicName",nicName);
                bundle.putString("userEmail",userEmail);
                friendsListFragment.setArguments(bundle);
                ft.replace(R.id.main_frame,friendsListFragment);
                ft.commit();
                break;
            case 1:
                getSupportActionBar().setTitle("채팅");
                bundle=new Bundle();
                bundle.putString("nicName",nicName);
                bundle.putString("userEmail",userEmail);
                bundle.putParcelableArrayList("data",chatRoomListData);
                chatRoomListFragment.setArguments(bundle);

                ft.replace(R.id.main_frame,chatRoomListFragment);
                ft.commit();

                break;
        }
    }
    BroadcastReceiver chatRoomListUpdate=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ChatRoomListItemVO chatRoomListItemVO=new ChatRoomListItemVO();
            chatRoomListItemVO.setChatRoomId(intent.getStringExtra("groupKey"));
            chatRoomListItemVO.setNames(intent.getStringArrayListExtra("nicNames"));
            chatRoomListItemVO.setPeopleCount(intent.getStringArrayListExtra("nicNames").size()+"");
            chatRoomListData.add(chatRoomListItemVO);
            chatRoomListFragment.chatRoomListRefresh();
            Log.v("chatRoomListUpdate","채팅룸 추가");
        }
    };
    BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type=intent.getIntExtra("type",1);
            String chatRoomID=intent.getStringExtra("chatRoomID");
            String nicName=intent.getStringExtra("nicName");
            String content=intent.getStringExtra("content");
            chatRoomListFragment.getDatas().get(chatRoomListHash.get(chatRoomID)).setLastReceivedMessage(content);
            //Toast.makeText(getApplicationContext(),content,Toast.LENGTH_LONG).show();
            chatRoomListFragment.chatRoomListRefresh();
        }
    };
    BroadcastReceiver friendsListUpdate=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            FriendsListItemVO friendsListItemVO=new FriendsListItemVO();
            friendsListItemVO.setName(intent.getStringExtra("nicName"));
            friendsListFragment.getDatas().add(friendsListItemVO);
            friendsListFragment.friendsListRefresh();
        }
    };

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==RESULT_OK){
            setFragment(1);
        }
    }
    @Override
    public void onStart(){
        super.onStart();
        AppLifeManager.getInstance().onActivityStarted(this);
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
    public void onDestroy(){
        super.onDestroy();
        manager=null;
        friendsListFragment=null;
        chatRoomListFragment=null;
        friendsListData=null;
        chatRoomListData=null;
        chatRoomListHash=null;
        unregisterReceiver(chatRoomListUpdate);
        unregisterReceiver(receiver);
        unregisterReceiver(friendsListUpdate);
        System.gc();
    }
}
