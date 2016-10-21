package com.lijunhuayc.sample;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {
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
//        downloadpathText.setText("http://221.236.21.155/imtt.dd.qq.com/16891/5C119BDFA17906E5D6F45BDF932460BB.apk?mkey=57d63b47fb8efb5e&f=3580&c=0&fsname=com.shangyi.postop.paitent.android_4.2.0.0_18.apk&hsr=4d5s&p=.apk");
        downloadpathText.setText("https://dl.google.com/dl/android/studio/install/2.2.0.12/android-studio-ide-145.3276617-windows.exe");
        progressBar = (ProgressBar) this.findViewById(R.id.downloadbar);
        resultView = (TextView) this.findViewById(R.id.resultView);

        findViewById(R.id.startBt).setOnClickListener(this);
        findViewById(R.id.pauseBt).setOnClickListener(this);
        findViewById(R.id.continueBt).setOnClickListener(this);
        findViewById(R.id.stopBt).setOnClickListener(this);

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


    }

    @Override
    public void onBackPressed() {
        Process.killProcess(Process.myPid());
        super.onBackPressed();
    }
}