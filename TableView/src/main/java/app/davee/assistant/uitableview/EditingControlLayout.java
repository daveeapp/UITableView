package app.davee.assistant.uitableview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageButton;
import android.widget.FrameLayout;

/**
 * EditingControlView
 * <p>
 * Created by davee 2018/4/28.
 * Copyright (c) 2018 davee. All rights reserved.
 */
class EditingControlLayout extends FrameLayout {
    
    // @IntDef({Default, Selection, Reorder})
    // @Retention(RetentionPolicy.SOURCE)
    // public @interface ControlStyle {
    //     int Default = 0;        // for editing style insert / delete
    //     int Selection = 1;      // for editing with allow multiple selection
    //     int Reorder = 2;        // for editing reorder
    // }
    //
    // @ControlStyle
    // int mControlStyle;
    
    boolean mAllowSelection = false;
    
    EditingButton mEditingButton;
    EditingCheckBox mEditingCheckBox;
    
    private int mIntrinsicWidth;
    
    // EditingControlLayout(@NonNull Context context, @ControlStyle int controlStyle) {
    //     super(context);
    //
    //     mControlStyle = controlStyle;
    //     switch (mControlStyle) {
    //         case ControlStyle.Default:
    //             enableEditingButton(true);
    //             break;
    //         case ControlStyle.Reorder:
    //             break;
    //         case ControlStyle.Selection:
    //             enableEditingCheckBox(true);
    //             break;
    //     }
    //
    //     init();
    // }
    
    public EditingControlLayout(@NonNull Context context) {
        super(context);
        init();
    }
    
    private void init() {
        mIntrinsicWidth = DimensionUtils.dp2px(getContext(), 24);
    }
    
    /// Delete / Insert
    
    void setupEditingStyle(int editingStyle) {
        if (editingStyle != UITableViewCell.EditingStyle.None) {
            // enableEditingButton(true);
            showEditingButton(true);
            mEditingButton.setEditingStyle(editingStyle);
        } else {
            // enableEditingButton(false);
            showEditingButton(false);
        }
    }
    
    void setEditingButtonClickListener(OnClickListener onClickListener) {
        if (mEditingButton != null) {
            mEditingButton.setOnClickListener(onClickListener);
        }
    }
    
    /// Selection Mode
    
    void setAllowSelection(boolean allowSelection) {
        if (mAllowSelection != allowSelection) {
            mAllowSelection = allowSelection;
            enableEditingCheckBox(mAllowSelection);
        }
    }
    
    void setEditingSelection(boolean selected) {
        if (mAllowSelection) {
            mEditingCheckBox.setChecked(selected);
        }
    }
    
    int getIntrinsicWidth() {
        return mIntrinsicWidth;
    }
    
    // =====================================
    // MARK - Layout
    // =====================================
    
    private void ensureEditingButton(){
        if (mEditingButton == null) {
            mEditingButton = new EditingButton(getContext());
            LayoutParams params = generateDefaultLayoutParams();
            this.addView(mEditingButton, params);
        }
    }
    
    private void showEditingButton(boolean showing){
        ensureEditingButton();
        if (showing){
            mEditingButton.setVisibility(VISIBLE);
        } else {
            mEditingButton.setVisibility(GONE);
        }
    }
    
    // private void enableEditingButton(boolean enable) {
    //     if (enable) {
    //         if (mEditingButton == null) {
    //             mEditingButton = new EditingButton(getContext());
    //             LayoutParams params = generateDefaultLayoutParams();
    //             this.addView(mEditingButton, params);
    //         }
    //     } else {
    //         if (mEditingButton != null && mEditingButton.getParent() == this) {
    //             removeView(mEditingButton);
    //         }
    //         mEditingButton = null;
    //     }
    // }
    
    private void enableEditingCheckBox(boolean enable) {
        if (enable) {
            if (mEditingCheckBox == null) {
                mEditingCheckBox = new EditingCheckBox(getContext());
                mEditingCheckBox.setClickable(false);
                LayoutParams params = generateDefaultLayoutParams();
                this.addView(mEditingCheckBox, params);
            }
        } else {
            if (mEditingCheckBox != null && mEditingCheckBox.getParent() == this) {
                removeView(mEditingCheckBox);
            }
            mEditingCheckBox = null;
        }
    }
    
    public EditingButton getEditingButton() {
        return mEditingButton;
    }
    
    public EditingCheckBox getEditingCheckBox() {
        return mEditingCheckBox;
    }
    
    // =====================================
    // MARK - Inner Classes
    // =====================================
    
    @SuppressLint("ViewConstructor")
    class EditingButton extends AppCompatImageButton {
        
        @UITableViewCell.EditingStyle
        private int mEditingStyle;
        
        private int mDefaultWidth;
        
        public EditingButton(Context context) {
            super(context);
            init();
        }
        
        private void init() {
            this.setPadding(0, 0, 0, 0);
            this.setMinimumWidth(0);
            this.setMinimumHeight(0);
            this.setBackground(null);
            this.setScaleType(ScaleType.CENTER);
            
            mDefaultWidth = DimensionUtils.dp2px(getContext(), 24);
            
        }
        
        public void setEditingStyle(int editingStyle) {
            // TODO: 2018/4/28 修改VectorDrawable颜色
            if (mEditingStyle != editingStyle){
                mEditingStyle = editingStyle;
                if (mEditingStyle == UITableViewCell.EditingStyle.Insert) {
                    this.setImageResource(R.drawable.ic_add_circle_green_24dp);
                } else if (mEditingStyle == UITableViewCell.EditingStyle.Delete) {
                    this.setImageResource(R.drawable.ic_remove_circle_red_24dp);
                }
            }
        }
        
        int getIntrinsicWidth(){
            if (mEditingStyle == UITableViewCell.EditingStyle.None){
                return mDefaultWidth;
            }
            return getDrawable() != null ? getDrawable().getIntrinsicWidth() : 0;
        }
        
    }
    
    public class EditingCheckBox extends AppCompatCheckBox {
        
        public EditingCheckBox(Context context) {
            super(context);
            
            setButtonDrawable(R.drawable.uitableview_cell_editing_checkable);
        }
        
        public int getIntrinsicWidth(){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return getButtonDrawable() == null ?  0 : getButtonDrawable().getIntrinsicWidth();
            } else {
                Drawable drawable = CompoundButtonCompat.getButtonDrawable(this);
                return drawable == null ? 0 : drawable.getIntrinsicWidth();
            }
        }
    }
}
