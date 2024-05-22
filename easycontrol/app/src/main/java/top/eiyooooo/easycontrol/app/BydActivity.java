package top.eiyooooo.easycontrol.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

import top.eiyooooo.easycontrol.app.client.BydClient;
import top.eiyooooo.easycontrol.app.client.view.ClientView;
import top.eiyooooo.easycontrol.app.databinding.ActivityBydBinding;
import top.eiyooooo.easycontrol.app.entity.AppData;
import top.eiyooooo.easycontrol.app.entity.Device;
import top.eiyooooo.easycontrol.app.entity.LayoutInfo;
import top.eiyooooo.easycontrol.app.helper.PublicTools;
import top.eiyooooo.easycontrol.app.helper.UsbChangeListener;
import top.eiyooooo.easycontrol.app.widget.CustomControl;
import top.eiyooooo.easycontrol.app.widget.OnPackageClickListener;

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

    private final List<CustomControl> views = new ArrayList<>();

    private void initViews() {
        SharedPreferences sp;
        sp = getSharedPreferences("layout", MODE_PRIVATE);
        String jsonStr = sp.getString("items", "");
        if (TextUtils.isEmpty(jsonStr)) {
            return;
        }
        try {
            JSONArray list = new JSONArray(jsonStr);
            int len = list.length();
            for (int i = 0; i < len; i++) {
                JSONObject jsonObject = list.getJSONObject(i);
                int x = jsonObject.getInt("left");
                int y = jsonObject.getInt("top");
                int w = jsonObject.getInt("right");
                int h = jsonObject.getInt("bottom");
                int z = jsonObject.getInt("zIndex");
                String app = jsonObject.getString("app");
                CustomControl customControl = new CustomControl(this);
                customControl.setPara(x, y, w, h, z, app);
                views.add(customControl);
            }
        } catch (JSONException e) {
            Log.e("init: ", "解析失败：" + e.getMessage());
        }
        views.sort(Comparator.comparingInt(CustomControl::getZIndex));
        mainActivity.container.removeAllViews();
        views.forEach(customControl -> {
            customControl.hideControl();
            customControl.setOnPackageClickListener(packageName -> {
                if (!TextUtils.isEmpty(packageName) && device != null && usbDevice != null) {
                    device.specified_app=packageName;
                    new BydClient(device, usbDevice, 1, this, packageName);
                    PublicTools.logToast(packageName);
                } else {
                    PublicTools.logToast("未连接");
                }
            });
            mainActivity.container.addView(customControl);
            customControl.layoutUpdate();
        });
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
    //com.baidu.BaiduMap
    //com.luna.music
    private void setButtonListener() {
        mainActivity.buttonSet.setOnClickListener(v -> startActivity(new Intent(this, SetActivity.class)));
        mainActivity.buttonLayout.setOnClickListener(view -> startActivity(new Intent(this, LayoutConfigActivity.class)));
    }

    @Override
    public void onConnect(Device device, UsbDevice usbDevice) {
        this.device = device;
        this.usbDevice = usbDevice;
        //new BydClient(device, usbDevice, 0, this, 0);
    }

    @Override
    public void onDisConnect() {
        this.device = null;
        this.usbDevice = null;
    }

    public void onClientView(ClientView clientView, String flag) {
        Log.e( "onClientView: ", flag);
        clientView.setBydActivity(this);
        if (!flag.isEmpty()) {
            int count = mainActivity.container.getChildCount();
            for (int i = 0; i < count; i++) {
                CustomControl cc = (CustomControl) mainActivity.container.getChildAt(i);
                if (cc.packageEq(flag)) {
                    cc.addView(clientView.textureView,0);
                    clientView.updateMaxSize(new Pair<>(cc.getMeasuredWidth(), cc.getMeasuredHeight()));
                    return;
                }
            }
        }
    }

    public void hide(ClientView clientView) {
        try {
            //mainActivity.mainClient.removeView(clientView.textureView);
            clientView.setBydActivity(null);
            clientView = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}