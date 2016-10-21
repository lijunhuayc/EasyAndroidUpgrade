package com.lijunhuayc.upgrade.model;

/**
 * Desc:
 * Created by ${junhua.li} on 2016/10/21 18:24.
 * Email: lijunhuayc@sina.com
 */
public class UpgradeInfoModel {
    private String appName;         //apk download url
    private String versionCode;
    private String versionName;
    private String packageName;
    private String apkUrl;          //apk download url
    private String upgradeNotes;     //update log.eg: 1.modify bug \n2.optimize codes
    private String upgradeTip;       //dialog alert title
    private boolean isForce;        //whether forced to upgrade
    private int fileSize;           //the apk file length.
    private String md5;             //the apk file md5

}
