package com.ezparking.com.blelearn;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zyh
 */

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DevciesViewHolder> {

    private List<BleScanBean> mdatas;

    public DevicesAdapter(List<BleScanBean> mdatas) {
        this.mdatas = mdatas;
    }

    @Override
    public DevciesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blue_devices, null);
        return new DevciesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DevciesViewHolder holder, int position) {
        BleScanBean scanBean = mdatas.get(position);
        if(scanBean == null) return;
        BluetoothDevice device = scanBean.getDevice();
        holder.tvName.setText(device.getName());
        holder.tvMac.setText(device.getAddress());
        holder.tvRssi.setText(scanBean.getRssi()+"");

        byte[] scanRecordata = scanBean.getScanRecord();//判断是否是iBeacon





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
}
