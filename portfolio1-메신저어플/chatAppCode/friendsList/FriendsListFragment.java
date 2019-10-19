package com.example.myfriends.friendsList;

//import android.app.ListFragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myfriends.managerPackage.NetworkManager;
import com.example.myfriends.R;
import com.example.myfriends.dbHelperPackage.ProfileDBHelper;

import java.util.ArrayList;

public class FriendsListFragment extends Fragment {
    String userEmail;
    String myNicName;
    ImageView myProfile_image;
    TextView myName;
    TextView myStateMessage;
    TextView friendsCount;
    RelativeLayout myProfileLayout;
    FriendListAdapter friendListAdapter;
    ArrayList<FriendsListItemVO> datas;
    SharedPreferences sharedPreferences;
    ProfileDBHelper profileDBHelper;
    String url="http://192.168.35.42:8006/files";
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        LinearLayout layout=(LinearLayout)inflater.inflate(R.layout.friends_list_fragment,container,false);
        profileDBHelper=new ProfileDBHelper(getContext());
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getContext());
        myStateMessage=(TextView)layout.findViewById(R.id.myStateMessage);
        friendsCount=(TextView)layout.findViewById(R.id.friendsCount);
        myProfile_image=(ImageView)layout.findViewById(R.id.myProfileImage);
        myStateMessage.setText(sharedPreferences.getString("myStateMessage",""));
        myName=(TextView)layout.findViewById(R.id.myName);
        myName.setText(getArguments().getString("nicName"));
        myNicName=getArguments().getString("nicName");
        Glide.with(this).load(url+"/profile/"+myNicName+".png").diskCacheStrategy(DiskCacheStrategy.NONE).into(myProfile_image);
        userEmail=getArguments().getString("userEmail");
        myProfileLayout=(RelativeLayout)layout.findViewById(R.id.myProfile);
        myProfileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getContext(),profileActivity.class);
                intent.putExtra("nicName",myName.getText().toString());
                intent.putExtra("type",0);
                intent.putExtra("stateMessage","");
                startActivity(intent);
            }
        });
        ListView listView=(ListView)layout.findViewById(R.id.friendsListView);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                makeDialog(position);
                return true;
            }
        });

        datas=getArguments().getParcelableArrayList("data");
        if(datas!=null)friendsCount.setText(datas.size()+"");
        else friendsCount.setText(0);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent =new Intent(getContext(),profileActivity.class);
                intent.putExtra("nicName",datas.get(position).getName());
                intent.putExtra("type",1);
                intent.putExtra("stateMessage",datas.get(position).getStateMessage());
                startActivity(intent);
            }
        });
        friendListAdapter=new FriendListAdapter(getContext(), R.layout.friends_list_item,datas);
        listView.setAdapter(friendListAdapter);

        return  layout;
    }
    public void friendsListRefresh(){
        friendListAdapter.notifyDataSetChanged();
    }

    public ArrayList<FriendsListItemVO> getDatas() {
        return datas;
    }
    public void makeDialog(final int position){
        AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
        CharSequence info[]=new CharSequence[]{"즐겨찾기에 추가","이름 변경","숨김","삭제"};
        alert.setTitle(datas.get(position).getName());
        alert.setItems(info, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==3){
                    NetworkManager.getInstance().friendsDelete(userEmail,myNicName);
                    datas.remove(position);
                    friendListAdapter.notifyDataSetChanged();
                }

            }
        });
        alert.create();
        alert.show();
    }
    @Override
    public void onResume(){
        super.onResume();
        Glide.with(this).load(url+"/profile/"+myNicName+".png").diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(myProfile_image);
    }
}
