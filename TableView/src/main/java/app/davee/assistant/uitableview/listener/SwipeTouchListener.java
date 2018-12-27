package app.davee.assistant.uitableview.listener;

/**
 * SwipeTouchListener
 * On swipe listener for the item view in recycler view, the item view must implement the {@link }
 * <p>
 * Created by davee 2018/3/7.
 * Copyright (c) 2018 davee. All rights reserved.
 */

@Deprecated
public class SwipeTouchListener  {// implements UITableView.OnItemTouchListener
    
//     private static final String TAG = "SwipeTouchListener";
//
//     private static final int UNITS_ONE_SECOND = 1000;
//     private static final int TRIGGER_VELOCITY = 1500;
//     private static final int TRIGGER_VELOCITY_DELETE = 2000;
//     //private static final int TRIGGER_DISTANCE = 100;
//
//     private int mTouchSlop;
//
//     @SwipeState
//     private int mSwipeState = SwipeState.NONE;
//
//     private boolean mSwipeToDeleteDirectly = false;
//
//     private float mLastTouchX, mLastTouchY;
//     private boolean mIsBeingSwiped;
//     private float mSwipedOffset = 0;
//     private VelocityTracker mVelocityTracker;
//
//     private Swipable mLastTouchedSwipable;
//     private int mLastTouchOptionId;
//     private final Rect hitRect = new Rect();
//
//     private RecyclerView mRecyclerView;
//     private OnItemSwipeListener mOnItemSwipeListener;
//
//     private ValueAnimator mExecutingAnimator;
//     private SimpleAnimatorListener mAnimateToStartListener = new SimpleAnimatorListener() {
//         @Override
//         public void onAnimationEnd(Animator animation) {
//             //notifyStateChanged(SwipeState.NONE);
//             setClosed();
//             mExecutingAnimator = null;
//         }
//     };
//
//     private SimpleAnimatorListener mOpenAnimatorListener = new SimpleAnimatorListener() {
//         @Override
//         public void onAnimationEnd(Animator animation) {
//             //notifyStateChanged(SwipeState.OPENED);
//             setOpened();
//             mExecutingAnimator = null;
//         }
//     };
//
//     private SimpleAnimatorListener mCloseAnimatorListener = new SimpleAnimatorListener() {
//         @Override
//         public void onAnimationEnd(Animator animation) {
//             //notifyStateChanged(SwipeState.NONE);
//             setClosed();
//             reset();
//             mExecutingAnimator = null;
//         }
//     };
//
//     private SimpleAnimatorListener mAnimateToDeleteListener = new SimpleAnimatorListener() {
//         @Override
//         public void onAnimationEnd(Animator animation) {
//             //notifyStateChanged(SwipeState.NONE);
//             setClosed();
//             notifyToDelete();
//             mExecutingAnimator = null;
//         }
//     };
    
    public SwipeTouchListener() {
    }


//
//     public SwipeTouchListener(RecyclerView recyclerView, OnItemSwipeListener onItemSwipeListener) {
//         mRecyclerView = recyclerView;
//         mOnItemSwipeListener = onItemSwipeListener;
//         init();
//     }
//
//     private void init() {
//         ViewConfiguration viewConfiguration = ViewConfiguration.get(mRecyclerView.getContext());
//         mTouchSlop = viewConfiguration.getScaledTouchSlop();
//     }
//
//     public RecyclerView getRecyclerView() {
//         return mRecyclerView;
//     }
//
//     public void setSwipeToDeleteDirectly(boolean swipeToDeleteDirectly) {
//         mSwipeToDeleteDirectly = swipeToDeleteDirectly;
//     }
//
//     private void setSwipeState(int swipeState) {
//         if (mSwipeState != swipeState) {
//             mSwipeState = swipeState;
//         }
//     }
//
//     private void setSwiping() {
//         mIsBeingSwiped = true;
//         if (mLastTouchedSwipable.getBackgroundLayout() != null) {
//             mLastTouchedSwipable.getBackgroundLayout().setVisibility(View.VISIBLE);
//         }
//     }
//
//     private void setOpened() {
//         setSwipeState(SwipeState.OPENED);
//     }
//
//     private void setClosed() {
//         setSwipeState(SwipeState.NONE);
//         if (mLastTouchedSwipable.getBackgroundLayout() != null) {
//             mLastTouchedSwipable.getBackgroundLayout().setVisibility(View.GONE);
//         }
//     }
//
//     private void reset() {
//         mLastTouchedSwipable = null;
//         mSwipedOffset = 0;
//     }
//
//     private void notifyToDelete() {
//         final int adapterPosition = mRecyclerView.getChildAdapterPosition(mLastTouchedSwipable.getView());
//         mOnItemSwipeListener.deleteSwipeable(this, mLastTouchedSwipable.getView(), adapterPosition);
//     }
//
//     //---------------------------------------------------------------
//     //              MARK: Implementation
//     //---------------------------------------------------------------
//
//     @Override
//     public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//     }
//
//     private boolean onOptionViewTouchEvent(MotionEvent e) {
//         ArrayList<Integer> optionsIds = mLastTouchedSwipable.getOptionsIds((int) mSwipedOffset);
//         if (optionsIds == null || optionsIds.size() == 0) {
//             Log.d(TAG, "onOptionTouchEvent: No Option View");
//             return false;
//         }
//
//         // Found the touched option view if have
//         final int rx = (int) e.getRawX();
//         final int ry = (int) e.getRawY();
//         View touchedOptionView = null;
//         for (int optionId : optionsIds) {
//             final View optionView = mLastTouchedSwipable.getBackgroundLayout().findViewById(optionId);
//             if (optionView != null) {
//                 optionView.getGlobalVisibleRect(hitRect);
//                 if (!hitRect.contains(rx, ry)) {
//                     optionView.setPressed(false);
//                 } else {
//                     touchedOptionView = optionView;
//                     break;
//                 }
//             }
//         }
//         // No option view was touched
//         if (touchedOptionView == null){
//             return false;
//         }
//         // If received CANCEL action
//         if (e.getAction() == MotionEvent.ACTION_CANCEL){
//             touchedOptionView.setPressed(false);
//             mLastTouchOptionId = 0;
//             return false;
//         }
//
//         if (e.getAction() == MotionEvent.ACTION_DOWN){
//             touchedOptionView.setPressed(true);
//             mLastTouchOptionId = touchedOptionView.getId();
//             return true;
//         } else if (e.getAction() == MotionEvent.ACTION_MOVE){
//             touchedOptionView.setPressed(true);
//             return true;
//         } else if(e.getAction() == MotionEvent.ACTION_UP && mLastTouchOptionId == touchedOptionView.getId()){
//             // this is option view on clicked
//             mLastTouchOptionId = 0;
//             touchedOptionView.setPressed(false);
//             if(mOnItemSwipeListener != null){
//                 mOnItemSwipeListener.onOptionViewClicked(this, mLastTouchedSwipable, touchedOptionView);
//             }
//             return true;
//         }
//         // Otherwise
//         touchedOptionView.setPressed(false);
//         return false;
//     }
//
//     private boolean handleActionDown(MotionEvent e) {
//         // If has executing animator, disturb it
//         if (mExecutingAnimator != null) {
//             mExecutingAnimator.end();
//             mExecutingAnimator = null;
//         }
//
//         View touchedChild = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
//         if (touchedChild == null) {
//             return false;
//         }
//         if (mSwipeState == SwipeState.OPENED) {
//             if (mLastTouchedSwipable == null) {
//                 setSwipeState(SwipeState.NONE);
//             } else {
//                 if (!mLastTouchedSwipable.equals(touchedChild)) {
//                     closeSwipable();
//                     return true;
//                 } else {
//                     // When the swipeable was opened, the touch event will be intercepted if not tap on the option menu.
//                     onOptionViewTouchEvent(e);
//                     mLastTouchX = e.getX();
//                     mLastTouchY = e.getY();
//                     if (mVelocityTracker == null) {
//                         mVelocityTracker = VelocityTracker.obtain();
//                     }
//                     mVelocityTracker.addMovement(e);
//                     return true;
//                 }
//             }
//         }
//
//         if (!(touchedChild instanceof Swipable)) {
//             return false;
//         }
//         Swipable swipable = (Swipable) touchedChild;
//         if (!swipable.isSwipeEnabled()) {
//             return false;
//         }
//         mLastTouchX = e.getX();
//         mLastTouchY = e.getY();
//         mLastTouchedSwipable = swipable;
//
//         if (mVelocityTracker == null) {
//             mVelocityTracker = VelocityTracker.obtain();
//         }
//         mVelocityTracker.addMovement(e);
//         return false;
//     }
//
//     @Override
//     public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
// //        Log.d(TAG, "onInterceptTouchEvent: " + MotionEvent.actionToString(e.getAction()));
//         switch (e.getAction()) {
//             case MotionEvent.ACTION_DOWN: {
//                 if (handleActionDown(e)) {
//                     return true;
//                 }
//                 break;
//             }
//
//             case MotionEvent.ACTION_MOVE: {
//                 if (mLastTouchedSwipable == null || mVelocityTracker == null) {
//                     mIsBeingSwiped = false;
//                     break;
//                 }
//
//                 mVelocityTracker.addMovement(e);
//                 mVelocityTracker.computeCurrentVelocity(UNITS_ONE_SECOND);
//
//                 final float touchX = e.getX();
//                 final float touchY = e.getY();
//                 final float dx = touchX - mLastTouchX;
//                 final float dy = touchY - mLastTouchY;
//                 mLastTouchX = touchX;
//                 mLastTouchY = touchY;
//
//                 if (mSwipeState == SwipeState.OPENED && !mIsBeingSwiped){
//                     if (!onOptionViewTouchEvent(e)){
//                         startSwiping(dx, dy);
//                     }
//                 } else {
//                     startSwiping(dx, dy);
//                 }
//
//                 break;
//             }
//             case MotionEvent.ACTION_CANCEL:
//             case MotionEvent.ACTION_UP:
//                 //Log.d(TAG, "onInterceptTouchEvent: " + MotionEvent.actionToString(e.getAction()));
//                 if (mVelocityTracker != null) {
//                     mVelocityTracker.recycle();
//                     mVelocityTracker = null;
//                 }
//
//                 if (!mIsBeingSwiped && mSwipeState == SwipeState.OPENED){
//                     if (!onOptionViewTouchEvent(e)){
//                         closeSwipable();
//                     }
//                 }
//
//                 break;
//         }
//
//         return mIsBeingSwiped;
//     }
//
//     @Override
//     public void onTouchEvent(RecyclerView rv, MotionEvent e) {
//         final float lastSwipeOffset = mSwipedOffset;
//         switch (e.getAction()) {
//             case MotionEvent.ACTION_DOWN: {
//                 handleActionDown(e);
//                 break;
//             }
//
//             case MotionEvent.ACTION_MOVE: {
//                 if (mLastTouchedSwipable == null || mVelocityTracker == null || !mIsBeingSwiped) {
//                     break;
//                 }
//                 mVelocityTracker.addMovement(e);
//                 mVelocityTracker.computeCurrentVelocity(UNITS_ONE_SECOND);
//
//                 final float touchX = e.getX();
//                 float dx = touchX - mLastTouchX;
//                 float swipeOffset = lastSwipeOffset + dx;
//                 mLastTouchX = touchX;
//
//                 if (mLastTouchedSwipable.getSwipeMode() == SwipeMode.ACTION_SUSPEND){
//                     if (mSwipedOffset < 0) {
//                         moveForegroundLayout(Math.max(swipeOffset, mLastTouchedSwipable.getRTLSuspendOffset()));
//                     } else {
//                         moveForegroundLayout(Math.min(swipeOffset, mLastTouchedSwipable.getLTRSuspendOffset()));
//                     }
//                 } else {
//                     moveForegroundLayout(swipeOffset);
//                 }
//
//                 break;
//             }
//
//             case MotionEvent.ACTION_CANCEL:
//             case MotionEvent.ACTION_UP: {
//                 //Log.d(TAG, "onTouchEvent: " + MotionEvent.actionToString(e.getAction()));
//                 finishSwiping();
//                 if (mVelocityTracker != null) {
//                     mVelocityTracker.recycle();
//                     mVelocityTracker = null;
//                 }
//                 mIsBeingSwiped = false;
//                 break;
//             }
//         }
//     }
//
//     private void startSwiping(float dx, float dy) {
//         if (mSwipeState == SwipeState.OPENED) {
//             if (!mIsBeingSwiped && Math.abs(dx) > mTouchSlop && Math.abs(dy) < Math.abs(dx) / 2) {
//                 setSwiping();//
//             }
//         } else {
//             if (!mIsBeingSwiped) {
//                 switch (mLastTouchedSwipable.getSwipeDirection()) {
//                     case SwipeDirection.SWIPE_RIGHT_TO_LEFT: {
//                         if ((dx < -mTouchSlop && Math.abs(dy) < Math.abs(dx) / 2)
//                                 || mVelocityTracker.getXVelocity() < -TRIGGER_VELOCITY) {
//                             setSwiping();
//                         }
//                         break;
//                     }
//
//                     case SwipeDirection.SWIPE_LEFT_TO_RIGHT: {
//                         if ((dx > mTouchSlop && Math.abs(dy) < Math.abs(dx) / 2)
//                                 || mVelocityTracker.getXVelocity() > TRIGGER_VELOCITY) {
//                             setSwiping();
//                         }
//                         break;
//                     }
//
//                     case SwipeDirection.SWIPE_BOTH: {
//                         if ((Math.abs(dx) > mTouchSlop && Math.abs(dy) < Math.abs(dx) / 2)
//                                 || Math.abs(mVelocityTracker.getXVelocity()) > TRIGGER_VELOCITY) {
//                             setSwiping();
//                         }
//                         break;
//                     }
//                 }
//             }
//         }
//     }
//
//     private void moveForegroundLayout(float swipedOffset) {
//         if (mSwipedOffset != swipedOffset) {
//             mSwipedOffset = swipedOffset;
//             switch (mLastTouchedSwipable.getSwipeDirection()) {
//                 case SwipeDirection.SWIPE_RIGHT_TO_LEFT: {
//                     if (mSwipedOffset > 0) {
//                         mSwipedOffset = 0;
//                     }
//                     break;
//                 }
//                 case SwipeDirection.SWIPE_LEFT_TO_RIGHT: {
//                     if (mSwipedOffset < 0) {
//                         mSwipedOffset = 0;
//                     }
//                     break;
//                 }
//             }
//             mLastTouchedSwipable.getForegroundLayout().setTranslationX(mSwipedOffset);
//         }
//     }
//
//     private void finishSwiping() {
//         if (mLastTouchedSwipable.getSwipeMode() == SwipeMode.ACTION_DELETE) {
//             finishSwipingToDelete();
//         } else {
//             switch (mLastTouchedSwipable.getSwipeDirection()) {
//                 case SwipeDirection.SWIPE_RIGHT_TO_LEFT: {
//                     if (mSwipedOffset > 0) {
//                         animateToStartPosition();
//                     } else {
//                         finishSwipingToLeft();
//                     }
//                     break;
//                 }
//
//                 case SwipeDirection.SWIPE_LEFT_TO_RIGHT: {
//                     if (mSwipedOffset < 0) {
//                         animateToStartPosition();
//                     } else {
//                         finishSwipingToRight();
//                     }
//                     break;
//                 }
//
//                 case SwipeDirection.SWIPE_BOTH: {
//                     if (mSwipedOffset < 0) {
//                         finishSwipingToLeft();
//                     } else {
//                         finishSwipingToRight();
//                     }
//                     break;
//                 }
//             }
//         }
//     }
//
//     private void finishSwipingToLeft() {
//         if (mSwipeState == SwipeState.OPENED) {
//             if (Math.abs(mSwipedOffset) < mLastTouchedSwipable.getOpenTriggerDistance()
//                     || mVelocityTracker.getXVelocity() > TRIGGER_VELOCITY) {
//                 closeSwipable();
//             } else {
//                 animateToOpenedPosition(mLastTouchedSwipable.getRTLSuspendOffset());
//             }
//         } else {
//             if (Math.abs(mSwipedOffset) > mLastTouchedSwipable.getOpenTriggerDistance()
//                     || mVelocityTracker.getXVelocity() < -TRIGGER_VELOCITY) {
//                 animateToOpenedPosition(mLastTouchedSwipable.getRTLSuspendOffset());
//             } else {
//                 animateToStartPosition();
//             }
//         }
//     }
//
//     private void finishSwipingToRight() {
//         if (mSwipeState == SwipeState.OPENED) {
//             if (mSwipedOffset < mLastTouchedSwipable.getOpenTriggerDistance()
//                     || mVelocityTracker.getXVelocity() < -TRIGGER_VELOCITY) {
//                 closeSwipable();
//             } else {
//                 animateToOpenedPosition(mLastTouchedSwipable.getLTRSuspendOffset());
//             }
//         } else {
//             if (mSwipedOffset > mLastTouchedSwipable.getOpenTriggerDistance()
//                     || mVelocityTracker.getXVelocity() > TRIGGER_VELOCITY) {
//                 animateToOpenedPosition(mLastTouchedSwipable.getLTRSuspendOffset());
//             } else {
//                 animateToStartPosition();
//             }
//         }
//     }
//
//     private void finishSwipingToDelete() {
//         if (Math.abs(mSwipedOffset) > mLastTouchedSwipable.getOpenTriggerDistance()
//                 || Math.abs(mSwipedOffset) > TRIGGER_VELOCITY_DELETE) {
//             if (mSwipeToDeleteDirectly) {
//                 animateToDelete();
//             } else {
//                 if (!mOnItemSwipeListener.alertToDeleteSwipable(this, mLastTouchedSwipable)) {
//                     // if not pop up alert dialog
//                     animateToDelete();
//                 }
//             }
//         } else {
//             animateToStartPosition();
//         }
//     }
//
//     private void animateToDelete() {
//         float endOffset;
//         if (mSwipedOffset < 0) {
//             endOffset = -mLastTouchedSwipable.getForegroundLayout().getWidth();
//         } else {
//             endOffset = mLastTouchedSwipable.getForegroundLayout().getWidth();
//         }
//         animateToDelete(endOffset);
//     }
//
//     public void deleteSwipable(Swipable swipable){
//         if (swipable != null && swipable.equals(mLastTouchedSwipable)){
//             animateToDelete();
//         }
//     }
//
//     public void cancelToDeleteSwipable(Swipable swipable) {
//         if (swipable != null && swipable.equals(mLastTouchedSwipable)){
//             closeSwipable();
//         }
//     }
//
//     private void animateToStartPosition() {
//         ValueAnimator valueAnimator = ValueAnimator.ofFloat(mSwipedOffset, 0);
//         valueAnimator.setDuration(200);
//         valueAnimator.setInterpolator(new DecelerateInterpolator());
//         valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//             @Override
//             public void onAnimationUpdate(ValueAnimator animation) {
//                 moveForegroundLayout((Float) animation.getAnimatedValue());
//             }
//         });
//         valueAnimator.addListener(mAnimateToStartListener);
//         valueAnimator.start();
//         mExecutingAnimator = valueAnimator;
//     }
//
//     private void animateToOpenedPosition(float endOffset) {
//         ValueAnimator valueAnimator = ValueAnimator.ofFloat(mSwipedOffset, endOffset);
//         valueAnimator.setDuration(200);
//         valueAnimator.setInterpolator(new DecelerateInterpolator());
//         valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//             @Override
//             public void onAnimationUpdate(ValueAnimator animation) {
//                 moveForegroundLayout((Float) animation.getAnimatedValue());
//             }
//         });
//         valueAnimator.addListener(mOpenAnimatorListener);
//         valueAnimator.start();
//         mExecutingAnimator = valueAnimator;
//     }
//
//     private void closeSwipable() {
//         if (mExecutingAnimator != null) {
//             return;
//         }
//         ValueAnimator valueAnimator = ValueAnimator.ofFloat(mSwipedOffset, 0);
//         valueAnimator.setDuration(200);
//         valueAnimator.setInterpolator(new DecelerateInterpolator());
//         valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//             @Override
//             public void onAnimationUpdate(ValueAnimator animation) {
//                 moveForegroundLayout((Float) animation.getAnimatedValue());
//             }
//         });
//         valueAnimator.addListener(mCloseAnimatorListener);
//         valueAnimator.start();
//         mExecutingAnimator = valueAnimator;
//     }
//
//     private void animateToDelete(float endOffset) {
//         ValueAnimator valueAnimator = ValueAnimator.ofFloat(mSwipedOffset, endOffset);
//         valueAnimator.setDuration(200);
//         valueAnimator.setInterpolator(new LinearInterpolator());
//         valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//             @Override
//             public void onAnimationUpdate(ValueAnimator animation) {
//                 moveForegroundLayout((Float) animation.getAnimatedValue());
//             }
//         });
//         valueAnimator.addListener(mAnimateToDeleteListener);
//         valueAnimator.start();
//         mExecutingAnimator = valueAnimator;
//     }
//
//     //---------------------------------------------------------------
//     //              MARK: Inner Classes
//     //---------------------------------------------------------------
//
//
//     public void setOnItemSwipeListener(@NonNull OnItemSwipeListener onItemSwipeListener) {
//         mOnItemSwipeListener = onItemSwipeListener;
//     }
//
//     public interface OnItemSwipeListener {
//         /*
//          * In this method, you can pop up a dialog to select whether to delete.
//          * <p>
//          * If select 'OK', you should call the method {@link SwipeTouchListener#animateToDelete()};
//          * <p>
//          * If select 'NO', you should call the method {@link SwipeTouchListener#cancelToDeleteSwipable()}
//          * <p>
//          * If return false, the SwipeTouchListener will not to delete the item
//          */
//         boolean alertToDeleteSwipable(SwipeTouchListener swipeTouchListener, Swipable swipable);
//
//         /**
//          * This method will be called after the animate, you should do the delete action in this
//          * @param swipeTouchListener    {@link SwipeTouchListener}
//          * @param itemView the item view on swiped
//          * @param position  the position for the item view
//          */
//         void deleteSwipeable(SwipeTouchListener swipeTouchListener, View itemView, int position);
//
//         /**
//          * On clicked for the option view
//          * @param swipeTouchListener {@link SwipeTouchListener}
//          * @param swipable  the touched swipable
//          * @param optionView    the option view on clicked
//          */
//         void onOptionViewClicked(SwipeTouchListener swipeTouchListener, Swipable swipable, View optionView);
//     }
//
//     public static class SimpleSwipeTouchListener implements OnItemSwipeListener{
//
//         @Override
//         public boolean alertToDeleteSwipable(SwipeTouchListener swipeTouchListener, Swipable swipable) {
//             return false;
//         }
//
//         @Override
//         public void deleteSwipeable(SwipeTouchListener swipeTouchListener, View itemView, int position) {
//
//         }
//
//         @Override
//         public void onOptionViewClicked(SwipeTouchListener swipeTouchListener, Swipable swipable, View optionView) {
//
//         }
//     }
}
