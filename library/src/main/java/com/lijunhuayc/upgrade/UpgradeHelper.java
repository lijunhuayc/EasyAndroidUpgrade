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
import android.util.Log;

import com.lijunhuayc.downloader.downloader.DownloadProgressListener;
import com.lijunhuayc.downloader.downloader.DownloaderConfig;
import com.lijunhuayc.downloader.downloader.WolfDownloader;
import com.lijunhuayc.upgrade.model.LocalAppInfo;
import com.lijunhuayc.upgrade.model.UpgradeInfoModel;
import com.lijunhuayc.upgrade.model.UpgradeInfoResult;
import com.lijunhuayc.upgrade.network.HttpUtils;
import com.lijunhuayc.upgrade.utils.LogUtils;

import java.io.File;

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
    private LocalAppInfo localAppInfo;

    public UpgradeHelper(Builder builder) {
        this.mContext = builder.mContext;
        this.upgradeUrl = builder.upgradeUrl;
        this.isAutoStartInstall = builder.isAutoStartInstall;
        this.isQuietDownload = builder.isQuietDownload;
        this.localAppInfo = readAppInfo(mContext);
    }

    public void check() {
        new CheckAsyncTask().execute(upgradeUrl);
    }

    class CheckAsyncTask extends AsyncTask<String, Integer, UpgradeInfoResult> {

        @Override
        protected UpgradeInfoResult doInBackground(String... params) {
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
        if (upgradeInfoModel.getVersionCode() > localAppInfo.getVersionCode()) {
            if (upgradeInfoModel.getIsForce() != UpgradeInfoModel.ForceLevel.NOT_FORCE) {
                showUpgradeAlertDialog(upgradeInfoModel);
            } else {
                if (isQuietDownload) {
                    //not force upgrade & is quiet download.

                } else {
                    showUpgradeAlertDialog(upgradeInfoModel);
                }
            }
        } else {
            //TODO ... if is about check upgrade, prompt than it's the latest version.
        }
    }

    private void showUpgradeAlertDialog(final UpgradeInfoModel upgradeInfoModel) {
        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        if (TextUtils.isEmpty(upgradeInfoModel.getUpgradeTitle())) {
            alertDialog.setTitle("升级提醒");
        } else {
            alertDialog.setTitle(upgradeInfoModel.getUpgradeTitle());
        }
        alertDialog.setMessage(upgradeInfoModel.getUpgradeNotes());
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(upgradeInfoModel.getIsForce() == UpgradeInfoModel.ForceLevel.NOT_FORCE);
        if (upgradeInfoModel.getIsForce() == UpgradeInfoModel.ForceLevel.NOT_FORCE) {
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
                startDownload(upgradeInfoModel);
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void startDownload(UpgradeInfoModel upgradeInfoModel) {
        String fileName = localAppInfo.getAppName() + "_" + localAppInfo.getVersionName() + ".apk";
        String filePath = mContext.getFilesDir().getPath() + File.separator + fileName;
        File saveFile = new File(filePath);
        DownloaderConfig config = new DownloaderConfig()
                .setDownloadUrl(upgradeInfoModel.getApkUrl())
                .setThreadNum(3)
                .setSaveDir(mContext.getFilesDir())
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
            Log.d(TAG, "about: LocalAppInfo = " + localAppInfo.toString());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return localAppInfo;
        }
        return localAppInfo;
    }

    public class Builder {
        private Context mContext;
        private String upgradeUrl;                      //upgrade interface
        private boolean isAutoStartInstall = false;
        private boolean isQuietDownload = false;        //whether quiet download when the update is detected.

        public Builder(Context mContext) {
            this.mContext = mContext;
        }

        public void setUpgradeUrl(String upgradeUrl) {
            LogUtils.e(TAG, "upgradeUrl = " + upgradeUrl);
            if (TextUtils.isEmpty(upgradeUrl)) {
                throw new IllegalArgumentException("The URL is invalid.");
            }
            this.upgradeUrl = upgradeUrl;
        }

        public void setAutoStartInstall(boolean autoStartInstall) {
            isAutoStartInstall = autoStartInstall;
        }

        public void setQuietDownload(boolean quietDownload) {
            isQuietDownload = quietDownload;
        }

        public UpgradeHelper build() {
            return new UpgradeHelper(this);
        }

    }
}
