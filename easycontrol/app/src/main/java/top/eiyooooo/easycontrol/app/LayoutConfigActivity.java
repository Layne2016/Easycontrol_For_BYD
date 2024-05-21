package top.eiyooooo.easycontrol.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import top.eiyooooo.easycontrol.app.databinding.ActivityLayoutConfigBinding;
import top.eiyooooo.easycontrol.app.databinding.DialogLayoutConfigBinding;
import top.eiyooooo.easycontrol.app.widget.CustomControl;

public class LayoutConfigActivity extends Activity {
    private ActivityLayoutConfigBinding layoutConfigBinding;
    private SharedPreferences sp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("layout", MODE_PRIVATE);
        String items = sp.getString("items", "");
        layoutConfigBinding = ActivityLayoutConfigBinding.inflate(getLayoutInflater());
        setContentView(layoutConfigBinding.getRoot());
        init(items);
        layoutConfigBinding.buttonAdd.setOnClickListener(view -> {
            CustomControl cc = new CustomControl(this);
            if (views.isEmpty()) {
                cc.setZIndex(0);
            } else {
                cc.setZIndex(views.get(views.size() - 1).getZIndex() + 1);
            }
            views.add(cc);
            layoutConfigBinding.containerApp.addView(cc);
        });
        layoutConfigBinding.buttonSave.setOnClickListener(view -> {
            JSONArray jsonArray = new JSONArray();
            int childCount = layoutConfigBinding.containerApp.getChildCount();
            for (int i = 0; i < childCount; i++) {
                CustomControl cc = (CustomControl) layoutConfigBinding.containerApp.getChildAt(i);
                JSONObject obj = cc.layoutInfo().toJson();
                if (obj != null) {
                    jsonArray.put(obj);
                }
            }
            sp.edit().putString("items", jsonArray.toString()).apply();
        });
    }

    private final List<CustomControl> views = new ArrayList<>();

    private void init(String jsonStr) {
        Log.e("init: ", jsonStr);
        views.clear();
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
        layoutConfigBinding.containerApp.removeAllViews();
        views.forEach(customControl -> {
            customControl.setOnClickListener(view -> {
                showOpDialog(customControl);
            });
            layoutConfigBinding.containerApp.addView(customControl);
            customControl.layoutUpdate();
        });
    }

    private AlertDialog dialog;

    private void showOpDialog(CustomControl customControl) {
        DialogLayoutConfigBinding dialogBinding = DialogLayoutConfigBinding.inflate(getLayoutInflater());
        dialogBinding.btnConfirm.setOnClickListener(view -> {
            customControl.setApp(dialogBinding.et.getText().toString().trim());
            if (dialog != null) {
                dialog.cancel();
            }
        });
        dialogBinding.btnDelete.setOnClickListener(view -> {
            layoutConfigBinding.containerApp.removeView(customControl);
            if (dialog != null) {
                dialog.cancel();
            }
        });
        dialog = new AlertDialog.Builder(this)
                .setView(dialogBinding.getRoot())
                .create();
        dialog.show();
    }

}
