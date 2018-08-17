package com.ezparking.com.blelearn;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ezparking.com.blelearn.ibeaconutil.ByteUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zyh
 */

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DevciesViewHolder> {

    private List<BleScanBean> mdatas;
    private onItemClickListener onItemClickListener;
    public DevicesAdapter(List<BleScanBean> mdatas) {
        this.mdatas = mdatas;
    }

    @Override
    public DevciesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blue_devices, null);
        return new DevciesViewHolder(itemView);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        this.onItemClickListener = listener;
    }

    @Override
    public void onBindViewHolder(DevciesViewHolder holder, int position) {
        final BleScanBean scanBean = mdatas.get(position);
        if(scanBean == null) return;
        BluetoothDevice device = scanBean.getDevice();
        holder.tvName.setText(TextUtils.isEmpty(device.getName())?"Unknow Device":device.getName());
        holder.tvMac.setText(device.getAddress());
        holder.tvRssi.setText(scanBean.getRssi()+"");

        byte[] scanRecord = scanBean.getScanRecord();//判断是否是iBeacon
//        02 01 06 1A FF 4C 00 02 15  （iBeacon的前缀，固定不变的）
//        E2 C5 6D B5 DF FB 48 D2 B0 60 D0 F5 A7 10 96 E0 （UUID的值）
//        00 03 （major值） index 22 23
//        00 03 （minor值） index 24 25
        // txPower index 26

        iBeaconClass.iBeacon iBeacon = iBeaconClass.fromScanData(scanBean.getDevice(), scanBean.getRssi(), scanRecord);
         if(iBeacon !=null){

        holder.ibeaconMajor.setText(iBeacon.major+"");
              holder.ibeaconMinor.setText(iBeacon.minor+"");
              holder.ibeaconTxPower.setText(iBeacon.txPower+"");
              holder.ibeaconUuid.setText(iBeacon.proximityUuid);
              holder.ibeaconDistance.setText(iBeacon.distance);
         }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener !=null){
                    onItemClickListener.onItemClickListener(scanBean);
                }
            }
        });




    }

    @Override
    public int getItemCount() {
        return mdatas.size();
    }


    class DevciesViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvMac, tvUpdated, tvRssi;
        TextView ibeaconUuid, ibeaconMajor, ibeaconMinor, ibeaconTxPower, ibeaconDistance, ibeaconDistanceDescriptor;

        public DevciesViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.device_name);
            tvMac = itemView.findViewById(R.id.device_address);
            tvUpdated = itemView.findViewById(R.id.device_last_update);
            tvRssi = itemView.findViewById(R.id.device_rssi);

            ibeaconUuid = itemView.findViewById(R.id.ibeacon_uuid);
            ibeaconMajor = itemView.findViewById(R.id.ibeacon_major);
            ibeaconMinor = itemView.findViewById(R.id.ibeacon_minor);
            ibeaconTxPower = itemView.findViewById(R.id.ibeacon_tx_power);
            ibeaconDistance = itemView.findViewById(R.id.ibeacon_distance);
            ibeaconDistanceDescriptor = itemView.findViewById(R.id.ibeacon_distance_descriptor);

        }
    }
    public interface onItemClickListener{
        void onItemClickListener(BleScanBean scanResul);
    }
}
