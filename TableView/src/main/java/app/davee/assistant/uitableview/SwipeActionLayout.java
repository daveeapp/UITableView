package app.davee.assistant.uitableview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import app.davee.assistant.uitableview.swipe.SwipeAction;
import app.davee.assistant.uitableview.swipe.SwipeActionView;
import app.davee.assistant.uitableview.swipe.SwipeActionsConfiguration;

import app.davee.assistant.uitableview.swipe.SwipeActionsConfiguration;

/**
 * SwipeActionLayout
 *
 * #ff3b30  destructive
 * #595ad3  normal
 * <p>
 * Created by davee 2018/4/24.
 * Copyright (c) 2018 davee. All rights reserved.
 */
@SuppressLint("ViewConstructor")
class SwipeActionLayout extends LinearLayout {
    
    @UITableViewCell.SwipeLocation
    private int mSwipeLocation;
    
    private int mOriginalWidth = 0;
    
    //private boolean mFullSwipeEnabled = false;
    private int mFullSwipeMinOffset = Integer.MAX_VALUE;
    private boolean mWillFullSwipe = false;
    
    private SwipeActionsConfiguration mSwipeActionsConfiguration;
    
    // private ValueAnimator mFirstViewToDefaultStateAnimator = null;
    
    public SwipeActionLayout(Context context, int swipeLocation) {
        super(context);
        mSwipeLocation = swipeLocation;
        init();
    }
    
    private void init() {
        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        if (mSwipeLocation == UITableViewCell.SwipeLocation.Trailing) {
            this.setLayoutDirection(LAYOUT_DIRECTION_RTL);
        }
    }
    
    boolean isFullSwipeEnabled() {
        return mSwipeActionsConfiguration.isPerformFirstActionWithFullSwipe();
    }
    
    SwipeAction getFirstSwipeAction(){
        return getSwipeActionsConfiguration().getSwipeActions().get(0);
    }
    
    @NonNull
    public SwipeActionsConfiguration getSwipeActionsConfiguration() {
        return mSwipeActionsConfiguration;
    }
    
    public void setSwipeActionsConfiguration(@NonNull SwipeActionsConfiguration swipeActionsConfiguration) {
        if (mSwipeActionsConfiguration != swipeActionsConfiguration) {
            // Remove old action views
            if (mSwipeActionsConfiguration != null) {
                removeAllViews();
            }
            mSwipeActionsConfiguration = swipeActionsConfiguration;
            layoutActionViews(mSwipeActionsConfiguration);
        }
    }
    
    void layoutActionViews(@NonNull SwipeActionsConfiguration swipeActionsConfiguration) {
        for (SwipeAction action : swipeActionsConfiguration.getSwipeActions()) {
            SwipeActionView actionView = newSwipeActionView(action);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -1);
            this.addView(actionView, params);
        }
    }
    
    private SwipeActionView newSwipeActionView(@NonNull SwipeAction swipeAction) {
        SwipeActionView button = new SwipeActionView(getContext());
        button.setup(swipeAction);
        return button;
    }
    
    // View findActionViewUnder(float rawX, float rawY){
    //     final int count = this.getChildCount();
    //     final Rect hitRect = new Rect();
    //     for (int i = 0; i < count; i++) {
    //         View child = getChildAt(i);
    //         child.getGlobalVisibleRect(hitRect);
    //     }
    //     return null;
    // }
    
    
    // public int getFullSwipeMinOffset() {
    //     return mFullSwipeMinOffset;
    // }
    //
    // public void setFullSwipeMinOffset(int fullSwipeMinOffset) {
    //     mFullSwipeMinOffset = fullSwipeMinOffset;
    // }
    
    // int getOriginalWidth() {
    //     return mOriginalWidth;
    // }
    //
    // void setOriginalWidth(int originalWidth) {
    //     mOriginalWidth = originalWidth;
    // }
    
    int getSwipeTriggerOffset(){
        return mOriginalWidth / 2;
    }
    
    int getSwipeMaxOffset(){
        return mOriginalWidth;
    }
    
    boolean isWillFullSwipe() {
        return mWillFullSwipe;
    }
    
    void willStartSwiping(int cellWidth){
        this.measure(-2, -1);
        this.mOriginalWidth = getMeasuredWidth();
        this.mFullSwipeMinOffset = cellWidth * 2/3;
    }
    
    void onSwiped(final float swipedOffset) {
        
        int offset = (int) Math.abs(swipedOffset);
        if (Math.abs(mOriginalWidth - offset) < 5){
            offset = mOriginalWidth;
        }
        
        ViewGroup.LayoutParams selfParams = this.getLayoutParams();
        selfParams.width = offset;
    
        final boolean isOverSwiped = offset > mOriginalWidth;
        
        if (isFullSwipeEnabled()) {
            swipedWithFullSwipe(offset, isOverSwiped);
        } else {
            swipedDefault(isOverSwiped);
        }
        this.setLayoutParams(selfParams);
    }
    
    private void swipedDefault(boolean isOverSwiped) {
        final int count = getChildCount();
        if (isOverSwiped) {
            for (int i = 0; i < count; i++) {
                SwipeActionView actionView = (SwipeActionView) getChildAt(i);
                LayoutParams lp = (LayoutParams) actionView.getLayoutParams();
                if (lp.width != 0) {
                    lp.width = 0;
                    lp.weight = (float) actionView.getWidth() / this.getWidth();
                    actionView.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
                }
            }
        } else {
            for (int i = 0; i < count; i++) {
                SwipeActionView actionView = (SwipeActionView) getChildAt(i);
                LayoutParams lp = (LayoutParams) actionView.getLayoutParams();
                if (lp.width != -2) {
                    lp.width = -2;
                    lp.weight = 0;
                    actionView.setGravity(Gravity.CENTER);
                }
            }
        }
    }
    
    private void swipedWithFullSwipe(final int offset, boolean isOverSwiped) {
        final int count = getChildCount();
        if (isOverSwiped) {
            final SwipeActionView firstActionView = (SwipeActionView) getChildAt(0);
            LayoutParams lp = (LayoutParams) firstActionView.getLayoutParams();
            lp.width = 0;
            lp.weight = 1;
            if (offset > mFullSwipeMinOffset) {
                firstActionView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                if (!mWillFullSwipe) {
                    final int overOffset = offset - mOriginalWidth;
                    animateFirstActionViewToFullSwipeState(overOffset);
                }
                mWillFullSwipe = true;
            } else {
                firstActionView.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
                if (mWillFullSwipe) {
                    final int overOffset = offset - mOriginalWidth;
                    animateFirstActionViewToDefaultState(overOffset);
                }
                mWillFullSwipe = false;
            }
            
        } else {
            mWillFullSwipe = false;
            if (count > 0) {
                SwipeActionView firstActionView = (SwipeActionView) getChildAt(0);
                LayoutParams lp = (LayoutParams) firstActionView.getLayoutParams();
                lp.width = -2;
                lp.weight = 0;
                firstActionView.setGravity(Gravity.CENTER);
            }
        }
    }
    
    private void animateFirstActionViewToFullSwipeState(final int overOffset) {
        // Use the padding attr
        final SwipeActionView firstActionView = (SwipeActionView) getChildAt(0);
        final int start = firstActionView.getPaddingLeft() + overOffset;
        final int end = firstActionView.getDEFAULT_PADDING();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
        valueAnimator.setDuration(220);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                firstActionView.setPaddingLeft((Integer) animation.getAnimatedValue());
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                firstActionView.setPaddingLeftToDefault();
            }
        });
        valueAnimator.start();
    }
    
    private void animateFirstActionViewToDefaultState(final int overOffset) {
        final SwipeActionView firstActionView = (SwipeActionView) getChildAt(0);
        final int start = firstActionView.getPaddingRight() + overOffset;
        final int end = firstActionView.getDEFAULT_PADDING();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
        valueAnimator.setDuration(220);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                firstActionView.setPaddingRight((Integer) animation.getAnimatedValue());
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                firstActionView.setPaddingRightToDefault();
            }
        });
        valueAnimator.start();
    }
    
}
