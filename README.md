# DLTool
[![](https://jitpack.io/v/546554574/DLTool.svg)](https://jitpack.io/#546554574/DLTool)
## Step1
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
## Step2
```
  implementation 'com.github.546554574:DLTool:1.0.2'
```
## Step3
```
将minSdkVersion版本设置为19
minSdkVersion 19
```
## Step4
```
自定义APP的onCreate中添加如下代码进行初始

        DLTool.init(this)
	  .setLongCangFont()//设置自定义字体
```
## 注意
```
如果报：Cannot fit requested classes in a single dex file (# methods: 71623 > 65536)异常
解决办法：

1、在app model中的build.gradledependencies里面添加：

    implementation 'androidx.multidex:multidex:2.0.0'


2、app model中的build.gradle文件的defaultConfig默认配置里面增加：
multiDexEnabled true

android {
    compileSdkVersion 29
    defaultConfig {
        applicationId "com.toune.dltool"
        minSdkVersion 19
        targetSdkVersion 29
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
        multiDexEnabled true
    }


3.最后在自定义了Application子类，需要在这个子类中重写一个方法

    // 主要是添加下面这句代码
    MultiDex.install(this);
    
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }
    
```

## 感谢以下开源项目：
+ [qmui-Android](https://github.com/Tencent/QMUI_Android)
+ [Toasty](https://github.com/GrenderG/Toasty)
+ [autosize今日头条适配方案](https://github.com/GrenderG/Toasty)
+ [agentweb](https://github.com/GrenderG/Toasty)
+ [xxpermissions权限](https://github.com/GrenderG/Toasty)
+ [rxbus](https://github.com/Blankj/RxBus)
+ [album](https://github.com/yanzhenjie/Album)
+ [多状态](https://github.com/qyxxjd/MultipleStatusView)
+ [adapter](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)
+ [okhttp](https://github.com/square/okhttp)
+ [GSON](https://github.com/google/gson)

## 版本更新
### 1.0.1
+ 自用的MVP框架
+ 引用的Toasty的吐司
+ 引入QMUI的QMUIAndroid框架
+ 引入的今日头条适配方案
+ 引入的agentweb框架
+ 权限框架xxpermissions
+ 照片选择框架album
+ 用过的识别速度最快的二维码扫码框架zbarlibary
+ 全局更换自定义字体，并提供了两种自定义字体
+ 文字转语音播报，转存语音文件TTS的简单封装
+ 验证码输入框（方框的和下横线的两种样式，支持常规样式修改）
