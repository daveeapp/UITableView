package app.davee.assistant.uitableview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * SeparatorDecoration
 * <p>
 * UITableView默认的分割线绘制类
 * <p>
 * Created by davee 2018/3/6.
 * Copyright (c) 2018 davee. All rights reserved.
 */

public class SeparatorDecoration extends RecyclerView.ItemDecoration {
    
    /// Separator
    private boolean mSeparatorEnable = true;
    private int mCommonSeparatorColor = Color.DKGRAY;
    private float mCommonSeparatorHeight = 0.5f;
    private Paint mSeparatorPaint;
    
    SeparatorDecoration() {
        ensureDividerPaint();
    }
    
    @SuppressWarnings("SuspiciousNameCombination")
    private void ensureDividerPaint() {
        if (mSeparatorPaint == null) {
            mSeparatorPaint = new Paint();
            mSeparatorPaint.setAntiAlias(true);
            mSeparatorPaint.setColor(mCommonSeparatorColor);
            mSeparatorPaint.setStyle(Paint.Style.FILL);
            mSeparatorPaint.setStrokeWidth(mCommonSeparatorHeight);
        }
    }

    public boolean isSeparatorEnable() {
        return mSeparatorEnable;
    }

    public void setSeparatorEnable(boolean separatorEnable) {
        mSeparatorEnable = separatorEnable;
    }

    @SuppressWarnings("WeakerAccess")
    public void setCommonSeparatorColor(int commonSeparatorColor) {
        if (mCommonSeparatorColor != commonSeparatorColor){
            mCommonSeparatorColor = commonSeparatorColor;
            mSeparatorPaint.setColor(mCommonSeparatorColor);
        }
    }
    
    @SuppressWarnings("SuspiciousNameCombination")
    public void setCommonSeparatorHeight(float commonSeparatorHeight) {
        if (mCommonSeparatorHeight != commonSeparatorHeight){
            mCommonSeparatorHeight = commonSeparatorHeight;
            if (mCommonSeparatorHeight < 1){
                mCommonSeparatorHeight = 0;
            }
            mSeparatorPaint.setStrokeWidth(mCommonSeparatorHeight);
        }
    }
    
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        // 默认不改变UITableView中每个Cell的绘制范围，分割线绘制在cell的上层
    }
    
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        // 此方法在绘制Cell之前调用，所以绘制内容在Cell的下层
    }
    
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        // 顾名思义，此方法绘制内容在Cell的上层

        if (mSeparatorEnable){
            drawVertical(c, (UITableView) parent);
        }
    }
    
    /**
     * 绘制垂直方向布局时的分割线
     */
    private void drawVertical(Canvas canvas, UITableView tableView) {
        final int childrenCount = tableView.getChildCount();
        final UITableViewAdapter adapter = tableView.getTableViewAdapter();
        for (int i = 0; i < childrenCount; i++) {
            final View child = tableView.getChildAt(i);
            final NSIndexPath indexPath = tableView.indexPathForCell(child);
            // final int position = tableView.getChildAdapterPosition(child);
            // final  NSIndexPath indexPath = adapter.getIndexPath(position);
            if (indexPath == null){
                continue;
            }
            //Log.d("SeparatorDecoration", "drawVertical: cache indexPath = " + holder.getNowIndexPath().toString());
            if (adapter.isSectionHeaderIndex(indexPath)
                    || adapter.isSectionFooterIndex(indexPath)) {
                continue;
            }
            
            UITableViewCell cell = (UITableViewCell) child;
            if (adapter.isFirstCellInSection(indexPath) && tableView.isSectionHeaderSeparatorEnable()) {
                /* 绘制Section的头分割线 */
                cell.drawSectionHeaderDivider(canvas, mSeparatorPaint);
            }
            if (adapter.isLastCellInSection(indexPath)) {
                // fixed bug: 修复设置sectionFooterSeparatorEnable = false时仍然绘制cell的separator的bug
                if (tableView.isSectionFooterSeparatorEnable()){
                    /* 绘制Section的尾部分割线 */
                    cell.drawSectionFooterDivider(canvas, mSeparatorPaint);
                }
            } else {
                cell.drawCellSeparator(canvas, mSeparatorPaint);
            }
            
        }
    }
}
