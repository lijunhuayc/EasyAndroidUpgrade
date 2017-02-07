package com.lijunhuayc.sample;

import android.app.Application;

import com.lijunhuayc.upgrade.utils.MyToast;

/**
 * Desc:
 * Created by ${junhua.li} on 2016/09/28 09:33.
 * Email: lijunhuayc@sina.com
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MyToast.init(this);

//        new UpgradeHelper.Builder(this)
//                .setUpgradeUrl("http://192.168.1.79/public/upgrade.html?version=3")
//                .setDelay(3000)
//                .build().check();

    }
}
