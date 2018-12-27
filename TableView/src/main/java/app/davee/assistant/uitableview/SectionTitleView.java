package app.davee.assistant.uitableview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * SectionTitleView
 * <p>
 * Created by davee 2018/3/3.
 * Copyright (c) 2018 davee. All rights reserved.
 */

@SuppressLint("ViewConstructor")
public class SectionTitleView extends FrameLayout {
    
    // public static final int VIEW_TYPE = R.id.uitableview_cell_viewType_section_title;
    public static final int VIEW_TYPE_HEADER = R.id.uitableview_cell_viewType_section_header;
    public static final int VIEW_TYPE_FOOTER = R.id.uitableview_cell_viewType_section_footer;
    
    private int mViewType;
    private int mDefaultPadding5dp = DimensionUtils.dp2px(getContext(), 5);
    private int mDefaultPadding8dp = DimensionUtils.dp2px(getContext(), 8);
    private TextView mTitleTextView;
    
    public SectionTitleView(@NonNull Context context, int viewType) {
        super(context);
        mViewType = viewType;
        init();
    }
    
    // public SectionTitleView(@NonNull Context context, @Nullable AttributeSet attrs) {
    //     super(context, attrs);
    //     init();
    // }
    //
    // public SectionTitleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    //     super(context, attrs, defStyleAttr);
    //     init();
    // }
    
    private void init() {
        this.setMinimumHeight(getResources().getDimensionPixelSize(R.dimen.uitableview_section_header_minHeight));
        final int paddingStart = getResources().getDimensionPixelOffset(R.dimen.uitableview_common_margin_start);
        this.setPadding(paddingStart, 0, paddingStart, 0);
    }
    
    protected TextView newTitleTextView(){
        TextView textView = new TextView(getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(R.style.UITableView_TextAppearance_SectionTitle);
        } else {
            textView.setTextAppearance(getContext(), R.style.UITableView_TextAppearance_SectionTitle);
        }
        return textView;
    }
    
    protected void enableTextView(boolean enable){
        if (enable){
            if (mTitleTextView == null){
                mTitleTextView = newTitleTextView();
                FrameLayout.LayoutParams params = generateTitleParams();
                this.addView(mTitleTextView, params);
            }
        } else {
            if (mTitleTextView != null && mTitleTextView.getParent() == this){
                this.removeView(mTitleTextView);
                mTitleTextView = null;
            }
        }
    }
    
    private LayoutParams generateTitleParams(){
        LayoutParams params = generateDefaultLayoutParams();
        if (mViewType == VIEW_TYPE_HEADER){
            params.gravity = Gravity.START | Gravity.BOTTOM;
            params.bottomMargin = mDefaultPadding5dp;
            params.topMargin = mDefaultPadding8dp;
        } else {
            params.gravity = Gravity.START | Gravity.TOP;
            params.topMargin = mDefaultPadding5dp;
            params.bottomMargin = mDefaultPadding8dp;
        }
        return params;
    }
    
    public void setTitleText(@StringRes int text) {
        if (mTitleTextView != null) {
            this.setTitleText(getResources().getText(text));
        }
    }
    
    public void setTitleText(CharSequence text) {
        enableTextView(text != null);
        if (mTitleTextView != null && !mTitleTextView.getText().equals(text)) {
            mTitleTextView.setText(text);
        }
    }
    
    public void setTitleGravity(int gravity) {
        if (mTitleTextView != null) {
            LayoutParams params = (LayoutParams) mTitleTextView.getLayoutParams();
            if (mViewType == VIEW_TYPE_HEADER){
                params.gravity = gravity;
            } else {
                params.gravity = gravity;
            }
            mTitleTextView.requestLayout();
        }
    }
    
    public void setTitleMarginLeft(int marginLeft) {
        if (mTitleTextView != null) {
            LayoutParams params = (LayoutParams) mTitleTextView.getLayoutParams();
            params.leftMargin = marginLeft;
            mTitleTextView.requestLayout();
        }
    }
    
    public void setTitleMarginTop(int marginTop) {
        if (mTitleTextView != null) {
            LayoutParams params = (LayoutParams) mTitleTextView.getLayoutParams();
            params.topMargin = marginTop;
            mTitleTextView.requestLayout();
        }
    }
    
    public void setTitleMarginRight(int marginRight) {
        if (mTitleTextView != null) {
            LayoutParams params = (LayoutParams) mTitleTextView.getLayoutParams();
            params.rightMargin = marginRight;
            mTitleTextView.requestLayout();
        }
    }
    
    public void setTitleMarginBottom(int marginBottom) {
        if (mTitleTextView != null) {
            LayoutParams params = (LayoutParams) mTitleTextView.getLayoutParams();
            params.bottomMargin = marginBottom;
            mTitleTextView.requestLayout();
        }
    }
    
    public void setTitleMargin(int marginLeft, int marginTop, int marginRight, int marginBottom) {
        if (mTitleTextView != null) {
            LayoutParams params = (LayoutParams) mTitleTextView.getLayoutParams();
            params.setMargins(marginLeft, marginTop, marginRight, marginBottom);
            mTitleTextView.requestLayout();
        }
    }
    
    public TextView getTitleTextView() {
        return mTitleTextView;
    }
    
    public void setAllCaps(boolean allCaps){
        if (mTitleTextView != null){
            mTitleTextView.setAllCaps(allCaps);
        }
    }
}
