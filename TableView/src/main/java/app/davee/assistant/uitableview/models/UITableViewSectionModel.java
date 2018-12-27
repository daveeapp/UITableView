package app.davee.assistant.uitableview.models;

import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

/**
 * UITableViewSectionModel
 * <p>
 * Created by davee 2018/3/4.
 * Copyright (c) 2018 davee. All rights reserved.
 */

@SuppressWarnings({"unchecked", "unused"})
public class UITableViewSectionModel{
    
    private static final String TAG = "UITableViewSectionModel";
    
    // Header title of section
    @Nullable
    private CharSequence mSectionHeaderTitle;
    // Footer title of section
    @Nullable
    private CharSequence mSectionFooterTitle;
    // The array of cell view models
    private ArrayList<UITableViewCellModel> mTableViewCellModels;
    
    public UITableViewSectionModel() {
        mTableViewCellModels = new ArrayList<>();
    }
    
    public void clear(){
        mTableViewCellModels.clear();
    }
    
    public int numOfRows(){
        return mTableViewCellModels.size();
    }
    
    public UITableViewCellModel getCellModel(int row){
        if (isRowOutOfBounds(row)){
            return null;
        }
        return mTableViewCellModels.get(row);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // Add & Delete & Get CellModel
    ///////////////////////////////////////////////////////////////////////////
    
    public void addCellModel(UITableViewCellModel cellModel){
        addCellModel(mTableViewCellModels.size(), cellModel);
    }
    
    public boolean addCellModel(int row, UITableViewCellModel cellModel){
        if (cellModel == null){
            Log.w(TAG, "[addCellModel]:the cellModel is null.");
            return false;
        }
        mTableViewCellModels.add(row, cellModel);
        return true;
    }
    
    public UITableViewCellModel removeCellModel(int row){
        if (isRowOutOfBounds(row)){
            return null;
        }
        return mTableViewCellModels.remove(row);
    }
    
    public void removeCellModelInRange(int start, int count){
        for (int i = 0; i < count; i++) {
            removeCellModel(start);
        }
    }
    
    public boolean removeCellModel(UITableViewCellModel cellModel){
        return mTableViewCellModels.remove(cellModel);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // Getter and Setter
    ///////////////////////////////////////////////////////////////////////////
    
    @Nullable
    public CharSequence getSectionHeaderTitle() {
        return mSectionHeaderTitle;
    }
    
    public void setSectionHeaderTitle(@Nullable CharSequence sectionHeaderTitle) {
        mSectionHeaderTitle = sectionHeaderTitle;
    }
    
    @Nullable
    public CharSequence getSectionFooterTitle() {
        return mSectionFooterTitle;
    }
    
    public void setSectionFooterTitle(@Nullable CharSequence sectionFooterTitle) {
        mSectionFooterTitle = sectionFooterTitle;
    }
    
    public ArrayList<UITableViewCellModel> getTableViewCellModels() {
        return mTableViewCellModels;
    }
    
    public void setTableViewCellModels(ArrayList<UITableViewCellModel> tableViewCellModels) {
        mTableViewCellModels = tableViewCellModels;
    }
    
    //---------------------------------------------------------------
    //              MARK: Convenience
    //---------------------------------------------------------------
    
    private boolean isRowOutOfBounds(final int row){
        if (row < 0 || row >= mTableViewCellModels.size()){
            Log.e(TAG, "row outOfBounds:row = " + row +", numOfRows = " + mTableViewCellModels.size());
            return true;
        }
        return false;
    }
}
