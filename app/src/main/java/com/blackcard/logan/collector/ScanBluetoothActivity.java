package com.blackcard.logan.collector;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.logan.bluetoothlibrary.bean.BLEDevice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanBluetoothActivity extends Activity {

    private Map<String, BLEDevice> map = new HashMap<>();
    private BaseQuickAdapter<BLEDevice, BaseViewHolder> adapter;
    private BluetoothAdapter mAdapter;

    private BluetoothAdapter.LeScanCallback le = new BluetoothAdapter.LeScanCallback() {
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            if (BuildConfig.DEBUG) Log.d("LeScan", String.format("设备名 = %2s, mac = %2s\n广播 = %2s"
                    , device.getName(), device.getAddress(), Arrays.toString(scanRecord)));
            BLEDevice bleDevice = new BLEDevice(device.getName(), device.getAddress(), rssi, scanRecord);
            if (!map.containsKey(bleDevice.getMac()) /*&& (bleDevice.getDevType() == BLEDevice.DeviceType.XIONGKA || bleDevice.getDevType() == BLEDevice.DeviceType.CAIJIKA)*/){
                Log.i("LeScan", "蓝牙地址：" + bleDevice.getMac());
                map.put(bleDevice.getMac(), bleDevice);
                adapter.getData().add(bleDevice);
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_bluetooth);

        RecyclerView recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new BaseQuickAdapter<BLEDevice, BaseViewHolder>(android.R.layout.simple_list_item_1,null) {
            @Override
            protected void convert(BaseViewHolder helper, BLEDevice item) {
                helper.setText(android.R.id.text1, (item.getName().isEmpty() ?
                        "未知设备" : item.getName()) + "\t\t\t\t" + getDevtype(item.getDevType())
                        +  (item.getDevType() == BLEDevice.DeviceType.JIZHAN ? ("\n" + getJIZHANElectric(item)) : "")
                        + "\n" + item.getMac() + "\t\t\t\t信号强度：" + item.getRri() )
                        .getConvertView().setPadding(30,20,30,20);
            }
        };
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter1, view, position) -> {
            Intent intent = new Intent();
            intent.putExtra("mac", adapter.getItem(position).getMac());
            intent.putExtra("name", adapter.getItem(position).getName());
            setResult(RESULT_OK, intent);
            finish();
        });

        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (manager == null) {
            ToastUtils.showShort("设备不支持蓝牙");
            finish();
            return;
        }
        mAdapter = manager.getAdapter();
        if (mAdapter == null) {
            ToastUtils.showShort("设备不支持蓝牙");
            finish();
            return;
        }

        PermissionUtils.permission(PermissionConstants.LOCATION)
                .callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        mAdapter.startLeScan(le);
                    }

                    @Override
                    public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
                        ToastUtils.showShort("用户拒绝权限");
                    }
                }).request();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) mAdapter.stopLeScan(le);
    }

    private String getDevtype(BLEDevice.DeviceType type){
        if (type == BLEDevice.DeviceType.CAIJIKA) return "采集卡";
        else if (type == BLEDevice.DeviceType.XIONGKA) return "无感胸卡";
        else if (type == BLEDevice.DeviceType.JIZHAN) return "定位标签基站";
        else if (type == BLEDevice.DeviceType.JIZHAN_GATEWAY) return "网关识别基站";
        else return "未知设备";
    }

    private String getJIZHANElectric(BLEDevice device){
        if (device.getDevType() == BLEDevice.DeviceType.JIZHAN) return "电量：" + device.getJIZHANElectricRate();
        else return "";
    }
}
