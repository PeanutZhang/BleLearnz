package com.ezparking.com.blelearn;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by zyh
 */

public class BlueToothUtils {

    private static BluetoothAdapter mBluetoothAdapter;
    private static Context mContext;
    public final static int REQUEST_ENABLE_BT = 2001;

    public void init(Context mContext) {
        this.mContext = mContext.getApplicationContext();
        BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter  = bluetoothManager.getAdapter();
    }

    public static boolean isBlueThoothOn(){
        if(mBluetoothAdapter == null){
            return false;
        }else {
            return mBluetoothAdapter.isEnabled();
        }
    }

    public static boolean isSupporeBLE(){
        return  mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public void askUserToEnableBluetoothIfNeeded() {
        if (!isBlueThoothOn() && isSupporeBLE()) {
            final Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity)mContext).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    /**
     * 计算到ibeacon的距离
     * @param txPower
     * @param rssi
     * @return
     */
    protected static double calculateAccuracy(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }
        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return accuracy;
        }
    }
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
