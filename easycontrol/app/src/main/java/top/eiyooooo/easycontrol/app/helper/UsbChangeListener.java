package top.eiyooooo.easycontrol.app.helper;

import android.hardware.usb.UsbDevice;

import top.eiyooooo.easycontrol.app.entity.Device;

public interface UsbChangeListener {
    void onConnect(Device device, UsbDevice usbDevice);
    void onDisConnect();
}
