# Collector 蓝牙采集器SDK使用文档
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
	implementation 'com.github.loyilin:collector:1.5.3'
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

## 构建环境
需要minSdkVersion大于或等于19，否则将无法使用，注意Android6.0及以上手机系统需要开启定位权限和定位功能，否则扫描不到设备。

## 初始化
在Application中初始化，别忘了在AndroidManifest中注册
```Java
public class APP extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BleHelper.getInstance().initialize();
    }
}
````
## 支持自定义配置
```Java
BleConfig config = new BleConfig()
   	.setRepeatConnect(true)//自动重连 默认true
        .setGetCycleElectricity(true)//持续发送电量请求  默认关闭
        .setIntervals(60000)//获取电量间隔时间 单位毫秒，值不能小于5000，默认5000
        .setDebug(true);//是否调试模式，开启Log显示 默认关闭
BleHelper.getInstance().initialize(config);
````

## 扫描设备
蓝牙扫描，请开启蓝牙，6.0系统请允许程序定位服务，打开GPS开关，否则扫描不到设备。例：
```Java
private BluetoothAdapter.LeScanCallback le = new BluetoothAdapter.LeScanCallback() {
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BLEDevice bleDevice = new BLEDevice(device.getName(), device.getAddress(), rssi, scanRecord);
                    Log.i("Load","获得蓝牙 地址："+bleDevice.getMac());
                    if(bleDevice.getDevType() == BLEDevice.DeviceType.CAIJIKA && !map.containsKey(bleDevice.getMac())) {
                        map.put(bleDevice.getMac(),bleDevice);
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
BleHelper.getInstance().connect(this, "08:7c:BE:00:02:25");

//断开设备
BleHelper.getInstance().disconnect(this);
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
boolean state = BleHelper.getInstance().isConnect();
````

## 连接状态、打卡、离线数据、设备固件版本、电量回调（可在多个Activity中同时接收）
```Java
BleHelper.getInstance().addCallBack(new BTCallBack(this){
            @Override
            public void OnConnected(boolean isconnected) {
                //连接状态，true连接，false断开
            }

            @Override
            public void OnVersion(String version) {
                //设备版本
            }

            @Override
            public void OnElectric(int electric, String lastcharge, boolean ischarge) {
                //electric百分比电量，lastcharge最后一次充电时间，ischarge是否正在充电
            }
	    
	     Override
            public void onPunch(BTBean card) {
                ToastUtils.showShort("在线打卡：" + card.getTab_id());
            }

            @Override
            public void onOffData(int total) {
                ToastUtils.showShort("查询到离线数据 " + total + "条");
            }

            @Override
            public void onOffProgress(int current, int total) {
                LogUtils.e("离线数据进度：当前第" + current + "条，共" + total + "条");
            }

            @Override
            public void onOffComplete(List<BTBean> data) {
                ToastUtils.showShort("离线数据读取完成 共" + data.size() + "条");
            }
        });
````


## OTA固件升级带进度
```Java
//开始OTA升级 传入ota升级文件
BleHelper.getInstance().startOtaUpdate(file);
BleHelper.getInstance().addCallBack(new BTCallBack(this){
             @Override
            public void onOTAProgress(int percent, int byteRate, int elapsedTime) {
                //ota升级进度
            }

            @Override
            public void onOTASuccess() {
                //ota升级成功
            }

            @Override
            public void onOTAFail(String error) {
                //ota升级失败
            }

            @Override
            public void onOTAComplete() {
                //ota升级完成，不管成功或失败最后都会执行
            }
        });
````

## 注意
如果不在使用监听器，请手动注销，节省资源
````
@Override
    protected void onDestroy() {
        super.onDestroy();
        callback.destroy();
    }
````

## 如何扫描附近基站设备
开启蓝牙扫描设备，使用指定构造方法 BLEDevice(String name, String mac, int rri, byte[] scanRecord) 来创建BLEDevice对象，getDevType()返回设备的类型
````
/**
 * 设备类型
 */
public enum DeviceType {
		UNKNOWN,//未知
		CAIJIKA,//采集卡
		XIONGKA,//无感胸卡
		JIZHAN,//定位标签基站
		JIZHAN_GATEWAY,//网关识别基站
	}
````
 属性名          | 类型           | 描述  
 --------       | :-----------:  | :-----------: 
 UNKNOWN        | 未知            | 未知设备
 CAIJIKA        | 采集卡          | 有感巡更卡，需要手动来打卡  
 XIONGKA        | 无感胸卡        | 无感巡更卡，只需要进入基站范围即可自动打卡
 JIZHAN         | 定位标签基站    | 跟胸卡配合使用
 JIZHAN_GATEWAY | 网关识别基站    | 


