package com.example.Bluetooth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-7-31
 * Time: 下午10:55
 * To change this template use File | Settings | File Templates.
 */
public class OpenService extends Service {
    private final byte[] openLockBytes = {(byte) 0x55, (byte) 0x01, (byte) 0x05, (byte) 0xAA};
    public static Bluetooth bluetooth;
    private Thread thread;
    private static boolean isReceiver = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "自动开锁开始启动", Toast.LENGTH_SHORT).show();
        bluetooth = MyActivity.bluetooth;
        PropertiesProvider provider = new PropertiesProvider(getApplicationContext());
        final String path = provider.get("image").toString();
        final int time = Integer.valueOf(provider.get("time").toString());

        thread = new Thread(new Runnable() {


            @Override
            public void run() {
                Looper.prepare();
                while (true) {
                    try {
                        while (!isReceiver) {
                            bluetooth.comminute(openLockBytes, 2);
                            Thread.sleep(100);
                            bluetooth.sendKey(Utils.createKey(path).getBytes());
                        }

                        Thread.sleep(time * 1000);
                        bluetooth.comminute(openLockBytes, 2);
                        Thread.sleep(100);
                        bluetooth.sendKey(Utils.createKey(path).getBytes());
                        break;
                    } catch (Exception e) {
                        break;
                    }
                }
                Looper.loop();
            }


        }

        );
        thread.start();

        return super.

                onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        try {
            Toast.makeText(this, "自动开锁停止", Toast.LENGTH_LONG).show();
            thread.wait();
        } catch (Exception e) {

        }
    }
}
