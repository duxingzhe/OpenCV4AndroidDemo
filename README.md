# OpenCV4AndroidDemo

Prepare for OpenCV course on CSDN.

Based on OpenCV 4.1.0 and 3.4.6.

Study and put mathematics and algorithm into practice.

#### How to compile

Android NDK r15c

```
mkdir <your opencv path>/build
cd build
```

armeabi-v7a

```
cmake -DCMAKE_INSTALL_PREFIX=<install-prefix>/android/armeabi-v7a -DCMAKE_TOOLCHAIN_FILE=../platforms/android/android.toolchain.cmake -DANDROID_NDK=<ndk path> -DCMAKE_BUILD_TYPE=Release -DANDROID_ABI=armeabi-v7a -DANDROID_NATIVE_API_LEVEL=21  ..
```

arm64-v8a

```
cmake -DCMAKE_INSTALL_PREFIX=<install-prefix>/android/arm64-v8a -DCMAKE_TOOLCHAIN_FILE=../platforms/android/android.toolchain.cmake -DANDROID_NDK=<ndk path> -DCMAKE_BUILD_TYPE=Release -DANDROID_ABI=arm64-v8a -DANDROID_NATIVE_API_LEVEL=21  ..
```

x86

```
cmake -DCMAKE_INSTALL_PREFIX=<install-prefix>/android/x86 -DCMAKE_TOOLCHAIN_FILE=../platforms/android/android.toolchain.cmake -DANDROID_NDK=<ndk path> -DCMAKE_BUILD_TYPE=Release -DANDROID_ABI=x86 -DANDROID_NATIVE_API_LEVEL=21  ..
```

x86_64

```
cmake -DCMAKE_INSTALL_PREFIX=<install-prefix>/android/x86_64 -DCMAKE_TOOLCHAIN_FILE=../platforms/android/android.toolchain.cmake -DANDROID_NDK=<ndk path> -DCMAKE_BUILD_TYPE=Release -DANDROID_ABI=x86_64 -DANDROID_NATIVE_API_LEVEL=21  ..
```

#### 包含程序

Color blob：斑点检测

app：人脸识别

answersheetscan：答题卡识别

androidimageanalysis：图像分析（使用Tensorflow on Android）

#### OpenCV 4.1.0文档翻译地址

[https://github.com/duxingzhe/OpenCV-doc-Translation](https://github.com/duxingzhe/OpenCV-doc-Translation)

如果本项目对你的学习和开发有所帮助，欢迎上一份茶水钱

<img src="./qr/alipay_qr.jpg" width="300"> <img src="./qr/weixin_qr.png" width="300">
