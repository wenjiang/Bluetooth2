package com.example.Bluetooth;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-6-27
 * Time: 下午10:29
 * To change this template use File | Settings | File Templates.
 */
public class BluetoothClient {
     private boolean isSearchSuccess;
     private boolean isConnectSuccess;

    public BluetoothClient(){
         isSearchSuccess = false;
         isConnectSuccess = false;
    }

    public void search(){
        //TODO Search PasswordLock

    }

    public void connect(){
        //TODO Connect PasswordLock

    }

    public boolean isSearchSuccess(){
        return this.isSearchSuccess;
    }

    public boolean isConnectSuccess(){
        return this.isConnectSuccess;
    }
}
