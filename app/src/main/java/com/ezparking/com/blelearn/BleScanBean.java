package com.ezparking.com.blelearn;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zyh
 */

public class BleScanBean implements Parcelable {

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public static Creator<BleScanBean> getCREATOR() {
        return CREATOR;
    }

    private int rssi;
   private BluetoothDevice device;
   private byte[] scanRecord;


    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public byte[] getScanRecord() {
        return scanRecord;
    }

    public void setScanRecord(byte[] scanRecord) {
        this.scanRecord = scanRecord;
    }

    public BleScanBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.rssi);
        dest.writeParcelable(this.device, flags);
        dest.writeByteArray(this.scanRecord);
    }

    protected BleScanBean(Parcel in) {
        this.rssi = in.readInt();
        this.device = in.readParcelable(BluetoothDevice.class.getClassLoader());
        this.scanRecord = in.createByteArray();
    }

    public static final Creator<BleScanBean> CREATOR = new Creator<BleScanBean>() {
        @Override
        public BleScanBean createFromParcel(Parcel source) {
            return new BleScanBean(source);
        }

        @Override
        public BleScanBean[] newArray(int size) {
            return new BleScanBean[size];
        }
    };
}
