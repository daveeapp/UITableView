package app.davee.assistant.uitableview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import app.davee.assistant.uitableview.models.UITableViewModel;
import app.davee.assistant.uitableview.swipe.SwipeAction;

import java.util.ArrayList;

/**
 * UITableView
 * Features :
 * 1) SwipeOptionCell
 * 2) RefreshListener
 * 3) Editing Mode
 * 4) Bounce
 * <p>
 * 性能优化：
 * 1）尽量使用代码布局，xml布局效率相对于代码更低，而且findViewById是潜在的耗时的操作
 * 2）尽量使用使用相同的布局
 * 3）尽量减少布局层次，复杂的布局使用ConstraintLayout（不要过度依赖，一般简单布局RelativeLayout性能更高）
 * <p>
 * Created by davee 2018/3/3.
 * Copyright (c) 2018 davee. All rights reserved.
 */

@SuppressWarnings("unused")
public class UITableView extends RecyclerView {
    
    private static final String TAG = "UITableView";
    private static final boolean DEBUG = BuildConfig.DEBUG;
    
    public static final int UNDEFINED = -7;
    
    public static final int DIRECTION_SCROLL_DOWN = 1;
    public static final int DIRECTION_SCROLL_UP = -1;
    
    public @interface SelectionMode {
        int None = 0;
        int Single = 1;
        int Multiple = 2;
    }
    
    @SelectionMode
    private int mSelectionMode = SelectionMode.None;
    private NSIndexPath mLastSelectedIndexPath;
    private ArrayList<NSIndexPath> mSelectedIndexPathArray;
    
    /// Fields
    
    UITableViewAdapter mTableViewAdapter;
    UITableViewDataSource mTableViewDataSource;
    UITableViewDelegate mTableViewDelegate;
    
    final TableViewCellHelper mTableViewCellHelper = new TableViewCellHelper();
    
    boolean mSectionHeaderSeparatorEnable = true;
    boolean mSectionFooterSeparatorEnable = true;
    
    private int mDefaultBackgroundColor = 0;
    private int mContentTopOffset = 0; // px, default 0
    private int mContentBottomOffset = 0; // px, default 0
    
    /// Separator
    
    /**
     * If true, will draw the separator on cell canvas, else will use SeparatorDecoration
     */
    // boolean mDrawSeparatorOnCell = false;
    int mSeparatorColor = Color.DKGRAY;
    float mSeparatorHeight = 0.5f;
    SeparatorDecoration mSeparatorDecoration;
    ItemDecoration mUserSeparatorDecoration;
    
    /// Editing
    
    private boolean mEditing = false;
    private boolean mAllowUserInteractionDuringEditing = false;
    // private boolean mAllowSelectionDuringEditing = false; // allow single selection
    // private boolean mAllowMultiSelectionDuringEditing = false; //  allow multiple selection
    private int mEditingSelectionMode = SelectionMode.None;
    private UITableViewDelegate.EditingDelegate mEditingDelegate = null;
    
    /// Swiping
    
    private boolean mSwipeActionEnabled = false;
    private OnItemSwipingListener mOnItemSwipingListener = null;
    UITableViewDelegate.SwipeDelegate mSwipeDelegate;
    
    private int mStateMask = UITableViewCell.StateMask.Normal;
    
    public UITableView(Context context) {
        super(context);
        
        initWithAttrs(null, 0);
    }
    
    public UITableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        
        initWithAttrs(attrs, 0);
    }
    
    public UITableView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        initWithAttrs(attrs, defStyle);
    }
    
    private void initWithAttrs(@Nullable AttributeSet attrs, int defStyle) {
        mContentTopOffset = mContentBottomOffset = DimensionUtils.dp2px(getContext(), 16);
        this.addItemDecoration(new InternalDecoration());
        
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.setLayoutManager(linearLayoutManager);
        
        mSeparatorDecoration = new SeparatorDecoration();
        this.addItemDecoration(mSeparatorDecoration);
        
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.UITableView, defStyle, 0);
            if (!typedArray.hasValue(R.styleable.UITableView_android_background)) {
                mDefaultBackgroundColor = ContextCompat.getColor(getContext(), R.color.uitableview_color_background);
            }
            final int count = typedArray.getIndexCount();
            for (int i = 0; i < count; i++) {
                final int attr = typedArray.getIndex(i);
                if (attr == R.styleable.UITableView_uitableview_contentStartOffset) {
                    mContentTopOffset = typedArray.getDimensionPixelOffset(attr, mContentTopOffset);
                } else if (attr == R.styleable.UITableView_uitableview_contentEndOffset) {
                    mContentBottomOffset = typedArray.getDimensionPixelOffset(attr, mContentBottomOffset);
                }
            }
            typedArray.recycle();
        } else {
            mDefaultBackgroundColor = ContextCompat.getColor(getContext(), R.color.uitableview_color_background);
        }
        
        if (mDefaultBackgroundColor != 0) {
            setBackgroundColor(mDefaultBackgroundColor);
        }
        
        if (isInEditMode()) {
            this.setTableViewDataSource(new PreviewDataSource());
        }
    }
    
    @Override
    protected void onAttachedToWindow() {
        if (DEBUG) {
            Log.d(TAG, "onAttachedToWindow: ");
        }
        if (mTableViewAdapter == null && mTableViewDataSource != null) {
            mTableViewAdapter = new UITableViewAdapter(this);
            super.setAdapter(mTableViewAdapter);
        }
        super.onAttachedToWindow();
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
    
    @Override
    public void onChildAttachedToWindow(View child) {
        super.onChildAttachedToWindow(child);
        
        resumeCellSelectionStyle(child);
    }
    
    private void resumeCellSelectionStyle(View child) {
        final int selectionMode = getCurrentSelectionMode();
        if (selectionMode == SelectionMode.None) {
            return;
        }
        if (!(child instanceof UITableViewCell)) {
            return;
        }
        if (selectionMode == SelectionMode.Single) {
            if (mLastSelectedIndexPath != null) {
                final NSIndexPath indexPath = indexPathForCell(child);
                child.setSelected(mLastSelectedIndexPath.equals(indexPath));
            }
        } else if (selectionMode == SelectionMode.Multiple) {
            if (mSelectedIndexPathArray != null && mSelectedIndexPathArray.size() > 0) {
                final NSIndexPath indexPath = indexPathForCell(child);
                child.setSelected(mSelectedIndexPathArray.contains(indexPath));
            }
        }
    }
    
    /**
     * 设置RecyclerView从底部开始显示
     *
     * @param stackFromEnd true, 从底部开始显示
     */
    public void setStackFromEnd(boolean stackFromEnd) {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getLayoutManager();
        linearLayoutManager.setStackFromEnd(stackFromEnd);
    }
    
    public int getContentTopOffset() {
        return mContentTopOffset;
    }
    
    public void setContentTopOffset(int contentTopOffset) {
        if (mContentTopOffset != contentTopOffset) {
            mContentTopOffset = contentTopOffset;
            invalidate();
        }
    }
    
    public int getContentBottomOffset() {
        return mContentBottomOffset;
    }
    
    public void setContentBottomOffset(int contentBottomOffset) {
        if (mContentBottomOffset != contentBottomOffset) {
            mContentBottomOffset = contentBottomOffset;
            invalidate();
        }
    }
    
    // =====================================
    // MARK - Separator
    // =====================================
    
    public boolean isSeparatorEnable() {
        return mSeparatorDecoration != null && mSeparatorDecoration.isSeparatorEnable();
    }
    
    public void setSeparatorEnable(boolean enable) {
        if (mSeparatorDecoration != null) {
            mSeparatorDecoration.setSeparatorEnable(enable);
        }
    }
    
    // public boolean isDrawSeparatorOnCell() {
    //     return mDrawSeparatorOnCell;
    // }
    //
    // public void setDrawSeparatorOnCell(boolean drawSeparatorOnCell) {
    //     if (mDrawSeparatorOnCell != drawSeparatorOnCell) {
    //         mDrawSeparatorOnCell = drawSeparatorOnCell;
    //         if (mDrawSeparatorOnCell) {
    //             removeDefaultSeparatorDecoration();
    //         } else {
    //             setDefaultSeparatorDecoration(new SeparatorDecoration());
    //         }
    //     }
    // }
    
    public void setDefaultSeparatorDecoration(SeparatorDecoration separatorDecoration) {
        if (mSeparatorDecoration != null) {
            this.removeItemDecoration(mSeparatorDecoration);
            mSeparatorDecoration = null;
        }
        mSeparatorDecoration = separatorDecoration;
        if (mSeparatorDecoration != null) {
            this.addItemDecoration(mSeparatorDecoration);
            requestLayout();
        }
    }
    
    public void removeDefaultSeparatorDecoration() {
        if (mSeparatorDecoration != null) {
            this.removeItemDecoration(mSeparatorDecoration);
            mSeparatorDecoration = null;
        }
    }
    
    public void setDefaultSeparatorColor(int color) {
        if (mSeparatorColor != color) {
            mSeparatorColor = color;
            if (mSeparatorDecoration != null) {
                mSeparatorDecoration.setCommonSeparatorColor(color);
            }
        }
    }
    
    public void setDefaultSeparatorHeight(float height) {
        if (mSeparatorHeight != height) {
            mSeparatorHeight = height;
            if (mSeparatorDecoration != null) {
                mSeparatorDecoration.setCommonSeparatorHeight(height);
            }
        }
    }
    
    public ItemDecoration getUserSeparatorDecoration() {
        return mUserSeparatorDecoration;
    }
    
    public void setUserSeparatorDecoration(ItemDecoration separatorDecoration) {
        // Remove the old if has
        if (mUserSeparatorDecoration != null) {
            this.removeItemDecoration(mUserSeparatorDecoration);
            mUserSeparatorDecoration = null;
        }
        mUserSeparatorDecoration = separatorDecoration;
        if (mUserSeparatorDecoration != null) {
            this.addItemDecoration(mUserSeparatorDecoration);
            requestLayout();
        }
    }
    
    public boolean isSectionHeaderSeparatorEnable() {
        return mSectionHeaderSeparatorEnable;
    }
    
    public void setSectionHeaderSeparatorEnable(boolean sectionHeaderSeparatorEnable) {
        mSectionHeaderSeparatorEnable = sectionHeaderSeparatorEnable;
    }
    
    public boolean isSectionFooterSeparatorEnable() {
        return mSectionFooterSeparatorEnable;
    }
    
    public void setSectionFooterSeparatorEnable(boolean sectionFooterSeparatorEnable) {
        mSectionFooterSeparatorEnable = sectionFooterSeparatorEnable;
    }
    
    // =====================================
    // MARK - DataSource & Delegate
    // =====================================
    
    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
    }
    
    public UITableViewAdapter getTableViewAdapter() {
        return mTableViewAdapter;
    }
    
    public UITableViewDataSource getTableViewDataSource() {
        return mTableViewDataSource;
    }
    
    public void setTableViewDataSource(UITableViewDataSource tableViewDataSource) {
        if (mTableViewDataSource != tableViewDataSource) {
            mTableViewDataSource = tableViewDataSource;
            // todo: mTableViewAdapter.updateSectionCache
            if (isAttachedToWindow()) {
                if (mTableViewAdapter == null && mTableViewDataSource != null) {
                    mTableViewAdapter = new UITableViewAdapter(this);
                    super.setAdapter(mTableViewAdapter);
                } else if (mTableViewAdapter != null && mTableViewDataSource == null) {
                    mTableViewAdapter = null;
                    super.setAdapter(null);
                }
            }
        }
    }
    
    @Nullable
    public UITableViewDelegate getTableViewDelegate() {
        return mTableViewDelegate;
    }
    
    public void setTableViewDelegate(@Nullable UITableViewDelegate tableViewDelegate) {
        mTableViewDelegate = tableViewDelegate;
    }
    
    /// Edit Delegate
    
    public UITableViewDelegate.EditingDelegate getEditingDelegate() {
        return mEditingDelegate;
    }
    
    public void setEditingDelegate(UITableViewDelegate.EditingDelegate editingDelegate) {
        mEditingDelegate = editingDelegate;
    }
    
    /// Swipe Delegate
    
    @Nullable
    public UITableViewDelegate.SwipeDelegate getSwipeDelegate() {
        return mSwipeDelegate;
    }
    
    public void setSwipeDelegate(@Nullable UITableViewDelegate.SwipeDelegate swipeDelegate) {
        mSwipeDelegate = swipeDelegate;
    }
    
    void setStateMask(int stateMask) {
        if (mStateMask != stateMask) {
            mStateMask = stateMask;
            
            if (BuildConfig.DEBUG) {
                if (mStateMask == UITableViewCell.StateMask.ShowingSwipeAction) {
                    Log.i(TAG, "setStateMask: Showing Swipe Action");
                } else if (mStateMask == UITableViewCell.StateMask.ShowingEditingControl) {
                    Log.i(TAG, "setStateMask: Showing Editing Control");
                } else {
                    Log.i(TAG, "setStateMask: Transition to state normal");
                }
            }
            
        }
    }
    
    // =====================================
    // MARK - Editing
    // =====================================
    
    @SelectionMode
    public int getEditingSelectionMode() {
        return mEditingSelectionMode;
    }
    
    public void setEditingSelectionMode(@SelectionMode int editingSelectionMode) {
        if (mEditingSelectionMode != editingSelectionMode) {
            mEditingSelectionMode = editingSelectionMode;
            if (mEditingSelectionMode == SelectionMode.Multiple) {
                ensureSelectedIndexPathArray();
            } else {
                clearMultipleSelectedCells();
                mSelectedIndexPathArray = null;
            }
        }
    }
    
    public boolean isAllowUserInteractionDuringEditing() {
        return mAllowUserInteractionDuringEditing;
    }
    
    public void setAllowUserInteractionDuringEditing(boolean allowUserInteractionDuringEditing) {
        mAllowUserInteractionDuringEditing = allowUserInteractionDuringEditing;
    }
    
    public boolean isEditing() {
        return mEditing;
    }
    
    public void setEditing(boolean editing) {
        if (mStateMask == UITableViewCell.StateMask.ShowingSwipeAction) {
            Log.w(TAG, "setEditing failed: Swipe action is showing now.");
            return;
        }
        if (mEditing && mEditingDelegate == null) {
            Log.w(TAG, "setEditing failed: Editing delegate has not setup.");
            return;
        }
        if (mEditing != editing) {
            mEditing = editing;
            onEditingStateChanged();
            updateEditingState();
            // reloadEnhanced(false, new HolderBindOption(HolderBindOption.UPDATE_EDITING));
        }
    }
    
    private void onEditingStateChanged() {
        // Clear all selected cells if has
        if (mEditingSelectionMode != SelectionMode.None) {
            clearAllSelectedCells();
        }
        if (mEditing) {
            setStateMask(UITableViewCell.StateMask.ShowingEditingControl);
            mEditingDelegate.willBeginEditing(this);
        } else {
            setStateMask(UITableViewCell.StateMask.Normal);
            mEditingDelegate.willEndEditing(this);
        }
    }
    
    private void updateEditingState() {
        final int count = getChildCount();
        if (count > 0 && mTableViewAdapter != null) {
            int positionStart = getChildAdapterPosition(getChildAt(0));
            positionStart = Math.max(0, positionStart - 3);
            int positionEnd = Math.min(mTableViewAdapter.getItemCount(), positionStart + 3 + count);
            // Log.e(TAG, "updateEditingState: positionStart = " + positionStart +", positionEnd = " + positionEnd);
            for (int i = positionStart; i <= positionEnd; i++) {
                // Log.d(TAG, "updateEditingState: position = " + i);
                UITableViewBaseHolder viewHolder = (UITableViewBaseHolder) findViewHolderForAdapterPosition(i);
                NSIndexPath indexPath = mTableViewAdapter.getIndexPath(i);
                if (viewHolder != null) {
                    if (indexPath.row >= 0) {
                        UITableViewCell cell = (UITableViewCell) viewHolder.itemView;
                        mTableViewAdapter.configureEditingForCell(cell, indexPath, true);
                    }
                    // viewHolder.setNowIndexPath(indexPath);
                } else {
                    // Log.d(TAG, "updateEditingState: viewHolder = null");
                    // if viewHolder is null, we should create a new one
                    mTableViewAdapter.notifyItemChanged(i);
                }
            }
        }
    }
    
    boolean isAllowSelectionDuringEditing() {
        return mEditingSelectionMode != SelectionMode.None;
    }
    
    // =====================================
    // MARK - Swiping
    // =====================================
    
    boolean canBeSwiped() {
        return mStateMask != UITableViewCell.StateMask.ShowingEditingControl;
    }
    
    void didStartSwipingCell(UITableViewCell cell) {
        setStateMask(UITableViewCell.StateMask.ShowingSwipeAction);
        if (mSwipeDelegate != null) {
            mSwipeDelegate.willBeginSwiping(this, indexPathForCell(cell));
        }
    }
    
    void didEndSwipingCell(UITableViewCell cell) {
        if (mSwipeDelegate != null) {
            mSwipeDelegate.didEndSwiping(this, indexPathForCell(cell));
        }
    }
    
    void onSwipingStopped() {
        setStateMask(UITableViewCell.StateMask.Normal);
    }
    
    void performSwipeAction(@NonNull SwipeAction swipeAction, UITableViewCell tableViewCell) {
        NSIndexPath indexPath = indexPathForCell(tableViewCell);
        if (mSwipeDelegate != null && indexPath != null) {
            if (swipeAction.getActionId() == R.id.uitableview_cell_swipe_action_delete) {
                mSwipeDelegate.didSelectDeleteSwipeAction(this, indexPath);
            } else {
                mSwipeDelegate.didSelectSwipeAction(this, swipeAction, indexPath);
            }
        }
    }
    
    public boolean isSwipeActionEnabled() {
        return mSwipeActionEnabled;
    }
    
    public void setSwipeActionEnabled(boolean swipeActionEnabled) {
        if (mSwipeActionEnabled != swipeActionEnabled) {
            mSwipeActionEnabled = swipeActionEnabled;
            if (mSwipeActionEnabled) {
                if (mOnItemSwipingListener == null) {
                    mOnItemSwipingListener = new OnItemSwipingListener(this);
                }
                this.addOnItemTouchListener(mOnItemSwipingListener);
            } else {
                if (mOnItemSwipingListener != null) {
                    this.removeOnItemTouchListener(mOnItemSwipingListener);
                    mOnItemSwipingListener = null;
                }
            }
        }
    }
    
    public void closeSwipedCell() {
        if (mOnItemSwipingListener != null) {
            mOnItemSwipingListener.closeLastSwipedCell();
        }
    }
    
    public void setDisallowParentInterceptWhenSwiping(boolean needsDisallow) {
        if (mOnItemSwipingListener != null) {
            mOnItemSwipingListener.setNeedsDisallowParentIntercept(needsDisallow);
        }
    }
    
    // =====================================
    // MARK - Click & Select Events
    // =====================================
    
    void onCellViewClicked(@NonNull UITableViewCell child, NSIndexPath indexPath) {
        if (DEBUG) {
            Log.d(TAG, "onCellViewClicked: position = " + getChildAdapterPosition(child));
        }
        if (mEditing) {
            if (!performSelectCellDuringEditing(child, indexPath) && mAllowUserInteractionDuringEditing) {
                performClickCell(child, indexPath);
            }
        } else {
            if (!performSelectCell(child, indexPath)) {
                performClickCell(child, indexPath);
            }
        }
    }
    
    private void performClickCell(UITableViewCell cell, NSIndexPath indexPath) {
        if (mTableViewDelegate != null) {
            // final NSIndexPath indexPath = indexPathForCell(cell);
            if (indexPath != null) {
                mTableViewDelegate.onTableViewCellClicked(this, cell, indexPath);
            }
        }
    }
    
    boolean onCellViewLongClicked(UITableViewCell child, @NonNull NSIndexPath indexPath) {
        if (DEBUG) {
            Log.d(TAG, "onCellViewLongClicked: position = " + getChildAdapterPosition(child));
        }
        return mTableViewDelegate != null && mTableViewDelegate.onTableViewCellLongClicked(this, child, indexPath);
    }
    
    // =====================================
    // MARK - Perform select cell
    // =====================================
    
    private int getCurrentSelectionMode() {
        return mEditing ? mEditingSelectionMode : mSelectionMode;
    }
    
    public int getSelectionMode() {
        return mSelectionMode;
    }
    
    public void setSelectionMode(@SelectionMode int selectionMode) {
        if (mSelectionMode != selectionMode) {
            mSelectionMode = selectionMode;
            if (mSelectionMode == SelectionMode.Multiple) {
                ensureSelectedIndexPathArray();
            } else {
                clearMultipleSelectedCells();
                mSelectedIndexPathArray = null;
            }
        }
    }
    
    public NSIndexPath getLastSelectedIndexPath() {
        return mLastSelectedIndexPath;
    }
    
    public ArrayList<NSIndexPath> getSelectedIndexPathArray() {
        return mSelectedIndexPathArray;
    }
    
    private void ensureSelectedIndexPathArray() {
        if (mSelectedIndexPathArray == null) {
            mSelectedIndexPathArray = new ArrayList<>();
        }
    }
    
    boolean performSelectCell(UITableViewCell cell, NSIndexPath indexPath) {
        if (mSelectionMode == SelectionMode.None) {
            return false;
        }
        
        if (mTableViewDelegate != null && !mTableViewDelegate.shouldSelectCellAtIndexPath(this, cell, indexPath)) {
            return false;
        }
        
        /* if mSelectionMode != SelectionStyleNone, perform select action */
        // final NSIndexPath indexPath = indexPathForCell(cell);
        // if (indexPath == null) {
        //     return true;
        // }
        
        if (mSelectionMode == SelectionMode.Single) {
            if (!cell.isSelected()) {
                if (mLastSelectedIndexPath != null) {
                    deselectCellAtIndexPath(mLastSelectedIndexPath);
                }
                selectCellInternal(cell, indexPath, true);
            }
        } else if (mSelectionMode == SelectionMode.Multiple) {
            if (cell.isSelected()) {
                deselectCellInternal(cell, indexPath, true);
            } else {
                selectCellInternal(cell, indexPath, true);
            }
        }
        return true;
    }
    
    boolean performSelectCellDuringEditing(UITableViewCell cell, NSIndexPath indexPath) {
        
        if (mTableViewDelegate != null && !mTableViewDelegate.shouldSelectCellAtIndexPath(this, cell, indexPath)) {
            return false;
        }
        
        if (mEditingSelectionMode == SelectionMode.Multiple) {
            // final NSIndexPath indexPath = indexPathForCell(cell);
            if (indexPath != null) {
                if (cell.isSelected()) {
                    deselectCellInternal(cell, indexPath, true);
                } else {
                    selectCellInternal(cell, indexPath, true);
                }
            }
        } else if (mEditingSelectionMode == SelectionMode.Single) {
            // final NSIndexPath indexPath = indexPathForCell(cell);
            if (indexPath != null) {
                if (!cell.isSelected()) {
                    if (mLastSelectedIndexPath != null) {
                        deselectCellAtIndexPath(mLastSelectedIndexPath);
                    }
                    selectCellInternal(cell, indexPath, true);
                } else {
                    deselectCellInternal(cell, indexPath, true);
                }
            }
        }
        
        return true;
    }
    
    private void selectCellInternal(@NonNull UITableViewCell cell, @NonNull NSIndexPath indexPath, boolean notify) {
        cell.setSelected(true);
        if (mTableViewDelegate != null && notify) {
            mTableViewDelegate.onTableViewCellSelected(this, cell, indexPath);
        }
        
        final int selectionMode = getCurrentSelectionMode();
        if (selectionMode == SelectionMode.Single) {
            mLastSelectedIndexPath = indexPath;
        } else if (selectionMode == SelectionMode.Multiple) {
            mSelectedIndexPathArray.add(indexPath);
        }
    }
    
    private void deselectCellInternal(@NonNull UITableViewCell cell, @NonNull NSIndexPath indexPath, boolean notify) {
        cell.setSelected(false);
        if (mTableViewDelegate != null && notify) {
            mTableViewDelegate.onTableViewCellDeselected(this, cell, indexPath);
        }
        
        final int selectionMode = getCurrentSelectionMode();
        if (selectionMode == SelectionMode.Single) {
            mLastSelectedIndexPath = null;
        } else if (selectionMode == SelectionMode.Multiple) {
            mSelectedIndexPathArray.remove(indexPath);
        }
    }
    
    // 这个方法用来在UITableView还没加载的时候，提前设置选项，不会触发 onTableViewCellSelected 方法
    public void presetSelectedCell(@NonNull NSIndexPath indexPath) {
        if (getCurrentSelectionMode() == SelectionMode.Single) {
            mLastSelectedIndexPath = indexPath;
        }
    }
    
    public void presetSelectedCells(@NonNull ArrayList<NSIndexPath> indexPaths) {
        if (getCurrentSelectionMode() == SelectionMode.Multiple) {
            clearMultipleSelectedCells();
            mSelectedIndexPathArray.addAll(indexPaths);
        }
    }
    
    /**
     * Select a cell.
     * This method will not call the delegate methods {@link UITableViewDelegate#onTableViewCellSelected(UITableView, UITableViewCell, NSIndexPath)}.
     *
     * @param indexPath indexPath for specified cell
     */
    public void selectCellAtIndexPath(@NonNull NSIndexPath indexPath) {
        if (getCurrentSelectionMode() != SelectionMode.None) {
            final UITableViewCell cell = cellForRowAtIndexPath(indexPath);
            if (cell != null && !cell.isSelected()) {
                selectCellInternal(cell, indexPath, false);
            }
        }
    }
    
    /**
     * Deselect a cell.
     * This method will not call the delegate methods {@link UITableViewDelegate#onTableViewCellDeselected(UITableView, UITableViewCell, NSIndexPath)}.
     *
     * @param indexPath indexPath for specified cell
     */
    public void deselectCellAtIndexPath(@NonNull NSIndexPath indexPath) {
        if (getCurrentSelectionMode() != SelectionMode.None) {
            final UITableViewCell cell = cellForRowAtIndexPath(indexPath);
            if (cell != null && cell.isSelected()) {
                deselectCellInternal(cell, indexPath, false);
            }
        }
        
    }
    
    public void selectCellsAtIndexPaths(@NonNull ArrayList<NSIndexPath> indexPaths) {
        if (getCurrentSelectionMode() == SelectionMode.Multiple) {
            clearMultipleSelectedCells();
            for (NSIndexPath indexPath : indexPaths) {
                selectCellAtIndexPath(indexPath);
            }
        }
    }
    
    public void deselectCellsAtIndexPaths(@NonNull ArrayList<NSIndexPath> indexPaths) {
        if (getCurrentSelectionMode() == SelectionMode.Multiple) {
            for (NSIndexPath indexPath : indexPaths) {
                deselectCellAtIndexPath(indexPath);
            }
        }
    }
    
    /**
     * Deselect all selected cells. This method will not call the delegate method onTableViewCellDeselected.
     */
    public void clearMultipleSelectedCells() {
        if (mSelectedIndexPathArray != null && mSelectedIndexPathArray.size() > 0) {
            for (NSIndexPath indexPath : mSelectedIndexPathArray) {
                UITableViewCell cell = cellForRowAtIndexPath(indexPath);
                if (cell != null) {
                    cell.setSelected(false);
                }
            }
            mSelectedIndexPathArray.clear();
        }
    }
    
    public void clearAllSelectedCells() {
        if (mLastSelectedIndexPath != null) {
            deselectCellAtIndexPath(mLastSelectedIndexPath);
            mLastSelectedIndexPath = null;
        }
        clearMultipleSelectedCells();
    }
    
    // =====================================
    // MARK - Reload
    // =====================================
    
    private void configureAnimator(boolean animated) {
        if (getItemAnimator() != null) {
            ((SimpleItemAnimator) getItemAnimator()).setSupportsChangeAnimations(animated);
        }
    }
    
    /// Reload All
    
    /**
     * Update all item,
     *
     * @see UITableView#reload(boolean, boolean)
     */
    public void reload() {
        reload(true);
    }
    
    public void reload(boolean structuralChanged) {
        reload(structuralChanged, true);
    }
    
    /**
     * Reload all rows
     *
     * @param structuralChanged if has position changes，must pass true
     * @param animated          whether enable animation
     */
    public void reload(boolean structuralChanged, boolean animated) {
        if (mTableViewAdapter != null) {
            configureAnimator(animated);
            mTableViewAdapter.reloadAll(structuralChanged);
        }
    }
    
    /// Reload Current Children
    
    public void reloadEnhanced(boolean animated) {
        reloadEnhanced(null, animated);
    }
    
    /**
     * 只更新当前显示的Cell，而非全部，这样可以提高性能，因为没有显示的部分，当滚动到显示的时候，也会触发onBindViewHolder方法
     *
     * @param animated 是否使用动画
     */
    public void reloadEnhanced(Object payload, boolean animated) {
        if (mTableViewAdapter != null) {
            int count = getChildCount();
            if (count > 0) {
                int positionStart = getChildAdapterPosition(getChildAt(0));
                positionStart = Math.max(0, positionStart - 3);
                count = Math.min(mTableViewAdapter.getItemCount(), count);
                configureAnimator(animated);
                mTableViewAdapter.notifyItemRangeChanged(positionStart, count, payload);
            }
        }
    }
    
    /// Reload Cell
    
    public void reloadRowAtIndexPath(@NonNull NSIndexPath indexPath) {
        reloadRowAtIndexPath(indexPath, null);
    }
    
    public void reloadRowAtIndexPath(@NonNull NSIndexPath indexPath, Object payload) {
        reloadRowAtIndexPath(indexPath, payload, true);
    }
    
    public void reloadRowAtIndexPath(@NonNull NSIndexPath indexPath, boolean animated) {
        reloadRowAtIndexPath(indexPath, null, animated);
    }
    
    public void reloadRowAtIndexPath(@NonNull NSIndexPath indexPath, Object payload, boolean animated) {
        if (mTableViewAdapter != null) {
            configureAnimator(animated);
            final int position = mTableViewAdapter.indexPathToPosition(indexPath);
            mTableViewAdapter.notifyItemChanged(position, payload);
        }
    }
    
    /// Reload Cells In Specified IndexPaths
    
    public void reloadRowsAtIndexPaths(@NonNull ArrayList<NSIndexPath> indexPaths){
        this.reloadRowsAtIndexPaths(indexPaths, true);
    }
    
    public void reloadRowsAtIndexPaths(@NonNull ArrayList<NSIndexPath> indexPaths, boolean animated){
        if (mTableViewAdapter != null && !indexPaths.isEmpty()){
            configureAnimator(animated);
            for (NSIndexPath indexPath : indexPaths) {
                this.reloadRowAtIndexPath(indexPath, animated);
            }
        }
    }
    
    /// Reload Section
    
    public void reloadSection(int section) {
        this.reloadSection(section, true);
    }
    
    public void reloadSection(int section, boolean animated) {
        this.reloadSection(section, null, animated);
    }
    
    public void reloadSection(int section, Object payload, boolean animated){
        if (mTableViewAdapter != null){
            configureAnimator(animated);
            mTableViewAdapter.notifySectionChanged(section, payload);
        }
    }
    
    /// Reload Cells In Range
    
    public void reloadRowsInRange(@NonNull NSIndexPath startIndexPath, int count){
        this.reloadRowsInRange(startIndexPath, count, null, true);
    }
    
    public void reloadRowsInRange(@NonNull NSIndexPath startIndexPath, int count, Object payload, boolean animated){
        if (mTableViewAdapter != null){
            final int position = mTableViewAdapter.indexPathToPosition(startIndexPath);
            reloadRowsInRange(position, count, payload, animated);
        }
    }
    
    public void reloadRowsInRange(int positionStart, int itemCount, Object payload, boolean animated) {
        if (mTableViewAdapter != null) {
            configureAnimator(animated);
            mTableViewAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }
    }
    
    /* Delete Row */
    
    public void deleteRow(View child, boolean animated) {
        if (child != null) {
            int adapterPosition = getChildAdapterPosition(child);
            deleteRowAtPosition(adapterPosition, animated);
        }
    }
    
    public void deleteRowAtIndexPath(@NonNull NSIndexPath indexPath){
        this.deleteRowAtIndexPath(indexPath, true);
    }
    
    public void deleteRowAtIndexPath(@NonNull NSIndexPath indexPath, boolean animated) {
        final int position = mTableViewAdapter.indexPathToPosition(indexPath);
        this.deleteRowAtPosition(position, animated);
    }
    
    private void deleteRowAtPosition(int adapterPosition, boolean animated) {
        if (mTableViewAdapter != null && adapterPosition != NO_POSITION){
            configureAnimator(animated);
            mTableViewAdapter.updateSectionCache();
            mTableViewAdapter.notifyItemRemoved(adapterPosition);
        }
    }
    
    /// Delete Cells In Range
    
    public void deleteRowsInRange(@NonNull NSIndexPath indexPathStart, int count){
        this.deleteRowsInRange(indexPathStart, count, true);
    }
    
    public void deleteRowsInRange(@NonNull NSIndexPath indexPathStart, int count, boolean animated){
        final int position = mTableViewAdapter.indexPathToPosition(indexPathStart);
        this.deleteRowsInRange(position, count, animated);
    }
    
    public void deleteRowsInRange(int positionStart, int count, boolean animated){
        if (mTableViewAdapter != null){
            configureAnimator(animated);
            mTableViewAdapter.updateSectionCache();
            mTableViewAdapter.notifyItemRangeRemoved(positionStart, count);
        }
    }
    
    /// Delete Cells In Specified Positions
    
    public void deleteRowsAtIndexPaths(@NonNull ArrayList<NSIndexPath> indexPaths, boolean needsSort){
        this.deleteRowsAtIndexPaths(indexPaths, needsSort, true);
    }
    
    /**
     * There is no api to delete items in discontinuous positions.
     * <p>Otherwise, you can delete item one by one like this.
     * <code>
     *     for( size-1 : 0 ){
     *         mTableViewModel.removeCellModel(indexPath);
     *         mTableView.deleteRowAtIndexPath(indexPath);
     *     }
     * </code>
     * </p>
     */
    public void deleteRowsAtIndexPaths(@NonNull ArrayList<NSIndexPath> indexPaths, boolean needsSort, boolean animated){
        if (!indexPaths.isEmpty() && mTableViewAdapter != null){
            if (needsSort){
                NSIndexPath.sortIndexPathArray(indexPaths);
            }
            final int positionStart = mTableViewAdapter.indexPathToPosition(indexPaths.get(0));
            configureAnimator(animated);
            mTableViewAdapter.updateSectionCache();
            mTableViewAdapter.notifyItemRangeChanged(positionStart, mTableViewAdapter.getItemCount());
        }
    }
    
    /**
     * Delete rows one by one.
     * <strong>The indexPaths must be sorted by smaller to bigger.</strong>
     */
    public void deleteRowsAtIndexPaths(@NonNull UITableViewModel tableViewModel,
                                       @NonNull ArrayList<NSIndexPath> indexPaths, boolean needsSort, boolean animated){
        if (mTableViewAdapter != null && !indexPaths.isEmpty()){
            if (needsSort){
                NSIndexPath.sortIndexPathArray(indexPaths);
            }
            int size = indexPaths.size();
            for (int i = size - 1; i >= 0; i--){
                tableViewModel.removeCellModel(indexPaths.get(i));
                this.deleteRowAtIndexPath(indexPaths.get(i), animated);
            }
        }
    }
    
    /// Delete Section
    
    public void deleteSection(int section, boolean animated){
        if (mTableViewAdapter != null){
            configureAnimator(animated);
            mTableViewAdapter.deleteSection(section);
        }
    }
    
    /// Insert Row
    
    public void insertRowAtIndexPath(@NonNull NSIndexPath indexPath) {
        this.insertRowAtIndexPath(indexPath, true);
    }
    
    public void insertRowAtIndexPath(NSIndexPath indexPath, boolean animated) {
        final int position = mTableViewAdapter.getPositionToInsert(indexPath);
        this.insertRowAtPosition(position, animated);
    }
    
    public void insertRowAtPosition(int position, boolean animated){
        if (mTableViewAdapter != null && position > NO_POSITION){
            configureAnimator(animated);
            mTableViewAdapter.updateSectionCache();
            mTableViewAdapter.notifyItemInserted(position);
        }
    }
    
    /// Insert Rows In Range
    
    public void insertRowsInRange(@NonNull NSIndexPath indexPathStart, int count){
        this.insertRowsInRange(indexPathStart, count, true);
    }
    
    public void insertRowsInRange(@NonNull NSIndexPath indexPathStart, int count, boolean animated){
        if (mTableViewAdapter != null){
            final int position = mTableViewAdapter.getPositionToInsert(indexPathStart);
            this.insertRowsInRange(position, count, animated);
        }
    }
    
    public void insertRowsInRange(int positionStart, int count, boolean animated){
        if (mTableViewAdapter != null){
            configureAnimator(animated);
            mTableViewAdapter.updateSectionCache();
            mTableViewAdapter.notifyItemRangeInserted(positionStart, count);
        }
    }
    
    /// Insert section
    
    public void insertSection(int section, boolean animated){
        if (mTableViewAdapter != null){
            configureAnimator(animated);
            mTableViewAdapter.insertSection(section);
        }
    }
    
    /// Scroll to position
    
    public void scrollToIndexPath(@NonNull NSIndexPath indexPath){
        if (mTableViewAdapter != null){
            final int position = mTableViewAdapter.indexPathToPosition(indexPath);
            this.scrollToPosition(position);
        }
    }
    
    public void smoothScrollToIndexPath(@NonNull NSIndexPath indexPath){
        if (mTableViewAdapter != null){
            final int position = mTableViewAdapter.indexPathToPosition(indexPath);
            this.smoothScrollToPosition(position);
        }
    }
    
    // ==================================
    // MARK - Find Child ViewHolder
    // ==================================
    
    @Override
    public UITableViewBaseHolder getChildViewHolder(View child) {
        return (UITableViewBaseHolder) super.getChildViewHolder(child);
    }
    
    /**
     * Find the index path for specified cell.
     *
     * @param cell The cell to query.
     * @return The IndexPath of cell or null if the cell not existed.
     */
    @Nullable
    public NSIndexPath indexPathForCell(@NonNull View cell) {
        final UITableViewBaseHolder holder = getChildViewHolder(cell);
        return holder != null ? holder.getNowIndexPath() : null;
    }
    
    /**
     * Find the cell for specified indexPath.
     *
     * @param indexPath The indexPath specified.
     * @return The UITableViewCell at <code>indexPath</code> or null if there is no such cell.
     */
    @Nullable
    public UITableViewCell cellForRowAtIndexPath(@NonNull NSIndexPath indexPath) {
        if (mTableViewAdapter == null) {
            Log.w(TAG, "cellForRowAtIndexPath: mTableViewAdapter = null.");
            return null;
        }
        final int position = mTableViewAdapter.indexPathToPosition(indexPath);
        if (position == NO_POSITION) {
            Log.w(TAG, "cellForRowAtIndexPath: no cells found at the specified indexPath.");
            return null;
        } else {
            UITableViewCellHolder holder = (UITableViewCellHolder) findViewHolderForAdapterPosition(position);
            return holder != null ? (UITableViewCell) holder.itemView : null;
        }
    }
    
    public UITableViewCell findTableViewCellUnder(float x, float y) {
        View child = findChildViewUnder(x, y);
        if (child != null && child instanceof UITableViewCell) {
            return (UITableViewCell) child;
        }
        return null;
    }
    
    // =====================================
    // MARK - Convenience
    // =====================================
    
    // SectionTitleView newSectionTitleView() {
    //     return new SectionTitleView(getContext());
    // }
    
    UITableViewCellHolder newTableViewCellHolder(UITableViewCell itemView) {
        return new UITableViewCellHolder(itemView);
    }
    
    UITableViewSectionHeaderHolder newSectionHeaderHolder(View headerView) {
        return new UITableViewSectionHeaderHolder(headerView);
    }
    
    UITableViewSectionFooterHolder newSectionFooterHolder(View headerView) {
        return new UITableViewSectionFooterHolder(headerView);
    }
    
    // =====================================
    // MARK - Inner Classes
    // =====================================
    
    class InternalDecoration extends ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            super.getItemOffsets(outRect, view, parent, state);
            
            final int position = parent.getChildAdapterPosition(view);
            if (position == 0) {
                // Set the UITableView top space
                outRect.top = mContentTopOffset;
            } else if (position == (parent.getAdapter().getItemCount() - 1)) {
                // Set the UITableView bottom space
                outRect.bottom = mContentBottomOffset;
            }
        }
    }
    
    /**
     * Helper class for UITableViewCell
     */
    class TableViewCellHelper {
        
        /// DataSource
        
        /// Delegate
        
        /// Listener
        
        void performAccessoryViewClicked(UITableViewCell tableViewCell){
            if (mTableViewDelegate != null){
                NSIndexPath indexPath = indexPathForCell(tableViewCell);
                if (indexPath != null){
                    mTableViewDelegate.onAccessoryViewClicked(UITableView.this, tableViewCell, indexPath);
                }
            }
        }
        
        void performCommitEditingStyle(UITableViewCell tableViewCell, int editingStyle){
            if (mEditingDelegate != null) {
                NSIndexPath indexPath = indexPathForCell(tableViewCell);
                mEditingDelegate.commitEditingStyle(UITableView.this, editingStyle, indexPath);
            }
        }
        
    }
    
    
    /// ViewHolder
    
    class UITableViewBaseHolder extends RecyclerView.ViewHolder {
        
        private int positionForIndexPath = NO_POSITION;
        private NSIndexPath nowIndexPath = null;
        
        UITableViewBaseHolder(View itemView) {
            super(itemView);
        }
        
        NSIndexPath getNowIndexPath() {
            final int position = this.getAdapterPosition();
            if (positionForIndexPath != position){
                // If the adapter position has changed, update the nowIndexPath
                nowIndexPath = mTableViewAdapter.getIndexPath(position);
            }
            return nowIndexPath;
        }
        
        void setNowIndexPath(NSIndexPath nowIndexPath, int positionForIndexPath) {
            this.nowIndexPath = nowIndexPath;
            this.positionForIndexPath = positionForIndexPath;
        }
    }
    
    class UITableViewCellHolder extends UITableViewBaseHolder
            implements View.OnClickListener, View.OnLongClickListener {
        
        UITableViewCellHolder(UITableViewCell itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }
        
        @Override
        public void onClick(View v) {
            NSIndexPath indexPath = getNowIndexPath();
            if (indexPath != null){
                UITableView.this.onCellViewClicked((UITableViewCell) v, indexPath);
            }
        }
        
        @Override
        public boolean onLongClick(View v) {
            NSIndexPath indexPath = getNowIndexPath();
            return indexPath != null && UITableView.this.onCellViewLongClicked((UITableViewCell) v, indexPath);
        }
    }
    
    class UITableViewSectionHeaderHolder extends UITableViewBaseHolder
            implements View.OnClickListener {
        
        UITableViewSectionHeaderHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }
        
        @Override
        public void onClick(View v) {
        
        }
    }
    
    class UITableViewSectionFooterHolder extends UITableViewBaseHolder
            implements View.OnClickListener {
        
        UITableViewSectionFooterHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }
        
        @Override
        public void onClick(View v) {
        
        }
    }
    
}
