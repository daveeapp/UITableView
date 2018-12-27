package app.davee.assistant.uitableview;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;

/**
 * CellSwipeHelper
 * <p>
 * Created by davee 2018/5/31.
 * Copyright (c) 2018 davee. All rights reserved.
 */
class CellSwipeHelper implements RecyclerView.OnItemTouchListener {
    
    /// State: IDLE, Swiping, Settling, Open
    
    /// Edge: Left, Right
    
    public abstract class Callback{
        
        public void onCellSwipeStateChanged(){
        
        }
        
    }
    
    
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return false;
    }
    
    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }
    
    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    
    }
}
