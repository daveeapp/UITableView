package app.davee.assistant.uitableview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import java.util.ArrayList;

import app.davee.assistant.uitableview.swipe.SwipeAction;
import app.davee.assistant.uitableview.swipe.SwipeState;

/**
 * OnItemSwipingListener
 * <p>
 * Created by davee 2018/4/22.
 * Copyright (c) 2018 davee. All rights reserved.
 */
class OnItemSwipingListener implements UITableView.OnItemTouchListener {
    
    private static final String TAG = "OnItemSwipingListener";
    
    private static final int UNITS_ONE_SECOND = 1000;
    private static final int TRIGGER_VELOCITY = 1500;
    // private static final int TRIGGER_VELOCITY_DELETE = 2000;
    
    // private static final int ANIMATE_DURATION = 200;
    
    @SwipeState
    private int mSwipeState = SwipeState.NONE;
    
    private int mTouchSlop;
    private float mLastTouchX, mLastTouchY;
    private float mSwipedOffset = 0;
    private VelocityTracker mVelocityTracker;
    private boolean mIsBeingSwiped;
    /**
     * Set true to disallow parent to intercept touch event.
     * <p>
     * e.g When tableView is attached to ViewPager.
     */
    private boolean mNeedsDisallowParentIntercept = false;
    /**
     * Width from the edge left/right for allowing parent to intercept touch event.
     * Default 20dp
     */
    private int mEdgeSize;
    
    private int mTouchDownActionId;
    private final Rect hitRect = new Rect();
    private UITableViewCell mCapturedCell;
    
    private UITableView mTableView;
    
    private ValueAnimator mExecutingAnimator;
    
    
    OnItemSwipingListener(UITableView tableView) {
        mTableView = tableView;
        init();
    }
    
    private void init() {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(mTableView.getContext());
        mTouchSlop = viewConfiguration.getScaledTouchSlop();
        
        mEdgeSize = DimensionUtils.dp2px(mTableView.getContext(), 20);
    }
    
    public void setNeedsDisallowParentIntercept(boolean needsDisallowParentIntercept) {
        mNeedsDisallowParentIntercept = needsDisallowParentIntercept;
    }
    
    public void setEdgeSize(int edgeSize) {
        mEdgeSize = edgeSize;
    }
    
    // =====================================
    // MARK - Swipe State
    // =====================================
    
    private void setSwipeState(int swipeState) {
        if (mSwipeState != swipeState) {
            mSwipeState = swipeState;
        }
    }
    
    private void setSwiping(@UITableViewCell.SwipeLocation int location) {
        mIsBeingSwiped = true;
        mCapturedCell.showSwipeActionLayout(location);
        mCapturedCell.willStartSwiping(location);
        // mTableView.setSwiped(true);
        mTableView.didStartSwipingCell(mCapturedCell);
    }
    
    private void setOpened() {
        setSwipeState(SwipeState.OPENED);
        mSwipedOffset = mCapturedCell.getForegroundLayout().getTranslationX();
    }
    
    private void setClosed() {
        setSwipeState(SwipeState.NONE);
        mSwipedOffset = 0;
        mCapturedCell = null;
    }
    
    private void reset() {
        setSwipeState(SwipeState.NONE);
        mSwipedOffset = 0;
        mCapturedCell = null;
    }
    
    void closeLastSwipedCell() {
        if (mCapturedCell != null) {
            mCapturedCell.transitionToNormalState(new AnimatorListenerAdapter(){
                final UITableViewCell target = mCapturedCell;
                @Override
                public void onAnimationEnd(Animator animation) {
                    mTableView.didEndSwipingCell(target);
                    if (mCapturedCell == null){
                        mTableView.onSwipingStopped();
                    }
                }
            });
            reset();
        }
    }
    
    //---------------------------------------------------------------
    //              MARK: Implementation
    //---------------------------------------------------------------
    
    /// Checking whether touched the swipe action view
    private boolean onSwipeActionTouchEvent(MotionEvent e) {
        SwipeActionLayout swipeActionLayout;
        if (mSwipedOffset > 0) {
            swipeActionLayout = mCapturedCell.getLeadingSwipeLayout();
        } else {
            swipeActionLayout = mCapturedCell.getTrailingSwipeLayout();
        }
        if (swipeActionLayout == null) {
            return false;
        }
        
        ArrayList<SwipeAction> actions = swipeActionLayout.getSwipeActionsConfiguration().getSwipeActions();
        
        // Found the touched option view if have
        final int rx = (int) e.getRawX();
        final int ry = (int) e.getRawY();
        View touchedOptionView = null;
        SwipeAction touchedAction = null;
        for (SwipeAction swipeAction : actions) {
            final View optionView = swipeActionLayout.findViewById(swipeAction.getActionId());
            if (optionView != null) {
                optionView.getGlobalVisibleRect(hitRect);
                if (!hitRect.contains(rx, ry)) {
                    optionView.setPressed(false);
                } else {
                    touchedOptionView = optionView;
                    touchedAction = swipeAction;
                    break;
                }
            }
        }
        // No option view was touched
        if (touchedOptionView == null) {
            return false;
        }
        // If received CANCEL action
        if (e.getAction() == MotionEvent.ACTION_CANCEL) {
            touchedOptionView.setPressed(false);
            mTouchDownActionId = 0;
            return false;
        }
        
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            touchedOptionView.setPressed(true);
            mTouchDownActionId = touchedOptionView.getId();
            return true;
            
        } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
            touchedOptionView.setPressed(true);
            return true;
            
        } else if (e.getAction() == MotionEvent.ACTION_UP && mTouchDownActionId == touchedOptionView.getId()) {
            // this is option view on clicked
            /* 如果触发了SwipeAction，则关闭 */
            closeCapturedCell();
            mTouchDownActionId = 0;
            touchedOptionView.setPressed(false);
            mTableView.performSwipeAction(touchedAction, mCapturedCell);
            return true;
        }
        // Otherwise
        touchedOptionView.setPressed(false);
        return false;
    }
    
    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    
    }
    
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        if (!mTableView.canBeSwiped()) {
            return false;
        }
        
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (handleActionDown(e)) {
                    return true;
                }
                break;
            }
            
            case MotionEvent.ACTION_MOVE: {
                if (mCapturedCell == null || mVelocityTracker == null) {
                    mIsBeingSwiped = false;
                    break;
                }
                
                mVelocityTracker.addMovement(e);
                mVelocityTracker.computeCurrentVelocity(UNITS_ONE_SECOND);
                
                final float touchX = e.getX();
                final float touchY = e.getY();
                final float dx = touchX - mLastTouchX;
                final float dy = touchY - mLastTouchY;
                mLastTouchX = touchX;
                mLastTouchY = touchY;
                
                if (mNeedsDisallowParentIntercept && rv.getParent() != null) {
                    final int left = mCapturedCell.getLeft();
                    final int right = mCapturedCell.getRight();
                    if (touchX > (left + mEdgeSize) && touchX < (right - mEdgeSize)) {
                        rv.getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                
                if (mSwipeState == SwipeState.OPENED && !mIsBeingSwiped) {
                    /* 如果当前已打开Cell且没有处于滑动状态，则先处理SwipeActionLayout事件 */
                    if (!onSwipeActionTouchEvent(e)) {
                        startSwiping(dx, dy);
                    }
                } else {
                    startSwiping(dx, dy);
                }
                
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                //Log.d(TAG, "onInterceptTouchEvent: " + MotionEvent.actionToString(e.getAction()));
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
    
                // !mIsBeingSwiped && mSwipeState == SwipeState.OPENED
                if (mSwipeState == SwipeState.OPENED) {
                    if (!onSwipeActionTouchEvent(e)) {
                        closeCapturedCell();
                    }
                }
                
                mCapturedCell = null;
                
                break;
        }
        
        return mIsBeingSwiped;
    }
    
    private boolean handleActionDown(MotionEvent e) {
        // If has executing animator, disturb it
        if (mExecutingAnimator != null) {
            mExecutingAnimator.end();
            mExecutingAnimator = null;
        }
        
        final View touchedChild = mTableView.findChildViewUnder(e.getX(), e.getY());
        if (touchedChild == null) {
            return false;
        }
    
        boolean handled = false;
        
        if (mSwipeState == SwipeState.OPENED) {
            handled = true;
            /* 如果已经有Cell处于打开状态 */
            if (mCapturedCell == null) {
                setSwipeState(SwipeState.NONE);
            } else {
                if (mCapturedCell != touchedChild) {
                    /* 如果当前点击的Cell与已经打开的Cell不是同一个，则关闭已经打开的Cell */
                    closeLastSwipedCell();
                } else {
                    // When the swipeable was opened, the touch event will be intercepted if not tap on the option menu.
                    // 如果点击的是已经打开的Cell，则判断是否点击了SwipeAction
                    mSwipedOffset = mCapturedCell.getForegroundLayout().getTranslationX();
                    onSwipeActionTouchEvent(e);
                    mLastTouchX = e.getX();
                    mLastTouchY = e.getY();
                    if (mVelocityTracker == null) {
                        mVelocityTracker = VelocityTracker.obtain();
                    }
                    mVelocityTracker.addMovement(e);
                    return true;
                }
            }
        }
        
        if (!(touchedChild instanceof UITableViewCell)) {
            /* 如果点击的不是UITableViewCell，则不用处理 */
            return handled;
        }
        
        UITableViewCell swipable = (UITableViewCell) touchedChild;
        if (!swipable.isSwipeActionEnabled()) {
            return handled;
        }
        mLastTouchX = e.getX();
        mLastTouchY = e.getY();
        mCapturedCell = swipable;
        
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(e);
        return handled;
    }
    
    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        // final float lastSwipeOffset = mSwipedOffset;
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                handleActionDown(e);
                break;
            }
            
            case MotionEvent.ACTION_MOVE: {
                if (mCapturedCell == null || mVelocityTracker == null || !mIsBeingSwiped) {
                    break;
                }
                mVelocityTracker.addMovement(e);
                mVelocityTracker.computeCurrentVelocity(UNITS_ONE_SECOND);
                
                final float touchX = e.getX();
                final float dx = touchX - mLastTouchX;
                // final float swipeOffset = lastSwipeOffset + dx;
                mLastTouchX = touchX;
                
                moveForegroundLayoutNew(dx);
                
                break;
            }
            
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                finishSwiping();
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                mIsBeingSwiped = false;
                break;
            }
        }
    }
    
    private void startSwiping(float dx, float dy) {
        if (mSwipeState == SwipeState.OPENED) {
            if (!mIsBeingSwiped && Math.abs(dx) > 1 && Math.abs(dy) < Math.abs(dx) / 2) {
                if (mSwipedOffset > 0) {
                    setSwiping(UITableViewCell.SwipeLocation.Leading);//
                } else {
                    setSwiping(UITableViewCell.SwipeLocation.Trailing);//
                }
            }
        } else {
            final float absDx = Math.abs(dx);
            final float absDy = Math.abs(dy);
            final float absVx = Math.abs(mVelocityTracker.getXVelocity());
            final float absVy = Math.abs(mVelocityTracker.getYVelocity());
            if (!mIsBeingSwiped) {
                // Swiped on trailing
                if (dx < 0 && mCapturedCell.isTrailingSwipeActionEnabled()) {
                    if ((absDx > mTouchSlop && absDy < absDx / 2)
                            || (absVx > TRIGGER_VELOCITY && absVy < absVx / 2)) {
                        setSwiping(UITableViewCell.SwipeLocation.Trailing);
                    }
                }
                // Swiped on leading
                else if (dx > 0 && mCapturedCell.isLeadingSwipeActionEnabled()) {
                    if ((dx > mTouchSlop && absDy < absDx / 2)
                            || (absVx > TRIGGER_VELOCITY && absVy < absVx / 2)) {
                        setSwiping(UITableViewCell.SwipeLocation.Leading);
                    }
                }
            }
        }
    }
    
    private void moveForegroundLayoutNew(float dx) {
        if (dx == 0) {
            return;
        }
        final float lastSwipeOffset = mSwipedOffset;
        mSwipedOffset = lastSwipeOffset + dx;
        
        if (mSwipedOffset < 0) {
            if (!mCapturedCell.isTrailingSwipeActionEnabled()) {
                mSwipedOffset = 0;
            } else if (lastSwipeOffset >= 0) {
                // BugFixed: 当同时有Leading & Trailing Actions的时候，滑动过程中切换到另一个的时候，需要计算宽度
                mCapturedCell.willStartSwiping(UITableViewCell.SwipeLocation.Trailing);
            }
        } else if (mSwipedOffset > 0) {
            if (!mCapturedCell.isLeadingSwipeActionEnabled()) {
                mSwipedOffset = 0;
            } else if (lastSwipeOffset <= 0) {
                mCapturedCell.willStartSwiping(UITableViewCell.SwipeLocation.Leading);
            }
        }
        
        mCapturedCell.translateForegroundLayout(mSwipedOffset);
    }
    
    // private void moveForegroundLayout(float swipedOffset) {
    //     if (mSwipedOffset != swipedOffset) {
    //         final float lastOffset = mSwipedOffset;
    //         mSwipedOffset = swipedOffset;
    //
    //         if (!mCapturedCell.isLeadingSwipeActionEnabled() && mSwipedOffset > 0){
    //             mSwipedOffset = 0;
    //         } else if (!mCapturedCell.isTrailingSwipeActionEnabled() && mSwipedOffset < 0){
    //             mSwipedOffset = 0;
    //         }
    //         //mCapturedCell.getForegroundLayout().setTranslationX(mSwipedOffset);
    //         mCapturedCell.translateForegroundLayout(mSwipedOffset);
    //     }
    // }
    
    private void finishSwiping() {
        if (mSwipedOffset < 0 && mCapturedCell.isTrailingSwipeActionEnabled()) {
            finishSwipingByTrailing();
        } else if (mSwipedOffset > 0 && mCapturedCell.isLeadingSwipeActionEnabled()) {
            finishSwipingByLeading();
        } else {
            // animateToStartPosition();
            closeCapturedCell();
        }
    }
    
    private void finishSwipingByTrailing() {
        // Check full swipe
        SwipeActionLayout swipeActionLayout = mCapturedCell.getTrailingSwipeLayout();
        if (swipeActionLayout == null) {
            return;
        }
        if (swipeActionLayout.isWillFullSwipe()) {
            // perform first action with full swipe
            mTableView.performSwipeAction(swipeActionLayout.getFirstSwipeAction(), mCapturedCell);
            closeCapturedCell();
            return;
        }
        
        if (mSwipeState == SwipeState.OPENED) {
            /* 如果当前已处于打开状态，如果滑动距离小于开启的距离，则关闭cell */
            if (Math.abs(mSwipedOffset) < swipeActionLayout.getSwipeTriggerOffset()
                    || mVelocityTracker.getXVelocity() > TRIGGER_VELOCITY) {
                closeCapturedCell();
            } else {
                animateToOpenedPosition(-swipeActionLayout.getSwipeMaxOffset());
            }
        } else {
            /* 判断移动距离是否已超过开启Cell的最小距离 */
            if (Math.abs(mSwipedOffset) > swipeActionLayout.getSwipeTriggerOffset()
                    || Math.abs(mVelocityTracker.getXVelocity()) > TRIGGER_VELOCITY) {
                animateToOpenedPosition(-swipeActionLayout.getSwipeMaxOffset());
            } else {
                // animateToStartPosition();
                closeCapturedCell();
            }
        }
    }
    
    private void finishSwipingByLeading() {
        // Check full swipe
        SwipeActionLayout swipeActionLayout = mCapturedCell.getLeadingSwipeLayout();
        if (swipeActionLayout == null) {
            return;
        }
        if (swipeActionLayout.isWillFullSwipe()) {
            // perform first action with full swipe
            mTableView.performSwipeAction(swipeActionLayout.getFirstSwipeAction(), mCapturedCell);
            closeCapturedCell();
            return;
        }
        
        if (mSwipeState == SwipeState.OPENED) {
            if (mSwipedOffset < swipeActionLayout.getSwipeTriggerOffset()
                    || mVelocityTracker.getXVelocity() < -TRIGGER_VELOCITY) {
                closeCapturedCell();
            } else {
                animateToOpenedPosition(swipeActionLayout.getSwipeMaxOffset());
            }
        } else {
            if (mSwipedOffset > swipeActionLayout.getSwipeTriggerOffset()
                    || mVelocityTracker.getXVelocity() > TRIGGER_VELOCITY) {
                animateToOpenedPosition(swipeActionLayout.getSwipeMaxOffset());
            } else {
                //animateToStartPosition();
                closeCapturedCell();
            }
        }
    }
    
    private void animateToOpenedPosition(float endOffset) {
        mExecutingAnimator = mCapturedCell.transitionToSwipeState(endOffset, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setOpened();
                mExecutingAnimator = null;
            }
        });
    }
    
    private void closeCapturedCell() {
        mExecutingAnimator = mCapturedCell.transitionToNormalState(new AnimatorListenerAdapter() {
            final UITableViewCell target = mCapturedCell;
            @Override
            public void onAnimationEnd(Animator animation) {
                setClosed();
                mTableView.didEndSwipingCell(target);
                if (mCapturedCell == null){
                    mTableView.onSwipingStopped();
                }
                mExecutingAnimator = null;
            }
        });
    }
    
}
