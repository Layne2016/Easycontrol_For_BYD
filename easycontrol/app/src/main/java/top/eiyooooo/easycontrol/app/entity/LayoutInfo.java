package top.eiyooooo.easycontrol.app.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class LayoutInfo {
 public    int left;
    public  int top;
    public  int right;
    public  int bottom;
    public int zIndex;
    public  String app;

    public LayoutInfo(int left, int top, int right, int bottom, int zIndex, String app) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.zIndex = zIndex;
        this.app = app;
    }

    public JSONObject toJson()  {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("left", left);
            jsonObject.put("top", top);
            jsonObject.put("right", right);
            jsonObject.put("bottom", bottom);
            jsonObject.put("zIndex", zIndex);
            jsonObject.put("app", app);
            return jsonObject;
        } catch (JSONException e) {
            return null;
        }
    }
}
