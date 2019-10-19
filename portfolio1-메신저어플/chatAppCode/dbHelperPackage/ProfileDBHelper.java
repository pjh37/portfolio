package com.example.myfriends.dbHelperPackage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;

import javax.annotation.Nullable;

public class ProfileDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="MyFriends.db";
    private static final String PROFILE_TABLE_NAME="profile_data";
    int scale=0;
    private static final BitmapFactory.Options options = new BitmapFactory.Options();
    public ProfileDBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query="CREATE TABLE IF NOT EXISTS "+PROFILE_TABLE_NAME+" ("+
                "_id integer primary key autoincrement,"+
                "nicName TEXT,"+
                "profileImageUri TEXT,"+
                "backgroundImageUri TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query="drop table "+PROFILE_TABLE_NAME;
        db.execSQL(query);
        onCreate(db);
    }
    public void initProfile(String nicName){
        boolean isExist=false;
        String query="select * from "+PROFILE_TABLE_NAME;
        SQLiteDatabase db=this.getWritableDatabase();
        this.onCreate(db);
        try{
            Cursor cursor=db.rawQuery(query,null);
            while(cursor.moveToNext()){
                if(cursor.getString(1).equals(nicName)){
                    isExist=true;
                    break;
                }
            }
            if(!isExist){
                ContentValues values=new ContentValues();
                values.put("nicName",nicName);
                db.insert(PROFILE_TABLE_NAME,null,values);
            }
        }catch (Exception error){
            error.printStackTrace();
        }finally {
            db.close();
        }
        Log.v("initProfile","초기화완료");

    }
    public void updateProfileImage(String nicName,String imageUriString){
        SQLiteDatabase db=this.getWritableDatabase();
        this.onCreate(db);
        ContentValues values=new ContentValues();
        values.put("profileImageUri",imageUriString);
        db.update(PROFILE_TABLE_NAME,values,"nicName=?",new String[]{nicName});
        db.close();
    }
    public void updateBackgroundProfileImage(String nicName,String imageUriString){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("backgroundImageUri",imageUriString);
        db.update(PROFILE_TABLE_NAME,values,"nicName=?",new String[]{nicName});
        db.close();
    }

    public byte[] getBytes(Bitmap bitmap){
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        return stream.toByteArray();
    }
    public Uri getProfileImage(String nicName){
        Log.v("scale","호출");
        boolean isExist=false;

        //Bitmap bitmap;
        //options.inJustDecodeBounds = true;
        String query="select * from "+PROFILE_TABLE_NAME+" WHERE nicName=?";
        SQLiteDatabase db=this.getWritableDatabase();
        this.onCreate(db);
        Cursor cursor=db.rawQuery(query,new String[]{nicName});


        if(cursor.moveToNext()){
            if(cursor.getString(2)!=null){
                db.close();
                return Uri.parse(cursor.getString(2));
            }
            /*
            Log.v("scale","이름은 검색됨");
            byte[] image= cursor.getBlob(2);

            if(image!=null){
                Log.v("scale","이미지가 널값은 아님");
                BitmapFactory.decodeByteArray(image,0,image.length,options);
                Log.v("scale","프로필 이미지 크기 "+"가로 : "+options.outWidth+" 세로 : "+options.outHeight);
                if (options.outHeight > 100 || options.outWidth > 100) {
                    scale = (int) Math.pow(2, (int) Math.round(Math.log(100 / (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
                }
                options.inSampleSize=scale;
                Log.v("scale",scale+"");
                bitmap=BitmapFactory.decodeByteArray(image,0,image.length,options);
                db.close();
                return bitmap;
            }
            */
        }
        db.close();
        return null;
    }
    public Uri getBackgroundProfileImage(String nicName){
        Log.v("scale","호출");
        boolean isExist=false;

        //Bitmap bitmap;
        //options.inJustDecodeBounds = true;
        String query="select * from "+PROFILE_TABLE_NAME+" WHERE nicName=?";
        SQLiteDatabase db=this.getWritableDatabase();
        this.onCreate(db);
        Cursor cursor=db.rawQuery(query,new String[]{nicName});


        if(cursor.moveToNext()){
            if(cursor.getString(3)!=null){
                db.close();
                return Uri.parse(cursor.getString(3));
            }
            /*
            Log.v("scale","이름은 검색됨");
            byte[] image= cursor.getBlob(3);

            if(image!=null){
                Log.v("scale","이미지가 널값은 아님");
                Log.v("scale","background 이미지 크기 "+"가로 : "+options.outWidth+" 세로 : "+options.outHeight);
                BitmapFactory.decodeByteArray(image,0,image.length,options);
                if (options.outHeight > 100 || options.outWidth > 100) {
                    scale = (int) Math.pow(2, (int) Math.round(Math.log(100 / (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
                }
                options.inSampleSize=scale;
                Log.v("scale",scale+"");
                bitmap=BitmapFactory.decodeByteArray(image,0,image.length);
                db.close();
                return bitmap;
            }
            */
        }
        db.close();
        return null;
    }
}
