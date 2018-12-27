package app.davee.assistant.uitableview.cell;

import android.text.InputFilter;

import app.davee.assistant.uitableview.models.UITableViewCellModel;

/**
 * EditTableViewCellModel
 * <p>
 * Created by davee 2018/5/7.
 * Copyright (c) 2018 davee. All rights reserved.
 */
public class EditTableViewCellModel extends UITableViewCellModel {
    
    public CharSequence editHint;
    
    public InputFilter[] inputFilters = null;
    
    public EditTableViewCellModel() {
        viewType = EditTableViewCell.VIEW_TYPE;
    }
    
    public CharSequence getEditHint() {
        return editHint;
    }
    
    public void setEditHint(CharSequence editHint) {
        this.editHint = editHint;
    }
    
    public InputFilter[] getInputFilters() {
        return inputFilters;
    }
    
    public void setInputFilters(InputFilter[] inputFilters) {
        this.inputFilters = inputFilters;
    }
}
