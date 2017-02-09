package com.lijunhuayc.upgrade.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Desc:
 * Created by ${junhua.li} on 2017/02/09 11:36.
 * Email: lijunhuayc@sina.com
 */
public class UpgradeConfig implements Parcelable {
    private String upgradeUrl;                      //upgrade check remote-interface.
    private boolean isAutoStartInstall = true;
    private boolean isQuietDownload = false;        //whether quiet download when the update is detected.
    private boolean isCheckPackageName = true;      //whether check the package name.
    private boolean isAboutChecking = false;        //whether is "about" check upgrade.
    private long delay = 0;                         //millisecond. whether delay check upgrade.

    public String getUpgradeUrl() {
        return upgradeUrl;
    }

    public void setUpgradeUrl(String upgradeUrl) {
        this.upgradeUrl = upgradeUrl;
    }

    public boolean isAutoStartInstall() {
        return isAutoStartInstall;
    }

    public void setAutoStartInstall(boolean autoStartInstall) {
        isAutoStartInstall = autoStartInstall;
    }

    public boolean isQuietDownload() {
        return isQuietDownload;
    }

    public void setQuietDownload(boolean quietDownload) {
        isQuietDownload = quietDownload;
    }

    public boolean isCheckPackageName() {
        return isCheckPackageName;
    }

    public void setCheckPackageName(boolean checkPackageName) {
        isCheckPackageName = checkPackageName;
    }

    public boolean isAboutChecking() {
        return isAboutChecking;
    }

    public void setAboutChecking(boolean aboutChecking) {
        isAboutChecking = aboutChecking;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.upgradeUrl);
        dest.writeByte(this.isAutoStartInstall ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isQuietDownload ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isCheckPackageName ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isAboutChecking ? (byte) 1 : (byte) 0);
        dest.writeLong(this.delay);
    }

    public UpgradeConfig() {
    }

    protected UpgradeConfig(Parcel in) {
        this.upgradeUrl = in.readString();
        this.isAutoStartInstall = in.readByte() != 0;
        this.isQuietDownload = in.readByte() != 0;
        this.isCheckPackageName = in.readByte() != 0;
        this.isAboutChecking = in.readByte() != 0;
        this.delay = in.readLong();
    }

    public static final Parcelable.Creator<UpgradeConfig> CREATOR = new Parcelable.Creator<UpgradeConfig>() {
        @Override
        public UpgradeConfig createFromParcel(Parcel source) {
            return new UpgradeConfig(source);
        }

        @Override
        public UpgradeConfig[] newArray(int size) {
            return new UpgradeConfig[size];
        }
    };
}
