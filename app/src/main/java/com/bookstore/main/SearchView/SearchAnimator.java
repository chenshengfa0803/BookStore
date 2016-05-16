package com.bookstore.main.SearchView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.bookstore.main.R;

/**
 * Created by Administrator on 2016/5/17.
 */
public class SearchAnimator {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void expandInAnimation(final Context context, final View view, int duration) {
        int centerX = view.getWidth() - context.getResources().getDimensionPixelOffset(R.dimen.search_animation_dx);
        int centerY = view.getHeight() / 2;

        if (centerX != 0 && centerY != 0) {
            float endRadius = (float) Math.hypot(centerX, centerY);
            Animator animator = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, 0.0f, endRadius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(duration);
            view.setVisibility(View.VISIBLE);
            animator.start();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void collaspeOutAnimation(final Context context, final View view, int duration) {
        int centerX = view.getWidth() - context.getResources().getDimensionPixelOffset(R.dimen.search_animation_dx);
        int centerY = view.getHeight() / 2;

        if (centerX != 0 && centerY != 0) {
            float startRadius = (float) Math.hypot(centerX, centerY);

            Animator animator = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, 0.0f);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(duration);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.GONE);
                }
            });
            animator.start();
        }
    }

    public static void fadeInAnimation(final View view, int duration) {
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setDuration(duration);

        view.setAnimation(animation);
        view.setVisibility(View.VISIBLE);
    }

    public static void fadeOutAnimation(final View view, int duration) {
        Animation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setDuration(duration);

        view.setAnimation(animation);
        view.setVisibility(View.GONE);
    }
}
