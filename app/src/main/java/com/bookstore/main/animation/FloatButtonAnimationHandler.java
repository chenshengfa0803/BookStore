package com.bookstore.main.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;

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
            animator.addListener(new SubFloatButtonAnimationListener(subFloatButtonItems.get(i), ActionType.OPENING));
            animator.setStartDelay(50 * i);
            animator.start();
        }
    }

    public void animateMenuClosing(Point center) {
        if (mainFloatButton == null) {
            throw new NullPointerException("animationHandler cannot animate with a null FloatButton");
        } else if (mainFloatButton.getSubFloatButtonItems().size() == 0) {
            throw new RuntimeException("animatorHandler cannot animate if mainFloatButton has no sub items");
        }
        animating = true;

        List<FloatButton.subItem> subFloatButtonItems = mainFloatButton.getSubFloatButtonItems();
        for (int i = 0; i < subFloatButtonItems.size(); i++) {
            PropertyValuesHolder translation_x = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, center.x - (subFloatButtonItems.get(i).x + subFloatButtonItems.get(i).width / 2));
            PropertyValuesHolder translation_y = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, center.y - (subFloatButtonItems.get(i).y + subFloatButtonItems.get(i).height / 2));
            PropertyValuesHolder scale_x = PropertyValuesHolder.ofFloat(View.SCALE_X, 0);
            PropertyValuesHolder scale_y = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0);
            PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0);

            final ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(subFloatButtonItems.get(i).view, translation_x, translation_y, scale_x, scale_y, alpha);
            animator.setDuration(mainFloatButton.getDuration());
            animator.setInterpolator(new AnticipateInterpolator());
            animator.addListener(new SubFloatButtonAnimationListener(subFloatButtonItems.get(i), ActionType.CLOSING));
            animator.start();
        }
    }

    public enum ActionType {OPENING, CLOSING}

    public class SubFloatButtonAnimationListener implements Animator.AnimatorListener {
        private FloatButton.subItem subItem;
        private ActionType actionType;

        public SubFloatButtonAnimationListener(FloatButton.subItem item, ActionType type) {
            subItem = item;
            actionType = type;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            animating = true;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            animating = false;
            subItem.view.setTranslationX(0);
            subItem.view.setTranslationY(0);
            subItem.view.setScaleX(1);
            subItem.view.setScaleY(1);
            subItem.view.setAlpha(1);
            ViewGroup.LayoutParams params = subItem.view.getLayoutParams();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) params;
            if (actionType == ActionType.OPENING) {
                layoutParams.setMargins(subItem.x, subItem.y, 0, 0);
                subItem.view.setLayoutParams(layoutParams);
            } else if (actionType == ActionType.CLOSING) {
                Point center = mainFloatButton.calculateMainFloatButtonPosition();
                layoutParams.setMargins(center.x - subItem.width / 2, center.y - subItem.height / 2, 0, 0);
                subItem.view.setLayoutParams(layoutParams);
                mainFloatButton.removeSubFloatButtonViewFromContainer(subItem.view);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            animating = false;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            animating = true;
        }
    }
}
