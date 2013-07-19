package com.example.Bluetooth;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-6-29
 * Time: 下午10:17
 * To change this template use File | Settings | File Templates.
 */
public class SettingActivity extends Activity {
    private boolean isAutomatic;
    private String time;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);

        RadioGroup group = (RadioGroup) this.findViewById(R.id.choices);
        Button saveButton = (Button) this.findViewById(R.id.save);
        Button settingKeyButton = (Button) this.findViewById(R.id.key);

        final RadioButton automaticBt = (RadioButton) this.findViewById(R.id.automatic);
        final RadioButton disautomaticBt = (RadioButton) this.findViewById(R.id.disAutomatic);
        final EditText editText = (EditText) this.findViewById(R.id.time);
        final PropertiesProvider provider = new PropertiesProvider(this);
        try {
            time = provider.get("time").toString();
            isAutomatic = Boolean.valueOf(provider.get("isAutomatic").toString());
        } catch (Exception e) {
            time = "0";
            isAutomatic = false;
        }
        editText.setText(time);

        if (isAutomatic) {
            automaticBt.setChecked(true);
        } else {
            disautomaticBt.setChecked(true);
        }

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == automaticBt.getId()) {
                    isAutomatic = true;
                } else if (checkedId == disautomaticBt.getId()) {
                    isAutomatic = false;
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                time = editText.getText().toString();
                if (provider.save("isAutomatic", isAutomatic) &&
                        provider.save("time", time)) {
                    Intent intent = new Intent(SettingActivity.this, MyActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });

        settingKeyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = getBitmap(uri);
                ImageView image = (ImageView) this.findViewById(R.id.image);
                image.setImageBitmap(bitmap);
                createKey(getPath(uri));
            } catch (Exception e) {
                Log.e("TAG", e.toString());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Bitmap getBitmap(Uri uri) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 5;
        File sdkRoot = Environment.getExternalStorageDirectory();
        sdkRoot.mkdir();
        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = this.getContentResolver().openAssetFileDescriptor(uri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);
                fileDescriptor.close();
            } catch (IOException e) {
                Log.e("TAG", e.toString());
            }
        }
        return bitmap;
    }

    private String getPath(Uri uri) {
        String[] data = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, data, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(columnIndex);
        return path;
    }

    private void createKey(String path) {
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
            Log.e("TAG", e.toString());
        }
        BigInteger bigInteger = new BigInteger(1, digest.digest());
        String key = bigInteger.toString(32);
        while (key.getBytes().length != 64) {
            key += '0';
        }
    }
}
