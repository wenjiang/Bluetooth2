package com.example.Bluetooth;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-6-27
 * Time: 下午10:09
 * To change this template use File | Settings | File Templates.
 */
public class Lock {
    private int time;
    private String key;

    public Lock() {
        time = 0;
    }

    public void createKey(String path) {
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
            e.printStackTrace();
        }
        BigInteger bigInteger = new BigInteger(1, digest.digest());
        key = bigInteger.toString(32);
        while (key.getBytes().length != 64) {
            key += '0';
        }
    }

    public void sendKey() {
        //TODO Send key to PasswordLock

    }

    public void sendKey(int time) {
        //TODO Send key to PasswordLock after time

    }

    public String getKey() {
        return this.key;
    }
}
