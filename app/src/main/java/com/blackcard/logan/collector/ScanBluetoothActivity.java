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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanBluetoothActivity extends Activity {

    private Map<String, BLEDevice> map = new HashMap<>();
    private BaseQuickAdapter<BLEDevice, BaseViewHolder> adapter;
    private BluetoothAdapter mAdapter;

    private BluetoothAdapter.LeScanCallback le = new BluetoothAdapter.LeScanCallback() {
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            runOnUiThread(() -> {
                BLEDevice bleDevice = new BLEDevice(device.getName(), device.getAddress(), rssi);
                if (!map.containsKey(bleDevice.getMac())) {
                    Log.i("Load", "获得蓝牙 地址：" + bleDevice.getMac());
                    map.put(bleDevice.getMac(), bleDevice);
                    adapter.setNewData(new ArrayList<>(map.values()));
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_bluetooth);

        RecyclerView recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new BaseQuickAdapter<BLEDevice, BaseViewHolder>(android.R.layout.simple_list_item_1) {
            @Override
            protected void convert(BaseViewHolder helper, BLEDevice item) {
                helper.setText(android.R.id.text1, (item.getName().isEmpty() ?
                        "未知设备" : item.getName()) + "\n" + item.getMac());
            }
        };
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter1, view, position) -> {
            Intent intent = new Intent();
            intent.putExtra("mac", adapter.getItem(position).getMac());
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
}
