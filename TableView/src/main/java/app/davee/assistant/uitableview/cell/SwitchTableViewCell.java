package app.davee.assistant.uitableview.cell;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import app.davee.assistant.uitableview.R;
import app.davee.assistant.uitableview.UITableViewCell;

/**
 * SwitchTableViewCell
 * <p>
 * Created by davee 2018/3/2.
 * Copyright (c) 2018 davee. All rights reserved.
 */

public class SwitchTableViewCell extends UITableViewCell<SwitchTableViewCellModel> implements View.OnClickListener{
    
    public static final int VIEW_TYPE = R.id.uitableview_cell_viewType_switch;
    
    private Switch mSwitchView;
    private boolean mEnableClickCellToCheck;
    
    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mModel != null && mModel.checked != isChecked){
                mModel.checked = isChecked;
                if (mModel.onCheckedValueChangedListener != null){
                    mModel.onCheckedValueChangedListener.onCheckedValueChanged(mModel);
                }
            }
        }
    };
    
    //---------------------------------------------------------------
    //              MARK: Instantiate
    //---------------------------------------------------------------
    
    public SwitchTableViewCell(Context context, int viewType) {
        super(context, viewType);
        initialize();
    }
    
    public SwitchTableViewCell(Context context) {
        super(context);
        initialize();
    }
    
    public SwitchTableViewCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }
    
    public SwitchTableViewCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }
    
    private void initialize(){
        mSwitchView.setOnCheckedChangeListener(mOnCheckedChangeListener);
        setEnableClickCellToCheck(true);
    }
    
    public boolean isEnableClickCellToCheck() {
        return mEnableClickCellToCheck;
    }
    
    public void setEnableClickCellToCheck(boolean enableClickCellToCheck) {
        if (mEnableClickCellToCheck != enableClickCellToCheck){
            mEnableClickCellToCheck = enableClickCellToCheck;
            if (mEnableClickCellToCheck){
                super.setOnClickListener(this);
            } else {
                super.setOnClickListener(null);
            }
        }
    }
    
    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        if (!mEnableClickCellToCheck){
            super.setOnClickListener(l);
        }
    }
    
    @Override
    public void onClick(View v) {
        mSwitchView.setChecked(!mSwitchView.isChecked());
    }
    
    @Override
    protected void createLayoutForCustom() {
        mTitleTextView = newTitleTextView();
        mSwitchView = new Switch(getContext());
        mSwitchView.setId(View.generateViewId());
        
        /* LayoutParams for Title */
        RelativeLayout.LayoutParams titleParams = mContentView.generateDefaultLayoutParams();
        titleParams.addRule(RelativeLayout.CENTER_VERTICAL);
        titleParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        
        /* LayoutParams for Switch View */
        RelativeLayout.LayoutParams switchParams = mContentView.generateDefaultLayoutParams();
        switchParams.addRule(RelativeLayout.CENTER_VERTICAL);
        switchParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        
        mContentView.addView(mTitleTextView, titleParams);
        mContentView.addView(mSwitchView, switchParams);
    }
    
    @Override
    protected void setModelInternal(SwitchTableViewCellModel model) {
        super.setModelInternal(model);
        
        if (model != null){
            this.setSwitchChecked(model.checked);
        }
    }
    
    public void setSwitchChecked(boolean checked){
        if (mSwitchView.isChecked() != checked){
            mSwitchView.setChecked(checked);
        }
    }
    
    public Switch getSwitchView() {
        return mSwitchView;
    }
}
