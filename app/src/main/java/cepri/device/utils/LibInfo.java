package cepri.device.utils;

import android.os.Build;
import android.util.Log;

public class LibInfo {
    static {
        try {
            String libcepri_model = String.format("cepri_%s", Build.MODEL);
            Log.e("SO名字", libcepri_model);
            System.loadLibrary(libcepri_model);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static native int getVersion();

    public static native int getCompany();
}
