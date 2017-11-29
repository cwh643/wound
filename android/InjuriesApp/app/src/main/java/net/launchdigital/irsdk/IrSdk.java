//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package net.launchdigital.irsdk;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;

public final class IrSdk {
    private static final int IR_PLUG_VID = 1155;
    private static final int IR_PLUG_PID = 32896;
    private static final String ACTION_USB_PERMISSION = "net.launchdigital.USB_PERMISSION";
    private static final String TAG = "IrSdk";
    private final UsbManager mUsbManager;
    private final WeakReference<Context> mWeakContext;
    private UsbDevice mUsbDevice = null;
    private volatile boolean mUsbDeviceConnected = false;
    private volatile boolean mIrSdkReady = false;
    private volatile boolean mDestroyed = true;
    private PendingIntent mPermissionIntent = null;
    private Handler mTheHandler = null;
    private final Runnable mUsbMonitorThread = new Runnable() {
        public void run() {
            if(!IrSdk.this.mUsbDeviceConnected) {
                IrSdk.this.checkUsbConnection();
            } else if(!IrSdk.this.mIrSdkReady && null != IrSdk.this.mUsbDevice) {
                synchronized(this) {
                    IrSdk.this.processConnect(IrSdk.this.mUsbDevice);
                }
            }

            IrSdk.this.mTheHandler.postDelayed(this, 1000L);
        }
    };
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if("net.launchdigital.USB_PERMISSION".equals(action)) {
                synchronized(this) {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra("device");
                    if(intent.getBooleanExtra("permission", false) && device != null) {
                        IrSdk.this.mUsbDevice = device;
                        IrSdk.this.processConnect(IrSdk.this.mUsbDevice);
                    }
                }
            } else if(!"android.hardware.usb.action.USB_DEVICE_ATTACHED".equals(action) && "android.hardware.usb.action.USB_DEVICE_DETACHED".equals(action)) {
                ;
            }

        }
    };
    private UsbDeviceConnection mUsbDeviceConnection = null;
    private static boolean isLoaded;

    public IrSdk(Context context) {
        this.mWeakContext = new WeakReference(context);
        this.mUsbManager = (UsbManager)context.getSystemService("usb");
        this.mUsbDeviceConnected = false;
        this.mDestroyed = false;
        this.mIrSdkReady = false;
        this.mTheHandler = new Handler();
    }

    public synchronized void register() throws IllegalStateException {
        if(this.mDestroyed) {
            throw new IllegalStateException("already destroyed");
        } else {
            if(null == this.mPermissionIntent) {
                Context context = (Context)this.mWeakContext.get();
                if(context != null) {
                    this.mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent("net.launchdigital.USB_PERMISSION"), 0);
                    IntentFilter filter = new IntentFilter("net.launchdigital.USB_PERMISSION");
                    filter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
                    filter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
                    context.registerReceiver(this.mUsbReceiver, filter);
                }

                this.mTheHandler.postDelayed(this.mUsbMonitorThread, 1000L);
            }

        }
    }

    public synchronized void unregister() throws IllegalStateException {
        if(this.mIrSdkReady) {
            if(0 != this.IrSdkDeinit()) {
                Log.v("IrSdk", "IrSdkDeinit error!");
            }

            this.mIrSdkReady = false;
            this.mUsbDevice = null;
        }

        this.mUsbDeviceConnected = false;
        if(!this.mDestroyed) {
            this.mTheHandler.removeCallbacks(this.mUsbMonitorThread);
        }

        if(this.mPermissionIntent != null) {
            Context context = (Context)this.mWeakContext.get();

            try {
                if(context != null) {
                    context.unregisterReceiver(this.mUsbReceiver);
                }
            } catch (Exception var3) {
                var3.printStackTrace();
            }

            this.mPermissionIntent = null;
        }

    }

    public void destroy() {
        this.unregister();
        if(!this.mDestroyed) {
            this.mDestroyed = true;
            this.mUsbDeviceConnection.close();
            this.mUsbDeviceConnection = null;

            try {
                this.mTheHandler.getLooper().quit();
            } catch (Exception var2) {
                Log.e("IrSdk", "destroy: ", var2);
            }
        }

    }

    public int getDeviceInfo(IrSdk.DeviceInfo devInfo) {
        return this.IrSdkGetDeviceInfo(devInfo);
    }

    public int getDeviceState() {
        return this.IrSdkGetDeviceState();
    }

    public int setDeviceCalibrationPeriod(int seconds) {
        return this.IrSdkSetDeviceCalibrationPeriod(seconds);
    }

    public int readImage(int[] imgBuffer, int buffSize) {
        boolean ret = false;
        if(!this.mIrSdkReady) {
            Log.v("IrSdk", "irsdk not inited, readFrameRgb failed!");
            return -1;
        } else {
            int ret1 = this.IrSdkReadFrameRgb(imgBuffer, buffSize);
            return ret1;
        }
    }

    public int captureImage(int[] imgBuffer, int imgBuffSize, double[] tempBuffer, int tempBuffSize) {
        boolean ret = false;
        if(!this.mIrSdkReady) {
            Log.e("IrSdk", "irsdk not inited, readFrameRgb failed!");
            return -1;
        } else {
            int ret1 = this.IrSdkCaptureFrame(imgBuffer, imgBuffSize, tempBuffer, tempBuffSize);
            return ret1;
        }
    }

    public int enterTempAccuracyCalibrationMode() {
        return this.IrSdkEnterTempAccuracyCalibrationMode();
    }

    public int abortTempAccuracyCalibrationMode() {
        return this.IrSdkAbortTempAccuracyCalibrationMode();
    }

    public int enterTempUniformityCalibrationMode() {
        return this.IrSdkEnterTempUniformityCalibrationMode();
    }

    public int abortTempUniformityCalibrationMode() {
        return this.IrSdkAbortTempUniformityCalibrationMode();
    }

    public double setCalibrationTemp(double temp, int srcX, int srcY, int dstX, int dstY) {
        return this.IrSdkSetCalibrationTemp(temp, srcX, srcY, dstX, dstY);
    }

    public int setTempRange(double range) {
        return this.IrSdkSetTempRange(range);
    }

    public int startMovePtz(int direction) {
        return this.IrSdkStartMovePtz(direction);
    }

    public int stopMovePtz() {
        return this.IrSdkStopMovePtz();
    }

    public int setLedFlashingPeriod(int onSec, int offSec) {
        return this.IrSdkSetLedFlashingPeriod(onSec, offSec);
    }

    public int startTacCapture() {
        return this.IrSdkStartTacCapture();
    }

    public boolean isIrSdkReady() {
        return !this.mDestroyed && this.mIrSdkReady;
    }

    public int setBaseTempRange(double val) {
        return this.IrSdkSetBaseTempRange(val);
    }

    public int enableFilter(boolean enable) {
        return enable?this.IrSdkFilterEnable(1):this.IrSdkFilterEnable(0);
    }

    public void enableDebug(boolean enable) {
        if(enable) {
            this.IrSdkEnableDebug(1);
        } else {
            this.IrSdkEnableDebug(0);
        }

    }

    public double getTemp(int x, int y) {
        return this.IrSdkGetCurrentTemp(x, y);
    }

    public void setLcdPower(boolean poweron) {
        IrSdk.LcdPowerManager mgr = new IrSdk.LcdPowerManager();
        if(poweron) {
            mgr.setPowerOn();
        } else {
            mgr.setPowerOff();
        }

    }

    private int checkUsbConnection() {
        UsbDevice irDevice = null;
        HashMap deviceList = this.mUsbManager.getDeviceList();
        Iterator deviceIterator = deviceList.values().iterator();

        while(deviceIterator.hasNext()) {
            UsbDevice itemDev = (UsbDevice)deviceIterator.next();
            if(itemDev.getVendorId() == 1155 && itemDev.getProductId() == '肀') {
                irDevice = itemDev;
                break;
            }
        }

        if(null != irDevice) {
            this.mUsbManager.requestPermission(irDevice, this.mPermissionIntent);
            this.mUsbDeviceConnected = true;
        }

        return 0;
    }

    private void processConnect(UsbDevice device) {
        if(null != this.mUsbDeviceConnection) {
            this.mUsbDeviceConnection.close();
            this.mUsbDeviceConnection = null;
        }

        this.mUsbDeviceConnection = this.mUsbManager.openDevice(device);
        String mUsbDeviceName = device.getDeviceName();
        int mUsbFileDescriptor = this.mUsbDeviceConnection.getFileDescriptor();
        int usbBusnum = 0;
        int usbDevnum = 0;
        String[] v = !TextUtils.isEmpty(mUsbDeviceName)?mUsbDeviceName.split("/"):null;
        if(v != null) {
            usbBusnum = Integer.parseInt(v[v.length - 2]);
            usbDevnum = Integer.parseInt(v[v.length - 1]);
        }

        Context context = (Context)this.mWeakContext.get();
        if(null != context) {
            boolean ret = false;
            int ret1 = this.IrSdkSetWorkDir(context.getFilesDir().getPath());
            if(0 != ret1) {
                return;
            }

            ret1 = this.IrSdkInit(mUsbDeviceName, mUsbFileDescriptor, usbBusnum, usbDevnum);
            this.mIrSdkReady = 0 == ret1;
        }

    }

    private native int IrSdkDeinit();

    private native int IrSdkInit(String var1, int var2, int var3, int var4);

    private native int IrSdkReadFrameRgb(int[] var1, int var2);

    private native int IrSdkCaptureFrame(int[] var1, int var2, double[] var3, int var4);

    private native int IrSdkSetWorkDir(String var1);

    private native int IrSdkGetDeviceInfo(IrSdk.DeviceInfo var1);

    private native int IrSdkGetDeviceState();

    private native int IrSdkSetDeviceCalibrationPeriod(int var1);

    private native int IrSdkEnterTempAccuracyCalibrationMode();

    private native int IrSdkAbortTempAccuracyCalibrationMode();

    private native int IrSdkEnterTempUniformityCalibrationMode();

    private native int IrSdkAbortTempUniformityCalibrationMode();

    private native double IrSdkSetCalibrationTemp(double var1, int var3, int var4, int var5, int var6);

    private native int IrSdkSetTempRange(double var1);

    private native int IrSdkStartMovePtz(int var1);

    private native int IrSdkStopMovePtz();

    private native int IrSdkSetLedFlashingPeriod(int var1, int var2);

    private native int IrSdkStartTacCapture();

    private native int IrSdkSetBaseTempRange(double var1);

    private native int IrSdkFilterEnable(int var1);

    private native int IrSdkEnableDebug(int var1);

    private native double IrSdkGetCurrentTemp(int var1, int var2);

    static {
        if(!isLoaded) {
            System.loadLibrary("usb1.0ir");
            System.loadLibrary("IrSdk");
            isLoaded = true;
        }

    }

    class LcdPowerManager {
        private final String PATH_DATA = "/sys/class/pinctrl_vortex_class/pinctrl_vortex_device/data";
        private final String PATH_PINNAME = "/sys/class/pinctrl_vortex_class/pinctrl_vortex_device/pin_name";
        private final String HIGH = "1";
        private final String LOW = "0";
        private final String BL_LCD = "PD23";

        LcdPowerManager() {
        }

        private void setData(String pinname, String val) {
            this.write_pinname(pinname);
            this.write_data(val);
        }

        private void write_pinname(String val) {
            BufferedWriter writer = null;

            try {
                writer = new BufferedWriter(new FileWriter("/sys/class/pinctrl_vortex_class/pinctrl_vortex_device/pin_name"));
                writer.write(val + "\n");
                writer.close();
            } catch (IOException var4) {
                var4.printStackTrace();
            }

        }

        private void write_data(String val) {
            BufferedWriter writer = null;

            try {
                writer = new BufferedWriter(new FileWriter("/sys/class/pinctrl_vortex_class/pinctrl_vortex_device/data"));
                writer.write(val + "\n");
                writer.close();
            } catch (IOException var4) {
                var4.printStackTrace();
            }

        }

        public void setPowerOn() {
            this.write_data("1");
        }

        public void setPowerOff() {
            this.write_data("0");
        }
    }

    public static class DeviceInfo {
        public String firmwareVersion;
        public int sensorVersion;
        public String tacDatetime;
        public String tucDatetime;

        public DeviceInfo() {
        }
    }
}
