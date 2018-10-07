package com.example.suguoqing.radarscan.utlis;

import android.content.Context;

public class DisplayUtils {

    /**
     * 将dp的值装换成px的值
     * @param context
     * @param i
     * @return
     */
    public static int dp2px(Context context, int i) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(i*scale + 0.5f);
    }
}
