package app.davee.assistant.uitableview;

import android.annotation.SuppressLint;

/**
 * PreviewDataSource
 * <p>
 * Created by davee 2018/5/7.
 * Copyright (c) 2018 davee. All rights reserved.
 */
@SuppressLint("DefaultLocale")
class PreviewDataSource extends UITableViewDataSource {
    
    @Override
    public int numberOfSections(UITableView tableView) {
        return 2;
    }
    
    @Override
    public int numberOfRowsInSection(UITableView tableView, int section) {
        return 3;
    }
    
    @Override
    public int viewTypeForCell(UITableView tableView, NSIndexPath indexPath) {
        if (indexPath.section == 0){
            return UITableViewCell.VIEW_TYPE_VALUE1;
        }
        return UITableViewCell.VIEW_TYPE_DEFAULT;
    }
    
    @Override
    public CharSequence titleForHeaderInSection(UITableView tableView, int section) {
        return String.format("Header %d", section);
    }
    
    @Override
    public CharSequence titleForFooterInSection(UITableView tableView, int section) {
        return String.format("Footer %d", section);
    }
    
    @Override
    public UITableViewCell onCreateTableViewCell(UITableView tableView, int viewType) {
        return new UITableViewCell(tableView.getContext(), viewType);
    }
    
    @Override
    public void onBindTableViewCell(UITableView tableView, UITableViewCell tableViewCell, NSIndexPath indexPath) {
        if (indexPath.section == 0){
            // For Style VALUE1
            tableViewCell.setTitleText("Title");
            tableViewCell.setDetailText("Detail");
            tableViewCell.setAccessoryType(UITableViewCell.AccessoryType.Disclosure);
        } else {
            tableViewCell.setTitleText("Title");
        }
    }
}
