package com.lijunhuayc.upgrade.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Desc:
 * Created by ${junhua.li} on 2016/03/22 13:30.
 * Email: jli@bpexps.com
 */
public class MyToast {
    static Toast toast;
    static Context mContext;
    static Handler mHandler;

    public static void init(Context context) {
        mContext = context;
        mHandler = new Handler(context.getMainLooper());
    }

    public static void showToast(String text, int duration, int gravity) {
        if (toast == null) {
            if (null == mContext) {
                throw new RuntimeException("MyToast uninitialized exception.");
//                mContext = MyApplication.getApplication().getApplicationContext();
            }
            toast = Toast.makeText(mContext, text, duration);
        }
        toast.setText(text);
        toast.setDuration(duration);
        toast.setGravity(gravity, 0, dip2px(mContext, 64));
        if (Looper.myLooper() == Looper.getMainLooper()) {
            toast.show();
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    toast.show();
                }
            });
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static void showToast(String text) {
        showToast(text, Toast.LENGTH_SHORT, Gravity.BOTTOM);
    }

    public static void showToast(String text, int duration) {
        showToast(text, duration, Gravity.BOTTOM);
    }

    private MyToast() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }


}