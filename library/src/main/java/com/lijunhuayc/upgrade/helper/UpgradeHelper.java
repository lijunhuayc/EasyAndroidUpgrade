package com.lijunhuayc.upgrade.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.lijunhuayc.upgrade.model.LocalAppInfo;
import com.lijunhuayc.upgrade.model.UpgradeConfig;
import com.lijunhuayc.upgrade.network.NetworkUtils;
import com.lijunhuayc.upgrade.utils.MyToast;
import com.lijunhuayc.upgrade.utils.SharedPreferencesUtils;

/**
 * Desc:
 * Created by ${junhua.li} on 2016/10/21 18:21.
 * Email: lijunhuayc@sina.com
 */
public class UpgradeHelper {
    private static final String TAG = UpgradeHelper.class.getSimpleName();
    private Context mContext;
    private UpgradeConfig config;
    private boolean isRegisterBR;
    private BroadcastReceiver broadcastReceiver;

    private UpgradeHelper(Builder builder) {
        this.mContext = builder.getContext();
        this.config = builder.getConfig();
        LocalAppInfo.init(mContext);
        MyToast.init(mContext);
        SharedPreferencesUtils.init(mContext);
    }

    public void check() {
        if (NetworkUtils.isNetworkAvailable(mContext)) {
            if (null != broadcastReceiver && isRegisterBR) {
                mContext.unregisterReceiver(broadcastReceiver);
                isRegisterBR = false;
            }
            new CheckAsyncTask(mContext).setConfig(config).execute();
        } else {
            if (!isRegisterBR) {
                broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        check();
                    }
                };
                IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                mContext.registerReceiver(broadcastReceiver, intentFilter);
                isRegisterBR = true;
            }
        }
    }

    public static class Builder {
        private static final String TAG = Builder.class.getSimpleName();
        private Context mContext;
        private UpgradeConfig config;

        public Builder(Context mContext) {
            this.mContext = mContext.getApplicationContext();
            config = new UpgradeConfig();
        }

        Context getContext() {
            return mContext;
        }

        UpgradeConfig getConfig() {
            return config;
        }

        public Builder setUpgradeUrl(String upgradeUrl) {
            if (TextUtils.isEmpty(upgradeUrl)) {
                throw new IllegalArgumentException("The URL is invalid.");
            }
            this.config.setUpgradeUrl(upgradeUrl);
            return this;
        }

        private Builder setAutoStartInstall(boolean autoStartInstall) {
            this.config.setAutoStartInstall(autoStartInstall);
            return this;
        }

        public Builder setQuietDownload(boolean quietDownload) {
            this.config.setQuietDownload(quietDownload);
            return this;
        }

        public Builder setCheckPackageName(boolean checkPackageName) {
            this.config.setCheckPackageName(checkPackageName);
            return this;
        }

        public Builder setIsAboutChecking(boolean aboutChecking) {
            this.config.setAboutChecking(aboutChecking);
            return this;
        }

        public Builder setDelay(long delay) {
            this.config.setDelay(delay);
            return this;
        }

        public UpgradeHelper build() {
            return new UpgradeHelper(this);
        }

    }
}
