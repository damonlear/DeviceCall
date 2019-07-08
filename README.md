# 本案例用于一般学习安卓java代码如何让写本地native方法，如何调用c工程师编辑好的硬件so接口。
# cepri_device_utils 硬件调用案例

## 协议说明

    so文件接口函数格式为 Java_cepri_device_utils_xxx_xxxxx,其中xxx 为类名，xxxxx 为函数名。
    示例：Java_cepri_device_utils_SecurityUnit_Init

## 返回值说明-注意返回值

	扫描头
	返回值
	返回参数数据类型为int，各返回值代码的意义如下：
	0	执行成功。
	-1	执行失败。

## 运行环境

	1.必须非debugable模式下运行
	    buildTypes {
        release {
            debuggable false
            ...
        }
        debug {
            debuggable false
            ...
        }
    }

	2.程序必须有android.permission.WRITE_EXTERNAL_STORAGE权限
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

	3.建议通过CPU锁，保持设备运行。
	private void wake_lock(){
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getPackageName());
        wl.acquire(500);
    }

    private void wake_unlock(){
        wl.release();
    }

	4.建议每次使用设备前，重启设备。每次使用完设备后，关闭设备。
