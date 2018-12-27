package app.davee.assistant.uitableview.swipe;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;

import app.davee.assistant.uitableview.R;
import app.davee.assistant.uitableview.DimensionUtils;

/**
 * SwipeActionView
 * <p>
 * Created by davee 2018/4/23.
 * Copyright (c) 2018 davee. All rights reserved.
 */
public class SwipeActionView extends android.support.v7.widget.AppCompatButton {
    
    private final int DEFAULT_PADDING = DimensionUtils.dp2px(getContext(), 16);
    
    public SwipeActionView(Context context) {
        super(context);
        init();
    }
    
    private void init(){
        this.setAllCaps(false);
        this.setMinimumWidth(0);
        this.setMinWidth(0);
        this.setMinHeight(0);
        this.setMinimumHeight(0);
        this.setPadding(DEFAULT_PADDING, 0, DEFAULT_PADDING,0);
        this.setBackground(null);
        // this.setMaxLength(4);
        // this.setMaxLines(2);
        // this.setSingleLine(true);
        this.setHorizontallyScrolling(true);// 防止当宽度变小时自动换行
    }
    
    // private void setMaxLength(int length){
    //     if (length > 0){
    //         setFilters(new InputFilter[] { new InputFilter.LengthFilter(length) });
    //     }
    // }
    
    public void setup(@NonNull SwipeAction swipeAction){
        if (swipeAction.getActionId() != View.NO_ID){
            this.setId(swipeAction.getActionId());
        }
        
        this.setText(swipeAction.getTitle());
        this.setTextColor(Color.WHITE);
        if (swipeAction.getBackgroundColor() != 0){
            this.setBackgroundColor(swipeAction.getBackgroundColor());
        } else {
            switch (swipeAction.getActionStyle()) {
                case SwipeAction.ActionStyleDestructive:
                    this.setBackgroundColor(internalGetColor(app.davee.assistant.uitableview.R.color.uitableview_color_swiping_destructive));
                    break;
                case SwipeAction.ActionStyleNormal:
                    this.setBackgroundColor(internalGetColor(app.davee.assistant.uitableview.R.color.uitableview_color_swiping_normal));
                    break;
            }
        }
        final Drawable drawable = swipeAction.getDrawable(getContext());
        if (drawable != null){
            drawable.setBounds(0,0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            switch (swipeAction.getDrawablePosition()) {
                case SwipeAction.DrawablePosition.Bottom:
                    this.setCompoundDrawables(null, null, null, drawable);
                    break;
                case SwipeAction.DrawablePosition.Left:
                    this.setCompoundDrawables(drawable, null, null, null);
                    break;
                case SwipeAction.DrawablePosition.Right:
                    this.setCompoundDrawables(null, null, drawable, null);
                    break;
                case SwipeAction.DrawablePosition.Top:
                    this.setCompoundDrawables(null, drawable, null, null);
                    break;
            }
        }
        
    }
    
    private int internalGetColor(int resId){
        return getResources().getColor(resId);
    }
    
    public int getDEFAULT_PADDING() {
        return DEFAULT_PADDING;
    }
    
    public void setPaddingLeft(int paddingStart){
        this.setPadding(paddingStart, getPaddingTop(), getPaddingRight(), getPaddingBottom());
    }
    
    public void setPaddingLeftToDefault(){
        this.setPadding(DEFAULT_PADDING, getPaddingTop(), getPaddingRight(), getPaddingBottom());
    }
    
    public void setPaddingRight(int paddingRight){
        this.setPadding(getPaddingLeft(), getPaddingTop(), paddingRight, getPaddingBottom());
    }
    
    public void setPaddingRightToDefault(){
        this.setPadding(getPaddingLeft(), getPaddingTop(), DEFAULT_PADDING, getPaddingBottom());
    }
}
