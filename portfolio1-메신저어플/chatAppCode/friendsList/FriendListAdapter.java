package com.example.myfriends.friendsList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myfriends.R;
import com.example.myfriends.chatRoomList.ChatRoomListHolder;

import java.util.ArrayList;

public class FriendListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<FriendsListItemVO> friendsListData;
    private LayoutInflater inflater;
    String url="http://192.168.35.42:8006/files";
    public FriendListAdapter(Context context,int talkList,ArrayList<FriendsListItemVO> list) {
        this.context = context;
        this.layout=talkList;
        this.friendsListData=list;
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public void add(FriendsListItemVO friendsListItemVO){
        friendsListData.add(friendsListItemVO);
    }
    @Override
    public int getCount() {
        return friendsListData.size();
    }

    @Override
    public Object getItem(int position) {
        return friendsListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(layout,parent,false);
            FriendsListHolder friendsListHolder=new FriendsListHolder(convertView);
            convertView.setTag(friendsListHolder);
        }
        FriendsListHolder friendsListHolder=(FriendsListHolder)convertView.getTag();
        ImageView profilImageView=friendsListHolder.profilImageView;
        TextView nameView=friendsListHolder.nameView;
        TextView stateMessageView=friendsListHolder.stateMessageView;
        final FriendsListItemVO friendsListItemVO=friendsListData.get(position);
        //Glide.with(context).load("http://image.yes24.com/momo/TopCate2357/MidCate002/235611259.jpg").into(profilImageView);
        Glide.with(context).load(url+"/profile/"+friendsListItemVO.getName()+".png").
                diskCacheStrategy(DiskCacheStrategy.NONE).
                skipMemoryCache(true).
                into(profilImageView);
        nameView.setText(friendsListItemVO.getName());
        stateMessageView.setText(friendsListItemVO.getStateMessage());
        return convertView;
    }
    private static final BitmapFactory.Options options = new BitmapFactory.Options();
    private Bitmap getProfileImage(Context context,int MAX_IMAGE_SIZE){
        int scale=0;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(),R.drawable.custom_item,options);
        if (options.outHeight > MAX_IMAGE_SIZE || options.outWidth > MAX_IMAGE_SIZE) {
            scale = (int) Math.pow(2, (int) Math.round(Math.log(MAX_IMAGE_SIZE / (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
        }
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;

        Bitmap bitmap=BitmapFactory.decodeResource(context.getResources(),R.drawable.custom_item,options);
        if (bitmap != null) {
            // finally rescale to exactly the size we need
            if (options.outWidth != MAX_IMAGE_SIZE || options.outHeight != MAX_IMAGE_SIZE) {
                Bitmap tmp = Bitmap.createScaledBitmap(bitmap, MAX_IMAGE_SIZE, MAX_IMAGE_SIZE, true);
                bitmap.recycle();
                bitmap = tmp;
            }
        }

        return bitmap;
    }
    private class BitmapAsync extends AsyncTask<Void,Void,Bitmap> {
        FriendsListHolder friendsListHolder;
        BitmapAsync( FriendsListHolder friendsListHolder){
            this.friendsListHolder=friendsListHolder;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            friendsListHolder.profilImageView.setImageBitmap(bitmap);
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            return getProfileImage(context,50);
        }
    }
}
