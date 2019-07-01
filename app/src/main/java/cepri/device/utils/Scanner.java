package cepri.device.utils;

import android.os.Build;
import android.util.Log;

/**
 * 注意：
 * 1.必须非debugable模式下运行
 * 2.程序必须有android.permission.WRITE_EXTERNAL_STORAGE权限
 */
public class Scanner {
    static {
        try {
            //根据机型加载SO
            String libcepri_model = String.format("cepri_%s", Build.MODEL);
            Log.e("SO名字", libcepri_model);
            System.loadLibrary(libcepri_model);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 电源控制
     * @return 0 执行成功。-1 执行失败。
     */
    public static native int Init();

    /**
     * 电源控制
     * @return 0 执行成功。-1 执行失败。
     */
    public static native int DeInit();

    /**
     * 读取条码或者二维码
     * 返回数据在buf
     * @return 0成功 -1失败
     */
    public static native int Decode(int timeout, byte[] buf, int offset, int count);

}
