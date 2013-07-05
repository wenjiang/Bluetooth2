package com.example.Bluetooth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class MyActivity extends Activity {
    private boolean isAutomatic;
    private boolean isDisautomatic;
    private String information = "";
    private Lock lock;
    private BluetoothClient bluetoothClient;
    private String time;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button settingButton = (Button) this.findViewById(R.id.setting);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        PropertiesProvider provider = new PropertiesProvider(this);
        try {
            isAutomatic = Boolean.valueOf(provider.get("isAutomatic").toString());
            time = provider.get("time").toString();
        } catch (NullPointerException e) {
            //TODO Handle the exception
        }
        try {
            sendKey();
        } catch (InterruptedException e) {
            //TODO Handle the exception
        }

    }

    private void sendKey() throws InterruptedException {
        if (isAutomatic) {
            bluetoothClient = new BluetoothClient(MyActivity.this);
            bluetoothClient.connect();
            lock = new Lock();
            lock.sendKey();
        } else {
            setContentView(R.layout.anothermainlayout);

            Button openLockBt = (Button) this.findViewById(R.id.openBt);
            Button settingBt = (Button) this.findViewById(R.id.setting);

            openLockBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        bluetoothClient = new BluetoothClient(MyActivity.this);
                        bluetoothClient.connect();
                        lock = new Lock();
                        lock.sendKey();
                    } catch (InterruptedException e) {
                        //TODO Handle the exception
                    }
                }
            });

            settingBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyActivity.this, SettingActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}



