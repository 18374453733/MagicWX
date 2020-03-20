package com.hdpfans.app.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import hdpfans.com.R;

public class ElementView extends android.support.v7.widget.AppCompatButton {

    private static final String TAG = "ElementView";

    private Paint mPaint;
    private boolean isChecked;
    private Bitmap bitmap;

    public ElementView(Context context) {
        this(context,null);
    }

    public ElementView(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public ElementView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = getPaint();
        int resourceId = R.drawable.icon_menu_check;
        bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
    }

    public void setIcon(int resourceId) {
        this.bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
        invalidate();
    }

    public void isChecked(boolean b) {
        isChecked = b;
        invalidate();
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            setIcon(R.drawable.icon_menu_checked);
        } else {
            setIcon(R.drawable.icon_menu_check);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //指定是否使用抗锯齿功能，如果使用，会使绘图速度变慢
        mPaint.setAntiAlias(true);
        // 图片尾部居中显示
        int length = (int) mPaint.measureText((String) this.getText());
        int h = (this.getMeasuredHeight() - (int) this.getTextSize()) / 2;
        int w = (this.getMeasuredWidth() + length) / 2;
        //        canvas.translate(0,(this.getMeasuredHeight()/2) - (int) this.getTextSize());
        if (isChecked) {
            canvas.drawBitmap(bitmap, w + 20, h, null);
        }
        super.onDraw(canvas);
    }

}

