package com.lijunhuayc.upgrade.view;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lijunhuayc.upgrade.R;
import com.lijunhuayc.upgrade.utils.MyToast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Desc:
 * Created by ${junhua.li} on 2016/11/02 15:32.
 * Email: lijunhuayc@sina.com
 */
public class EasyToastDialog implements View.OnTouchListener {
    private static final String TAG = EasyToastDialog.class.getSimpleName();

    public static final int LENGTH_ALWAYS = 0;
    public static final int LENGTH_SHORT = 2;
    public static final int LENGTH_LONG = 4;

    private Toast toast;
    private Context mContext;
    private int mDuration = LENGTH_SHORT;
    private int animations = -1;
    private boolean isShow = false;
    private boolean isCancelable = false;
    private boolean isCanceledOnTouchOutside = false;

    private Object mTN;
    private Method show;
    private Method hide;
    private View mView;
    private LinearLayout visualLayout;//dialog content layout
    private TextView titleTV;
    private CharSequence mTitleText;
    private TextView messageTV;
    private CharSequence mMessageText;
    private ProgressBar progressBar;
    private TextView mButtonPositive;
    private CharSequence mPositiveText;
    private View.OnClickListener positiveListener;
    private TextView mButtonNegative;
    private CharSequence mNegativeText;
    private View.OnClickListener negativeListener;
    private TextView mButtonNeutral;
    private CharSequence mNeutralText;
    private View.OnClickListener neutralListener;

    private Handler handler = new Handler();
    private Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    public EasyToastDialog(Context mContext) {
        this.mContext = mContext;
        if (toast == null) {
            toast = new Toast(mContext);
        }
        mView = View.inflate(mContext, R.layout.easy_dialog_layout, null);
        mView.setOnTouchListener(this);
        visualLayout = (LinearLayout) mView.findViewById(R.id.visualLayout);
    }

    private void initTN() {
        try {
            Field tnField = toast.getClass().getDeclaredField("mTN");
            tnField.setAccessible(true);
            mTN = tnField.get(toast);
            show = mTN.getClass().getMethod("show");
            hide = mTN.getClass().getMethod("hide");

            Field tnParamsField = mTN.getClass().getDeclaredField("mParams");
            tnParamsField.setAccessible(true);
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) tnParamsField.get(mTN);
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
//            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
//                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            params.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                    | WindowManager.LayoutParams.FLAG_FULLSCREEN;

            if (animations != -1) {
                params.windowAnimations = animations;
            }

            //调用tn.show()之前一定要先设置 mNextView
            Field tnNextViewField = mTN.getClass().getDeclaredField("mNextView");
            tnNextViewField.setAccessible(true);
            tnNextViewField.set(mTN, toast.getView());
//            mWM = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setGravity(Gravity.CENTER | Gravity.TOP, 0, 0);
    }

    public void show() {
        if (isShow) return;
        TextView messageTV = (TextView) mView.findViewById(R.id.messageTV);
        messageTV.setText(mMessageText);
        toast.setView(mView);
        mView.findViewById(R.id.mButtonPositive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyToast.showToast("点击升级");
            }
        });
        mView.findViewById(R.id.mButtonNegative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyToast.showToast("点击取消");
            }
        });
        initTN();
        try {
            show.invoke(mTN);
        } catch (Exception e) {
            e.printStackTrace();
        }
        isShow = true;
        if (mDuration > LENGTH_ALWAYS) {
            handler.postDelayed(hideRunnable, mDuration * 1000);
        }
    }

    public void hide() {
        if (!isShow) return;
        try {
            mMessageText = null;
            mView = null;
            hide.invoke(mTN);
        } catch (Exception e) {
            e.printStackTrace();
        }
        isShow = false;
    }

    public EasyToastDialog setCancelable(boolean cancelable) {
        this.isCancelable = cancelable;
        return this;
    }

    public EasyToastDialog setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        isCanceledOnTouchOutside = canceledOnTouchOutside;
        return this;
    }

    public EasyToastDialog setTitle(CharSequence title) {
        this.mTitleText = title;
        return this;
    }

    public EasyToastDialog setMessage(int resId) {
        setMessage(mContext.getText(resId));
        return this;
    }

    public EasyToastDialog setMessage(CharSequence mMessageText) {
        this.mMessageText = mMessageText;
        return this;
    }

    public EasyToastDialog setPositive(CharSequence mPositiveText, View.OnClickListener listener) {
        this.mPositiveText = mPositiveText;
        this.positiveListener = listener;
        return this;
    }

    public EasyToastDialog setNegative(CharSequence mNegativeText, View.OnClickListener listener) {
        this.mNegativeText = mNegativeText;
        this.negativeListener = listener;
        return this;
    }

    public EasyToastDialog setNeutral(CharSequence mNeutralText, View.OnClickListener listener) {
        this.mNeutralText = mNeutralText;
        this.neutralListener = listener;
        return this;
    }

    /**
     * @see #LENGTH_SHORT
     * @see #LENGTH_LONG
     * @see #LENGTH_ALWAYS
     */
    public EasyToastDialog setDuration(int duration) {
        mDuration = duration;
        return this;
    }

    public EasyToastDialog setGravity(int gravity, int xOffset, int yOffset) {
        toast.setGravity(gravity, xOffset, yOffset);
        return this;
    }

//    public EasyToastDialog setAnimations(int animations) {
//        this.animations = animations;
//        return this;
//    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
//                LogUtils.d(TAG, "eventx = " + event.getX());
//                LogUtils.d(TAG, "eventy = " + event.getY());
//                LogUtils.d(TAG, "xl = " + visualLayout.getX());
//                LogUtils.d(TAG, "yt = " + visualLayout.getY());
//                LogUtils.d(TAG, "xr = " + (visualLayout.getX() + visualLayout.getWidth()));
//                LogUtils.d(TAG, "yb = " + (visualLayout.getY() + visualLayout.getHeight()));
                float eventX = event.getX();
                float eventY = event.getY();
                float visualL = visualLayout.getX();
                float visualR = visualL + visualLayout.getWidth();
                float visualT = visualLayout.getY();
                float visualB = visualT + visualLayout.getHeight();
                if (!(eventX > visualL
                        && eventX < visualR
                        && eventY > visualT
                        && eventY < visualB)) {
                    hide();
                }
                break;
        }
        return true;
    }

}
