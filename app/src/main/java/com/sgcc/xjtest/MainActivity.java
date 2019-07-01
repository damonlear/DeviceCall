package com.sgcc.xjtest;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.TextView;

import cepri.device.utils.IRDA;
import cepri.device.utils.LibInfo;
import cepri.device.utils.Scanner;

public class MainActivity extends Activity {

    private TextView tv_tip;
    private PowerManager pm;
    private PowerManager.WakeLock wl;
    private Runnable irda_runable = new Runnable() {
        public void run() {
            clearResult();
            isEnable(false);
            irda(new IListener() {
                @Override
                public void listener(String result) {
                    appendResult(result);
                }
            });
            isEnable(true);
        }
    };
    private Runnable scanner_runable = new Runnable() {
        public void run() {
            clearResult();
            isEnable(false);
            scan(new IListener() {
                @Override
                public void listener(String result) {
                    appendResult(result);
                }
            });
            isEnable(true);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wake_unlock();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wake_lock();
        tv_tip = findViewById(R.id.tv_tip);
        if (init()) {
            findViewById(R.id.btn_test_ir).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(irda_runable).start();
                }
            });

            findViewById(R.id.btn_test_scan).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(scanner_runable).start();
                }
            });
        } else {
            isEnable(false);
        }
    }

    private Boolean init() {
        try {
            appendResult("SO加载成功，版本:" + LibInfo.getVersion());
            return true;
        } catch (Throwable e) {
            appendResult("SO加载异常:" + e.getMessage());
            return false;
        }
    }

    private void irda(IListener listener) {
        //设备初始化
        int result = IRDA.Init();
        if (listener != null) {
            listener.listener(result >= 0 ? "设备初始化成功" : "设备初始化失败");
        }

        //设备通信参数配置
        result = IRDA.Config(1200, 8, 2, 1, 0);
        if (listener != null) {
            listener.listener(result >= 0 ? "设备参数配置成功" : "设备参数配置失败");
        }

        //设备发送数据
        byte[] sendDatas = new byte[]{(byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE,
                (byte) 0x68, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x68,
                (byte) 0x11, (byte) 0x04, (byte) 0x33, (byte) 0x33, (byte) 0x34, (byte) 0x33,
                (byte) 0xAE, (byte) 0x16};
        result = IRDA.SendData(sendDatas, 0, sendDatas.length);
        if (listener != null) {
            listener.listener(result == 0 ? "设备发送数据成功:" + sendDatas.length : "设备发送数据失败");
        }

        //设备接收数据-默认过滤非645、698、376.1数据
        byte[] buffer = new byte[1024 * 4];
        result = IRDA.RecvData(buffer, 0, buffer.length);
        if (listener != null) {
            listener.listener(result > 0 ? "设备接收数据成功" : "设备接收数据无数据");
        }

        //设备注销
        result = IRDA.DeInit();
        if (listener != null) {
            listener.listener(result >= 0 ? "设备注销成功" : "设备注销失败");
        }
    }

    private void scan(IListener listener) {
        //设备初始化
        int result = Scanner.Init();
        if (listener != null) {
            listener.listener(result >= 0 ? "设备初始化成功" : "设备初始化失败");
        }
        if (result < 0) {
            return;
        }

        //扫码
        byte[] buffer = new byte[1024 * 4];
        result = Scanner.Decode(3000, buffer, 0, buffer.length);
        if (listener != null) {
            listener.listener(result >= 0 ? "设备接收数据成功:" + new String(buffer) : "设备接收数据无数据");
        }

        //设备注销
        result = Scanner.DeInit();
        if (listener != null) {
            listener.listener(result >= 0 ? "设备注销成功" : "设备注销失败");
        }
    }

    private void isEnable(final boolean enabled) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.btn_test_ir).setEnabled(enabled);
                findViewById(R.id.btn_test_scan).setEnabled(enabled);
            }
        });
    }

    private void clearResult() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_tip.setText("");
            }
        });
    }

    private void appendResult(final String tip) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_tip.append(tip + "\r\n");
            }
        });
    }

    private void wake_lock() {
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getPackageName());
        wl.acquire(500);
    }

    private void wake_unlock() {
        if (wl != null) {
            wl.release();
        }
    }
}
