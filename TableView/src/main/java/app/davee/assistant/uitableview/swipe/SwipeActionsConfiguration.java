package app.davee.assistant.uitableview.swipe;

import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * SwipeActionsConfiguration
 * <p>
 * Created by davee 2018/4/22.
 * Copyright (c) 2018 davee. All rights reserved.
 */
public class SwipeActionsConfiguration {
    
    private ArrayList<SwipeAction> mSwipeActions;
    
    private boolean mPerformFirstActionWithFullSwipe = false;
    
    public SwipeActionsConfiguration() {
        mSwipeActions = new ArrayList<>();
    }
    
    @NonNull
    public ArrayList<SwipeAction> getSwipeActions() {
        return mSwipeActions;
    }
    
    public boolean isPerformFirstActionWithFullSwipe() {
        return mPerformFirstActionWithFullSwipe;
    }
    
    public void setPerformFirstActionWithFullSwipe(boolean performFirstActionWithFullSwipe) {
        mPerformFirstActionWithFullSwipe = performFirstActionWithFullSwipe;
    }
    
    public boolean hasActions(){
        return mSwipeActions.size() > 0;
    }
    
    public void addSwipeAction(@NonNull SwipeAction swipeAction){
        mSwipeActions.add(swipeAction);
    }
    
    public void removeSwipeAction(@NonNull SwipeAction swipeAction) {
        mSwipeActions.remove(swipeAction);
    }
}
