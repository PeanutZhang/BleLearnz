package com.ezparking.com.blelearn;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Start1Activity extends AppCompatActivity {

    protected RecyclerView recylerView;
    private boolean isScaning;
    private BluetoothAdapter mBlueAdapter;
    private List<BleScanBean> mdevices;
    private HashMap<String,String> mDevicesMap;
    private Context mContext;
    private DevicesAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_start1);
        mContext = this;
        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBlueAdapter = manager.getAdapter();
        initView();




    }


    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Logutils.e(mContext,"---devices- >" +device.getAddress());
            if(!mDevicesMap.containsKey(device.getAddress())){
                mDevicesMap.put(device.getAddress(),"jz");
                BleScanBean scanBean = new BleScanBean();
                scanBean.setDevice(device);
                scanBean.setRssi(rssi);
                scanBean.setScanRecord(scanRecord);
                mdevices.add(scanBean);
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.mean_scan, menu);

        if (!isScaning) {
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_stopscan).setVisible(false);
            menu.findItem(R.id.menu_refreshing).setActionView(null);
        } else {
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_stopscan).setVisible(true);
            menu.findItem(R.id.menu_refreshing).setActionView(R.layout.layout_refreshing);

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_scan:
                if(isScaning)break;
                    mBlueAdapter.startLeScan(mLeScanCallback);
                    isScaning = true;
                break;
            case R.id.menu_stopscan:
                   if(!isScaning)break;
                   mBlueAdapter.stopLeScan(mLeScanCallback);
                   isScaning = false;
                break;

        }
        invalidateOptionsMenu();
        return true;
    }

    private void initView() {
        recylerView = (RecyclerView) findViewById(R.id.recylerView);
        mdevices = new ArrayList<>();
        mDevicesMap = new HashMap<>();
        mAdapter = new DevicesAdapter(mdevices);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recylerView.setLayoutManager(layoutManager);
        recylerView.setAdapter(mAdapter);
    }


}
