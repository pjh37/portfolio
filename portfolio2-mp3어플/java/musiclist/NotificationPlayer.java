package com.example.playmusic.musiclist;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.RemoteViews.RemoteView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.example.playmusic.MainActivity;
import com.example.playmusic.R;
import com.example.playmusic.servicePackage.MusicService;

public class NotificationPlayer {
    private final static int NOTIFICATION_ID=0X111;
    private final static String channelID="default_channel_id";
    private final static String channelName="player";
    private MusicService mService;
    private NotificationManager mNotificationManager;
    private NotificationManagerBuilder mNotificationManagerBuilder;
    private NotificationChannel mNotificationChannel;

    private boolean isForeground;
    public NotificationPlayer(MusicService service){
        mService=service;
        mNotificationManager=(NotificationManager)service.getSystemService(Context.NOTIFICATION_SERVICE);
    }
    public void updateNotificationPlayer(){
        cancel();
        mNotificationManagerBuilder=new NotificationManagerBuilder();
        mNotificationManagerBuilder.execute();
    }
    public void removeNotificationPlayer(){
        cancel();
        mService.stopForeground(true);
        isForeground=false;
    }
    private void cancel(){
        if(mNotificationManagerBuilder!=null){
            mNotificationManagerBuilder.cancel(true);
            mNotificationManagerBuilder=null;
        }
    }
    private class NotificationManagerBuilder extends AsyncTask<Void,Void, Notification>{
        private RemoteViews mRemoteViews;
        private NotificationCompat.Builder mNotificationBuilder;
        private PendingIntent mMainPendingIntent;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Intent mainIntent=new Intent(mService, MainActivity.class);
            mMainPendingIntent=PendingIntent.getActivity(mService,0,mainIntent,0);
            mRemoteViews=creteRemoteView(R.layout.notification_player);
            if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O){
                mNotificationChannel=new NotificationChannel(channelID,channelName,NotificationManager.IMPORTANCE_DEFAULT);
                mNotificationManager.createNotificationChannel(mNotificationChannel);
                mNotificationBuilder=new NotificationCompat.Builder(mService,channelID);
            }else{
                mNotificationBuilder=new NotificationCompat.Builder(mService);
            }

            mNotificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                    .setOngoing(true)
                    .setContentIntent(mMainPendingIntent)
                    .setContent(mRemoteViews);
            Notification notification=mNotificationBuilder.build();
            notification.contentIntent=mMainPendingIntent;
            if(!isForeground){
                isForeground=true;
                mService.startForeground(NOTIFICATION_ID,notification);
            }
        }

        @Override
        protected void onPostExecute(Notification notification) {
            super.onPostExecute(notification);
            try{
                mNotificationManager.notify(NOTIFICATION_ID,notification);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected Notification doInBackground(Void... voids) {
            mNotificationBuilder.setContent(mRemoteViews);
            mNotificationBuilder.setContentIntent(mMainPendingIntent);
            mNotificationBuilder.setPriority(Notification.PRIORITY_MAX);
            Notification notification=mNotificationBuilder.build();
            updateRemoteView(mRemoteViews,notification);
            return notification;
        }
        private RemoteViews creteRemoteView(int layoutId){
            RemoteViews remoteView=new RemoteViews(mService.getPackageName(),layoutId);
            Intent actionTogglePlay=new Intent(BroadcastActions.TOGGLE_PLAY);
            Intent actionPrevious=new Intent(BroadcastActions.PREVIOUS);
            Intent actionNext=new Intent(BroadcastActions.NEXT);
            PendingIntent togglePlay=PendingIntent.getService(mService,0,actionTogglePlay,0);
            PendingIntent previous=PendingIntent.getService(mService,0,actionPrevious,0);
            PendingIntent next=PendingIntent.getService(mService,0,actionNext,0);
            remoteView.setOnClickPendingIntent(R.id.previous,previous);
            remoteView.setOnClickPendingIntent(R.id.playOrPause,togglePlay);
            remoteView.setOnClickPendingIntent(R.id.next,next);
            return remoteView;
        }
        private void updateRemoteView(RemoteViews remoteViews, Notification notification) {
            if (mService.isPlaying()) {
                remoteViews.setImageViewResource(R.id.playOrPause, R.drawable.ic_pause_btn);
            } else {
                remoteViews.setImageViewResource(R.id.playOrPause, R.drawable.ic_play_btn);
            }

            MusicVO info = mService.getPlayInfo();
            remoteViews.setTextViewText(R.id.Title, info.getmTitle());
            Uri albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), info.getmAlbumId());
            NotificationTarget notificationTarget=new NotificationTarget(mService,R.id.bottom_albumImage,remoteViews,notification,NOTIFICATION_ID);
            Glide.with(mService).asBitmap().load(albumArtUri).error(R.drawable.ic_album_default_img).into(notificationTarget);
        }
    }
}
