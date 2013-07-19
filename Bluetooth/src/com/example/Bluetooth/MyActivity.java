package com.example.Bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class MyActivity extends Activity {
    private boolean isAutomatic;

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
        } catch (NullPointerException e) {
            Log.e("TAG", e.toString());
        }
        try {
            sendKey();
        } catch (InterruptedException e) {
            Log.e("TAG", e.toString());
        }

        search();

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void sendKey() throws InterruptedException {
        if (isAutomatic) {
            Intent intent = new Intent(MyActivity.this, SettingActivity.class);
            startActivity(intent);
        } else {
            setContentView(R.layout.disautomatic_layout);

            Button openLockBt = (Button) this.findViewById(R.id.openBt);
            Button settingBt = (Button) this.findViewById(R.id.setting);

            openLockBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent comminuteIntent = new Intent(MyActivity.this, ComminuteActivity.class);
                    startActivity(comminuteIntent);
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

    private void search() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (!adapter.isEnabled()) {
            adapter.enable();
        }
        Intent enable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        enable.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);//3600为蓝牙设备可见时间
        startActivity(enable);
    }
}



