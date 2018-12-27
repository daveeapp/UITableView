package app.davee.assistant.uitableview;

import android.content.Context;

/**
 * DimensionUtils
 * <p>
 * Created by davee 2017/3/22.
 * Copyright (c) 2017 davee. All rights reserved.
 */

public class DimensionUtils {
    
    ///////////////////////////////////////////////////////////////////////////
    // Resources
    ///////////////////////////////////////////////////////////////////////////
    
    public static int resolveDp(Context context, int resId){
        return context.getResources().getDimensionPixelOffset(resId);
    }
    
    public static int dp2px(Context context, int dp){
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5f);
    }
    
}
