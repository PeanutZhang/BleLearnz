package com.ezparking.com.blelearn.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

public class BleConnectService extends Service {





    //连接中，连接成功，断开连接，接收到消息
    public final static String ACTION_GATT_CONNECTED = BleConnectService.class.getName() + ".ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_CONNECTING = BleConnectService.class.getName() + ".ACTION_GATT_CONNECTING";
    public final static String ACTION_GATT_DISCONNECTED = BleConnectService.class.getName() + ".ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = BleConnectService.class.getName() + ".ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = BleConnectService.class.getName() + ".ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA_RAW = BleConnectService.class.getName() + ".EXTRA_DATA_RAW";
    public final static String EXTRA_UUID_CHAR = BleConnectService.class.getName() + ".EXTRA_UUID_CHAR";
    private final static String TAG = "zyh bleconnectservice";

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private BluetoothGatt mBluetoothGatt;
    private String mDeviceAddress;
    private State mConnectionState = State.DISCONNECTED;


    public BleConnectService() {

    }
    private BleBinder bleBinder =  new BleBinder();

    public class BleBinder extends Binder{

        public BleConnectService getService(){
            return BleConnectService.this;
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
       return  bleBinder;

    }

    public boolean initalize(){
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        }
       if(mBluetoothManager == null)return false;
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if(mBluetoothAdapter== null) return false;
        return true;
    }



  public boolean connectDevice( final String address){
            boolean connectSucess = false;
         if(mBluetoothAdapter == null || TextUtils.isEmpty(address))return false;
        if (!TextUtils.isEmpty(mDeviceAddress) && mBluetoothGatt !=null && mDeviceAddress.equals(address)){
            Log.e(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if(mBluetoothGatt.connect()){
                connectSucess = true;
                Log.d(TAG, "Connection CONECTTING attempt OK.");
                setConnectState(State.CONECTTING,true);

            }else {
                 connectSucess = false;
                Log.d(TAG, "Connection CONECTTING attempt failed.");
                setConnectState(State.DISCONNECTED,true);
            }
        }else {
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            if(device ==null){
                connectSucess = false;
            }else {
                Log.e(TAG, "Trying to create a new connection.");
                mBluetoothGatt = device.connectGatt(this,false,gattCallback);
                mDeviceAddress = address;
                setConnectState(State.CONECTTING,true);
            }


        }

        return connectSucess;
   }
  private void updateBroadcast(String action){
      Intent intent = new Intent(action);
      sendBroadcast(intent);
  }
   private void updateBroadcast(String action,BluetoothGattCharacteristic characteristic){
         Intent intent = new Intent(action);
       String characteristicUuid = characteristic.getUuid().toString();
       intent.putExtra(EXTRA_UUID_CHAR,characteristicUuid);
       byte[] value = characteristic.getValue();
       intent.putExtra(EXTRA_DATA_RAW,value);
       sendBroadcast(intent);
   }

   private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
       @Override
       public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
           super.onConnectionStateChange(gatt, status, newState);
           Log.e("zyh","onConnectionChange--> status = "+status+" newState= "+newState);
             if(newState == BluetoothProfile.STATE_CONNECTED){
                 setConnectState(State.CONNECTED,true);
             }else if(newState == BluetoothProfile.STATE_DISCONNECTED) {
                 setConnectState(State.DISCONNECTED,true);
             }
       }


       @Override
       public void onServicesDiscovered(BluetoothGatt gatt, int status) {
           super.onServicesDiscovered(gatt, status);
           Log.e("zyh","onServicesDiscovered is going");

           if(status ==  BluetoothGatt.GATT_SUCCESS){
               updateBroadcast(ACTION_GATT_SERVICES_DISCOVERED);
           }else {
               Log.e("zyh","onServicesDiscoverd status-> "+status);
           }
       }

       @Override
       public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
           super.onCharacteristicChanged(gatt, characteristic);
           Log.e("zyh","onCharacteristicChanged is going");
           updateBroadcast(ACTION_DATA_AVAILABLE, characteristic);

       }

       @Override
       public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
           super.onCharacteristicRead(gatt, characteristic, status);
           Log.e("zyh","onCharacteristicRead is going");
           if(status == BluetoothGatt.GATT_SUCCESS){
               updateBroadcast(ACTION_DATA_AVAILABLE,characteristic);//读到信息
           }

       }

       @Override
       public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
           super.onCharacteristicWrite(gatt, characteristic, status);
       }
   };

    private void setConnectState(State connected, boolean sendBroad) {
        mConnectionState = connected;
         String broadcast = "";
         switch (connected){

             case CONNECTED:
                 broadcast = ACTION_GATT_CONNECTED;
                 break;
             case CONECTTING:
                 broadcast = ACTION_GATT_CONNECTING;
                 break;
             case DISCONNECTED:
                 broadcast = ACTION_GATT_DISCONNECTED;
                 break;
         }

          if(sendBroad){
             Intent it = new Intent(broadcast);
             sendBroadcast(it);
          }

    }


    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }
    public void disconnect(){
        if(mBluetoothAdapter !=null || mBluetoothGatt !=null){
            mBluetoothGatt.disconnect();
            mBluetoothGatt =null;
            Log.e("zyh"," mBluetoothGatt.disconnect is going");
        }
    }

    enum State{
      CONECTTING,
       CONNECTED,
       DISCONNECTED
   }

}
