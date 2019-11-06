package com.liu.custom.utils;

import android.content.Context;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * Created by yangcaihao on 16/5/20.
 */
public class DensityUtil {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static int calcTextWidth(Paint paint, String demoText) {
        return (int) paint.measureText(demoText);
    }

    /**
     * 适配三星手机屏幕缩放（暂时）
     */
    public static void adapterScreen(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        Log.e("density", "adapterScreen:density= "+  displayMetrics.density+",scaledDensity="+displayMetrics.scaledDensity);
        if (displayMetrics.density > 4||displayMetrics.scaledDensity>4) {
            displayMetrics.scaledDensity=4;
            displayMetrics.density = 4;
            displayMetrics.densityDpi = 640;
        }else{
            displayMetrics.scaledDensity = displayMetrics.density > displayMetrics.scaledDensity ? displayMetrics.density : displayMetrics.scaledDensity;
            displayMetrics.density=displayMetrics.scaledDensity;
        }
    }

    public static boolean isScreenBiggerThan720_1280(Context context) {
        boolean result = false;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        if (displayMetrics.widthPixels > 720 && displayMetrics.heightPixels > 1280) {
            result = true;
        }
        return result;
    }
}
