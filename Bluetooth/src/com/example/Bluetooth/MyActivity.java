package com.example.Bluetooth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class MyActivity extends Activity {
    private boolean isAutomatic;
    private boolean isDisautomatic;
    private String information = "";
    private Lock lock;
    private BluetoothClient bluetoothClient;
    private String time;
    private LinearLayout layout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button openButton = (Button) this.findViewById(R.id.setting);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        PropertiesProvider provider = new PropertiesProvider(this);
        try {
            isAutomatic = Boolean.valueOf(provider.get("isAutomatic").toString());
            isDisautomatic = Boolean.valueOf(provider.get("isDisautomatic").toString());
            time = provider.get("time").toString();
        } catch (NullPointerException e) {
            //TODO Handle the exce
        }

        bluetoothClient = new BluetoothClient();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                bluetoothClient.connect();
            }
        });
        /*
        while (true) {
            if (bluetoothClient.isSearchSuccess()) {
                thread.start();
                if(bluetoothClient.isConnectSuccess()){
                    sendKey();
                }
            }
            else{
                bluetoothClient.search();
            }
        }
        */
    }


    private void sendKey() {
        if (isAutomatic) {
            lock = new Lock();
            if (information.equals("关门")) {
                int delayTime = Integer.parseInt(time);
                lock.sendKey(delayTime);
            } else {
                lock.sendKey();
            }
        } else if (isDisautomatic) {
            Button openButton = new Button(this);
            openButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            openButton.setText("开锁");
            layout = (LinearLayout) this.findViewById(R.layout.main);
            layout.addView(openButton);

            openButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lock = new Lock();
                    lock.sendKey();
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



