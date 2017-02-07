package com.lijunhuayc.upgrade;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

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
import com.lijunhuayc.upgrade.view.EasyToastDialog;

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
    private boolean isAboutChecking;
    private long delay;
    private LocalAppInfo localAppInfo;

    private UpgradeHelper(Builder builder) {
        this.mContext = builder.mContext;
        this.upgradeUrl = builder.upgradeUrl;
        this.isAutoStartInstall = builder.isAutoStartInstall;
        this.isQuietDownload = builder.isQuietDownload;
        this.isCheckPackageName = builder.isCheckPackageName;
        this.isAboutChecking = builder.isAboutChecking;
        this.delay = builder.delay;
        this.localAppInfo = readAppInfo(mContext);
        MyToast.init(mContext);
        SharedPreferencesUtils.init(mContext);
    }

    public void check() {
        new CheckAsyncTask().execute(upgradeUrl);
    }

    private class CheckAsyncTask extends AsyncTask<String, Integer, UpgradeInfoResult> {

        @Override
        protected UpgradeInfoResult doInBackground(String... params) {
            try {
                if (delay > 0) {
                    Thread.sleep(delay);
                }
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
                MyToast.showToast(mContext.getString(R.string.package_name_is_different_label));
                return;
            }
        }
        if (upgradeInfoModel.getVersionCode() > localAppInfo.getVersionCode()) {
            showUpgradeAlertDialog(upgradeInfoModel);
        } else if (isAboutChecking) {
            //TO-DO ... if is "about" check upgrade, prompt than it's the latest version.
            MyToast.showToast(mContext.getString(R.string.is_latest_version_label));
        }
    }

    private CharSequence getString(int resId) {
        return mContext.getResources().getString(resId);
    }

    private void showUpgradeAlertDialog(final UpgradeInfoModel upgradeInfoModel) {
        switch (upgradeInfoModel.getIsForce()) {
            case NOT_FORCE://Dialog box can cancel when touch the outside.
                //not force upgrade & whether quiet download.
                startDownload(upgradeInfoModel, isQuietDownload);
                if (isQuietDownload) {
                } else {
                }
                break;
            case HALF_FORCE://Dialog box can click button cancel.
                EasyToastDialog exToast = new EasyToastDialog(mContext);
                exToast.setTitle(getString(R.string.dialog_title_default))
                        .setMessage(upgradeInfoModel.getUpgradeNotes())
                        .setDuration(EasyToastDialog.LENGTH_ALWAYS)
                        .setCanceledOnTouchOutside(false)
                        .setPositive(getString(R.string.dialog_btn_label_upgrade), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .setNegative(getString(R.string.dialog_btn_label_cancel), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .show();
                break;
            case ABSOLUTE_FORCE://Dialog box can never cancel.
                SharedPreferencesUtils.setInt("", upgradeInfoModel.getIsForce());
                //TODO ... 强制升级只能在前端升级

                break;

        }
//        EasyToastDialog.makeText(mContext, "您有新的升级\n您有新的升级\n您有新的升级\n您有新的升级", EasyToastDialog.LENGTH_ALWAYS).show();

//        //todo...
//        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
//        if (TextUtils.isEmpty(upgradeInfoModel.getUpgradeTitle())) {
//            alertDialog.setTitle("升级提醒");
//        } else {
//            alertDialog.setTitle(upgradeInfoModel.getUpgradeTitle());
//        }
//        alertDialog.setMessage(upgradeInfoModel.getUpgradeNotes());
//        alertDialog.setCanceledOnTouchOutside(false);
//        alertDialog.setCancelable(upgradeInfoModel.getIsForce() == NOT_FORCE);
//        if (upgradeInfoModel.getIsForce() == NOT_FORCE) {
//            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "下次再说", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    LogUtils.d(TAG, "cancel upgrade.");
//                }
//            });
//        }
//        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "马上升级", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                startDownload(upgradeInfoModel, false);
//                dialog.cancel();
//            }
//        });
//        alertDialog.show();
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
        private boolean isCheckPackageName = true;      //whether check the package name.
        private boolean isAboutChecking = false;        //whether is "about" check upgrade.
        private long delay = 0;                         //millisecond. whether delay check upgrade.

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
