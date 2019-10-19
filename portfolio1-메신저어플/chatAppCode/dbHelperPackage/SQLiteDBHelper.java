package com.example.myfriends.dbHelperPackage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.myfriends.chat.ChatDTO;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class SQLiteDBHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="MyFriends.db";
    private static final String CHAT_TABLE_NAME="chat_data";
    private Context context;

    public SQLiteDBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        this.context=context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query="create TABLE IF NOT EXISTS "+CHAT_TABLE_NAME+" (" +
                "_id integer primary key autoincrement,"+
                "chatRoomID TEXT,"+
                "clientID TEXT,"+
                "message TEXT,"+
                "messageTYPE integer,"+
                "fileUrl TEXT,"+
                "fileType integer,"+
                "date TEXT)";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query="drop table "+CHAT_TABLE_NAME;
        db.execSQL(query);
        onCreate(db);
    }
    public void chatInsert(ChatDTO chatDTO){
        SQLiteDatabase db=this.getWritableDatabase();
        this.onCreate(db);
        ContentValues values=new ContentValues();
        chatDTO.setChatRoomID(chatDTO.getChatRoomID().split("/")[1]);
        values.put("chatRoomID",chatDTO.getChatRoomID());
        values.put("messageType",chatDTO.getMessageType());
        values.put("clientID",chatDTO.getClientID());
        values.put("message",chatDTO.getMessage());
        values.put("fileUrl",chatDTO.getFileUrl());
        values.put("fileType",chatDTO.getFileType());
        values.put("date",chatDTO.getDate());

        //db.beginTransaction();
        db.insert(CHAT_TABLE_NAME,null,values);
        //db.endTransaction();
        db.close();
    }
    public ArrayList<ChatDTO> getChatList(String chatRoomID){
        ArrayList<ChatDTO> chatDTOs=new ArrayList<>();
        chatRoomID=chatRoomID.split("/")[1];
        Log.v("splite",chatRoomID);
        String query="select * from "+CHAT_TABLE_NAME+ " where chatRoomID="+chatRoomID;
        SQLiteDatabase db=this.getWritableDatabase();
        this.onCreate(db);
        Cursor cursor=db.rawQuery(query,null);
        while(cursor.moveToNext()){
            Log.v("cursor","getChatList");
            ChatDTO chatDTO=new ChatDTO();
            chatDTO.setChatRoomID(cursor.getString(1));
            chatDTO.setClientID(cursor.getString(2));
            chatDTO.setMessage(cursor.getString(3));
            chatDTO.setMessageType(cursor.getInt(4));
            chatDTO.setFileUrl(cursor.getString(5));
            chatDTO.setFileType(cursor.getInt(6));
            chatDTO.setDate(cursor.getString(7));
            chatDTOs.add(chatDTO);
        }
        db.close();
        return chatDTOs;
    }
    public String getLastMessage(String chatRoomID){
        SQLiteDatabase db=this.getWritableDatabase();
        this.onCreate(db);
        chatRoomID=chatRoomID.split("/")[1];
        String query="select * from "+CHAT_TABLE_NAME+ " where chatRoomID=?";
        Cursor cursor=db.rawQuery(query,new String[]{chatRoomID});
        if(cursor!=null&&cursor.moveToLast()){
            return cursor.getString(3);
        }
        return "";
    }
    public String getLastMessageDate(String chatRoomID){
        SQLiteDatabase db=this.getWritableDatabase();
        this.onCreate(db);
        chatRoomID=chatRoomID.split("/")[1];
        String query="select * from "+CHAT_TABLE_NAME+ " where chatRoomID=?";
        Cursor cursor=db.rawQuery(query,new String[]{chatRoomID});
        if(cursor!=null&&cursor.moveToLast()){
            return cursor.getString(7);
        }
        return "";
    }
}
