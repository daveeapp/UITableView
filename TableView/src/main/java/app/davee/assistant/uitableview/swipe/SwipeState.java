package app.davee.assistant.uitableview.swipe;

/**
 * SwipeState
 * 0000 (CLOSED + NONE)
 * 0001 (CLOSED + SWIPING)
 * 1000 (OPENED + NONE)
 * 1001 (OPENED + SWIPING)
 * <p>
 * Created by davee 2018/3/25.
 * Copyright (c) 2018 davee. All rights reserved.
 */

public @interface SwipeState {
    
    //int MASK_SWIPING = 0x0001;
    //int MASK_SWIPE_OPENED = 0x0010;
    
    /**
     * Default state
     */
    int NONE = 0;
    
    /**
     * Opened
     */
    int OPENED = 2;
    
}
