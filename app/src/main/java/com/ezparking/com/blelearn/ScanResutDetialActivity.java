package com.ezparking.com.blelearn;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.ezparking.com.blelearn.ibeaconutil.ByteUtils;
import com.ezparking.com.blelearn.ibeaconutil.GattAttributeResolver;
import com.ezparking.com.blelearn.ibeaconutil.GattDataAdapterFactory;
import com.ezparking.com.blelearn.services.BleConnectService;

import java.util.List;

import static com.ezparking.com.blelearn.services.BleConnectService.*;

public class ScanResutDetialActivity extends AppCompatActivity {

    protected TextView deviceAddress;
    protected TextView connectionState;
    protected TextView uuid;
    protected TextView description;
    protected TextView dataAsString;
    protected TextView dataAsArray;
    protected ExpandableListView gattServicesList;
    private BleScanBean mScanResult;
    private BleConnectService mBleConnectService;
    private BluetoothDevice mBleDevice;
    private State mCurrentState = State.DISCONNECTED;
    private String mExportString;
    private com.ezparking.com.blelearn.eneity.Exporter mExporter;
    private BluetoothGattCharacteristic mNotifyCharacteristic;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_scan_resut_detial);
        Bundle bundle = getIntent().getBundleExtra("bundle");
        mScanResult = bundle.getParcelable("scanResult");
        mBleDevice = mScanResult.getDevice();

        initService();//初始化连接的service
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = initIntenFilter();
        registerReceiver(mGattUpdateReceiver, filter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGattUpdateReceiver == null) return;
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServiceConnetion == null) return;
        unbindService(mServiceConnetion);
        mServiceConnetion = null;
    }

    private IntentFilter initIntenFilter() {

        final IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_GATT_CONNECTING);
        filter.addAction(ACTION_GATT_CONNECTED);
        filter.addAction(ACTION_GATT_DISCONNECTED);
        filter.addAction(ACTION_GATT_SERVICES_DISCOVERED);
        filter.addAction(ACTION_DATA_AVAILABLE);
        return filter;

    }

    private void initService() {
        Intent bleService = new Intent(this, BleConnectService.class);
        bindService(bleService, mServiceConnetion, Context.BIND_AUTO_CREATE);
    }

    /**
     * 广播接收者，监听连接状态，及数据
     */
    private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Log.e("zyh", "gattUpdateReceiver-->  " + action);
            if(BleConnectService.ACTION_GATT_CONNECTING.equals(action)){
                updateConnectionState(State.CONECTTING);
                invalidateOptionsMenu();

            }else if(BleConnectService.ACTION_GATT_CONNECTED.equals(action)){
              updateConnectionState(State.CONNECTED);
                invalidateOptionsMenu();


            }else if (BleConnectService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)){
               displayGattServices(  mBleConnectService.getSupportedGattServices());

            } else if (BleConnectService.ACTION_GATT_DISCONNECTED.equals(action)) {
              updateConnectionState(State.DISCONNECTED);
                invalidateOptionsMenu();

            }else if(BleConnectService.ACTION_DATA_AVAILABLE.equals(action)){
                final String noData = "无数据";
                final String suuid = intent.getStringExtra(BleConnectService.EXTRA_UUID_CHAR);
                final byte[] dataArr = intent.getByteArrayExtra(BleConnectService.EXTRA_DATA_RAW);

                uuid.setText(tryString(suuid, noData));
               description.setText(GattAttributeResolver.getAttributeName(suuid, getString(R.string.unknown)));
                dataAsArray.setText(ByteUtils.byteArrayToHexString(dataArr));
                dataAsString.setText(new String(dataArr));
            }


        }
    };

    private static String tryString(final String string, final String fallback) {
        if (string == null) {
            return fallback;
        } else {
            return string;
        }
    }

    private void updateConnectionState(final State state) {
        mCurrentState = state;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final int resId;

                switch (state) {
                    case CONNECTED:
                        resId = R.string.connected;
                        break;
                    case DISCONNECTED:
                        resId = R.string.disconnected;
                        break;
                    case CONECTTING:
                        resId = R.string.connecting;
                        break;
                    default:
                        resId = 0;
                        break;
                }

                connectionState.setText(resId);
            }
        });
    }
    private ServiceConnection mServiceConnetion = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBleConnectService = ((BleConnectService.BleBinder) service).getService();
           if(!mBleConnectService.initalize())return;//初始化失败
            mBleConnectService.connectDevice(mBleDevice.getAddress());

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.mean_scandetial, menu);

        if(mCurrentState == State.CONECTTING){
            menu.findItem(R.id.menu_bt_connect).setVisible(false);
            menu.findItem(R.id.menu_bt_disconnect).setVisible(false);
            menu.findItem(R.id.menu_refreshing).setActionView(R.layout.layout_refreshing);
        }else if(mCurrentState == State.CONNECTED){
            menu.findItem(R.id.menu_bt_connect).setVisible(false);
            menu.findItem(R.id.menu_bt_disconnect).setVisible(true);
            menu.findItem(R.id.menu_refreshing).setActionView(null);
        }else if(mCurrentState == State.DISCONNECTED){
            menu.findItem(R.id.menu_bt_connect).setVisible(true);
            menu.findItem(R.id.menu_bt_disconnect).setVisible(false);
            menu.findItem(R.id.menu_refreshing).setActionView(null);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_bt1:
                analyze();
                break;
            case R.id.menu_bt_connect:
                mBleConnectService.connectDevice(mBleDevice.getAddress());
                break;
            case R.id.menu_bt_disconnect:
                mBleConnectService.disconnect();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void analyze() {

        if (mScanResult == null) return;
        byte[] scanRecord = mScanResult.getScanRecord();

        for (int i = 0; i < scanRecord.length; i++) {
            byte bt = scanRecord[i];
            Logutils.e(getBaseContext(), "scanRecord[ " + i + " ]- hex-> " + byte2HexStr(bt) + " = " + (bt & 0xff));
        }

    }

    private String byte2HexStr(byte bt) {

        String s = Integer.toHexString(bt & 0xFF);
        if (s.length() == 1) {
            return "0" + s;
        } else {
            return s;
        }
    }


    private void initView() {
        deviceAddress = (TextView) findViewById(R.id.device_address);
        connectionState = (TextView) findViewById(R.id.connection_state);
        uuid = (TextView) findViewById(R.id.uuid);
        description = (TextView) findViewById(R.id.description);
        dataAsString = (TextView) findViewById(R.id.data_as_string);
        dataAsArray = (TextView) findViewById(R.id.data_as_array);
        gattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);

        gattServicesList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                final GattDataAdapterFactory.GattDataAdapter adapter =
                        (GattDataAdapterFactory.GattDataAdapter) parent.getExpandableListAdapter();

                final BluetoothGattCharacteristic characteristic =
                        adapter.getBluetoothGattCharacteristic(groupPosition, childPosition);

                final int charaProp = characteristic.getProperties();
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                    // If there is an active notification on a characteristic, clear
                    // it first so it doesn't update the data field on the user interface.
                    if (mNotifyCharacteristic != null) {
                        mBleConnectService.setCharacteristicNotification(mNotifyCharacteristic, false);
                        mNotifyCharacteristic = null;
                    }
                    mBleConnectService.readCharacteristic(characteristic);
                }
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    mNotifyCharacteristic = characteristic;
                    mBleConnectService.setCharacteristicNotification(characteristic, true);
                }

                return true;
            }
        });

    }


    private void displayGattServices(final List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        mExportString = mExporter.generateExportString(
                mBleDevice.getName(),
                mBleDevice.getAddress(),
                gattServices);

        final GattDataAdapterFactory.GattDataAdapter adapter = GattDataAdapterFactory.createAdapter(this, gattServices);
        gattServicesList.setAdapter(adapter);
        invalidateOptionsMenu();
    }

    enum State{
        CONECTTING,
        CONNECTED,
        DISCONNECTED
    }
}
