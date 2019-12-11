package com.blackcard.logan.collector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blackcard.logan.collector.bean.OtaBean;
import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.logan.bluetoothlibrary.BleHelper;
import com.logan.bluetoothlibrary.bean.BLEDevice;
import com.logan.bluetoothlibrary.bean.BTBean;
import com.logan.bluetoothlibrary.itf.BTCallBack;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity {

    private Button bt;
    private Button bt2;
    private TextView tv1;
    private TextView tv4;
    private TextView tv5;
    private RecyclerView recyclerview;

    private BTCallBack callback;
    private BaseQuickAdapter<BTBean, BaseViewHolder> adapter;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tv1 = (TextView) findViewById(R.id.tv1);
        final TextView tv2 = (TextView) findViewById(R.id.tv2);
        final TextView tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);
        tv5 = (TextView) findViewById(R.id.tv5);
        bt = (Button) findViewById(R.id.bt1);
        bt2 = (Button) findViewById(R.id.bt2);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        adapter = new BaseQuickAdapter<BTBean, BaseViewHolder>(android.R.layout.simple_list_item_1) {
            @Override
            protected void convert(BaseViewHolder helper, BTBean item) {
                helper.setText(android.R.id.text1, item.getTab_id() + "\t\t\t\t\t" + item.getMac()
                        + "\n" + item.getCheck_in())
                        .getConvertView().setPadding(30,20,30,20);
            }
        };
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerview.setAdapter(adapter);

        bt.setOnClickListener(v -> {
            if (bt.getText().toString().equals("扫描设备")) {
                startActivityForResult(new Intent(MainActivity.this, ScanBluetoothActivity.class), 200);
            } else {
                BleHelper.getInstance().disconnect(MainActivity.this);
                bt.setText("扫描设备");
                tv1.setText("设备名称：");
                tv2.setText("固件版本：");
                tv3.setText("剩余电量：");
                tv4.setText("MAC地址：");
                tv5.setText("连接状态：");
                adapter.setNewData(null);
                clearDev();
            }
        });
        bt2.setOnClickListener(v -> PermissionUtils.permission(PermissionConstants.STORAGE)
                .callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        bt.setEnabled(false);
                        bt2.setEnabled(false);
                        OTAupload();
                    }

                    @Override
                    public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
                        ToastUtils.showShort("用户拒绝权限");
                    }
                }).request());

        callback = new BTCallBack() {
            @Override
            public void onConnected(boolean isconnected) {
                tv1.setText("设备名称：" + getDevName());
                tv4.setText("MAC地址：" + getMac());
                tv5.setText("连接状态：" + (getMac().isEmpty() ? "" : (isconnected ? "已连接" : "等待连接")));
                if (!isconnected) {
                    tv2.setText("固件版本：");
                    tv3.setText("剩余电量：");
                    bt2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onVersion(String version) {
                tv2.setText("固件版本：" + version);
                bt2.setVisibility(BleHelper.getInstance().getDevice().getDevType() == BLEDevice.DeviceType.CAIJIKA ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onPunch(BTBean card) {
                adapter.addData(card);
                ToastUtils.showShort("在线打卡：" + card.getTab_id());
            }

            @Override
            public void onOffData(int total) {
                ToastUtils.showShort("查询到离线数据 " + total + "条");
                dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("查询到离线数据共 " + total + " 条")
                        .setMessage("是否要读取？")
                        .setPositiveButton("读取", (dialog, which) -> BleHelper.getInstance().startOffLineData())
                        .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                        .show();
            }

            @Override
            public void onOffProgress(int current, int total) {
                ToastUtils.showShort("离线数据进度：当前第" + current + "条，共" + total + "条");
            }

            @Override
            public void onOffComplete(List<BTBean> data) {
                adapter.setNewData(data);
            }

            @Override
            public void onElectric(int electric, long lastcharge, boolean ischarge) {
                tv3.setText("剩余电量：" + electric + "%\t\t\t\t" + (ischarge ? "正在充电中..." : "上次充电时间：" + (lastcharge == 0 ? "" : TimeUtils.millis2String(lastcharge))));
            }

            @Override
            public void onOTAProgress(int percent, int byteRate, int elapsedTime) {
                bt2.setText("升级到最新固件" + percent + "%");
            }

            @Override
            public void onOTASuccess() {
                ToastUtils.showShort("OTA升级成功");
            }

            @Override
            public void onOTAFail(String error) {
                ToastUtils.showShort(error);
            }

            @Override
            public void onOTAComplete() {
                bt2.setText("升级到最新固件");
                bt.setEnabled(true);
                bt2.setEnabled(true);
            }
        };
        BleHelper.getInstance().addCallBack(callback);
        if (!getMac().isEmpty()) {
            tv1.setText("设备名称：" + getDevName());
            tv4.setText("MAC地址：" + getMac());
            tv5.setText("连接状态：等待连接");
            bt.setText("断开连接");
            BleHelper.getInstance().connect(this, getMac());
        }
    }

    private void OTAupload() {
        final OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(500, TimeUnit.MILLISECONDS)
                .writeTimeout(500, TimeUnit.MILLISECONDS)
                .connectTimeout(500, TimeUnit.MILLISECONDS)
                .build();
        Request request = new Request.Builder()
                .url("http://szydak.eicp.net:82/ezx_syset/apk/checkDeviceVersion")
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtils.showShort("OTA下载失败");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                OtaBean bean = new Gson().fromJson(response.body().string(), OtaBean.class);
                if (bean == null || !bean.getResult().equals("Sucess")) return;
                DownloadUtil util = new DownloadUtil();
                util.download("http://www.allsps.com/ezx_syset/download?filename="
                                + bean.getData1().getFilename() + "&filepath=" + bean.getData1().getPath()
                        , Environment.getExternalStorageDirectory().getAbsolutePath()
                        , bean.getData1().getFilename(), new DownloadUtil.DownloadProgress() {
                            @Override
                            public void onCompleted(File file) {
                                BleHelper.getInstance().startOtaUpdate(file);
                            }

                            @Override
                            public void onProgressChanged(long read, long contentLength, int percentage) {
                                Log.d(getClass().getName(), "read=" + read + ",contentLength=" + contentLength + ",percentage=" + percentage);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                super.onFailure(e);
                                bt2.setText("升级到最新固件");
                                bt.setEnabled(true);
                                bt2.setEnabled(true);
                            }
                        });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK) {
            String mac = data.getStringExtra("mac");
            String name = data.getStringExtra("name");
            saveDev(name, mac);
            tv1.setText("设备名称：" + name);
            tv4.setText("MAC地址：" + mac);
            tv5.setText("连接状态：等待连接");
            BleHelper.getInstance().connect(this, mac);
            bt.setText("断开连接");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) dialog.dismiss();
        BleHelper.getInstance().disconnect(this);
        //退出注销监听
        BleHelper.getInstance().remoCallBack(callback);
    }

    private String getDevName() {
        return getSharedPreferences()
                .getString("name", "");
    }

    private String getMac() {
        return getSharedPreferences()
                .getString("mac", "");
    }

    private void saveDev(String name, String mac) {
        getSharedPreferences()
                .edit()
                .putString("name", name)
                .putString("mac", mac)
                .apply();
    }

    private void clearDev() {
        getSharedPreferences()
                .edit()
                .clear()
                .apply();
    }

    private SharedPreferences getSharedPreferences() {
        return getSharedPreferences("collector.xml", MODE_PRIVATE);
    }
}
