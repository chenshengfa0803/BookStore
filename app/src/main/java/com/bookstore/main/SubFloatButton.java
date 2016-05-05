package com.bookstore.main;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Administrator on 2015/12/24.
 */
public class SubFloatButton extends ViewGroup {
    private final Paint pen = new Paint(Paint.ANTI_ALIAS_FLAG);
    ImageView subfloatButtonIcon = null;
    private int mColor = -1;

    private int left = 0;
    private int right = 0;
    private int top = 0;
    private int bottom = 0;

    public SubFloatButton(Context context) {
        this(context,null);
    }

    public SubFloatButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SubFloatButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionButton);
        mColor = a.getColor(R.styleable.FloatingActionButton_bgColor, Color.WHITE);
        pen.setStyle(Paint.Style.FILL);
        pen.setColor(mColor);

        float radius, dx, dy;
        int shadowColor;
        radius = a.getFloat(R.styleable.FloatingActionButton_shadowRadius, 10.0f);
        dx = a.getFloat(R.styleable.FloatingActionButton_shadowDx, 0);
        dy = a.getFloat(R.styleable.FloatingActionButton_shadowDy, 0);
        shadowColor = a.getColor(R.styleable.FloatingActionButton_shadowColor, Color.argb(100, 0, 0, 0));
        pen.setShadowLayer(radius, dx, dy, shadowColor);
        a.recycle();

        setWillNotDraw(false);//enable onDraw

    }

    public SubFloatButton(Context context, Drawable drawable, ViewGroup.MarginLayoutParams params)
    {
        this(context);
        if(drawable != null) {
            subfloatButtonIcon = new ImageView(context);
            subfloatButtonIcon.setImageDrawable(drawable);
            addView(subfloatButtonIcon);
            left = params.leftMargin;
            top = params.topMargin;
            right = left + params.width;
            bottom = top + params.height;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = getContext().getResources().getDimensionPixelSize(R.dimen.sub_float_button_size);
        setMeasuredDimension(size, size);
        for (int index = 0; index < getChildCount(); index++) {
            View v = getChildAt(index);
            v.setLeft(left);
            v.setRight(right);
            v.setTop(top);
            v.setBottom(bottom);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            v.layout(v.getLeft(), v.getTop(), v.getLeft() + v.getWidth(), v.getTop() + v.getHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        canvas.drawCircle(getWidth()/2, getHeight()/2, (float)(getWidth()/2.6), pen);

    }

    public void setContentView(final Drawable contentView) {
        //this.addView(contentView);
        if(contentView != null) {
            //mBitmap = ((BitmapDrawable) contentView).getBitmap();
        }
        this.invalidate();
    }

}
