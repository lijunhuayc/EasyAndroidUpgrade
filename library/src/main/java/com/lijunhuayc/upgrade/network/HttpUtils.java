package com.lijunhuayc.upgrade.network;

import android.text.TextUtils;

import com.lijunhuayc.upgrade.model.UpgradeInfoModel;
import com.lijunhuayc.upgrade.model.UpgradeInfoResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Desc:
 * Created by ${junhua.li} on 2016/10/25 10:30.
 * Email: lijunhuayc@sina.com
 */
public class HttpUtils {
    public static String getResponse(String urlParam) {
        try {
            URL url = new URL(urlParam);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE6.0; Windows NT 5.1; SV1)");
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = conn.getInputStream();
                return inputStream2String(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    private static String inputStream2String(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] array = new byte[1024];
            int len;
            while ((len = is.read(array, 0, array.length)) != -1) {
                baos.write(array, 0, len);
            }
            return baos.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static UpgradeInfoResult parseResult(String result) {
        if (!TextUtils.isEmpty(result)) {
            try {
                UpgradeInfoResult upgradeInfoResult = new UpgradeInfoResult();
                JSONObject jsonObject = new JSONObject(result);
                upgradeInfoResult.setStatus(jsonObject.getInt("status"));
                upgradeInfoResult.setMessage(jsonObject.getString("message"));
                if (upgradeInfoResult.getStatus() == 1) {
                    UpgradeInfoModel upgradeInfoModel = new UpgradeInfoModel();
                    JSONObject jsonData = jsonObject.getJSONObject("data");
                    upgradeInfoModel.setAppName(jsonData.getString("appName"));
                    upgradeInfoModel.setVersionCode(jsonData.getInt("versionCode"));
                    upgradeInfoModel.setVersionName(jsonData.getString("versionName"));
                    upgradeInfoModel.setPackageName(jsonData.getString("packageName"));
                    upgradeInfoModel.setApkUrl(jsonData.getString("apkUrl"));
                    upgradeInfoModel.setUpgradeNotes(jsonData.getString("upgradeNotes"));
                    upgradeInfoModel.setUpgradeTitle(jsonData.getString("upgradeTitle"));
                    upgradeInfoModel.setIsForce(jsonData.getInt("isForce"));
                    upgradeInfoModel.setFileSize(jsonData.getInt("fileSize"));
                    upgradeInfoModel.setMd5(jsonData.getString("md5"));
                    upgradeInfoResult.setData(upgradeInfoModel);
                }
                return upgradeInfoResult;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

}
