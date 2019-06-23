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

![](https://github.com/duxingzhe/OpenCV4AndroidDemo/blob/master/qr/alipay_qr.jpg)
![](https://github.com/duxingzhe/OpenCV4AndroidDemo/blob/master/qr/weixin_qr.png)