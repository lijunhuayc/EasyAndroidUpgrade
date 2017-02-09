package com.lijunhuayc.upgrade.view;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.lijunhuayc.upgrade.R;

import java.util.List;

/**
 * Desc:
 * Created by ${junhua.li} on 2017/02/09 10:25.
 * Email: jli@bpexps.com
 */
public abstract class BaseActivity extends FragmentActivity {
    protected String TAG = getClass().getSimpleName();
    public Context mContext;
    public Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        mContext = this;
        setContentView(initLayout());
        init();
    }

    protected abstract int initLayout();

    protected abstract void init();

    public AlertDialog.Builder createAlertDialog() {
        return new AlertDialog.Builder(mContext, R.style.AppTheme_Dialog_Alert);
    }

    public boolean isAppOnForeground() {
        //Returns a list of application processes that are running on the device
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    public View inflateView(@LayoutRes int resource) {
        return View.inflate(mContext, resource, null);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

}
