package com.lijunhuayc.upgrade.model;

/**
 * Desc:
 * {
 * "status": 1,
 * "data": {
 * "appName": xxx,
 * "versionCode": 2,
 * "versionName": 1.0.2,
 * ...
 * },
 * "message": "desc...",
 * ...
 * }
 * Created by ${junhua.li} on 2016/10/31 18:00.
 * Email: lijunhuayc@sina.com
 */
public class UpgradeInfoResult {
    private int status;
    private String message;
    private UpgradeInfoModel data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UpgradeInfoModel getData() {
        return data;
    }

    public void setData(UpgradeInfoModel data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UpgradeInfoResult{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
