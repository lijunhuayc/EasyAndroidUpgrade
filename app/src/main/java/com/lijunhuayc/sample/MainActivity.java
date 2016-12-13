package com.lijunhuayc.sample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lijunhuayc.upgrade.UpgradeHelper;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private EditText downloadpathText;
    private TextView resultView;
    private ProgressBar progressBar;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context = this;

        downloadpathText = (EditText) this.findViewById(R.id.path);
        downloadpathText.setText("http://221.236.21.155/imtt.dd.qq.com/16891/5C119BDFA17906E5D6F45BDF932460BB.apk?mkey=57d63b47fb8efb5e&f=3580&c=0&fsname=com.shangyi.postop.paitent.android_4.2.0.0_18.apk&hsr=4d5s&p=.apk");
//        downloadpathText.setText("https://dl.google.com/dl/android/studio/install/2.2.0.12/android-studio-ide-145.3276617-windows.exe");
        progressBar = (ProgressBar) this.findViewById(R.id.downloadbar);
        resultView = (TextView) this.findViewById(R.id.resultView);

        findViewById(R.id.startBt).setOnClickListener(this);
        findViewById(R.id.pauseBt).setOnClickListener(this);
        findViewById(R.id.continueBt).setOnClickListener(this);
        findViewById(R.id.stopBt).setOnClickListener(this);

        init();
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                MiExToast.makeText(MainActivity.this, "特使Toast", MiExToast.LENGTH_ALWAYS).show();
//                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        }, 2000);

    }

    private void init() {
        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {
//            int permission = checkSelfPermission(Manifest.permission.SYSTEM_ALERT_WINDOW);
//            if (permission == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "未授权", Toast.LENGTH_SHORT).show();
////                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, 123);
//                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SYSTEM_ALERT_WINDOW);
//                return;
//            }
        }
        showTestView();
        WindowUtils.showPopupWindow(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showTestView();
            } else {
                Toast.makeText(this, "权限被拒绝", Toast.LENGTH_SHORT).show();
            }
        }
    }

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

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startBt:
                startDownload();
                break;
            case R.id.pauseBt:
                break;
            case R.id.continueBt:
                break;
            case R.id.stopBt:
                break;
        }
    }

    private void startDownload() {
        String path = downloadpathText.getText().toString();
        System.out.println(Environment.getExternalStorageState() + "------" + Environment.MEDIA_MOUNTED);


        new UpgradeHelper.Builder(this);

    }

    @Override
    public void onBackPressed() {
        Process.killProcess(Process.myPid());
        super.onBackPressed();
    }
}