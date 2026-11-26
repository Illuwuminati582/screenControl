package com.example.screencontrol;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

public class orientationController extends Service {

    private View view;
    private WindowManager wm;
    public int orientation;
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder{
        orientationController getService(){
            return orientationController.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        view = new View(this);

        orientation=0;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.x = 0;
        params.y = 0;
        params.width = 0;
        params.height = 0;
        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        wm.addView(view,params);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        wm.removeView(view);
        view = null;
    }

    public void changeOrientation(int req_orientation){
        int selected_orientation;
        switch (req_orientation){
            case 0:
                selected_orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
            case 1:
                selected_orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
            case 2:
                selected_orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                break;
            case 3:
                selected_orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                break;
            default:
                selected_orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.x = 0;
        params.y = 0;
        params.width = 0;
        params.height = 0;
        params.screenOrientation = selected_orientation;

        wm.updateViewLayout(view, params);
    }

    public void rotate90 (){
        if(orientation<3){
            orientation = orientation + 1;
        }else{
            orientation = 0;
        }
        changeOrientation(orientation);
    }

    public void flip180(){
        switch(orientation){
            case 0:
                orientation = 2;
                break;
            case 1:
                orientation = 3;
                break;
            case 2:
                orientation = 0;
                break;
            case 3:
                orientation = 1;
                break;
        }
        changeOrientation(orientation);
    }
}


