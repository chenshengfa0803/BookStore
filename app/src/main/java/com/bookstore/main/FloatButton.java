package com.bookstore.main;

import android.app.Activity;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/6.
 */
public class FloatButton extends ViewGroup {
    private final Paint pen = new Paint(Paint.ANTI_ALIAS_FLAG);
    ImageView floatButtonIcon;
    private int mColor = -1;
    private List<Item> subFloatButtonItems;

    public FloatButton(Context context) {
        this(context, null);
    }

    public FloatButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionButton);
        mColor = a.getColor(R.styleable.FloatingActionButton_bgColor, Color.WHITE);
        pen.setStyle(Paint.Style.FILL);
        pen.setColor(mColor);

        float radius, dx, dy;
        int shadowColor;
        Drawable drawable;
        radius = a.getFloat(R.styleable.FloatingActionButton_shadowRadius, 10.0f);
        dx = a.getFloat(R.styleable.FloatingActionButton_shadowDx, 0);
        dy = a.getFloat(R.styleable.FloatingActionButton_shadowDy, 0);
        shadowColor = a.getColor(R.styleable.FloatingActionButton_shadowColor, Color.argb(100, 0, 0, 0));
        pen.setShadowLayer(radius, dx, dy, shadowColor);
        a.recycle();

        drawable = a.getDrawable(R.styleable.FloatingActionButton_drawable);
        floatButtonIcon = new ImageView(context);
        //floatButtonIcon.setImageDrawable(drawable);
        floatButtonIcon.setImageResource(R.drawable.main_floatbutton_add);

        int size = context.getResources().getDimensionPixelSize(R.dimen.main_float_button_icon_size);
        ViewGroup.LayoutParams params = new ViewGroup.MarginLayoutParams(size, size);
        addView(floatButtonIcon);

        subFloatButtonItems = new ArrayList<Item>();
        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int size = getContext().getResources().getDimensionPixelSize(R.dimen.main_float_button_icon_size);
        for (int index = 0; index < getChildCount(); index++) {
            getChildAt(index).measure(MeasureSpec.makeMeasureSpec(size, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(size, MeasureSpec.AT_MOST));
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int index = 0; index < getChildCount(); index++) {
            View v = getChildAt(index);
            v.layout(0, 0, 144, 144);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, (float) (getWidth() / 2.6), pen);
        //if (mBitmap != null) {
        //    canvas.drawBitmap(mBitmap, (getWidth() - mBitmap.getWidth()) / 2, (getHeight() - mBitmap.getHeight()) / 2, pen);
        //}
    }

    public void setContentView(View contentView, LayoutParams contentParams) {
        LayoutParams params;
        if (contentParams == null) {
            int size = getContext().getResources().getDimensionPixelSize(R.dimen.main_float_button_icon_size);
            params = new LayoutParams(size, size);
        } else {
            params = contentParams;
        }
        this.addView(contentView, params);
        int width = contentView.getWidth();
        int height = contentView.getHeight();
    }

    public View getActivityContentView() {
        return ((Activity) getContext()).getWindow().getDecorView().findViewById(android.R.id.content);
    }

    public void addSubFloatButtonViewToContainer(View view, ViewGroup.LayoutParams layoutParams) {
        if (layoutParams != null) {
            ((ViewGroup) getActivityContentView()).addView(view, layoutParams);
        } else {
            ((ViewGroup) getActivityContentView()).addView(view);
        }
    }

    public FloatButton addSubFloatButton(View subFloatButton) {
        subFloatButtonItems.add(new Item(subFloatButton, subFloatButton.getWidth(), subFloatButton.getHeight()));
        return this;
    }

    public void createFloatButtonMenu() {
        for (final Item item : subFloatButtonItems) {
            //if (item.width == 0 || item.height == 0) {
            //int size = getContext().getResources().getDimensionPixelSize(R.dimen.sub_float_button_size);
            //ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(size, size);
            //params.setMargins(5, 5, 5, 5);
            //item.view.setLayoutParams(params);
            addSubFloatButtonViewToContainer(item.view, null);
            //item.alpha = 0;
            // }
        }
    }

    public static class Item {
        public int x;
        public int y;
        public int width;
        public int height;
        public float alpha;
        public View view;

        public Item(View view, int width, int height) {
            this.view = view;
            this.width = width;
            this.height = height;
            alpha = view.getAlpha();
            x = 0;
            y = 0;
        }
    }
}
