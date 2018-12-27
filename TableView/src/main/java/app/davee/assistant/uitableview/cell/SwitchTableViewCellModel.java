package app.davee.assistant.uitableview.cell;

import app.davee.assistant.uitableview.models.UITableViewCellModel;

/**
 * SwitchTableViewCellModel
 * <p>
 * Created by davee 2018/3/3.
 * Copyright (c) 2018 davee. All rights reserved.
 */

public class SwitchTableViewCellModel extends UITableViewCellModel {
    
    public interface OnCheckedValueChangedListener{
        void onCheckedValueChanged(SwitchTableViewCellModel cellModel);
    }
    
    public OnCheckedValueChangedListener onCheckedValueChangedListener;
    
    public boolean checked;
    
    public SwitchTableViewCellModel() {
        viewType = SwitchTableViewCell.VIEW_TYPE;
        // cellStyle = UITableViewCell.Style.Custom;
    }
}
