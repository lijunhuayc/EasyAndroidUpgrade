package com.lijunhuayc.upgrade.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Desc: json model
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
 * Created by ${junhua.li} on 2016/10/21 18:24.
 * Email: lijunhuayc@sina.com
 */
public class UpgradeInfoModel implements Parcelable {
    private String appName;         //apk download url
    private String packageName;
    private int versionCode;
    private String versionName;
    private String apkUrl;          //apk download url
    private String upgradeNotes;     //update log.eg: 1.modify bug \n2.optimize codes
    private String upgradeTitle;       //dialog alert title
    private int isForce;        //whether forced to upgrade. {0[not force],1[absolute force],2[half force]}
    private int fileSize;           //the apk file length.
    private String md5;             //the apk file md5

    public interface ForceLevel {
        int NOT_FORCE = 0;
        int ABSOLUTE_FORCE = 1;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getUpgradeNotes() {
        return upgradeNotes;
    }

    public void setUpgradeNotes(String upgradeNotes) {
        this.upgradeNotes = upgradeNotes;
    }

    public String getUpgradeTitle() {
        return upgradeTitle;
    }

    public void setUpgradeTitle(String upgradeTitle) {
        this.upgradeTitle = upgradeTitle;
    }

    public int getIsForce() {
        return isForce;
    }

    public void setIsForce(int isForce) {
        this.isForce = isForce;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Override
    public String toString() {
        return "UpgradeInfoModel{" +
                "appName='" + appName + '\'' +
                ", versionCode='" + versionCode + '\'' +
                ", versionName='" + versionName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", apkUrl='" + apkUrl + '\'' +
                ", upgradeNotes='" + upgradeNotes + '\'' +
                ", upgradeTitle='" + upgradeTitle + '\'' +
                ", isForce=" + isForce +
                ", fileSize=" + fileSize +
                ", md5='" + md5 + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.appName);
        dest.writeString(this.packageName);
        dest.writeInt(this.versionCode);
        dest.writeString(this.versionName);
        dest.writeString(this.apkUrl);
        dest.writeString(this.upgradeNotes);
        dest.writeString(this.upgradeTitle);
        dest.writeInt(this.isForce);
        dest.writeInt(this.fileSize);
        dest.writeString(this.md5);
    }

    public UpgradeInfoModel() {
    }

    protected UpgradeInfoModel(Parcel in) {
        this.appName = in.readString();
        this.packageName = in.readString();
        this.versionCode = in.readInt();
        this.versionName = in.readString();
        this.apkUrl = in.readString();
        this.upgradeNotes = in.readString();
        this.upgradeTitle = in.readString();
        this.isForce = in.readInt();
        this.fileSize = in.readInt();
        this.md5 = in.readString();
    }

    public static final Parcelable.Creator<UpgradeInfoModel> CREATOR = new Parcelable.Creator<UpgradeInfoModel>() {
        @Override
        public UpgradeInfoModel createFromParcel(Parcel source) {
            return new UpgradeInfoModel(source);
        }

        @Override
        public UpgradeInfoModel[] newArray(int size) {
            return new UpgradeInfoModel[size];
        }
    };
}
