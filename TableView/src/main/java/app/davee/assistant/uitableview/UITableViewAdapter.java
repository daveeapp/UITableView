package app.davee.assistant.uitableview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import app.davee.assistant.uitableview.UITableView.UITableViewBaseHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * UITableViewAdapter
 * <p>
 * Created by davee 2018/3/3.
 * Copyright (c) 2018 davee. All rights reserved.
 */
public class UITableViewAdapter extends RecyclerView.Adapter<UITableViewBaseHolder> {
    
    private static final String TAG = "UITableViewAdapter";
    // 当indexPath.row = -1时，表示Section Header
    private static final int INDEXPATH_SECTION_HEADER = -1;
    // 当indexPath.row = -2时，表示Section Footer
    private static final int INDEXPATH_SECTION_FOOTER = -2;
    
    // Item counts = cell + header + footer
    private int mItemCount = 0;
    // 记录每个Section的属性
    private ArrayList<SectionHolder> mSectionHolderList;
    
    private UITableView mTableView;
    private UITableViewDataSource mTableViewDataSource;
    
    UITableViewAdapter(UITableView tableView) {
        mTableView = tableView;
        initialize();
    }
    
    private void initialize() {
        mTableViewDataSource = mTableView.getTableViewDataSource();
        if (mTableViewDataSource == null) {
            Log.e(TAG, "initialize: the data source has not been set up yet.");
            return;
        }
        mItemCount = 0;
        if (mSectionHolderList == null) {
            mSectionHolderList = new ArrayList<>();
        } else {
            mSectionHolderList.clear();
        }
        
        // 初始化所有view的数量：rowCount = cellCount + headerCount + footerCount;
        final int countOfSection = mTableViewDataSource.numberOfSections(mTableView);
        for (int s = 0; s < countOfSection; s++) {
            SectionHolder sectionHolder = new SectionHolder();
            mSectionHolderList.add(sectionHolder);
            
            // 当前Section的起始position
            sectionHolder.sectionStartPosition = mItemCount;
            
            // 判断当前Section是否有Header
            HeaderFooterModel headerModel = sectionHolder.headerModel;
            final int headerHeight = mTableViewDataSource.heightForHeaderInSection(mTableView, s);
            final CharSequence headerTitle = mTableViewDataSource.titleForHeaderInSection(mTableView, s);
            if (headerHeight != UITableView.UNDEFINED || headerTitle != null) {
                headerModel.setEnabled(true);
                headerModel.setPosition(mItemCount);
                headerModel.height = headerHeight;
                mItemCount += 1;
            } else {
                headerModel.setEnabled(false);
            }
            
            //  当前section下的cell个数
            final int countOfRowInSection = mTableViewDataSource.numberOfRowsInSection(mTableView, s);
            sectionHolder.numOfRowsInSection = countOfRowInSection;
            mItemCount += countOfRowInSection;
            
            // 判断当前Section是否有Footer
            HeaderFooterModel footerModel = sectionHolder.footerModel;
            final int footerHeight = mTableViewDataSource.heightForFooterInSection(mTableView, s);
            final CharSequence footerTitle = mTableViewDataSource.titleForFooterInSection(mTableView, s);
            if (footerHeight != UITableView.UNDEFINED || footerTitle != null) {
                footerModel.setEnabled(true);
                footerModel.setPosition(mItemCount);
                footerModel.height = footerHeight;
                mItemCount += 1;
            } else {
                footerModel.setEnabled(false);
            }
            
        }
    }
    
    void updateSectionCache() {
        this.initialize();
    }
    
    /**
     * <p>There are two different classes of data change events, item changes and structural
     * changes. Item changes are when a single item has its data updated but no positional
     * changes have occurred. Structural changes are when items are inserted, removed or moved
     * within the data set.</p>
     *
     * @param structuralChanged whether have position changes
     */
    /// 更新所有item，如果item有动画且数量比较大时会有性能问题，尽量不要使用
    void reloadAll(boolean structuralChanged) {
        if (structuralChanged) {
            updateSectionCache();
            // BugFixed:notifyDataSetChanged包含了两种变化（item数据改变和item结构改变）
            notifyDataSetChanged();
        } else {
            // notifyItemRangeChanged 只包含数据改变
            notifyItemRangeChanged(0, mItemCount);
        }
    }
    
    void notifySectionChanged(int section, Object payload) {
        if (isSectionOutOfIndex(section)) {
            return;
        }
        SectionHolder sectionHolder = mSectionHolderList.get(section);
        int positionStart = sectionHolder.sectionStartPosition;
        int itemCount = sectionHolder.getAllRowsInSection();
        notifyItemRangeChanged(positionStart, itemCount, payload);
    }
    
    void deleteSection(int section) {
        if (isSectionOutOfIndex(section)) {
            return;
        }
        final SectionHolder sectionHolder = mSectionHolderList.get(section);
        final int positionStart = sectionHolder.sectionStartPosition;
        final int itemCount = sectionHolder.getAllRowsInSection();
        updateSectionCache();
        notifyItemRangeRemoved(positionStart, itemCount);
    }
    
    void insertSection(int section) {
        final int oldNumOfSections = mSectionHolderList.size();
        updateSectionCache();
        if (isSectionOutOfIndex(section)) {
            return;
        }
        final SectionHolder sectionHolder = mSectionHolderList.get(section);
        int positionStart = sectionHolder.sectionStartPosition;
        int itemCount = sectionHolder.getAllRowsInSection();
        // if insert to end
        if (section == oldNumOfSections) {
            // update contentBottomOffset
            positionStart = Math.max(0, positionStart - 1);
        } else if (section == 0) { // if insert to top
            // update contentTopOffset
            itemCount = Math.min(getItemCount(), itemCount + 1);
        }
        notifyItemRangeInserted(positionStart, itemCount);
    }
    
    // ==========================
    // MARK - Overrides Adapter
    // ==========================
    
    // @Override
    // public void onViewRecycled(UITableViewCellHolder holder) {
    //     super.onViewRecycled(holder);
    // }
    //
    // @Override
    // public void onViewAttachedToWindow(UITableViewCellHolder holder) {
    //     super.onViewAttachedToWindow(holder);
    // }
    //
    // @Override
    // public void onViewDetachedFromWindow(UITableViewCellHolder holder) {
    //     super.onViewDetachedFromWindow(holder);
    // }
    
    @Override
    public int getItemCount() {
        return mItemCount;
    }
    
    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
    
    @Override
    public int getItemViewType(int position) {
        NSIndexPath indexPath = getIndexPath(position);
        if (indexPath == null) {
            return 0;
        }
        if (isSectionHeaderIndex(indexPath)) {
            HeaderFooterModel headerModel = mSectionHolderList.get(indexPath.section).headerModel;
            int viewType = mTableViewDataSource.viewTypeForSectionHeader(mTableView, indexPath.section);
            headerModel.setViewType(viewType);
            return viewType;
        } else if (isSectionFooterIndex(indexPath)) {
            HeaderFooterModel footerModel = mSectionHolderList.get(indexPath.section).footerModel;
            int viewType = mTableViewDataSource.viewTypeForSectionFooter(mTableView, indexPath.section);
            footerModel.setViewType(viewType);
            return viewType;
        } else {
            return mTableViewDataSource.viewTypeForCell(mTableView, indexPath);
        }
    }
    
    @Override
    public UITableViewBaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Log.d(TAG, "onCreateViewHolder: ");
        // 初始化view, 这个方法只有在需要新的ViewHolder的时候才会调用，
        // 1）没有发现可以复用的CellView，2）在有动画的时候也会重新创建holder
        // 每个ViewType对应一个ItemView
        if (isHeaderViewType(viewType)) {
            View itemView = mTableViewDataSource.createSectionHeader(mTableView, viewType);
            return mTableView.newSectionHeaderHolder(itemView);
        } else if (isFooterViewType(viewType)) {
            View itemView = mTableViewDataSource.createSectionFooter(mTableView, viewType);
            return mTableView.newSectionFooterHolder(itemView);
        } else {
            UITableViewCell itemView = mTableViewDataSource.onCreateTableViewCell(mTableView, viewType);
            // mTableViewDataSource.onCreateTableViewCell(mTableView, viewType);
            if (itemView == null) {
                // itemView can not be null
                itemView = new UITableViewCell(mTableView.getContext());
            }
            setupItemView(itemView);
            // setup cell helper
            itemView.setCellHelper(mTableView.mTableViewCellHelper);
            return mTableView.newTableViewCellHolder(itemView);
        }
    }
    
    private void setupItemView(View itemView) {
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        UITableView.LayoutParams params = new UITableView.LayoutParams(width, height);
        itemView.setLayoutParams(params);
    }
    
    @Override
    public void onBindViewHolder(UITableViewBaseHolder holder, int position) {
        // Called by RecyclerView to display the data at the specified position.
        // This method should update the contents of the {@link RecyclerView.ViewHolder#itemView}
        // to reflect the item at the given * position.
        // Log.e(TAG, "onBindViewHolder: position = " + position);
        performBindViewHolder(holder, position, null);
    }
    
    @Override
    public void onBindViewHolder(UITableViewBaseHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
            return;
        }
        // Log.i(TAG, "onBindViewHolder(payload): position = " + position);
        
        performBindViewHolder(holder, position, payloads);
    }
    
    @SuppressWarnings("WeakerAccess")
    public void performBindViewHolder(UITableViewBaseHolder holder, int position, List<Object> payloads) {
        final int viewType = holder.getItemViewType();
        NSIndexPath indexPath = getIndexPath(position);
        if (indexPath == null) {
            holder.setNowIndexPath(null, RecyclerView.NO_POSITION);
            return;
        }
        if (isSectionHeaderIndex(indexPath)) {
            // configure the section header view
            configureSectionHeader(viewType, holder.itemView, indexPath.section, payloads);
        } else if (isSectionFooterIndex(indexPath)) {
            // configure the section footer view
            configureSectionFooter(viewType, holder.itemView, indexPath.section, payloads);
        } else {
            UITableViewCell tableViewCell = (UITableViewCell) holder.itemView;
            
            /// Configure the contents of cell
            if (payloads == null) {
                mTableViewDataSource.onBindTableViewCell(mTableView, tableViewCell, indexPath);
            } else {
                mTableViewDataSource.onBindTableViewCell(mTableView, tableViewCell, indexPath, payloads);
            }
            
            /// Editing
            if (mTableView.getEditingDelegate() != null) {
                configureEditingForCell(tableViewCell, indexPath, false);
            }
            
            /// Swiping
            if (mTableView.isSwipeActionEnabled() && mTableView.getSwipeDelegate() != null) {
                configureSwipeActionForCell(tableViewCell, indexPath);
            }
        }
        // Cache the indexPath
        holder.setNowIndexPath(indexPath, position);
    }
    
    private void configureSectionHeader(int viewType, View headerView, int section, List<Object> payloads) {
        if (viewType == SectionTitleView.VIEW_TYPE_HEADER) {
            // If the default HeaderView used, setup the title text
            CharSequence title = mTableViewDataSource.titleForHeaderInSection(mTableView, section);
            ((SectionTitleView) headerView).setTitleText(title);
        }
        final int height = mTableViewDataSource.heightForHeaderInSection(mTableView, section);
        if (height != UITableView.UNDEFINED) {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) headerView.getLayoutParams();
            if (params != null && height != params.height) {
                params.height = height;
                headerView.setLayoutParams(params);
            }
        }
        if (payloads == null) {
            mTableViewDataSource.onBindSectionHeader(mTableView, headerView, section);
        } else {
            mTableViewDataSource.onBindSectionHeader(mTableView, headerView, section, payloads);
        }
    }
    
    private void configureSectionFooter(int viewType, View footerView, int section, List<Object> payloads) {
        if (viewType == SectionTitleView.VIEW_TYPE_FOOTER) {
            CharSequence title = mTableViewDataSource.titleForFooterInSection(mTableView, section);
            ((SectionTitleView) footerView).setTitleText(title);
        }
        
        int height = mTableViewDataSource.heightForFooterInSection(mTableView, section);
        if (height != UITableView.UNDEFINED) {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) footerView.getLayoutParams();
            if (params != null && height != params.height) {
                params.height = height;
                footerView.setLayoutParams(params);
            }
        }
        if (payloads == null) {
            mTableViewDataSource.onBindSectionFooter(mTableView, footerView, section);
        } else {
            mTableViewDataSource.onBindSectionFooter(mTableView, footerView, section, payloads);
        }
    }
    
    public void configureEditingForCell(final UITableViewCell tableViewCell, final NSIndexPath indexPath, boolean animated) {
        if (mTableView.isEditing()) {
            UITableViewDelegate.EditingDelegate editingDelegate = mTableView.getEditingDelegate();
            boolean canEdit = editingDelegate.canEditForCell(mTableView, indexPath);
            tableViewCell.setEditing(canEdit, animated);
            if (canEdit) {
                if (mTableView.isAllowSelectionDuringEditing()) {
                    tableViewCell.setEditingForSelection();
                } else {
                    int editingStyle = editingDelegate.editingStyleForCell(mTableView, indexPath);
                    tableViewCell.setEditingStyle(editingStyle);
                }
            } else {
                tableViewCell.setEditing(false, animated);
            }
        } else {
            tableViewCell.setEditing(false, animated);
        }
    }
    
    private void configureSwipeActionForCell(final UITableViewCell tableViewCell, NSIndexPath indexPath) {
        UITableViewDelegate.SwipeDelegate swipeDelegate = mTableView.mSwipeDelegate;
        if (swipeDelegate.canSwipeForCell(mTableView, indexPath)) {
            tableViewCell.setSwipeActionEnabled(true);
            tableViewCell.configureLeadingSwipeActions(swipeDelegate.leadingSwipeActions(mTableView, indexPath));
            tableViewCell.configureTrailingSwipeActions(swipeDelegate.internalGetTrailingConfiguration(mTableView, indexPath));
            
        } else {
            tableViewCell.setSwipeActionEnabled(false);
        }
    }
    
    // ================================
    // MARK - Insert & Delete Cell
    // ================================
    
    void insertRowAtIndexPath(NSIndexPath indexPath) {
        if (indexPath == null) {
            return;
        }
        final int section = indexPath.section;
        if (section >= mSectionHolderList.size() || section < 0) {
            Log.w(TAG, "insertCellAtIndexPath: "
                    + String.format("Failed to insert cell at indexPath = %s (section size = %d)", indexPath.toString(), mSectionHolderList.size()));
            return;
        }
        
        final SectionHolder sectionHolder = mSectionHolderList.get(section);
        final int row = indexPath.row;
        if (row < 0 || row > sectionHolder.numOfRowsInSection) {
            Log.w(TAG, "insertCellAtIndexPath: "
                    + String.format("Failed to insert cell at indexPath = %s (row size = %d)", indexPath.toString(), sectionHolder.numOfRowsInSection));
            return;
        }
        sectionHolder.addRow();
        mItemCount += 1;
        
        for (int i = section + 1, size = mSectionHolderList.size(); i < size; i++) {
            SectionHolder holder = mSectionHolderList.get(i);
            holder.sectionStartPosition += 1;
        }
    }
    
    void deleteRowAtIndexPath(@NonNull NSIndexPath indexPath) {
        final int section = indexPath.section;
        if (section >= mSectionHolderList.size() || section < 0) {
            Log.w(TAG, "deleteRowAtIndexPath: "
                    + String.format("Failed to delete cell at indexPath = %s (section size = %d)", indexPath.toString(), mSectionHolderList.size()));
            return;
        }
        
        final SectionHolder sectionHolder = mSectionHolderList.get(section);
        final int row = indexPath.row;
        if (row < 0 || row >= sectionHolder.numOfRowsInSection) {
            Log.w(TAG, "deleteRowAtIndexPath: "
                    + String.format("Failed to delete cell at indexPath = %s (row size = %d)", indexPath.toString(), sectionHolder.numOfRowsInSection));
            return;
        }
        sectionHolder.deleteRow();
        mItemCount -= 1;
        if (mItemCount < 0) {
            mItemCount = 0;
        }
        for (int i = section + 1, size = mSectionHolderList.size(); i < size; i++) {
            SectionHolder holder = mSectionHolderList.get(i);
            holder.sectionStartPosition -= 1;
        }
    }
    
    // ==================================
    // MARK - IndexPath & Position
    // ==================================
    
    private boolean isHeaderViewType(int viewType) {
        for (SectionHolder section : mSectionHolderList) {
            if (viewType == section.headerModel.viewType) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isFooterViewType(int viewType) {
        for (SectionHolder section : mSectionHolderList) {
            if (viewType == section.footerModel.viewType) {
                return true;
            }
        }
        return false;
    }
    
    boolean isFirstCellInSection(NSIndexPath indexPath) {
        return indexPath.row == 0;
    }
    
    boolean isLastCellInSection(NSIndexPath indexPath) {
        SectionHolder sectionHolder = mSectionHolderList.get(indexPath.section);
        return indexPath.row == (sectionHolder.numOfRowsInSection - 1);
    }
    
    boolean isSectionHeaderIndex(NSIndexPath indexPath) {
        return indexPath.row == INDEXPATH_SECTION_HEADER;
    }
    
    boolean isSectionFooterIndex(NSIndexPath indexPath) {
        return indexPath.row == INDEXPATH_SECTION_FOOTER;
    }
    
    // boolean isIndexPathValid(NSIndexPath indexPath){
    //     if (indexPath == null || isSectionOutOfIndex(indexPath.section)){
    //         return false;
    //     }
    //     final SectionHolder sectionHolder = mSectionHolderList.get(indexPath.section);
    //     final int row = indexPath.row;
    //     if (row >= sectionHolder.numOfRowsInSection) {
    //         return false;
    //     }
    //     return true;
    // }
    
    /**
     * 根据Adapter Position转换为IndexPath
     *
     * @return 如果是section header, indexPath.row = -1,
     * 如果是section footer, indexPath.row = -2
     */
    NSIndexPath getIndexPath(int position) {
        if (position == RecyclerView.NO_POSITION) {
            return null;
        }
        final int numOfSections = mSectionHolderList.size();
        for (int s = 0; s < numOfSections; s++) {
            final SectionHolder sectionHolder = mSectionHolderList.get(s);
            
            if (position == sectionHolder.headerPosition()) {
                return new NSIndexPath(s, INDEXPATH_SECTION_HEADER);
            } else if (position == sectionHolder.footerPosition()) {
                return new NSIndexPath(s, INDEXPATH_SECTION_FOOTER);
            }
            
            final int sectionStartPosition = sectionHolder.sectionStartPosition;
            final int numberOfAllRowsInSection = sectionHolder.getAllRowsInSection();
            final int limit = sectionStartPosition + numberOfAllRowsInSection;
            if (position < limit) {
                int row;
                if (sectionHolder.hasHeader()) {
                    row = position - sectionStartPosition - 1;
                } else {
                    row = position - sectionStartPosition;
                }
                return new NSIndexPath(s, row);
            }
        }
        return null;
    }
    
    int indexPathToPosition(NSIndexPath indexPath) {
        if (indexPath == null) {
            return RecyclerView.NO_POSITION;
        }
        
        if (indexPath.section > (mSectionHolderList.size() - 1)) {
            return RecyclerView.NO_POSITION;
        }
        
        final SectionHolder locateSection = mSectionHolderList.get(indexPath.section);
        if (indexPath.row > (locateSection.numOfRowsInSection - 1)) {
            return RecyclerView.NO_POSITION;
        }
        
        return locateSection.positionForRow(indexPath.row);
    }
    
    int getPositionToInsert(NSIndexPath indexPath) {
        if (indexPath == null) {
            return RecyclerView.NO_POSITION;
        }
        
        if (indexPath.section > (mSectionHolderList.size() - 1)) {
            return RecyclerView.NO_POSITION;
        }
        
        final SectionHolder locateSection = mSectionHolderList.get(indexPath.section);
        if (indexPath.row > locateSection.numOfRowsInSection) {
            return RecyclerView.NO_POSITION;
        }
        
        return locateSection.positionForRow(indexPath.row);
    }
    
    // =====================================
    // MARK - Convenience
    // =====================================
    
    private boolean isSectionOutOfIndex(int section) {
        return section < 0 || section >= mSectionHolderList.size();
    }
    
    //---------------------------------------------------------------
    //              MARK: Inner Class
    //---------------------------------------------------------------
    
    /**
     * 记录分组Section Header／Footer属性
     */
    class HeaderFooterModel {
        // 标记Header／Footer是否存在
        private boolean mEnabled = false;
        private int mPosition = RecyclerView.NO_POSITION;
        
        private int viewType = -1;
        public int height = UITableView.UNDEFINED;
        //public CharSequence title = null;
        
        HeaderFooterModel() {
        }
        
        boolean isEnabled() {
            return mEnabled;
        }
        
        void setEnabled(boolean enabled) {
            mEnabled = enabled;
        }
        
        public int getPosition() {
            return mPosition;
        }
        
        void setPosition(int position) {
            mPosition = position;
        }
        
        void setViewType(int viewType) {
            this.viewType = viewType;
        }
    } // HeaderFooterModel.Class
    
    
    /**
     * 记录每个分组信息
     */
    class SectionHolder {
        // cell的个数
        int numOfRowsInSection = 0;
        // 当前分组在Adapter中的开始position
        int sectionStartPosition;
        HeaderFooterModel headerModel;
        HeaderFooterModel footerModel;
        
        SectionHolder() {
            headerModel = new HeaderFooterModel();
            footerModel = new HeaderFooterModel();
        }
        
        boolean hasHeader() {
            return headerModel.isEnabled();
        }
        
        boolean hasFooter() {
            return footerModel.isEnabled();
        }
        
        // cell + header + footer
        int getAllRowsInSection() {
            int count = numOfRowsInSection;
            if (hasHeader())
                count += 1;
            if (hasFooter())
                count += 1;
            return count;
        }
        
        int positionForRow(int row) {
            if (hasHeader()) {
                return sectionStartPosition + 1 + row;
            } else {
                return sectionStartPosition + row;
            }
        }
        
        int headerPosition() {
            return headerModel.isEnabled() ? sectionStartPosition : UITableView.NO_POSITION;
        }
        
        int footerPosition() {
            return footerModel.isEnabled() ? footerModel.getPosition() : UITableView.NO_POSITION;
        }
        
        void deleteRow() {
            numOfRowsInSection -= 1;
            if (numOfRowsInSection < 0) {
                numOfRowsInSection = 0;
            }
        }
        
        void addRow() {
            numOfRowsInSection += 1;
        }
    }
}
