package app.davee.assistant.uitableview;

import android.view.View;

import java.util.List;

/**
 * UITableViewDataSource
 * <p>
 * Created by davee 2018/3/3.
 * Copyright (c) 2018 davee. All rights reserved.
 */

@SuppressWarnings("WeakerAccess")
public abstract class UITableViewDataSource {
    
    public int numberOfSections(UITableView tableView) {
        return 1;
    }
    
    public abstract int numberOfRowsInSection(UITableView tableView, int section);
    
    /**
     * 获取对应[section, row]行的ViewType，UITableViewAdapter将根据这个ViewType来创建对应的Cell
     */
    public abstract int viewTypeForCell(UITableView tableView, NSIndexPath indexPath);
    
    /**
     * 获取指定ViewType的UITableViewCell
     *
     * Deprecated: Use onCreateTableViewCell() instead.
     */
    // @Deprecated
    // public abstract UITableViewCell onCreateTableViewCell(UITableView tableView, int viewType);
    
    public abstract UITableViewCell onCreateTableViewCell(UITableView tableView, int viewType);
    
    /**
     * 设置UITableViewCell的内容, Called in {@link UITableViewAdapter :onBindViewHolder}
     */
    public abstract void onBindTableViewCell(UITableView tableView, UITableViewCell tableViewCell, NSIndexPath indexPath);
    
    public void onBindTableViewCell(UITableView tableView, UITableViewCell tableViewCell, NSIndexPath indexPath, List<Object> payloads){
    
    }
    
    // ===============================================
    // MARK - Section Header/Footer 分组的首／尾View设置
    // ===============================================
    
    /**
     * Set height of section header view
     *
     * @param tableView which tableView
     * @param section   which section
     * @return Height in pixels. return {@link UITableView#UNDEFINED} to indicate no header view
     */
    public int heightForHeaderInSection(UITableView tableView, int section) {
        return UITableView.LayoutParams.WRAP_CONTENT;
    }
    
    public int heightForFooterInSection(UITableView tableView, int section) {
        // 默认有Footer，但是title为空
        return UITableView.LayoutParams.WRAP_CONTENT;
    }
    
    /**
     * Default return null
     */
    public CharSequence titleForHeaderInSection(UITableView tableView, int section) {
        return null;
    }
    
    /**
     * Default return null
     */
    public CharSequence titleForFooterInSection(UITableView tableView, int section) {
        return null;
    }
    
    /**
     * 获取Section分组HeaderView的ViewType，UITableView将根据ViewType来创建对应的HeaderView
     * <p>
     * 如果需要自定义HeaderView， 则重写此方法，并返回自定义的ViewType，例如 R.id.yourHeaderViewType
     * <p>
     * 默认返回自带的Header类型{@link SectionTitleView#VIEW_TYPE_HEADER}
     */
    public int viewTypeForSectionHeader(UITableView tableView, int section) {
        return SectionTitleView.VIEW_TYPE_HEADER;
    }
    
    public View createSectionHeader(UITableView tableView, int viewType) {
        if (viewType == SectionTitleView.VIEW_TYPE_HEADER) {
            return new SectionTitleView(tableView.getContext(), viewType);
        }
        // 如果是自定义的ViewType， 则需要重写此方法，返回自定义的HeaderView
        return null;
    }
    
    public void onBindSectionHeader(UITableView tableView, View headerView, int section) {
        // if (headerView instanceof SectionTitleView){
        //     ((SectionTitleView) headerView).setTitleText(this.titleForHeaderInSection(tableView, section));
        // }
    }
    
    public void onBindSectionHeader(UITableView tableView, View headerView, int section, List<Object> payloads) {
    }
    
    /// Footer
    
    public int viewTypeForSectionFooter(UITableView tableView, int section) {
        return SectionTitleView.VIEW_TYPE_FOOTER;
    }
    
    public View createSectionFooter(UITableView tableView, int viewType) {
        if (viewType == SectionTitleView.VIEW_TYPE_FOOTER) {
            return new SectionTitleView(tableView.getContext(), viewType);
        }
        return null;
    }
    
    public void onBindSectionFooter(UITableView tableView, View footerView, int section) {
        // if (footerView instanceof SectionTitleView){
        //     ((SectionTitleView) footerView).setTitleText(this.titleForFooterInSection(tableView, section));
        // }
    }
    
    public void onBindSectionFooter(UITableView tableView, View headerView, int section, List<Object> payloads) {
    }
    
}
