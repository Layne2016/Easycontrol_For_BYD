package top.eiyooooo.easycontrol.app.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import top.eiyooooo.easycontrol.app.entity.LayoutInfo;

public class CustomControl extends FrameLayout {

    private View dragView; // 用于拖动位置的控件
    private ImageView resizeView; // 用于调整大小的控件

    private TextView infoView;

    private int lastX;
    private int lastY;

    public CustomControl(Context context) {
        super(context);
        init();
    }

    public CustomControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 初始化两个子控件和相关的事件监听器
        dragView = new ImageView(getContext());
        FrameLayout.LayoutParams params1 = new LayoutParams(50, 50);
        params1.gravity = Gravity.CENTER_HORIZONTAL; // 中间位置
        dragView.setLayoutParams(params1);
        dragView.setBackgroundColor(0xFFFF0000); // 红色，用于表示可拖动区域

        infoView = new TextView(getContext());

        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params2.gravity = Gravity.CENTER; // 位置在右下角
        infoView.setLayoutParams(params2);
        addView(infoView);


        resizeView = new ImageView(getContext());
        FrameLayout.LayoutParams params = new LayoutParams(50, 50); // 设置大小为50x50
        params.gravity = Gravity.RIGHT | Gravity.BOTTOM; // 位置在右下角
        resizeView.setLayoutParams(params);
        resizeView.setBackgroundColor(0xFF0000FF); // 蓝色，用于表示可调整大小的区域
        addView(dragView);
        addView(resizeView);
        setupDrag(dragView);
        setupResize(resizeView);
        setBackgroundColor(Color.CYAN);
    }

    public LayoutInfo layoutInfo() {
        return new LayoutInfo(
                getLeft(),
                getTop(),
                getRight(),
                getBottom(),
                zIndex,
                app);
    }

    private void setupDrag(View dragView) {
        dragView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int x = (int) event.getRawX();
                final int y = (int) event.getRawY();
                if (!(getParent() instanceof View)) return true; // 确保父级是 View
                View parentView = (View) getParent();

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = x;
                        lastY = y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int deltaX = x - lastX;
                        int deltaY = y - lastY;
                        int left = getLeft() + deltaX;
                        int top = getTop() + deltaY;
                        int right = getRight() + deltaX;
                        int bottom = getBottom() + deltaY;

                        // 确保不超出父容器边界
                        left = Math.max(left, 0);
                        top = Math.max(top, 0);
                        if (right > parentView.getWidth()) {
                            right = parentView.getWidth();
                            left = right - getWidth();
                        }
                        if (bottom > parentView.getHeight()) {
                            bottom = parentView.getHeight();
                            top = bottom - getHeight();
                        }

                        layout(left, top, right, bottom);

                        lastX = x;
                        lastY = y;
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return true;
            }
        });
    }

    private int zIndex = -1;
    private String app = "";

    private int l;
    private int t;
    private int r;
    private int b;


    public void setPara(int left, int top, int right, int bottom, int z, String app) {
        this.l = left;
        this.t = top;
        this.r = right;
        this.b = bottom;
        zIndex = z;
        this.app = app;
        infoView.setText(app);
    }

    public void layoutUpdate() {
        LayoutParams params = new LayoutParams(r - l, b - t);
        params.leftMargin = l;
        params.topMargin = t;
        setLayoutParams(params);
    }


    public int getZIndex() {
        return zIndex;
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
        this.infoView.setText(app);
    }

    private void setupResize(ImageView resizeView) {
        resizeView.setOnTouchListener((v, event) -> {
            final int x = (int) event.getRawX();
            final int y = (int) event.getRawY();
            if (!(getParent() instanceof View)) return true; // 确保父级是 View
            View parentView = (View) getParent();

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_MOVE:
                    int width = x - getLeft();
                    int height = y - getTop();

                    // 限制大小，确保不超出父容器
                    width = Math.min(width, parentView.getWidth() - getLeft());
                    height = Math.min(height, parentView.getHeight() - getTop());

                    setLayoutParams(new LayoutParams(Math.max(width, 100), Math.max(height, 100)));
                    break;
            }
            return true;
        });
    }

}