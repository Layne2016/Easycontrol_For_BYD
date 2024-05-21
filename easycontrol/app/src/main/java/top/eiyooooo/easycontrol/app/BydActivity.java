package top.eiyooooo.easycontrol.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;

import top.eiyooooo.easycontrol.app.client.BydClient;
import top.eiyooooo.easycontrol.app.client.view.ClientView;
import top.eiyooooo.easycontrol.app.databinding.ActivityBydBinding;
import top.eiyooooo.easycontrol.app.entity.AppData;
import top.eiyooooo.easycontrol.app.entity.Device;
import top.eiyooooo.easycontrol.app.helper.PublicTools;
import top.eiyooooo.easycontrol.app.helper.UsbChangeListener;

public class BydActivity extends Activity implements UsbChangeListener {

    // 创建界面
    private ActivityBydBinding mainActivity;
    private Device device;
    private UsbDevice usbDevice;


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppData.init(this);
        PublicTools.setStatusAndNavBar(this);
        PublicTools.setLocale(this);
        mainActivity = ActivityBydBinding.inflate(this.getLayoutInflater());
        setContentView(mainActivity.getRoot());
        initViews();
        startApp();
    }

    private void initViews() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int height = (int) (screenHeight * 0.9);
        int width = height * 1080 / 1920;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        mainActivity.mainClient.setLayoutParams(layoutParams);
        mainActivity.moreClient.setLayoutParams(layoutParams);
    }

    private void startApp() {
        // 设置设备列表适配器、广播接收器
        AppData.myBroadcastReceiver.setUsbChangeListener(this);
        // 设置按钮监听
        setButtonListener();
    }

    @Override
    protected void onDestroy() {
        AppData.myBroadcastReceiver.setDeviceListAdapter(null);
        AppData.myBroadcastReceiver.setUsbChangeListener(null);
        super.onDestroy();
    }


    // 设置按钮监听
    private void setButtonListener() {
        mainActivity.buttonSet.setOnClickListener(v -> startActivity(new Intent(this, SetActivity.class)));
        mainActivity.add.setOnClickListener(v -> {
            //
            device.specified_app = "com.baidu.BaiduMap";
            Device newDevice = new Device(device.uuid, device.type);
            Device.copyDevice(this.device,newDevice);
            newDevice.specified_app="com.baidu.BaiduMap";
            new BydClient(newDevice, usbDevice, 1, this, 1);
        });
    }

    @Override
    public void onConnect(Device device, UsbDevice usbDevice) {
        this.device = device;
        this.usbDevice = usbDevice;
        new BydClient(device, usbDevice, 0, this, 0);
    }

    @Override
    public void onDisConnect() {
        this.device = null;
        this.usbDevice = null;
    }

    public void onClientView(ClientView clientView, int flag) {
        clientView.setBydActivity(this);
        if (flag == 0) {
            mainActivity.mainClient.addView(clientView.textureView, 0);
            mainActivity.mainClient.post(() -> {
                clientView.updateMaxSize(new Pair<>(mainActivity.mainClient.getMeasuredWidth(), mainActivity.mainClient.getMeasuredHeight()));
            });
        } else {
            mainActivity.moreClient.addView(clientView.textureView, 0);
            mainActivity.moreClient.post(() -> {
                clientView.updateMaxSize(new Pair<>(mainActivity.moreClient.getMeasuredWidth(), mainActivity.moreClient.getMeasuredHeight()));
            });
        }

    }

    public void hide(ClientView clientView) {
        try {
            mainActivity.mainClient.removeView(clientView.textureView);
            clientView.setBydActivity(null);
            clientView = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}