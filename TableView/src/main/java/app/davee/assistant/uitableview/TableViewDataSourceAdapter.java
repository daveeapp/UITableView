package app.davee.assistant.uitableview;

import android.support.annotation.NonNull;

import app.davee.assistant.uitableview.models.UITableViewModel;

/**
 * TableViewDataSourceAdapter
 * <p>
 * Created by davee 2018/6/14.
 * Copyright (c) 2018 davee. All rights reserved.
 */
public class TableViewDataSourceAdapter extends UITableViewDataSource {
    
    private UITableViewModel mTableViewModel;
    
    public TableViewDataSourceAdapter(@NonNull UITableViewModel tableViewModel) {
        mTableViewModel = tableViewModel;
    }
    
    public UITableViewModel getTableViewModel() {
        return mTableViewModel;
    }
    
    public void setTableViewModel(@NonNull UITableViewModel tableViewModel) {
        if (mTableViewModel != tableViewModel){
            mTableViewModel = tableViewModel;
        }
    }
    
    @Override
    public int numberOfSections(UITableView tableView) {
        return mTableViewModel == null ? super.numberOfSections(tableView) : mTableViewModel.numberOfSections();
    }
    
    @Override
    public int numberOfRowsInSection(UITableView tableView, int section) {
        return mTableViewModel == null ? 0 : mTableViewModel.numberOfRowsInSection(section);
    }
    
    @Override
    public int viewTypeForCell(UITableView tableView, NSIndexPath indexPath) {
        return mTableViewModel == null ? 0 : mTableViewModel.viewTypeAtIndexPath(indexPath);
    }
    
    @Override
    public UITableViewCell onCreateTableViewCell(UITableView tableView, int viewType) {
        return new UITableViewCell(tableView.getContext(), viewType);
    }
    
    @Override
    public CharSequence titleForHeaderInSection(UITableView tableView, int section) {
        return mTableViewModel == null ? super.titleForHeaderInSection(tableView, section) : mTableViewModel.titleForHeaderInSection(section);
    }
    
    @Override
    public CharSequence titleForFooterInSection(UITableView tableView, int section) {
        return mTableViewModel == null ? super.titleForFooterInSection(tableView, section) : mTableViewModel.titleForFooterInSection(section);
    }
    
    @Override
    public void onBindTableViewCell(UITableView tableView, UITableViewCell tableViewCell, NSIndexPath indexPath) {
        if (mTableViewModel != null){
            tableViewCell.setModel(mTableViewModel.cellModelAtIndexPath(indexPath));
        }
    }
}
