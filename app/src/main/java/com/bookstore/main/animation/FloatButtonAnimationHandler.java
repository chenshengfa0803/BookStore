package com.bookstore.main.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.Point;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;

import com.bookstore.main.FloatButton;

import java.util.List;

/**
 * Created by Administrator on 2016/1/9.
 */
public class FloatButtonAnimationHandler {
    private FloatButton mainFloatButton;
    private boolean animating;

    public FloatButtonAnimationHandler(View actionButton) {
        mainFloatButton = (FloatButton) actionButton;
    }

    public boolean isAnimating() {
        return animating;
    }

    public void animateMenuOpening(Point center) {
        if (mainFloatButton == null) {
            throw new NullPointerException("animationHandler cannot animate with a null FloatButton");
        } else if (mainFloatButton.getSubFloatButtonItems().size() == 0) {
            throw new RuntimeException("animatorHandler cannot animate if mainFloatButton has no sub items");
        }
        animating = true;

        Animator tempAnimator = null;
        List<FloatButton.subItem> subFloatButtonItems = mainFloatButton.getSubFloatButtonItems();
        for (int i = 0; i < subFloatButtonItems.size(); i++) {
            subFloatButtonItems.get(i).view.setScaleX(0);
            subFloatButtonItems.get(i).view.setScaleY(0);
            subFloatButtonItems.get(i).view.setAlpha(0);

            PropertyValuesHolder translation_x = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, subFloatButtonItems.get(i).x + subFloatButtonItems.get(i).width / 2 - center.x);
            PropertyValuesHolder translation_y = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, subFloatButtonItems.get(i).y + subFloatButtonItems.get(i).height / 2 - center.y);
            PropertyValuesHolder scale_x = PropertyValuesHolder.ofFloat(View.SCALE_X, 1);
            PropertyValuesHolder scale_y = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1);
            PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1);

            final ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(subFloatButtonItems.get(i).view, translation_x, translation_y, scale_x, scale_y, alpha);
            animator.setDuration(mainFloatButton.getDuration());
            animator.setInterpolator(new AnticipateOvershootInterpolator());
            animator.start();
        }
    }
}
