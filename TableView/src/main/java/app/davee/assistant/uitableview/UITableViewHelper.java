package app.davee.assistant.uitableview;

import android.support.annotation.NonNull;

import app.davee.assistant.uitableview.models.UITableViewModel;

import java.util.ArrayList;

/**
 * UITableViewHelper
 * <p>
 * Created by davee 2018/6/4.
 * Copyright (c) 2018 davee. All rights reserved.
 */
public class UITableViewHelper {
    
    public static void deleteSelectedCells(@NonNull UITableView tableView, @NonNull UITableViewModel tableViewModel){
        ArrayList<NSIndexPath> selectedIndexPaths = tableView.getSelectedIndexPathArray();
        if (selectedIndexPaths != null && !selectedIndexPaths.isEmpty()){
            /// smaller to bigger
            NSIndexPath.sortIndexPathArray(selectedIndexPaths);
            for (int i = selectedIndexPaths.size() - 1; i >= 0; i--) {
                NSIndexPath indexPath = selectedIndexPaths.remove(i);
                // First deselect the cell(We must deselect the cell when removing, because the cell is reusable)
                tableView.deselectCellAtIndexPath(indexPath);
                // Remove data set
                tableViewModel.removeCellModel(indexPath);
                // Remove cell
                tableView.deleteRowAtIndexPath(indexPath);
            }
            // tableView.clearMultipleSelectedCells();
        }
    }
    
}
