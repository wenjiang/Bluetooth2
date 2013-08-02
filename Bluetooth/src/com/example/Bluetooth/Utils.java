package com.example.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-7-20
 * Time: 下午9:45
 * To change this template use File | Settings | File Templates.
 */
public class Utils {
    private static Toast toast;
    public static boolean isReceiver = false;
    public static boolean isConnect = false;


    public static void setContext(Context context) {
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }

    public static String bytesToHexString(byte[] bytes) {
        String result = "";
        for (int i = 0; i < bytes.length; i++) {
            String hexString = Integer.toHexString(bytes[i] & 0xFF);
            if (hexString.length() == 1) {
                hexString = '0' + hexString;
            }
            result += hexString.toUpperCase();
        }
        return result;
    }

    public static Handler readState() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Bluetooth.CONNECT_FAILED:
                        toast.setText("连接失败");
                        toast.show();
                        break;
                    case Bluetooth.CONNECT_SUCCESS:
                        toast.setText("连接成功");
                        toast.show();
                        isConnect = true;
                        break;
                    case Bluetooth.READ_FAILED:
                        toast.setText("读取失败");
                        toast.show();
                        break;
                    case Bluetooth.WRITE_FAILED:
                        toast.setText("写入失败");
                        toast.show();
                        break;
                    case Bluetooth.DATA:
                        String message = (String) msg.obj;
                        switch (msg.arg1) {
                            case 0:
                                if (message.equals("550201AA")) {
                                    toast.setText("请保存Key");
                                    toast.show();
                                    break;
                                } else if (message.equals("550103AA")) {
                                    toast.setText("Key设置失败！");
                                    toast.show();
                                    break;
                                } else if (message.equals("550102AA")) {
                                    toast.setText("Key设置成功！");
                                    toast.show();
                                    break;
                                } else if (message.equals("550104AA")) {
                                    toast.setText("请再保存一次Key!");
                                    toast.show();
                                    break;
                                } else if (message.equals("550106AA")) {
                                    toast.setText("开锁成功");
                                    isReceiver = true;
                                    toast.show();
                                    break;
                                } else if (message.equals("550107AA")) {
                                    toast.setText("开锁失败");
                                    toast.show();
                                    break;
                                } else {
                                    toast.setText("未知错误!");
                                    toast.show();
                                    break;
                                }
                            case 1:
                                if (message.equals("550205AA")) {
                                    toast.setText("请按一下锁");
                                    toast.show();
                                    break;
                                } else if (message.equals("550108AA")) {
                                    toast.setText("密码错误");
                                    toast.show();
                                    break;
                                } else {
                                    toast.setText("未知错误!");
                                    toast.show();
                                    break;
                                }
                            case 2:
                                if (message.equals("550205AA")) {
                                    toast.setText("正开锁中");
                                    toast.show();
                                    break;
                                } else if (message.equals("550108AA")) {
                                    toast.setText("密码错误");
                                    toast.show();
                                    break;
                                } else {
                                    toast.setText(message);
                                    toast.show();
                                    break;
                                }
                            default:
                                break;
                        }
                    default:
                        break;
                }
            }
        };
        return handler;
    }

    public static String getPath(Uri uri, Context context) {
        String[] data = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, data, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(columnIndex);
        return path;
    }


    public static Bitmap getBitmap(Uri uri, Context context) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 5;
        File sdkRoot = Environment.getExternalStorageDirectory();
        sdkRoot.mkdir();
        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(uri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);
                fileDescriptor.close();
            } catch (IOException e) {
                Log.e("CreateBitmap", e.toString());
            }
        }
        return bitmap;
    }

    public static String createKey(String path) {
        String result = "";
        File file = new File(path);
        int len;
        byte[] keyByte = new byte[128];
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
            FileInputStream inputStream = new FileInputStream(file);
            while ((len = inputStream.read(keyByte, 0, 64)) != -1) {
                digest.update(keyByte, 0, len);
            }
            inputStream.close();
        } catch (Exception e) {
            Log.e("CreateKey", e.toString());
        }
        byte[] digestKey = digest.digest();
        result += Utils.bytesToHexString(digestKey);
        byte[] bytes = {(byte) 0x00};
        String bluetoothAddress = (BluetoothAdapter.getDefaultAdapter()).getAddress();
        String address = bluetoothAddress.replace(":", "");
        String byteStr = Utils.bytesToHexString(bytes);
        while (result.getBytes().length != 52) {
            result += byteStr;
        }
        result += address;
        return result;
    }
}
