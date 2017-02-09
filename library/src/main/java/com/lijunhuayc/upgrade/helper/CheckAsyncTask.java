package com.lijunhuayc.upgrade.helper;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.lijunhuayc.upgrade.R;
import com.lijunhuayc.upgrade.model.LocalAppInfo;
import com.lijunhuayc.upgrade.model.UpgradeConfig;
import com.lijunhuayc.upgrade.model.UpgradeInfoModel;
import com.lijunhuayc.upgrade.model.UpgradeInfoResult;
import com.lijunhuayc.upgrade.network.HttpUtils;
import com.lijunhuayc.upgrade.utils.MyToast;
import com.lijunhuayc.upgrade.view.EasyDialogActivity;

/**
 * Desc:
 * Created by ${junhua.li} on 2017/02/09 11:45.
 * Email: lijunhuayc@sina.com
 */
class CheckAsyncTask extends AsyncTask<String, Integer, UpgradeInfoResult> {
    private static final String TAG = CheckAsyncTask.class.getSimpleName();
    private UpgradeConfig config;
    private Context mContext;

    CheckAsyncTask(Context context) {
        this.mContext = context;
    }

    CheckAsyncTask setConfig(UpgradeConfig config) {
        this.config = config;
        return this;
    }

    @Override
    protected UpgradeInfoResult doInBackground(String... params) {
        try {
            if (config.getDelay() > 0) {
                Thread.sleep(config.getDelay());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return HttpUtils.parseResult(HttpUtils.getResponse(config.getUpgradeUrl()));
    }

    @Override
    protected void onPostExecute(UpgradeInfoResult upgradeInfoResult) {
        super.onPostExecute(upgradeInfoResult);
        if (null != upgradeInfoResult && upgradeInfoResult.getStatus() == 1) {
            if (null != upgradeInfoResult.getData()) {
                executeResult(upgradeInfoResult.getData());
            } else if (config.isAboutChecking()) {
                //if is "about" check upgrade, prompt than it's the latest version.
                MyToast.showToast(mContext.getString(R.string.is_latest_version_label));

            }
        }
    }

    private void executeResult(UpgradeInfoModel upgradeInfoModel) {
        LocalAppInfo localAppInfo = LocalAppInfo.getLocalAppInfo();
        if (config.isCheckPackageName()
                && !upgradeInfoModel.getPackageName().equals(localAppInfo.getPackageName())) {
            //enable set callback notify coder and coder can dispose callback notify server or not.
            MyToast.showToast(mContext.getString(R.string.package_name_is_different_label));
            return;
        }
        if (upgradeInfoModel.getVersionCode() > localAppInfo.getVersionCode()) {
            showUpgradeAlertDialog(upgradeInfoModel, config);
        } else if (config.isAboutChecking()) {
            //if is "about" check upgrade, prompt than it's the latest version.
            MyToast.showToast(mContext.getString(R.string.is_latest_version_label));
        }
    }

    private void showUpgradeAlertDialog(UpgradeInfoModel upgradeInfoModel, UpgradeConfig config) {
        Intent intent = new Intent(mContext, EasyDialogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("model", upgradeInfoModel);
        intent.putExtra("config", config);
        mContext.startActivity(intent);
    }

}
