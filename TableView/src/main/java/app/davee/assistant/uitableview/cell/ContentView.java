package app.davee.assistant.uitableview.cell;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import app.davee.assistant.uitableview.R;

/**
 * ContentView
 * <p>
 * Created by davee 2018/4/21.
 * Copyright (c) 2018 davee. All rights reserved.
 */
@SuppressWarnings("unused")
public class ContentView extends RelativeLayout {
    
    private int mDefaultPaddingLeft, mDefaultPaddingRight;
    private final Rect mContentInsets = new Rect();
    private int mContentMinHeight;
    
    public ContentView(Context context) {
        super(context);
        init(null, 0);
    }
    
    public ContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }
    
    public ContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }
    
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ContentView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }
    
    private void init(AttributeSet attrs, int defStyleAttr){
        // Default attrs
        mContentMinHeight = getResources().getDimensionPixelOffset(app.davee.assistant.uitableview.R.dimen.uitableview_cell_minHeight);
        mContentInsets.left = mContentInsets.right = resolveDp(app.davee.assistant.uitableview.R.dimen.uitableview_cell_contentInsets_start);
        mContentInsets.top = mContentInsets.bottom = resolveDp(app.davee.assistant.uitableview.R.dimen.uitableview_cell_contentInsets_top);
        
        if (attrs != null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, app.davee.assistant.uitableview.R.styleable.ContentView, defStyleAttr, 0);
            
            final int count = typedArray.getIndexCount();
            for (int i = 0; i < count; i++) {
                int attr = typedArray.getIndex(i);
                if (attr == app.davee.assistant.uitableview.R.styleable.ContentView_android_padding){
                    int padding = typedArray.getDimensionPixelOffset(attr, 0);
                    mContentInsets.set(padding, padding, padding, padding);
                } else if (attr == app.davee.assistant.uitableview.R.styleable.ContentView_android_paddingLeft
                        || attr == app.davee.assistant.uitableview.R.styleable.ContentView_android_paddingStart){
                    mContentInsets.left = typedArray.getDimensionPixelOffset(attr, mContentInsets.left);
                } else if (attr == app.davee.assistant.uitableview.R.styleable.ContentView_android_paddingTop){
                    mContentInsets.top = typedArray.getDimensionPixelOffset(attr, mContentInsets.top);
                } else if (attr == app.davee.assistant.uitableview.R.styleable.ContentView_android_paddingRight
                        || attr == app.davee.assistant.uitableview.R.styleable.ContentView_android_paddingEnd){
                    mContentInsets.right = typedArray.getDimensionPixelOffset(attr, mContentInsets.right);
                } else if (attr == app.davee.assistant.uitableview.R.styleable.ContentView_android_paddingBottom){
                    mContentInsets.bottom = typedArray.getDimensionPixelOffset(attr, mContentInsets.bottom);
                } else if (attr == app.davee.assistant.uitableview.R.styleable.ContentView_android_minHeight){
                    mContentMinHeight = typedArray.getDimensionPixelSize(attr, mContentMinHeight);
                }
            }
            
            typedArray.recycle();
        }
        
        setMinimumHeight(mContentMinHeight);
        onContentInsetsChanged();
        mDefaultPaddingLeft = mContentInsets.left;
        mDefaultPaddingRight = mContentInsets.right;
    }
    
    @Override
    public RelativeLayout.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // Getter & Setter
    ///////////////////////////////////////////////////////////////////////////
    
    
    public int getContentMinHeight() {
        return mContentMinHeight;
    }
    
    public void setContentMinHeight(int contentMinHeight) {
        mContentMinHeight = contentMinHeight;
    }
    
    private void onContentInsetsChanged(){
        this.setPadding(mContentInsets.left, mContentInsets.top, mContentInsets.right, mContentInsets.bottom);
    }
    
    @NonNull
    public Rect getContentInsets() {
        return mContentInsets;
    }
    
    public void setContentInsets(Rect contentInsets) {
        if (contentInsets != null && !mContentInsets.equals(contentInsets)){
            mContentInsets.set(contentInsets);
            onContentInsetsChanged();
        }
    }
    
    public void setContentInsetsLeft(int left){
        if (left != mContentInsets.left){
            mContentInsets.left = left;
            onContentInsetsChanged();
        }
    }
    
    public void setContentInsetsLeftToDefault(){
        if (mContentInsets.left != mDefaultPaddingLeft){
            mContentInsets.left = mDefaultPaddingLeft;
            onContentInsetsChanged();
        }
    }
    
    public void setContentInsetsTop(int top){
        if (top != mContentInsets.top){
            mContentInsets.top = top;
            onContentInsetsChanged();
        }
    }
    
    public void setContentInsetsRight(int right){
        if (right != mContentInsets.right){
            mContentInsets.right = right;
            onContentInsetsChanged();
        }
    }
    
    public void setContentInsetsRightToDefault(){
        if (mContentInsets.right != mDefaultPaddingRight){
            mContentInsets.right = mDefaultPaddingRight;
            onContentInsetsChanged();
        }
    }
    
    public void setContentInsetsBottom(int bottom){
        if (bottom != mContentInsets.bottom){
            mContentInsets.bottom = bottom;
            onContentInsetsChanged();
        }
    }
    
    // =====================================
    // MARK - Convenience
    // =====================================
    
    private int resolveDp(int dimension){
        return getResources().getDimensionPixelOffset(dimension);
    }
}
