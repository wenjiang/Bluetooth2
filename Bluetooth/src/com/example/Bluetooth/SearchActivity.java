package com.example.Bluetooth;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.*;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-7-5
 * Time: 下午8:18
 * To change this template use File | Settings | File Templates.
 */
public class SearchActivity extends ListActivity {
    private Handler handler = new Handler();
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
    private boolean isSearchSuccess;
    private Runnable searchWork = new Runnable() {
        public void run() {
            bluetoothAdapter.startDiscovery();
            while (true) {
                if (isSearchSuccess) {
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    //TODO Handle the exception
                }
            }
        }
    };

    private BroadcastReceiver resultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            devices.add(device);
            showDevices();
        }
    };

    private BroadcastReceiver searchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            unregisterReceiver(resultReceiver);
            unregisterReceiver(this);
            isSearchSuccess = true;
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        setContentView(R.layout.searchlayout);
        if (!bluetoothAdapter.isEnabled()) {
            finish();
            return;
        }

        IntentFilter searchFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(searchReceiver, searchFilter);
        IntentFilter resultFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(resultReceiver, resultFilter);
        Utils.indeterminate(SearchActivity.this, handler, "搜索中...", searchWork, new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                while (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }
                isSearchSuccess = true;
            }
        }, true);
    }

    protected void showDevices() {
        List<String> list = new ArrayList<String>();
        list.add("ha");
        for (int i = 0, size = devices.size(); i < size; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            BluetoothDevice device = devices.get(i);
            stringBuilder.append(device.getAddress());
            stringBuilder.append('\n');
            stringBuilder.append(device.getName());
            String deviceInfo = stringBuilder.toString();
            list.add(deviceInfo);
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, list);
        setListAdapter(adapter);
    }

    protected void onListItemClick(ListView listView, View view, int position, long id) {
        Intent result = new Intent();
        result.putExtra(BluetoothDevice.EXTRA_DEVICE, devices.get(position));
        setResult(RESULT_OK, result);
        finish();
    }
}
