package com.lijunhuayc.sample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.lijunhuayc.upgrade.helper.UpgradeHelper;

public class MainActivity extends FragmentActivity {
    private EditText downloadpathText;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context = this;
        downloadpathText = (EditText) this.findViewById(R.id.path);//仅仅测试下载用的APK(使用者无需关注)
//        downloadpathText.setText("http://221.236.21.155/imtt.dd.qq.com/16891/5C119BDFA17906E5D6F45BDF932460BB.apk?mkey=57d63b47fb8efb5e&f=3580&c=0&fsname=com.shangyi.postop.paitent.android_4.2.0.0_18.apk&hsr=4d5s&p=.apk");
        downloadpathText.setText("http://future-service.oss-cn-hangzhou.aliyuncs.com/apk/app-future-release_110_jiagu_sign.apk");

        init();//使用者只需关注此方法中的代码
    }

    private void init() {
        new UpgradeHelper.Builder(this)
                .setUpgradeUrl("http://192.168.1.79/public/upgrade.html?version=3")
                .setDelay(1000)
                .setIsAboutChecking(true)//关于页面手动检测更新需要设置isAboutChecking(true), 启动时检测设为false
                .build().check();
    }


    /**
     * test
     */
    private void showTestView() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("升级提醒");
        alertDialog.setMessage("消息消息");
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
//        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "下次再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "马上升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();

        final WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams para = new WindowManager.LayoutParams();
        para.height = -1;
        para.width = -1;
        para.format = 1;

        para.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        para.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        final TextView mView = new TextView(this);
        mView.setText("测试弹窗");
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.removeView(mView);
            }
        });
        wm.addView(mView, para);
    }

    @Override
    public void onBackPressed() {
        Process.killProcess(Process.myPid());
        super.onBackPressed();
    }
}