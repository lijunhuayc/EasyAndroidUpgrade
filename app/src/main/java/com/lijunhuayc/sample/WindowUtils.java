package com.lijunhuayc.sample;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.lijunhuayc.upgrade.utils.MyToast;

import java.io.Serializable;

public class WindowUtils {
    public static String ACTION_UPDATE_LIST = "update_ui_action";
    private static View mView = null;
    private static WindowManager mWindowManager = null;
    private static Boolean isShown = false;
    private static Context mContext;
    private static String msgType;
    private static Handler mHandler;
    private static TextView alertTitleTV;

    /**
     * @author ljh @desc 设置数据
     */
    public static void setDate(String msgType, Serializable item, Handler handler) {
        WindowUtils.mHandler = handler;
        WindowUtils.msgType = msgType;
    }

    /**
     * 显示弹出框
     *
     * @param context
     */
    public static void showPopupWindow(final Context context) {
        if (isShown) {
            return;
        }
        isShown = true;
        // 获取WindowManager
        mContext = context.getApplicationContext();
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mView = setUpView(context);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        // 类型
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        int flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
				| WindowManager.LayoutParams.FLAG_FULLSCREEN;
//				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件
        params.flags = flags;
        // 不设置这个弹出框的透明遮罩显示为黑色
        params.format = PixelFormat.TRANSLUCENT;
        // FLAG_NOT_TOUCH_MODAL不阻塞事件传递到后面的窗口
        // 设置 FLAG_NOT_FOCUSABLE 悬浮窗口较小时，后面的应用图标由不可长按变为可长按
        // 不设置这个flag的话，home页的划屏会有问题
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        mWindowManager.addView(mView, params);
    }

    /**
     * 隐藏弹出框
     */
    public static void hidePopupWindow() {
        msgType = null;
        if (isShown && null != mView) {
            mWindowManager.removeView(mView);
            isShown = false;
        }
    }

    /**
     * @author ljh @desc 若在其他APP界面则调起本APP
     */
    private static void enterApp() {
//		Intent stIntent = new Intent(mContext, WelcomeActivity.class).setAction(Intent.ACTION_MAIN);
//		stIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//		stIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		stIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//		mContext.startActivity(stIntent);
    }

    private static View setUpView(final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_locked_alert_layout, null);
        view.setBackgroundColor(Color.parseColor("#77000000"));
        alertTitleTV = ((TextView) view.findViewById(R.id.alertTitle));
        Button positiveBtn = (Button) view.findViewById(R.id.mButtonPositive);
        MyToast.init(mContext);
        positiveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MyToast.showToast("点击确认");
//				sendStopBroadcast();
//				updateSignforStatus(context, 2);//签收
                hidePopupWindow();
            }
        });
        Button negativeBtn = (Button) view.findViewById(R.id.mButtonNegative);
        negativeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MyToast.showToast("点击取消");
//				sendStopBroadcast();
//				enterApp();
//				sendUpdateUIBrocast(context);
                hidePopupWindow();
            }
        });
        // 点击窗口外部区域可消除
        // 这点的实现主要将悬浮窗设置为全屏大小，外层有个透明背景，中间一部分视为内容区域
        // 所以点击内容区域外部视为点击悬浮窗外部
        final View parentPanel = view.findViewById(R.id.realLayout);// 非透明的内容区域
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                Rect rect = new Rect();
                parentPanel.getGlobalVisibleRect(rect);//获取view所在的矩形
                //触点不在矩形内则表示点击非内容区
                if (!rect.contains(x, y)) {
                    WindowUtils.hidePopupWindow();
                }
                return false;
            }
        });
        // 点击back键可消除
        view.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                System.out.println("keyCode：" + keyCode);
                System.out.println("event.getKeyCode()：" + event.getKeyCode());
                System.out.println("event.getAction()：" + event.getAction());
                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_BACK:
                        WindowUtils.hidePopupWindow();
                        return true;
                    default:
                        return false;
                }
            }
        });
        return view;
    }

//	private static void sendUpdateUIBrocast(Context context){
//		if(context.getPackageName().equals(CommonUtils.getRunPackageName(context))){
//			Intent intent = new Intent(ACTION_UPDATE_LIST);
//			intent.putExtra("msgType", msgType);
//			context.sendBroadcast(intent);
//		}
//	}

}
