package com.bookstore.main;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bookstore.main.animation.FloatButtonAnimationHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shfa.chen on 2015/12/6.
 */
public class FloatButton extends ViewGroup implements View.OnClickListener {
    private final Paint pen = new Paint(Paint.ANTI_ALIAS_FLAG);
    private ImageView floatButtonIcon = null;
    private int mColor = -1;
    private List<subItem> subFloatButtonItems;
    private boolean menuOpened;
    private int subItemStartAngle;
    private int subItemEndAngle;
    private int menu_radio;
    private int menu_duration;
    private FloatButtonAnimationHandler animationHandler;
    private MenuStateListener menuStateListener;

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
        floatButtonIcon.setImageDrawable(drawable);
        //floatButtonIcon.setImageResource(R.drawable.main_floatbutton_add);

        addView(floatButtonIcon);
        //setClickable(true);
        setOnClickListener(this);
        menuOpened = false;

        subFloatButtonItems = new ArrayList<>();
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
            int margin_left = getResources().getDimensionPixelSize(R.dimen.main_float_button_margin_left);
            int margin_top = getResources().getDimensionPixelSize(R.dimen.main_float_button_margin_top);
            int size = getResources().getDimensionPixelSize(R.dimen.main_float_button_icon_size);
            v.layout(margin_left, margin_top, margin_left + size, margin_top + size);
            //v.layout(margin_left, margin_top, 144, 144);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, (float) (getWidth() / 2.6), pen);
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

    public View getContentView() {
        return floatButtonIcon;
    }

    public int getDuration() {
        return menu_duration;
    }

    public List<subItem> getSubFloatButtonItems() {
        return subFloatButtonItems;
    }

    private View getActivityContentView() {
        return ((Activity) getContext()).getWindow().getDecorView().findViewById(android.R.id.content);
    }

    public void addSubFloatButtonViewToContainer(View view, FrameLayout.LayoutParams layoutParams) {
        if (layoutParams != null) {
            ((ViewGroup) getActivityContentView()).addView(view, layoutParams);
        } else {
            ((ViewGroup) getActivityContentView()).addView(view);
        }
    }

    public void removeSubFloatButtonViewFromContainer(View view) {
        ((ViewGroup) getActivityContentView()).removeView(view);
    }

    public FloatButton addSubFloatButton(View subFloatButton) {
        int size = getContext().getResources().getDimensionPixelSize(R.dimen.sub_float_button_size);
        subFloatButtonItems.add(new subItem(subFloatButton, size, size));
        return this;
    }

    public void createFloatButtonMenu(int startAngle, int endAngle, int radio, int duration) {
        this.subItemStartAngle = startAngle;
        this.subItemEndAngle = endAngle;
        this.menu_radio = radio;
        this.animationHandler = new FloatButtonAnimationHandler(this);
        this.menu_duration = duration;
    }

    @Override
    public void onClick(View v) {
        if (menuOpened) {
            closeMenu();
        } else {
            openMenu();
        }
    }

    public void closeMenu() {
        if (animationHandler.isAnimating()) {
            return;//do not add view to container if animating
        }
        Point center = calculateMainFloatButtonPosition();
        animationHandler.animateMenuClosing(center);
        menuOpened = false;

        if (menuStateListener != null) {
            menuStateListener.onMenuClosed(this);
        }
    }

    public void openMenu() {
        if (animationHandler.isAnimating()) {
            return;//do not add view to container if animating
        }
        final Point center = calculateSubFloatButtonsPosition();
        for (int i = 0; i < subFloatButtonItems.size(); i++) {
            if (subFloatButtonItems.get(i).view.getParent() != null) {
                throw new RuntimeException("view must has no parent before addView");
            }
            final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(subFloatButtonItems.get(i).width, subFloatButtonItems.get(i).height, Gravity.TOP | Gravity.LEFT);
            params.setMargins(center.x - subFloatButtonItems.get(i).width / 2, center.y - subFloatButtonItems.get(i).height / 2, 0, 0);
            addSubFloatButtonViewToContainer(subFloatButtonItems.get(i).view, params);
        }
        animationHandler.animateMenuOpening(center);
        menuOpened = true;

        if (menuStateListener != null) {
            menuStateListener.onMenuOpened(this);
        }
    }

    private Point getScreenSize() {
        Point size = new Point();
        WindowManager mWm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        mWm.getDefaultDisplay().getSize(size);
        return size;
    }

    private Point calculateMainFloatButtonCoordinates() {
        int[] coords = new int[2];
        this.getLocationOnScreen(coords);

        Rect activityFrame = new Rect();
        getActivityContentView().getWindowVisibleDisplayFrame(activityFrame);
        coords[0] -= (getScreenSize().x - getActivityContentView().getMeasuredWidth());
        coords[1] -= (activityFrame.height() + activityFrame.top - getActivityContentView().getMeasuredHeight());
        return new Point(coords[0], coords[1]);
    }

    public Point calculateMainFloatButtonPosition() {
        Point point = calculateMainFloatButtonCoordinates();
        point.x += this.getMeasuredWidth() / 2;
        point.y += this.getMeasuredHeight() / 2;
        return point;
    }

    public Point calculateSubFloatButtonsPosition() {
        final Point mainCenterPos = calculateMainFloatButtonPosition();
        RectF area = new RectF(mainCenterPos.x - menu_radio, mainCenterPos.y - menu_radio, mainCenterPos.x + menu_radio, mainCenterPos.y + menu_radio);

        Path orbit = new Path();
        orbit.addArc(area, subItemStartAngle, subItemEndAngle - subItemStartAngle);

        int divisor = subFloatButtonItems.size() - 1;

        PathMeasure measure = new PathMeasure(orbit, false);
        for (int i = 0; i < subFloatButtonItems.size(); i++) {
            float[] coords = new float[]{0f, 0f};
            measure.getPosTan(i * measure.getLength() / divisor, coords, null);
            subFloatButtonItems.get(i).x = (int) coords[0] - subFloatButtonItems.get(i).width / 2;
            subFloatButtonItems.get(i).y = (int) coords[1] - subFloatButtonItems.get(i).height / 2;
        }
        return mainCenterPos;
    }

    public void addMenuStateListener(MenuStateListener listener) {
        this.menuStateListener = listener;
    }

    public interface MenuStateListener {
        void onMenuOpened(FloatButton fb);

        void onMenuClosed(FloatButton fb);
    }

    public static class subItem {
        public int x;
        public int y;
        public int width;
        public int height;
        public float alpha;
        public View view;

        public subItem(View view, int width, int height) {
            this.view = view;
            this.width = width;
            this.height = height;
            alpha = view.getAlpha();
            x = 0;
            y = 0;
        }
    }
}
