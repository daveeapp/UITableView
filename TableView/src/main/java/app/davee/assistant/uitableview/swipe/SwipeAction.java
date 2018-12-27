package app.davee.assistant.uitableview.swipe;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;

import java.lang.annotation.Retention;

import static app.davee.assistant.uitableview.swipe.SwipeAction.DrawablePosition.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * SwipeAction
 * <p>
 * Created by davee 2018/4/22.
 * Copyright (c) 2018 davee. All rights reserved.
 */
public class SwipeAction {
    
    /* SwipeActionStyle */
    public static final int ActionStyleNormal = 0;
    public static final int ActionStyleDestructive = 1;
    
    @IntDef(value = {ActionStyleNormal, ActionStyleDestructive})
    @Retention(SOURCE)
    public @interface ActionStyle {
    }
    
    /// Drawable Position
    @IntDef({Left, Top, Right, Bottom})
    @Retention(SOURCE)
    public @interface DrawablePosition {
        int Left = 0;
        int Top = 1;
        int Right = 2;
        int Bottom = 3;
    }
    
    @ActionStyle
    private int mActionStyle = ActionStyleDestructive;
    
    /**
     * The id to specify the action and will be setup to action view. It should be like the view id
     */
    private int mActionId;
    private CharSequence mTitle;
    private int mBackgroundColor;
    private Drawable mDrawable;
    private int mDrawableRes;
    @DrawablePosition
    private int mDrawablePosition = DrawablePosition.Top;
    
    public SwipeAction(int actionId) {
        mActionId = actionId;
    }
    
    public SwipeAction(int actionId, CharSequence title) {
        mActionId = actionId;
        mTitle = title;
    }
    
    @ActionStyle
    public int getActionStyle() {
        return mActionStyle;
    }
    
    public void setActionStyle(int actionStyle) {
        mActionStyle = actionStyle;
    }
    
    public int getActionId() {
        return mActionId;
    }
    
    public void setActionId(int actionId) {
        mActionId = actionId;
    }
    
    public CharSequence getTitle() {
        return mTitle;
    }
    
    public void setTitle(CharSequence title) {
        mTitle = title;
    }
    
    public int getBackgroundColor() {
        return mBackgroundColor;
    }
    
    public void setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
    }
    
    public Drawable getDrawable(Context context) {
        if (mDrawable == null && mDrawableRes != 0){
            mDrawable = ContextCompat.getDrawable(context, mDrawableRes);
        }
        return mDrawable;
    }
    
    public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
    }
    
    public void setDrawableRes(int drawableRes) {
        mDrawableRes = drawableRes;
    }
    
    @DrawablePosition
    public int getDrawablePosition() {
        return mDrawablePosition;
    }
    
    public void setDrawablePosition(@DrawablePosition int drawablePosition) {
        mDrawablePosition = drawablePosition;
    }
    
    // public OnSwipeActionClickedListener getActionClickedListener() {
    //     return actionClickedListener;
    // }
    //
    // public void setActionClickedListener(OnSwipeActionClickedListener actionClickedListener) {
    //     this.actionClickedListener = actionClickedListener;
    // }
}
