package cepri.device.utils;

import android.os.Build;
import android.util.Log;

import java.io.File;

public class IRDA {
    static {
        //本程序的so名字格式为“libcepri_机型”
        //加载顺序，先加载系统内SO
        //系统中没有，再加载本程序中的SO
        try {
            String libcepri_model = "";
            if (new File("/system/lib/libcepri.so").exists()) {
                System.loadLibrary("cepri");
            } else if (new File("/system/lib/libcepri_dev.so").exists()) {
                System.loadLibrary("cepri_dev");
            } else {
                libcepri_model = String.format("cepri_%s", Build.MODEL);
                System.loadLibrary(libcepri_model);
            }
            Log.e("SO名字", libcepri_model);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * @return 0  执行成功。-1	执行失败。
     */
    public native static int Init();

    /**
     * @return 0  执行成功。-1	执行失败。
     */
    public native static int DeInit();

    /**
     * @return 0  执行成功。-1	执行失败。
     */
    public native static int ClearSendCache();

    /**
     * @return 0  执行成功。-1	执行失败。
     */
    public native static int ClearRecvCache();

    /**
     * @return 0  执行成功。-1	执行失败。
     */
    public native static int Config(int baudrate, int databits, int parity, int stopbits, int blockmode);

    /**
     * @return 0    成功。1	端口未打开。2	发送出错。
     */
    public native static int SendData(byte[] buf, int offset, int count);

    /**
     * @return 返回参数为实际读取数据个数，数据类型为int
     */
    public native static int RecvData(byte[] buf, int offset, int count);

    /**
     * @return 0  执行成功。-1	执行失败。
     */
    public native static int SetTimeOut(int direction, int timeout);

}
