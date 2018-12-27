package app.davee.assistant.uitableview;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import app.davee.assistant.uitableview.swipe.SwipeAction;
import app.davee.assistant.uitableview.swipe.SwipeActionsConfiguration;

/**
 * UITableViewDelegate
 * <p>
 * Created by davee 2018/3/3.
 * Copyright (c) 2018 davee. All rights reserved.
 */

public class UITableViewDelegate {
    
    public void onTableViewCellClicked(UITableView tableView, UITableViewCell tableViewCell, NSIndexPath indexPath) {
        // nothing
    }
    
    public boolean onTableViewCellLongClicked(UITableView tableView, UITableViewCell tableViewCell, NSIndexPath indexPath) {
        // nothing
        return false;
    }
    
    public boolean shouldSelectCellAtIndexPath(UITableView tableView, UITableViewCell tableViewCell, NSIndexPath indexPath){
        return true; // default true
    }
    
    public void onTableViewCellSelected(UITableView tableView, UITableViewCell tableViewCell, NSIndexPath indexPath) {
        // nothing
    }
    
    public void onTableViewCellDeselected(UITableView tableView, UITableViewCell tableViewCell, NSIndexPath indexPath) {
        // nothing
    }
    
    /// For Custom
    
    /**
     * You must set accessory view clickable true by called method {@link UITableViewCell#setAccessoryViewClickable(boolean)}
     */
    public void onAccessoryViewClicked(UITableView tableView, UITableViewCell tableViewCell, NSIndexPath indexPath){
    
    }
    
    
    // =====================================
    // MARK - Editing
    // =====================================
    
    public static abstract class EditingDelegate {
        
        public boolean canEditForCell(UITableView tableView, NSIndexPath indexPath) {
            return true;
        }
        
        @UITableViewCell.EditingStyle
        public int editingStyleForCell(UITableView tableView, NSIndexPath indexPath) {
            return UITableViewCell.EditingStyle.Delete;
        }
        
        public void commitEditingStyle(UITableView tableView, int editingStyle, NSIndexPath indexPath) {
        
        }
        
        public void willBeginEditing(UITableView tableView){
        
        }
        
        public void willEndEditing(UITableView tableView){
        
        }
        
    }
    
    // =====================================
    // MARK - Swiping
    // =====================================
    
    public static abstract class SwipeDelegate {
        
        /**
         * Configuration for delete action.
         * <p>
         * if {@link #trailingSwipeActions(UITableView, NSIndexPath)} return null, will return this.
         */
        private SwipeActionsConfiguration mDeleteActionConfiguration = null;
        
        void ensureDeleteActionConfiguration(CharSequence title) {
            if (mDeleteActionConfiguration == null) {
                SwipeAction swipeAction = new SwipeAction(R.id.uitableview_cell_swipe_action_delete);
                swipeAction.setTitle(title);
                
                mDeleteActionConfiguration = new SwipeActionsConfiguration();
                mDeleteActionConfiguration.setPerformFirstActionWithFullSwipe(true);
                mDeleteActionConfiguration.addSwipeAction(swipeAction);
            } else {
                mDeleteActionConfiguration.getSwipeActions().get(0).setTitle(title);
            }
        }
        
        SwipeActionsConfiguration internalGetTrailingConfiguration(UITableView tableView, NSIndexPath indexPath) {
            SwipeActionsConfiguration configuration = trailingSwipeActions(tableView, indexPath);
            if (configuration == null) {
                CharSequence deleteTitle = titleForDeleteSwipeAction(tableView, indexPath);
                if (TextUtils.isEmpty(deleteTitle)) {
                    return null;
                } else {
                    ensureDeleteActionConfiguration(deleteTitle);
                    return mDeleteActionConfiguration;
                }
            } else {
                return configuration;
            }
        }
        
        public boolean canSwipeForCell(UITableView tableView, NSIndexPath indexPath) {
            return false;
        }
        
        /**
         * Setup delete swipe action.
         * <p>
         * This method will be called when {@link #trailingSwipeActions(UITableView, NSIndexPath)}return null.
         * <p>
         * If delete swipe action has been clicked, the method {@link #didSelectDeleteSwipeAction(UITableView, NSIndexPath)} will be called.
         *
         * @param tableView --
         * @param indexPath --
         * @return title for delete action
         */
        public CharSequence titleForDeleteSwipeAction(UITableView tableView, NSIndexPath indexPath) {
            return null;
        }
        
        public void didSelectDeleteSwipeAction(UITableView tableView, NSIndexPath indexPath) {
        
        }
        
        @Nullable
        public SwipeActionsConfiguration trailingSwipeActions(UITableView tableView, NSIndexPath indexPath) {
            return null;
        }
        
        @Nullable
        public SwipeActionsConfiguration leadingSwipeActions(UITableView tableView, NSIndexPath indexPath) {
            return null;
        }
        
        public void willBeginSwiping(UITableView tableView, NSIndexPath indexPath) {
        
        }
        
        public void didEndSwiping(UITableView tableView, NSIndexPath indexPath) {
        
        }
        
        public void didSelectSwipeAction(UITableView tableView, SwipeAction swipeAction, NSIndexPath indexPath) {
        
        }
    }
}
