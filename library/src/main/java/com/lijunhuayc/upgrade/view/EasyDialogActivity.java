package com.lijunhuayc.upgrade.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lijunhuayc.downloader.downloader.DownloadProgressListener;
import com.lijunhuayc.downloader.downloader.DownloaderConfig;
import com.lijunhuayc.downloader.downloader.FileDownloader;
import com.lijunhuayc.downloader.downloader.HistoryCallback;
import com.lijunhuayc.downloader.downloader.WolfDownloader;
import com.lijunhuayc.downloader.utils.LogUtils;
import com.lijunhuayc.upgrade.R;
import com.lijunhuayc.upgrade.model.UpgradeConfig;
import com.lijunhuayc.upgrade.model.UpgradeInfoModel;
import com.lijunhuayc.upgrade.utils.MD5Utils;
import com.lijunhuayc.upgrade.utils.MyToast;

import java.io.File;

import static com.lijunhuayc.upgrade.model.UpgradeInfoModel.ForceLevel.ABSOLUTE_FORCE;
import static com.lijunhuayc.upgrade.model.UpgradeInfoModel.ForceLevel.NOT_FORCE;

/**
 * Desc:
 * Created by ${junhua.li} on 2017/02/09 10:16.
 * Email: lijunhuayc@sina.com
 */
public class EasyDialogActivity extends BaseActivity {
    private boolean isCancelable = false;
    private static final int STOP = 0;
    private static final int START = 1;
    private static final int PAUSE = 2;
    private int downloadStatus = STOP; //1 start 2 pause 0 stop

    @Override
    protected int initLayout() {
        return R.layout.easy_dialog_activity_layout;
    }

    UpgradeInfoModel upgradeInfoModel;
    UpgradeConfig config;

    @Override
    protected void init() {
        upgradeInfoModel = getIntent().getParcelableExtra("model");
        config = getIntent().getParcelableExtra("config");
        execute();
    }

    private void execute() {
        AlertDialog.Builder alertDialog = createAlertDialog()
                .setTitle(getString(R.string.dialog_title_default))
                .setMessage(upgradeInfoModel.getUpgradeNotes())
                .setCancelable(false);
        switch (upgradeInfoModel.getIsForce()) {
            case NOT_FORCE:
                alertDialog.setPositiveButton(getString(R.string.dialog_btn_label_upgrade), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startDownload(upgradeInfoModel);
                    }
                }).setNegativeButton(getString(R.string.dialog_btn_label_talk_later), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                break;
            case ABSOLUTE_FORCE://Dialog box can never cancel.
                alertDialog.setPositiveButton(getString(R.string.dialog_btn_label_upgrade), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startDownload(upgradeInfoModel);
                    }
                });
                break;
        }
        alertDialog.show();
    }

    private void startDownload(final UpgradeInfoModel upgradeInfoModel) {
        View mProgressLayout = inflateView(R.layout.progress_bar_view);
        final TextView mPercentLabelTV = (TextView) mProgressLayout.findViewById(R.id.percentLabelTV);
        final TextView mSizeLabelTV = (TextView) mProgressLayout.findViewById(R.id.sizeLabelTV);
        final TextView mSpeedLabelTV = (TextView) mProgressLayout.findViewById(R.id.speedLabelTV);
        final ProgressBar mProgressBar = (ProgressBar) mProgressLayout.findViewById(R.id.progressBar);
        final TextView mButtonPositive = (TextView) mProgressLayout.findViewById(R.id.mButtonPositive);
        final TextView mButtonNegative = (TextView) mProgressLayout.findViewById(R.id.mButtonNegative);

        final AlertDialog alertDialog = createAlertDialog()
                .setTitle(getString(R.string.dialog_title_download))
                .setCancelable(false)
                .setView(mProgressLayout)
                .create();

        String fileName = upgradeInfoModel.getAppName() + "_" + upgradeInfoModel.getVersionName() + ".apk";
        File saveFile = mContext.getExternalFilesDir("");
        File apkFile = new File(saveFile, fileName);
        if (apkFile.exists()) {
            if (upgradeInfoModel.getMd5().equals(MD5Utils.MD5EncodeFile(apkFile))) {
                installAPK(apkFile);
                finish();
                return;
            }
        }
        final DownloaderConfig config = new DownloaderConfig()
                .setDownloadUrl(upgradeInfoModel.getApkUrl())
                .setThreadNum(3)
                .setSaveDir(saveFile)//saveDir = /storage/emulated/0/Android/data/com.echi.future/files
                .setFileName(fileName)
                .setDownloadListener(new DownloadProgressListener() {
                    @Override
                    public void onDownloadTotalSize(int totalSize) {
                        mProgressBar.setMax(totalSize);
                    }

                    @Override
                    public void updateDownloadProgress(int size, float percent, float speed) {
                        mProgressBar.setProgress(size);
                        mPercentLabelTV.setText(String.valueOf(percent + "%"));
                        mSizeLabelTV.setText(new StringBuilder()
                                .append(FileDownloader.formatSize(size))
                                .append("/")
                                .append(FileDownloader.formatSize(mProgressBar.getMax())));
                        mSpeedLabelTV.setText(FileDownloader.formatSpeed(speed));
                    }

                    @Override
                    public void onDownloadSuccess(String apkPath) {
                        alertDialog.cancel();
                        finish();

                        File apkFile = new File(apkPath);
                        LogUtils.d(TAG, "apkPath = " + apkPath);
                        LogUtils.d(TAG, "apkSize = " + apkFile.length());
                        LogUtils.d(TAG, "apkMD5 = " + MD5Utils.MD5EncodeFile(apkFile));
                        if (upgradeInfoModel.getMd5().equals(MD5Utils.MD5EncodeFile(apkFile))) {
                            installAPK(apkFile);
                        } else {
                            MyToast.showToast(getString(R.string.upgrade_info_exception));
                        }
                    }

                    @Override
                    public void onDownloadFailed() {
                        alertDialog.cancel();

                        createAlertDialog()
                                .setTitle(getString(R.string.dialog_title_alert))
                                .setMessage(getString(R.string.upgrade_download_exception))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.dialog_btn_label_re_download), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startDownload(upgradeInfoModel);
                                    }
                                })
                                .setNeutralButton(getString(R.string.dialog_btn_label_cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                    }

                    @Override
                    public void onPauseDownload() {
                    }

                    @Override
                    public void onStopDownload() {
                    }
                });
        final WolfDownloader wolfDownloader = config.buildWolf(mContext);
        wolfDownloader.readHistory(new HistoryCallback() {
            @Override
            public void onReadHistory(int downloadLength, int fileSize) {
                mProgressBar.setMax(fileSize);
                mProgressBar.setProgress(downloadLength);
                mPercentLabelTV.setText(String.valueOf(FileDownloader.calculatePercent(downloadLength, fileSize) + "%"));
                mSizeLabelTV.setText(new StringBuilder()
                        .append(FileDownloader.formatSize(downloadLength))
                        .append("/")
                        .append(FileDownloader.formatSize(mProgressBar.getMax())));
                mSpeedLabelTV.setText(FileDownloader.formatSpeed(0));
            }
        });

        mButtonPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (downloadStatus == START) {
                    wolfDownloader.pauseDownload();
                    downloadStatus = PAUSE;
                    mButtonPositive.setText(getString(R.string.dialog_btn_label_continue));
                } else if (downloadStatus == PAUSE) {
                    wolfDownloader.restartDownload();
                    downloadStatus = START;
                    mButtonPositive.setText(getString(R.string.dialog_btn_label_pause));
                }
            }
        });
        mButtonNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlertDialog()
                        .setTitle(getString(R.string.dialog_title_alert))
                        .setMessage(getString(R.string.download_info_msg))
                        .setNeutralButton(getString(R.string.dialog_btn_label_not_download), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                wolfDownloader.exitDownload();
                                downloadStatus = STOP;
                                alertDialog.cancel();
                                finish();
                            }
                        })
                        .setPositiveButton(getString(R.string.dialog_btn_label_continue_download), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
            }
        });
        alertDialog.show();
        downloadStatus = START;
        wolfDownloader.startDownload();
    }

    private void installAPK(File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isCancelable) {
            return super.onTouchEvent(event);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (isCancelable) {
            super.onBackPressed();
        }
    }
}
