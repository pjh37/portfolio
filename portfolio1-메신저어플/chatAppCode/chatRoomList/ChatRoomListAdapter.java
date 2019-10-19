package com.example.myfriends.chatRoomList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myfriends.R;
import com.example.myfriends.dbHelperPackage.SQLiteDBHelper;

import java.util.ArrayList;

public class ChatRoomListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<ChatRoomListItemVO> chatRoomListData;
    private LayoutInflater inflater;
    SQLiteDBHelper dbHelper;
    String url="http://192.168.35.42:8006/files";
    public ChatRoomListAdapter(Context context,int talkList,ArrayList<ChatRoomListItemVO> list) {
        this.context = context;
        this.layout=talkList;
        this.chatRoomListData=list;
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dbHelper=new SQLiteDBHelper(context);
    }
    public void add(ChatRoomListItemVO chatRoomListItemVO){
        chatRoomListData.add(chatRoomListItemVO);
    }
    @Override
    public int getCount() {
        return chatRoomListData.size();
    }

    @Override
    public Object getItem(int position) {
        return chatRoomListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(layout,parent,false);
            ChatRoomListHolder chatRoomListHolder=new ChatRoomListHolder(convertView);
            convertView.setTag(chatRoomListHolder);
        }
        ChatRoomListHolder chatRoomListHolder=(ChatRoomListHolder)convertView.getTag();
        ImageView profilIemageView=chatRoomListHolder.profilImageView;
        TextView nameView=chatRoomListHolder.nameView;
        TextView lastReceivedMessage=chatRoomListHolder.lastReceivedMessage;
        TextView peopleCount=chatRoomListHolder.peopleCount;
        TextView date=chatRoomListHolder.date;
        final ChatRoomListItemVO chatRoomListItemVO=chatRoomListData.get(position);
        Glide.with(context).load(url+"/profile/"+chatRoomListItemVO.getNames().get(0)+".png").
                diskCacheStrategy(DiskCacheStrategy.NONE).
                skipMemoryCache(true).
                into(profilIemageView);
        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append(chatRoomListItemVO.getNames().get(0));
        for(int i=1;i<chatRoomListItemVO.getNames().size();i++){
            stringBuffer.append(",");
            stringBuffer.append(chatRoomListItemVO.getNames().get(i));
        }
        nameView.setText(stringBuffer);

        lastReceivedMessage.setText(dbHelper.getLastMessage(chatRoomListItemVO.getChatRoomId()));
        peopleCount.setText(chatRoomListItemVO.getNames().size()+"");
        date.setText(dbHelper.getLastMessageDate(chatRoomListItemVO.getChatRoomId()));
        return convertView;
    }
    private static final BitmapFactory.Options options = new BitmapFactory.Options();
    private static Bitmap getProfileImage(Context context,int MAX_IMAGE_SIZE){
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
        ChatRoomListHolder chatRoomListHolder;
        BitmapAsync(ChatRoomListHolder chatRoomListHolder){
            this.chatRoomListHolder=chatRoomListHolder;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            chatRoomListHolder.profilImageView.setImageBitmap(bitmap);
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            return getProfileImage(context,50);
        }
    }
}
