package com.ezparking.com.blelearn;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ezparking.com.blelearn.services.BleConnectService;

public class ScanResutDetialActivity extends AppCompatActivity {

    private BleScanBean mScanResult;
    private BleConnectService mBleConnectService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_resut_detial);
        Bundle bundle = getIntent().getBundleExtra("bundle");
        mScanResult = bundle.getParcelable("scanResult");

        initService();//初始化连接的service

    }

    @Override
    protected void onResume() {
        super.onResume();
       IntentFilter filter = initIntenFilter();
        registerReceiver(mGattUpdateReceiver,filter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mGattUpdateReceiver ==null)return;
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mServiceConnetion == null)return;
        unbindService(mServiceConnetion);
        mServiceConnetion = null;
    }

    private IntentFilter initIntenFilter() {

      final   IntentFilter filter = new IntentFilter();
        filter.addAction(BleConnectService.ACTION_GATT_CONNECTING);
        filter.addAction(BleConnectService.ACTION_GATT_CONNECTED);
        filter.addAction(BleConnectService.ACTION_GATT_DISCONNECTED);
        filter.addAction(BleConnectService.ACTION_GATT_SERVICES_DISCOVERED);
        filter.addAction(BleConnectService.ACTION_DATA_AVAILABLE);
        return  filter;

    }

    private void initService() {
        Intent bleService = new Intent(this,BleConnectService.class);
        bindService(bleService,mServiceConnetion,Context.BIND_AUTO_CREATE);
    }
   private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
       @Override
       public void onReceive(Context context, Intent intent) {

           String action = intent.getAction();
           Log.e("zyh","gattUpdateReceiver-->  "+action);

       }
   };


     private ServiceConnection mServiceConnetion = new ServiceConnection() {
         @Override
         public void onServiceConnected(ComponentName name, IBinder service) {
            mBleConnectService = ((BleConnectService.BleBinder)service).getService();

         }

         @Override
         public void onServiceDisconnected(ComponentName name) {

         }
     };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.mean_scandetial,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.menu_bt1:
                analyze();
                break;
            case R.id.menu_bt_connect:

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void analyze() {

        if(mScanResult == null)return;
        byte[] scanRecord = mScanResult.getScanRecord();

        for (int i = 0; i < scanRecord.length; i++) {
             byte bt = scanRecord[i];
             Logutils.e(getBaseContext(),"scanRecord[ "+i+" ]- hex-> "+byte2HexStr(bt)+" = "+ (bt & 0xff));
        }

    }
    private String byte2HexStr(byte bt){

        String s = Integer.toHexString(bt & 0xFF);
        if (s.length() == 1){
            return "0" + s;
        }else{
            return s;
        }
    }


}
