package app.davee.assistant.uitableview.models;

import android.support.annotation.NonNull;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.util.SortedList;
import android.util.Log;

import app.davee.assistant.uitableview.NSIndexPath;

import java.util.ArrayList;

import app.davee.assistant.uitableview.NSIndexPath;

/**
 * UITableViewModel
 * <p>
 * Created by davee 2018/3/4.
 * Copyright (c) 2018 davee. All rights reserved.
 */
@SuppressWarnings("unused")
public class UITableViewModel {
    
    private static final String TAG = "UITableViewModel";
    
    /**
     * Array of section models
     */
    private ArrayList<UITableViewSectionModel> mSectionModels;
    
    /**
     * Array of cell view models to removed.
     */
    private SparseArrayCompat<SortedList<Integer>> pendingToRemoveCellModels;
    
    public UITableViewModel() {
        mSectionModels = new ArrayList<>();
    }
    
    public void clear() {
        mSectionModels.clear();
    }
    
    public int numberOfSections() {
        return mSectionModels.size();
    }
    
    public int numberOfRowsInSection(int section) {
        if (isSectionOutOfBounds(section)) {
            return 0;
        }
        return mSectionModels.get(section).numOfRows();
    }
    
    public int viewTypeAtIndexPath(NSIndexPath indexPath) {
        UITableViewCellModel cellModel = cellModelAtIndexPath(indexPath);
        return cellModel == null ? 0 : cellModel.viewType;
    }
    
    public UITableViewSectionModel getSectionModel(int section) {
        return mSectionModels.get(section);
    }
    
    /**
     * Create a new section model and append to sections array
     */
    public UITableViewSectionModel appendNewSectionModel() {
        UITableViewSectionModel sectionModel = new UITableViewSectionModel();
        addSectionModel(sectionModel);
        return sectionModel;
    }
    
    //---------------------------------------------------------------
    //              MARK: Add & Remove Section Model
    //---------------------------------------------------------------
    
    public void addSectionModel(@NonNull UITableViewSectionModel sectionModel) {
        addSectionModel(mSectionModels.size(), sectionModel);
    }
    
    public void addSectionModel(int index, @NonNull UITableViewSectionModel sectionModel) {
        // if (sectionModel == null) {
        //     Log.w(TAG, "addSectionModel: the element sectionModel can not be null.");
        //     return;
        // }
        mSectionModels.add(index, sectionModel);
    }
    
    public boolean removeSectionModel(UITableViewSectionModel sectionModel) {
        return mSectionModels.remove(sectionModel);
    }
    
    public UITableViewSectionModel removeSectionModel(int section) {
        if (isSectionOutOfBounds(section)) {
            return null;
        }
        return mSectionModels.remove(section);
    }
    
    public CharSequence titleForHeaderInSection(int section) {
        return getSectionModel(section).getSectionHeaderTitle();
    }
    
    public CharSequence titleForFooterInSection(int section) {
        return getSectionModel(section).getSectionFooterTitle();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // Get CellModel
    ///////////////////////////////////////////////////////////////////////////
    
    public UITableViewCellModel cellModelAtIndexPath(NSIndexPath indexPath) {
        if (indexPath == null) {
            return null;
        }
        return cellModelForRowInSection(indexPath.section, indexPath.row);
    }
    
    public UITableViewCellModel cellModelForRowInSection(int section, int row) {
        if (isSectionOutOfBounds(section)) {
            return null;
        }
        return mSectionModels.get(section).getCellModel(row);
    }
    
    public NSIndexPath indexPathForCellModel(@NonNull UITableViewCellModel cellModel) {
        for (int i = 0; i < numberOfSections(); i++) {
            UITableViewSectionModel sectionModel = getSectionModel(i);
            for (int j = 0; j < sectionModel.numOfRows(); j++) {
                if (sectionModel.getCellModel(j) == cellModel) {
                    return new NSIndexPath(i, j);
                }
            }
        }
        return null;
    }
    
    public boolean addCellModel(UITableViewCellModel cellModel, NSIndexPath indexPath) {
        if (cellModel == null || indexPath == null) {
            Log.w(TAG, "addCellModel: Failed! cellModel = " + String.valueOf(cellModel) + ", indexPath = " + String.valueOf(indexPath));
            return false;
        }
        final int section = indexPath.section;
        if (isSectionOutOfBounds(section)) {
            return false;
        }
        final UITableViewSectionModel sectionModel = mSectionModels.get(section);
        return sectionModel.addCellModel(indexPath.row, cellModel);
    }
    
    public UITableViewCellModel removeCellModel(NSIndexPath indexPath) {
        if (indexPath == null) {
            return null;
        }
        if (isSectionOutOfBounds(indexPath.section)) {
            return null;
        }
        return mSectionModels.get(indexPath.section).removeCellModel(indexPath.row);
    }
    
    public boolean removeCellModel(UITableViewCellModel cellModel) {
        for (UITableViewSectionModel sectionModel : mSectionModels) {
            if (sectionModel.removeCellModel(cellModel)) {
                return true;
            }
        }
        return false;
    }
    
    public void removeCellModels(@NonNull ArrayList<UITableViewCellModel> cellModels) {
        for (UITableViewCellModel cellModel : cellModels) {
            this.removeCellModel(cellModel);
        }
    }
    
    /**
     * Remove specified cell models
     *
     * @param indexPaths to remove cell models
     * @param needsSort  if the <code>indexPaths</code> has been sorted, set to false. Else must set to true
     * @return removed cell models. may be empty
     */
    @NonNull
    public ArrayList<UITableViewCellModel> removeCellModelsAtIndexPaths(@NonNull ArrayList<NSIndexPath> indexPaths, boolean needsSort) {
        ArrayList<UITableViewCellModel> removedCellModels = new ArrayList<>();
        if (!indexPaths.isEmpty()) {
            // First, sort the list if need
            if (needsSort) {
                NSIndexPath.sortIndexPathArray(indexPaths);
            }
            final int size = indexPaths.size();
            for (int i = size - 1; i >= 0; i--) {
                removedCellModels.add(this.removeCellModel(indexPaths.get(i)));
            }
        }
        return removedCellModels;
    }
    
    //---------------------------------------------------------------
    //              MARK: Convenience
    //---------------------------------------------------------------
    
    private boolean isSectionOutOfBounds(int section) {
        if (section >= mSectionModels.size() || section < 0) {
            Log.e(TAG, "section outOfIndexBounds: section = " + section + ", section size = " + mSectionModels.size());
            return true;
        } else {
            return false;
        }
    }
    
}
