package com.blackcard.logan.collector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.logan.bluetoothlibrary.BTCallBack;
import com.logan.bluetoothlibrary.BleHelper;

public class MainActivity extends Activity {

    private Button bt;
    private TextView tv4;
    private TextView tv5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final TextView tv1 = findViewById(R.id.tv1);
        final TextView tv2 = findViewById(R.id.tv2);
        final TextView tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);
        tv5 = findViewById(R.id.tv5);
        bt = findViewById(R.id.bt1);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bt.getText().toString().equals("扫描设备")) {
                    startActivityForResult(new Intent(MainActivity.this, ScanBluetoothActivity.class), 200);
                } else {
                    BleHelper.getInstance().disconnect();
                    bt.setText("扫描设备");
                }
            }
        });

        BleHelper.getInstance().addCallBack(new BTCallBack(this) {
            @Override
            public void OnConnected(boolean isconnected) {
                tv1.setText("设备名称：" + BleHelper.getInstance().getCurrentDeviceNotNull().getName());
                if (!isconnected) {
                    tv2.setText("固件版本：");
                    tv3.setText("剩余电量：");
                }
                tv5.setText("连接状态：" + (isconnected ? "已连接" : "等待连接"));
                if (!BleHelper.getInstance().isconnect()) {
                    tv4.setText("MAC地址：");
                    tv5.setText("连接状态：");
                }
            }

            @Override
            public void OnVersion(String version) {
                tv2.setText("固件版本：" + version);
            }

            @Override
            public void OnElectric(int electric, String lastcharge, boolean ischarge) {
                tv3.setText("剩余电量：" + (ischarge ? "正在充电中" : electric + "%\t\t\t\t上次充电时间：" + lastcharge));
            }

            @Override
            public void OnOTAProgress(int percent, int byteRate, int elapsedTime) {
            }

            @Override
            public void OnOTAComplete() {
            }

            @Override
            public void OnOTAFail(String error) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK) {
            String mac = data.getStringExtra("mac");
            tv4.setText("MAC地址：" + mac);
            tv5.setText("连接状态：等待连接");
            BleHelper.getInstance().connect(mac);
            bt.setText("断开连接");
        }
    }
}
