# Collector
# 蓝牙采集器工具


## 如何使用
1. [点我下载依赖包](http://gitcafe.com)

2. 使用okhttp等出现重复依赖
```Java
implementation ('com.squareup.okhttp3:okhttp:3.10.0')  {
        exclude group: 'com.squareup.okhttp3'
    }
```
    
3.多个依赖重复时
```Java
configurations {
    compile.exclude module: 'okhttp'
    compile.exclude module: 'okio'
    compile.exclude module: 'greendao'
    compile.exclude module: 'gson'
}
```

4.构建环境
需要minSdkVersion大于或等于19，否则将无法使用，注意Android6.0及以上手机系统需要开启定位权限和定位功能，否则扫描不到设备。

# 蓝牙采集器工具使用文档

