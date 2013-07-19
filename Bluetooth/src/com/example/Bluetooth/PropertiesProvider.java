package com.example.Bluetooth;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-6-29
 * Time: 下午11:13
 * To change this template use File | Settings | File Templates.
 */
public class PropertiesProvider {
    private Properties properties;
    private Context context;

    public PropertiesProvider(Context context) {
        this.properties = new Properties();
        this.context = context;
    }

    public boolean save(String key, Object value) {
        properties.put(key, String.valueOf(value));
        try {
            FileOutputStream outputStream = context.openFileOutput("value.cfg", Context.MODE_WORLD_WRITEABLE);
            properties.store(outputStream, "");
            outputStream.close();
        } catch (Exception e) {
            Log.e("TAG", e.toString());
            return false;
        }
        return true;
    }

    public Object get(String key) {
        try {
            FileInputStream inputStream = context.openFileInput("value.cfg");
            properties.load(inputStream);
            inputStream.close();
        } catch (Exception e) {
            Log.e("TAG", e.toString());
            return null;
        }
        return properties.getProperty(key);
    }
}
