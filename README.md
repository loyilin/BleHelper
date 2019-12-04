# Collector
# 蓝牙采集器工具使用文档
[![](https://jitpack.io/v/loyilin/collector.svg)](https://jitpack.io/#loyilin/collector)

## 引入到项目中
1 在根目录build.gradle文件中repositories方法中添加:
````Java
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
````

2 添加依赖关系
````Java
dependencies {
	implementation 'com.github.loyilin:collector:version'
}
````

## 如何避免重复依赖
避免多个依赖重复，如果项目中没使用可以忽略
```Java
android {
    ...
    defaultConfig {
        ...
    }
}

configurations {
    compile.exclude module: 'okhttp'
    compile.exclude module: 'okio'
    compile.exclude module: 'greendao'
    compile.exclude module: 'gson'
}

dependencies {
        ...
}
````

## 初始化
在Application中初始化，别忘了在AndroidManifest中注册
```Java
public class APP extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BleHelper.getInstance().init(this);
    }
}
````
## 支持自定义配置
```Java
BleConfig config = new BleConfig()
   	.setRepeatConnect(true)//断开自动重连 默认true
        .setCyclePower(true)//持续获取电量  默认true
        .setIntervals(60000)//获取电量间隔时间 单位毫秒，值不能小于10000，默认10000
        .setShowLog(true);//开启log输出  默认false
BleHelper.getInstance().init(this, config);
````

## 扫描设备
蓝牙扫描，请开启蓝牙，6.0系统请允许程序定位服务，打开GPS开关，否则扫描不到设备。例：
```Java
private BluetoothAdapter.LeScanCallback le = new BluetoothAdapter.LeScanCallback() {
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BLEDevice bleDevice = new BLEDevice(device.getName(), device.getAddress(), rssi);
                    Log.i("Load","获得蓝牙 地址："+bleDevice.getMac());
                    if(!map.containsKey(bleDevice.getMac())) {
                        map.put(bleDevice.getMac(),bleDevice);
                        data.add(bleDevice);
                        adapter.setData(data);
                    }
                }
            });
        }
};
BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if(manager==null){
            Toast.makeText(this,"不支持蓝牙",Toast.LENGTH_SHORT).show();
            finish();
        }
        mAdapter = manager.getAdapter();
        if(mAdapter==null){
            Toast.makeText(this,"不支持蓝牙",Toast.LENGTH_SHORT).show();
            finish();
        }
mAdapter.startLeScan(le);
````

## 连接或断开设备
```Java
//连接设备
BleHelper.getInstance().connect("08:7c:BE:00:02:25");

//断开设备
BleHelper.getInstance().disconnect();
````

## 获取当前连接的设备
```Java
//获取当前设备
BLEDevice device = BleHelper.getInstance().getDevice();
//设备名
String name = device.getName();
//设备地址mac
String mac = device.getMac();
````

## 获取连接状态
```Java
//当前连接状态
boolean state = BleHelper.getInstance().ismConnectionState();
````


## 连接状态、打卡上传成功提醒、设备固件版本、电量回调（可在多个Activity中同时接收）
```Java
BleHelper.getInstance().addCallBack(new BTCallBack(this){
            @Override
            public void OnConnected(boolean isconnected) {
                //连接状态，true连接，false断开
            }
            
            @Override
            public void OnUploadedSuccessfully(int total) {
                ToastUtils.showShort("成功上传了"+total+"条数据");
            }

            @Override
            public void OnVersion(String version) {
                //设备版本
            }

            @Override
            public void OnElectric(int electric, String lastcharge, boolean ischarge) {
                //electric百分比电量，lastcharge最后一次充电时间，ischarge是否正在充电
            }
        });
````


## OTA固件升级带进度
```Java
//开始OTA升级
BleHelper.getInstance().startOtaUpdate(filepath);
BleHelper.getInstance().addCallBack(new BTCallBack(this){
            @Override
            public void OnOTAProgress(int percent, int byteRate, int elapsedTime) {
                //OTA升级进度
            }

            @Override
            public void OnOTAComplete() {
                //OTA升级成功
            }

            @Override
            public void OnOTAFail(String error) {
                //OTA升级失败
            }
        });
````

## 构建环境
需要minSdkVersion大于或等于19，否则将无法使用，注意Android6.0及以上手机系统需要开启定位权限和定位功能，否则扫描不到设备。



