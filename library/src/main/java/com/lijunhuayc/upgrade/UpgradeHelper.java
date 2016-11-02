package com.lijunhuayc.upgrade;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.lijunhuayc.downloader.downloader.DownloadProgressListener;
import com.lijunhuayc.downloader.downloader.DownloaderConfig;
import com.lijunhuayc.downloader.downloader.WolfDownloader;
import com.lijunhuayc.downloader.utils.LogUtils;
import com.lijunhuayc.upgrade.model.LocalAppInfo;
import com.lijunhuayc.upgrade.model.UpgradeInfoModel;
import com.lijunhuayc.upgrade.model.UpgradeInfoResult;
import com.lijunhuayc.upgrade.network.HttpUtils;
import com.lijunhuayc.upgrade.utils.MyToast;
import com.lijunhuayc.upgrade.utils.SharedPreferencesUtils;

import static com.lijunhuayc.upgrade.model.UpgradeInfoModel.ForceLevel.ABSOLUTE_FORCE;
import static com.lijunhuayc.upgrade.model.UpgradeInfoModel.ForceLevel.HALF_FORCE;
import static com.lijunhuayc.upgrade.model.UpgradeInfoModel.ForceLevel.NOT_FORCE;

/**
 * Desc:
 * Created by ${junhua.li} on 2016/10/21 18:21.
 * Email: lijunhuayc@sina.com
 */
public class UpgradeHelper {
    private static final String TAG = UpgradeHelper.class.getSimpleName();
    private Context mContext;
    private String upgradeUrl;
    private boolean isAutoStartInstall;
    private boolean isQuietDownload;
    private boolean isCheckPackageName;
    private long delay;
    private LocalAppInfo localAppInfo;

    public UpgradeHelper(Builder builder) {
        this.mContext = builder.mContext;
        this.upgradeUrl = builder.upgradeUrl;
        this.isAutoStartInstall = builder.isAutoStartInstall;
        this.isQuietDownload = builder.isQuietDownload;
        this.isCheckPackageName = builder.isCheckPackageName;
        this.delay = builder.delay;
        this.localAppInfo = readAppInfo(mContext);
        MyToast.init(mContext);
        SharedPreferencesUtils.init(mContext);
    }

    public void check() {
        new CheckAsyncTask().execute(upgradeUrl);
    }

    class CheckAsyncTask extends AsyncTask<String, Integer, UpgradeInfoResult> {

        @Override
        protected UpgradeInfoResult doInBackground(String... params) {
            try {
                if (delay > 0) Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return HttpUtils.parseResult(HttpUtils.getResponse(upgradeUrl));
        }

        @Override
        protected void onPostExecute(UpgradeInfoResult upgradeInfoResult) {
            super.onPostExecute(upgradeInfoResult);
            if (null != upgradeInfoResult && upgradeInfoResult.getStatus() == 1 && null != upgradeInfoResult.getData()) {
                executeResult(upgradeInfoResult.getData());
            } else {
                //TODO ...
            }

        }
    }

    private void executeResult(UpgradeInfoModel upgradeInfoModel) {
        if (isCheckPackageName) {
            if (!upgradeInfoModel.getPackageName().equals(localAppInfo.getPackageName())) {
                //enable set callback notify coder and coder can dispose callback notify server or not.
                MyToast.showToast("升级包与当前APP包名不同");
                return;
            }
        }
        if (upgradeInfoModel.getVersionCode() > localAppInfo.getVersionCode()) {
            showUpgradeAlertDialog(upgradeInfoModel);
        } else {
            //TODO ... if is about check upgrade, prompt than it's the latest version.
        }
    }

    private void showUpgradeAlertDialog(final UpgradeInfoModel upgradeInfoModel) {
        switch (upgradeInfoModel.getIsForce()){
            case NOT_FORCE:
                if(isQuietDownload){
                    //not force upgrade & is quiet download.
                    startDownload(upgradeInfoModel, true);
                }else {

                }
                break;
            case HALF_FORCE:
                break;
            case ABSOLUTE_FORCE:
                SharedPreferencesUtils.setInt("", upgradeInfoModel.getIsForce());

                break;

        }
        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        if (TextUtils.isEmpty(upgradeInfoModel.getUpgradeTitle())) {
            alertDialog.setTitle("升级提醒");
        } else {
            alertDialog.setTitle(upgradeInfoModel.getUpgradeTitle());
        }
        alertDialog.setMessage(upgradeInfoModel.getUpgradeNotes());
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(upgradeInfoModel.getIsForce() == NOT_FORCE);
        if (upgradeInfoModel.getIsForce() == NOT_FORCE) {
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "下次再说", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LogUtils.d(TAG, "cancel upgrade.");
                }
            });
        }
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "马上升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startDownload(upgradeInfoModel, false);
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void startDownload(UpgradeInfoModel upgradeInfoModel, boolean isQuietDownload) {
        String fileName = upgradeInfoModel.getAppName() + "_" + upgradeInfoModel.getVersionName() + ".apk";
        DownloaderConfig config = new DownloaderConfig()
                .setDownloadUrl(upgradeInfoModel.getApkUrl())
                .setThreadNum(3)
                .setSaveDir(mContext.getFilesDir())//saveDir = /data/data/packagename/files
                .setFileName(fileName)
                .setDownloadListener(new DownloadProgressListener() {
                    @Override
                    public void onDownloadTotalSize(int totalSize) {

                    }

                    @Override
                    public void updateDownloadProgress(int size, float percent, float speed) {

                    }

                    @Override
                    public void onDownloadSuccess(String apkPath) {

                    }

                    @Override
                    public void onDownloadFailed() {

                    }

                    @Override
                    public void onPauseDownload() {

                    }

                    @Override
                    public void onStopDownload() {

                    }
                });
        WolfDownloader wolfDownloader = config.buildWolf(mContext);
        wolfDownloader.startDownload();
    }

    private LocalAppInfo readAppInfo(Context mContext) {
        LocalAppInfo localAppInfo = new LocalAppInfo();
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (info != null) {
                ApplicationInfo appInfo = info.applicationInfo;
                localAppInfo.setAppName(pm.getApplicationLabel(appInfo).toString());
                localAppInfo.setPackageName(info.packageName);
                localAppInfo.setVersionCode(info.versionCode);
                localAppInfo.setVersionName(info.versionName);
            }
            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            localAppInfo.setImei(tm.getDeviceId());
            LogUtils.d(TAG, "about: LocalAppInfo = " + localAppInfo.toString());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return localAppInfo;
        }
        return localAppInfo;
    }

    public static class Builder {
        private static final String TAG = Builder.class.getSimpleName();
        private Context mContext;
        private String upgradeUrl;                      //upgrade check remote-interface.
        private boolean isAutoStartInstall = false;
        private boolean isQuietDownload = false;        //whether quiet download when the update is detected.
        private boolean isCheckPackageName = false;     //whether check the package name.
        private long delay = 0;                          //millisecond. whether delay check upgrade.

        public Builder(Context mContext) {
            this.mContext = mContext.getApplicationContext();
        }

        public Builder setUpgradeUrl(String upgradeUrl) {
            LogUtils.e(TAG, "upgradeUrl = " + upgradeUrl);
            if (TextUtils.isEmpty(upgradeUrl)) {
                throw new IllegalArgumentException("The URL is invalid.");
            }
            this.upgradeUrl = upgradeUrl;
            return this;
        }

        public Builder setAutoStartInstall(boolean autoStartInstall) {
            LogUtils.e(TAG, "autoStartInstall = " + autoStartInstall);
            isAutoStartInstall = autoStartInstall;
            return this;
        }

        public Builder setQuietDownload(boolean quietDownload) {
            LogUtils.e(TAG, "quietDownload = " + quietDownload);
            isQuietDownload = quietDownload;
            return this;
        }

        public Builder setCheckPackageName(boolean checkPackageName) {
            LogUtils.e(TAG, "checkPackageName = " + checkPackageName);
            isCheckPackageName = checkPackageName;
            return this;
        }

        public Builder setDelay(long delay) {
            LogUtils.e(TAG, "delay = " + delay);
            this.delay = delay;
            return this;
        }

        public UpgradeHelper build() {
            return new UpgradeHelper(this);
        }

    }
}
