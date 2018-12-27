package app.davee.assistant.uitableview.cell;

import android.content.Context;
import android.util.AttributeSet;

import app.davee.assistant.uitableview.R;
import app.davee.assistant.uitableview.UITableViewCell;

import app.davee.assistant.uitableview.UITableViewCell;

/**
 * SelectableTableViewCell
 * <p>
 * Created by davee 2018/4/21.
 * Copyright (c) 2018 davee. All rights reserved.
 *
 * @deprecated Use {@link #VIEW_TYPE_DEFAULT_SELECTABLE} instead
 */
@Deprecated
public class SelectableTableViewCell extends UITableViewCell {
    
    public static final int VIEW_TYPE = app.davee.assistant.uitableview.R.id.uitableview_cell_viewType_selectable;
    
    public SelectableTableViewCell(Context context, int viewType) {
        super(context, VIEW_TYPE_DEFAULT);
        init();
    }
    
    public SelectableTableViewCell(Context context) {
        super(context);
        init();
    }
    
    public SelectableTableViewCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public SelectableTableViewCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init(){
        this.setSelectionStyle(SelectionStyle.None);
    }
    
    @Override
    protected void onSelectionChanged(boolean selected) {
        if (isSelected()){
            setAccessoryType(AccessoryType.CheckMark);
        } else {
            setAccessoryType(AccessoryType.None);
        }
    }
    
}
