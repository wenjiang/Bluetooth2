package com.example.Bluetooth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.*;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-6-29
 * Time: 下午10:17
 * To change this template use File | Settings | File Templates.
 */
public class SettingActivity extends Activity {
    private final byte[] keySettingBytes = {(byte) 0x55, (byte) 0x01, (byte) 0x01, (byte) 0xAA};
    private Bluetooth bluetooth;
    private TextView delayTime;
    private ArrayAdapter adapter;
    private PropertiesProvider provider;
    private String key;
    private ImageView imageView;
    private Uri uri;
    private boolean isAutomatic = false;
    private int timePosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        Resources resource = getResources();
        Drawable drawable = resource.getDrawable(R.drawable.background_color);
        this.getWindow().setBackgroundDrawable(drawable);
        bluetooth = MyActivity.bluetooth;
        imageView = (ImageView) findViewById(R.id.image);
        ApplicationInfo appInfo = getApplicationInfo();
        int imageID = getResources().getIdentifier("image", "drawable", appInfo.packageName);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageID);
        imageView.setImageBitmap(bitmap);
        provider = new PropertiesProvider(this);


        Spinner spinner = (Spinner) this.findViewById(R.id.spinner);
        delayTime = (TextView) this.findViewById(R.id.delay);
        adapter = ArrayAdapter.createFromResource(this, R.array.array_spinner,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setPrompt("请选择开锁延时时间");
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                delayTime.setText("开锁延时时间:" + adapter.getItem(position));
                String timeStr = adapter.getItem(position).toString();
                timePosition = position;
                int time = 0;
                if (timeStr.indexOf("分") == -1) {
                    time = Integer.valueOf(timeStr.substring(0, timeStr.length() - 1));
                } else {
                    time = Integer.valueOf(timeStr.substring(0, timeStr.length() - 2)) * 60;
                }

                provider.save("time", time);
                provider.save("position", position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        spinner.setVisibility(View.VISIBLE);

        SwitchButton slidBt = (SwitchButton) this.findViewById(R.id.switch_button);
        try {
            isAutomatic = Boolean.valueOf((provider.get("isAutomatic")).toString());
            timePosition = Integer.valueOf(provider.get("position").toString());

        } catch (NullPointerException e) {
            slidBt.setChecked(false);
            timePosition = 0;
        }
        slidBt.setChecked(isAutomatic);
        spinner.setSelection(timePosition);
        delayTime.setText("开锁延时时间:" + adapter.getItem(timePosition));

        slidBt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                provider.save("isAutomatic", isChecked);
            }
        });


        Button settingBt = (Button) this.findViewById(R.id.settingBt);
        Button sendBt = (Button) this.findViewById(R.id.sendBt);
        settingBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        sendBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendKey();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            uri = data.getData();
            try {
                Bitmap bitmap = Utils.getBitmap(uri, SettingActivity.this);
                imageView.setImageBitmap(bitmap);
                String path = Utils.getPath(uri, SettingActivity.this);
                provider.save("image", path);
                key = Utils.createKey(path);
                bluetooth.comminute(keySettingBytes, 0);
            } catch (Exception e) {
                Log.e("showKey", e.toString());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void sendKey() {
        final ProgressDialog progressDialog = ProgressDialog.show(SettingActivity.this,
                "发送", "正在发送中...");
        Thread thread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    bluetooth.sendKey(key.getBytes());
                    progressDialog.dismiss();
                } catch (Exception e) {
                    progressDialog.dismiss();
                    Log.e("SendKey", e.toString());
                }
                Looper.loop();
            }
        };
        thread.start();
    }


}
