package com.lijunhuayc.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

/**
 * Desc:
 * Created by ${junhua.li} on 2016/11/02 12:45.
 * Email: lijunhuayc@sina.com
 */
public class SecondActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("我是第二个界面");
        tv.setTextSize(20);
        setContentView(tv);

    }
}
