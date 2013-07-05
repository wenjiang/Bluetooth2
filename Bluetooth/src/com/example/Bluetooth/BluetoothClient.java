package com.example.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-6-27
 * Time: 下午10:29
 * To change this template use File | Settings | File Templates.
 */
public class BluetoothClient {
    private boolean isConnectSuccess;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private Context context;

    public BluetoothClient(Context context) {
        isConnectSuccess = false;
        this.context = context;
    }

    private void search() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (!adapter.isEnabled()) {
            adapter.enable();
        }
        Intent enable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        enable.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600); //3600为蓝牙设备可见时间
        Toast.makeText(context, "设备可以被查找", Toast.LENGTH_LONG).show();
        context.startActivity(enable);
        Intent searchIntent = new Intent(context, SearchActivity.class);
        context.startActivity(searchIntent);
    }

    public void connect() throws InterruptedException {
        search();
        Thread.sleep(100);
        Toast.makeText(context, "连接到设备", Toast.LENGTH_LONG).show();
        isConnectSuccess = true;
    }

    public boolean isConnectSuccess() {
        return this.isConnectSuccess;
    }
}
