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
}
