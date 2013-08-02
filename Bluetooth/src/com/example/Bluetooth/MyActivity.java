package com.example.Bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MyActivity extends Activity {
    private BluetoothDevice device;
    public static Bluetooth bluetooth;
    private ImageButton lockBt;
    public static BluetoothReceiver receiver;
    private BluetoothAdapter bluetoothAdapter;
    private final String lockName = "BOLUTEK";
    private final byte[] openLockBytes = {(byte) 0x55, (byte) 0x01, (byte) 0x05, (byte) 0xAA};
    private boolean isAutomatic;
    private PropertiesProvider provider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Utils.setContext(MyActivity.this);

        Resources resource = getResources();
        Drawable drawable = resource.getDrawable(R.drawable.background_color);
        this.getWindow().setBackgroundDrawable(drawable);
        provider = new PropertiesProvider(this);
        isAutomatic = Boolean.valueOf((provider.get("isAutomatic")).toString());

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
            Toast.makeText(this, "蓝牙打开中...", Toast.LENGTH_LONG).show();
            receiveDevice();
        } else {
            receiveDevice();
        }


        Button settingBt = (Button) this.findViewById(R.id.settingBt);
        lockBt = (ImageButton) this.findViewById(R.id.lockBt);

        lockBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
        settingBt.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                bluetooth.comminute(openLockBytes, 1);
                Thread.sleep(100);
                String path = Utils.getPath(uri, MyActivity.this);
                bluetooth.sendKey((Utils.createKey(path)).getBytes());
                Thread.sleep(100);
                lockBt.setImageDrawable(getResources().getDrawable(R.drawable.open_lock));
            } catch (Exception e) {
                Log.e("openKey", e.toString());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        try {
            stopService(new Intent(MyActivity.this, OpenService.class));
            unregisterReceiver(receiver);
            bluetooth.close();

        } catch (Exception e) {
            Log.e("Close", e.toString());
        }
        super.onDestroy();
    }

    private class BluetoothReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice receiverDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (isLock(receiverDevice)) {
                    device = receiverDevice;
                }
            }
            bluetooth = new Bluetooth(device, Utils.readState());

            bluetooth.connect();
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    while (true) {
                        if (Utils.isConnect) {
                            try {
                                if (isAutomatic) {
                                    startService(new Intent(MyActivity.this, OpenService.class));
                                    break;
                                }
                            } catch (Exception e) {
                                break;
                            }
                        }
                    }
                }
            });
            thread.start();
        }

    }

    private void receiveDevice() {
        bluetoothAdapter.startDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        receiver = new BluetoothReceiver();
        registerReceiver(receiver, filter);
    }

    private boolean isLock(BluetoothDevice device) {
        return (device.getName()).equals(lockName);
    }
}



