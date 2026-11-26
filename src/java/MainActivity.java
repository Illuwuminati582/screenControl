package com.example.screencontrol;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    orientationController mService;
    boolean mBound = false;
    Intent svc;

    private final ServiceConnection con = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            orientationController.LocalBinder binder = (orientationController.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if(!Settings.canDrawOverlays(this)) {
            Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(myIntent);
        }

        if(Settings.canDrawOverlays(this)) {
            svc = new Intent(this, orientationController.class);
            startService(svc);
            bindService(svc, con, Context.BIND_AUTO_CREATE);
        }

        Button stop_bttn = (Button) findViewById(R.id.stopbutton);
        stop_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                if(mBound) {
                    unbindService(con);
                }
                if(serviceIsRunning(orientationController.class)) {
                    stopService(svc);
                }
                finish();
            }
        });

        ImageButton r90 = (ImageButton) findViewById(R.id.rotate90);
        r90.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBound) {
                    mService.rotate90();
                }
            }
        });

        ImageButton flip180 = (ImageButton) findViewById(R.id.flip180);
        flip180.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBound) {
                    mService.flip180();
                }
            }
        });
    }

    @Override
    protected  void onDestroy(){
        super.onDestroy();
    }

    protected void onRestart(){
        super.onRestart();
        reconnect();
    }

    protected void reconnect(){
        if(Settings.canDrawOverlays(this)) {
            if (!mBound) {
                if (serviceIsRunning(orientationController.class)) {
                    bindService(svc, con, Context.BIND_AUTO_CREATE);
                } else {
                    svc = new Intent(this, orientationController.class);
                    startService(svc);
                    bindService(svc, con, Context.BIND_AUTO_CREATE);
                }
            }
        }
    }

    protected boolean serviceIsRunning(Class<?> serviceClass){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if(!manager.getRunningServices(Integer.MAX_VALUE).isEmpty()) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }
}