package com.ptkebonagung.gotani.utils;

import android.app.Activity;
import android.app.Application;

public class MyApp extends Application {
    private Activity mCurrentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity currentActivity){
        this.mCurrentActivity = currentActivity;
    }
}
