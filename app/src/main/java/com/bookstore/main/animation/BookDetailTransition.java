package com.bookstore.main.animation;

import android.annotation.TargetApi;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;

/**
 * Created by Administrator on 2016/4/11.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BookDetailTransition extends TransitionSet {
    public BookDetailTransition() {
        init();
    }

    private void init() {
        setOrdering(ORDERING_TOGETHER);
        addTransition(new ChangeBounds()).
                addTransition(new ChangeTransform()).
                addTransition(new ChangeImageTransform());
    }
}
