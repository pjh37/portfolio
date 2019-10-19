package com.example.myfriends.managerPackage;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

public class AppLifeManager implements Application.ActivityLifecycleCallbacks {
    AppStatus mAppStatus;
    private int running;
    public AppLifeManager(){
        running=0;
        mAppStatus=AppStatus.INIT;
    }
    private static class AppControlHolder{
        public static final AppLifeManager appControl=new AppLifeManager();
    }
    public static AppLifeManager getInstance(){
        return AppControlHolder.appControl;
    }
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        running++;
        if(running>1){
            mAppStatus=AppStatus.FOREGROUND;
        }
        Log.v("running",running+"");
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        if(--running==0){
            mAppStatus=AppStatus.BACKGROUND;
        }
        Log.v("running",running+"");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public enum AppStatus{
        INIT,
        BACKGROUND,
        FOREGROUND;
    }
    public AppStatus getAppStatus(){
        return mAppStatus;
    }
}

