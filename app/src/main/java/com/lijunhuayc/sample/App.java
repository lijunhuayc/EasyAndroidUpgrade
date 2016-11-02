package com.lijunhuayc.sample;

import android.app.Application;

/**
 * Desc:
 * Created by ${junhua.li} on 2016/09/28 09:33.
 * Email: lijunhuayc@sina.com
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//        WindowUtils.showPopupWindow(this);

//        if (Build.VERSION_CODES.M == Build.VERSION.SDK_INT) {
//            int permission = checkSelfPermission(Manifest.permission.SYSTEM_ALERT_WINDOW);
//            if (permission != PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "未授权", Toast.LENGTH_SHORT).show();
//
//            }
//        }
//
//        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//        alertDialog.setTitle("升级提醒");
//        alertDialog.setMessage("消息消息");
//        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
////        alertDialog.setCanceledOnTouchOutside(false);
//        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "下次再说", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//            }
//        });
//        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "马上升级", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        alertDialog.show();
//
//        final WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//        WindowManager.LayoutParams para = new WindowManager.LayoutParams();
//        para.height = -1;
//        para.width = -1;
//        para.format = 1;
//
//        para.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
//
//        para.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
//        final TextView mView = new TextView(this);
//        mView.setText("测试弹窗");
//        wm.addView(mView, para);
//        mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                wm.removeView(mView);
//            }
//        });

    }
}
