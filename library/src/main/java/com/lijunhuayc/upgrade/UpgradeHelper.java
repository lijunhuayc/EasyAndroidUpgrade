package com.lijunhuayc.upgrade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.StringRes;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.lijunhuayc.downloader.downloader.DownloadProgressListener;
import com.lijunhuayc.downloader.downloader.DownloaderConfig;
import com.lijunhuayc.downloader.downloader.FileDownloader;
import com.lijunhuayc.downloader.downloader.HistoryCallback;
import com.lijunhuayc.downloader.downloader.WolfDownloader;
import com.lijunhuayc.downloader.utils.LogUtils;
import com.lijunhuayc.upgrade.model.LocalAppInfo;
import com.lijunhuayc.upgrade.model.UpgradeInfoModel;
import com.lijunhuayc.upgrade.model.UpgradeInfoResult;
import com.lijunhuayc.upgrade.network.HttpUtils;
import com.lijunhuayc.upgrade.utils.MD5Utils;
import com.lijunhuayc.upgrade.utils.MyToast;
import com.lijunhuayc.upgrade.utils.NetworkUtil;
import com.lijunhuayc.upgrade.utils.SharedPreferencesUtils;
import com.lijunhuayc.upgrade.view.EasyToastDialog;

import java.io.File;

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
    BroadcastReceiver broadcastReceiver;

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
        if (NetworkUtil.isNetworkAvailable(mContext)) {
            new CheckAsyncTask().setUrl(upgradeUrl).setDelay(delay).execute();
            if (null != broadcastReceiver) {
                mContext.unregisterReceiver(broadcastReceiver);
            }
        } else {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    check();
                }
            };
            IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            mContext.registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    private class CheckAsyncTask extends AsyncTask<String, Integer, UpgradeInfoResult> {
        String url;
        long delayMS;

        CheckAsyncTask setUrl(String url) {
            this.url = url;
            return this;
        }

        CheckAsyncTask setDelay(long delayMS) {
            this.delayMS = delayMS;
            return this;
        }

        @Override
        protected UpgradeInfoResult doInBackground(String... params) {
            try {
                if (delayMS > 0) {
                    Thread.sleep(delayMS);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return HttpUtils.parseResult(HttpUtils.getResponse(url));
        }

        @Override
        protected void onPostExecute(UpgradeInfoResult upgradeInfoResult) {
            super.onPostExecute(upgradeInfoResult);
            if (null != upgradeInfoResult && upgradeInfoResult.getStatus() == 1) {
                if (null != upgradeInfoResult.getData()) {
                    executeResult(upgradeInfoResult.getData());
                } else {
                    //if upgradeInfoResult.data is null will is latest version.
                    if (isAboutChecking) {//if is "about" check upgrade, prompt than it's the latest version.
                        MyToast.showToast(mContext.getString(R.string.is_latest_version_label));
                    }
                }
            }
        }
    }

    private void executeResult(UpgradeInfoModel upgradeInfoModel) {
        if (isCheckPackageName && !upgradeInfoModel.getPackageName().equals(localAppInfo.getPackageName())) {
            //enable set callback notify coder and coder can dispose callback notify server or not.
            MyToast.showToast(mContext.getString(R.string.package_name_is_different_label));
            return;
        }
        if (upgradeInfoModel.getVersionCode() > localAppInfo.getVersionCode()) {
            showUpgradeToastDialog(upgradeInfoModel);
        } else if (isAboutChecking) {
            //if is "about" check upgrade, prompt than it's the latest version.
            MyToast.showToast(mContext.getString(R.string.is_latest_version_label));
        }
    }

    private void showUpgradeToastDialog(final UpgradeInfoModel upgradeInfoModel) {
        final EasyToastDialog exToast = new EasyToastDialog(mContext)
                .setMode(EasyToastDialog.MODE_MESSAGE)
                .setTitle(getString(R.string.dialog_title_default))
                .setMessage(upgradeInfoModel.getUpgradeNotes())
                .setDuration(EasyToastDialog.LENGTH_ALWAYS);
        //not force upgrade & whether quiet download.
        switch (upgradeInfoModel.getIsForce()) {
            case NOT_FORCE://Dialog box can cancel when touch the outside.
                exToast.setCanceledOnTouchOutside(true)
                        .setPositive(getString(R.string.dialog_btn_label_upgrade), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                exToast.hide();
                                startDownload(upgradeInfoModel, isQuietDownload);
                            }
                        })
                        .setNegative(getString(R.string.dialog_btn_label_talk_later), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                exToast.hide();
                            }
                        });
                break;
            case HALF_FORCE://Dialog box can click button cancel.
                exToast.setCancelable(true)
                        .setPositive(getString(R.string.dialog_btn_label_upgrade), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                exToast.hide();
                                startDownload(upgradeInfoModel, isQuietDownload);
                            }
                        })
                        .setNegative(getString(R.string.dialog_btn_label_talk_later), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                exToast.hide();
                            }
                        })
                        .show();
                break;
            case ABSOLUTE_FORCE://Dialog box can never cancel.
                exToast.setPositive(getString(R.string.dialog_btn_label_upgrade), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        exToast.hide();
                        startDownload(upgradeInfoModel, isQuietDownload);
                    }
                });
                break;
        }
        exToast.show();
    }

    /**
     * undetermined
     *
     * @param upgradeInfoModel
     */
    private void showUpgradeAlertDialog(final UpgradeInfoModel upgradeInfoModel) {
//        //todo...
//        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();//AlertDialog must show in activity.
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

    private void startDownload(final UpgradeInfoModel upgradeInfoModel, boolean isQuietDownload) {
        final EasyToastDialog exToast = new EasyToastDialog(mContext)
                .setTitle(getString(R.string.dialog_title_download))
                .setDuration(EasyToastDialog.LENGTH_ALWAYS)
                .setMode(EasyToastDialog.MODE_PROGRESS);
        String fileName = upgradeInfoModel.getAppName() + "_" + upgradeInfoModel.getVersionName() + ".apk";
        File saveFile = mContext.getExternalFilesDir("");
        File apkFile = new File(saveFile, fileName);
        // TODO: 2017/02/08  检测apk是否已经下载版本是否正确文件是否完整
        // TODO: ...  否则重新下载
        final DownloaderConfig config = new DownloaderConfig()
                .setDownloadUrl(upgradeInfoModel.getApkUrl())
                .setThreadNum(3)
                .setSaveDir(saveFile)//saveDir = /data/data/packagename/files
                .setFileName(fileName)
                .setDownloadListener(new DownloadProgressListener() {
                    @Override
                    public void onDownloadTotalSize(int totalSize) {
                        exToast.setProgressMax(totalSize);
                    }

                    @Override
                    public void updateDownloadProgress(int size, float percent, float speed) {
                        exToast.setProgressBarSize(size);
                        exToast.setDownloadPercentLabelText(String.valueOf(percent + "%"));
                        exToast.setDownloadSizeLabelText(new StringBuilder()
                                .append(FileDownloader.formatSize(size))
                                .append("/")
                                .append(FileDownloader.formatSize(exToast.getProgressMax())));
                        exToast.setDownloadSpeedLabelText(FileDownloader.formatSpeed(speed));
                    }

                    @Override
                    public void onDownloadSuccess(String apkPath) {
                        exToast.hide();
                        installAPK(upgradeInfoModel, apkPath);
                    }

                    @Override
                    public void onDownloadFailed() {
                        // TODO: 2017/02/08   提示重新下载
                    }

                    @Override
                    public void onPauseDownload() {
                    }

                    @Override
                    public void onStopDownload() {
                    }
                });
        WolfDownloader wolfDownloader = config.buildWolf(mContext);
        wolfDownloader.readHistory(new HistoryCallback() {
            @Override
            public void onReadHistory(int downloadLength, int fileSize) {
                exToast.setProgressMax(fileSize);
                exToast.setProgressBarSize(downloadLength);

                exToast.setDownloadPercentLabelText(String.valueOf(FileDownloader.calculatePercent(downloadLength, fileSize) + "%"));
                exToast.setDownloadSizeLabelText(new StringBuilder()
                        .append(FileDownloader.formatSize(downloadLength))
                        .append("/")
                        .append(FileDownloader.formatSize(exToast.getProgressMax())));
                exToast.setDownloadSpeedLabelText(FileDownloader.formatSpeed(0));
            }
        });
        exToast.show();
        wolfDownloader.startDownload();
    }

    private void installAPK(UpgradeInfoModel model, String apkPath) {
        File apkFile = new File(apkPath);
        if (model.getMd5().equals(MD5Utils.getFileMD5(apkFile))) {
            LogUtils.d(TAG, "apkPath = " + apkPath);
            LogUtils.d(TAG, "apkSize = " + apkFile.length());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } else {
            MyToast.showToast(getString(R.string.upgrade_info_exception).toString());
        }
    }

    private CharSequence getString(@StringRes int resId) {
        return mContext.getResources().getString(resId);
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
//            this.mContext = mContext;
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
