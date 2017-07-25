package cn.louis.shark.jbox2ddemo;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by LouisShark on 2017/7/22.
 * this is on cn.louis.shark.jbox2ddemo.
 */

public class Collision extends FrameLayout {
    private Jbox2dImpl jboxImpl;

    public Collision(Context context) {
        this(context, null);
    }

    public Collision(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Collision(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWillNotDraw(false); //显式的关闭，不需要viewgroup不绘制
        initView();
    }

    private void initView() {
        jboxImpl = new Jbox2dImpl(getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        jboxImpl.setWorldSize(w, h);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        jboxImpl.createWorld();
        //子view创建tag设置body
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (!jboxImpl.isBodyView(view) || changed) {
                jboxImpl.createBody(view);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        jboxImpl.startWorld();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (jboxImpl.isBodyView(view)) {
                view.setX(jboxImpl.getViewX(view));
                view.setY(jboxImpl.getViewY(view));
                view.setRotation(jboxImpl.getViewRotation(view));
            }
        }
        invalidate();
    }

    public void onSensorChanged(float x, float y) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (jboxImpl.isBodyView(view)) {
                jboxImpl.applyLinearImpulse(x, y, view);
            }
        }
    }
}
