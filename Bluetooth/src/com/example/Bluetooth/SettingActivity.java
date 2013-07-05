package com.example.Bluetooth;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-6-29
 * Time: 下午10:17
 * To change this template use File | Settings | File Templates.
 */
public class SettingActivity extends Activity {
    private String time;
    private boolean isAutomatic;
    private RadioButton automaticBt;
    private RadioButton disautomaticBt;
    private EditText editText;
    private PropertiesProvider provider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settinglayout);

        RadioGroup group = (RadioGroup) this.findViewById(R.id.choices);
        Button saveButton = (Button) this.findViewById(R.id.save);
        Button settingKeyButton = (Button) this.findViewById(R.id.key);

        automaticBt = (RadioButton) this.findViewById(R.id.automatic);
        disautomaticBt = (RadioButton) this.findViewById(R.id.disAutomatic);
        editText = (EditText) this.findViewById(R.id.time);
        provider = new PropertiesProvider(this);

        time = provider.get("time").toString();
        editText.setText(time);
        isAutomatic = Boolean.valueOf(provider.get("isAutomatic").toString());

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
                Lock lock = new Lock();
                lock.createKey(getPath(uri));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Bitmap getBitmap(Uri uri) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 5;
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
                e.printStackTrace();
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
}
