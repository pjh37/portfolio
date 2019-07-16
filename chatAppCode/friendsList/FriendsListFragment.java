package com.example.myfriends.friendsList;

//import android.app.ListFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.support.v4.app.ListFragment;
import android.widget.TextView;

import com.example.myfriends.R;

import java.util.ArrayList;

public class FriendsListFragment extends Fragment {
    ImageView profil;
    TextView myName;
    TextView stateMessage;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        LinearLayout layout=(LinearLayout)inflater.inflate(R.layout.friends_list_fragment,container,false);

        ListView listView=(ListView)layout.findViewById(R.id.friendsListView);
        ArrayList<FriendsListItemVO> datas=getArguments().getParcelableArrayList("data");
        FriendListAdapter friendListAdapter=new FriendListAdapter(getContext(), R.layout.friends_list_item,datas);
        listView.setAdapter(friendListAdapter);

        return  layout;
    }
    /*
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);


        profil=(ImageView)view.findViewById(R.id.img);
        myName=(TextView)view.findViewById(R.id.name);
        stateMessage=(TextView)view.findViewById(R.id.stateMessage);
        //Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.custom_item1);
        //profil.setImageBitmap(bitmap);
        //myName.setText("박지효");
        //stateMessage.setText("vim편집기는 쓰레기다");
        ArrayList<FriendsListItemVO> datas=getArguments().getParcelableArrayList("data");
        //getArguments().getParcelableArrayList("data").size();


        FriendListAdapter friendListAdapter=new FriendListAdapter(getContext(), R.layout.friends_list_item,datas);
        /*
        FriendsListItemVO test=new FriendsListItemVO();
        test.setName("홍길동");
        test.setStateMessage("상태메세지");
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.custom_item1);
        test.setProfilImage(bitmap);
        friendListAdapter.add(test);
        */
        //setListAdapter(friendListAdapter);

   // }
    /*
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }
    */
}
