package app.davee.assistant.uitableview.models;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import app.davee.assistant.uitableview.UITableViewCell;

import app.davee.assistant.uitableview.UITableViewCell;

/**
 * UITableViewCellModel
 * <p>
 * Created by davee 2018/3/2.
 * Copyright (c) 2018 davee. All rights reserved.
 */

public class UITableViewCellModel {
    
    public int tag;
    
    public int viewType = UITableViewCell.VIEW_TYPE_DEFAULT;
    
    /**
     * @see UITableViewCell.AccessoryType
     */
    public int accessoryType = UITableViewCell.UNDEFINED;
    
    /**
     * @see UITableViewCell.SelectionStyle
     */
    public int selectionStyle = UITableViewCell.UNDEFINED;
    
    public CharSequence titleText = null;
    public CharSequence detailText = null;
    
    private Drawable mImageDrawable = null;
    private int mImageDrawableRes = 0;
    private boolean mImageDrawableSet = false;
    
    /**
     * Id to specify the cell model
     */
    private int cellId = View.NO_ID;
    
    public UITableViewCellModel() {
    }
    
    public int getCellId() {
        return cellId;
    }
    
    public void setCellId(int cellId) {
        this.cellId = cellId;
    }
    
    public int getViewType() {
        return viewType;
    }
    
    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
    
    public int getAccessoryType() {
        return accessoryType;
    }
    
    public void setAccessoryType(int accessoryType) {
        this.accessoryType = accessoryType;
    }
    
    public int getSelectionStyle() {
        return selectionStyle;
    }
    
    public void setSelectionStyle(int selectionStyle) {
        this.selectionStyle = selectionStyle;
    }
    
    public Drawable getImageDrawable(Context context) {
        if (mImageDrawableRes != 0){
            return  ContextCompat.getDrawable(context, mImageDrawableRes);
        } else {
            return mImageDrawable;
        }
    }
    
    public void setImageDrawable(Drawable imageDrawable) {
        this.mImageDrawableSet = true;
        this.mImageDrawable = imageDrawable;
    }
    
    public void setImageDrawableRes(int imageDrawableRes) {
        this.mImageDrawableSet = true;
        mImageDrawableRes = imageDrawableRes;
    }
    
    public boolean isImageDrawableSet() {
        return mImageDrawableSet;
    }
    
    public CharSequence getTitleText() {
        return titleText;
    }
    
    public void setTitleText(CharSequence titleText) {
        this.titleText = titleText;
    }
    
    public CharSequence getDetailText() {
        return detailText;
    }
    
    public void setDetailText(CharSequence detailText) {
        this.detailText = detailText;
    }
}

