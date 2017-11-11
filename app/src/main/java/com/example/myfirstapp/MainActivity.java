package com.example.myfirstapp;

import android.content.BroadcastReceiver;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ToggleButton;
import android.view.View;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class MainActivity extends AppCompatActivity {

    private boolean status = false;
    private boolean playing = false;
    private MediaPlayer mPlayer;

    Camera cam;
    Parameters camParams;
    boolean hasCam;
    int freq = 100;
    StroboRunner sr;
    Thread t;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        wl.acquire();

        final ToggleButton button = findViewById(R.id.toggleButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                status = status ? false : true;
                System.out.println("Did button click; status is " + status);
                if (playing)
                    mPlayer.stop();
                    turnOnOff(false);

            }


        });

        BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
            int scale = -1;
            int level = -1;
            int voltage = -1;
            int temp = -1;
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("in onrecevie");
                level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);

                System.out.println("BatteryManager" + "level is "+level+"/"+scale+", temp is "+temp+", voltage is "+voltage);

                //System.out.println(isConnected(context));


                if(status) {
                    mPlayer = MediaPlayer.create(MainActivity.this, R.raw.siren);
                    mPlayer.start();
                    playing = true;

                    turnOnOff(true);




                }






            }
        };
        IntentFilter filter = new IntentFilter(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(batteryReceiver, filter);







    }

    public static boolean isConnected(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        return plugged != BatteryManager.BATTERY_PLUGGED_AC || plugged != BatteryManager.BATTERY_PLUGGED_USB;
    }

    public boolean doNothing(){
        return true;
    }


    protected void onResume() {
        super.onResume();

        try {
            cam = Camera.open();
            camParams = cam.getParameters();
            cam.startPreview();
            hasCam = true;
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void turnOnOff(boolean on) {
        if(on) {

            if(freq != 0) {
                sr = new StroboRunner();
                sr.freq = freq;
                t = new Thread(sr);
                t.start();
                return;
            } else {
                camParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            }

        } else if(!on) {
            if(t != null) {
                sr.stopRunning = true;
                t = null;
                return;
            } else {
                camParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
        }

        cam.setParameters(camParams);
        cam.startPreview();
    }

    private class StroboRunner implements Runnable {
        int freq;
        boolean stopRunning = false;

        @Override
        public void run() {
            Camera.Parameters paramsOn = cam.getParameters();
            Camera.Parameters paramsOff = camParams;
            paramsOn.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            paramsOff.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

            try {
                while(!stopRunning) {
                    cam.setParameters(paramsOn);
                    cam.startPreview();
                    Thread.sleep(100 - freq);
                    cam.setParameters(paramsOff);
                    cam.startPreview();
                    Thread.sleep(freq);
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }






}
