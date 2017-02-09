package com.lijunhuayc.upgrade.view;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lijunhuayc.downloader.utils.LogUtils;
import com.lijunhuayc.upgrade.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Desc:
 * Created by ${junhua.li} on 2016/11/02 15:32.
 * Email: lijunhuayc@sina.com
 * @deprecated
 */
public class EasyToastDialog implements View.OnTouchListener, View.OnKeyListener {
    private static final String TAG = EasyToastDialog.class.getSimpleName();

    public static final int LENGTH_ALWAYS = 0;
    public static final int LENGTH_SHORT = 2;
    public static final int LENGTH_LONG = 4;
    public static final int MODE_MESSAGE = 0;
    public static final int MODE_PROGRESS = 1;

    private Toast mToast;
    private Context mContext;
    private int mDuration = LENGTH_ALWAYS;
    private int animations = -1;
    private boolean isShow = false;
    private boolean isCancelable = false;
    private boolean isCanceledOnTouchOutside = false;
    private int mMode = MODE_MESSAGE;

    private Object mTN;
    private Method show;
    private Method hide;
    private View mView;
    private LinearLayout mVisualLayout;//dialog content layout
    private TextView mTitleTV;
    private CharSequence mTitleText;
    private TextView mMessageTV;
    private CharSequence mMessageText;

    private View mProgressLayout;
    private TextView mPercentLabelTV;
    private TextView mSizeLabelTV;
    private TextView mSpeedLabelTV;
    private ProgressBar mProgressBar;

    private View mButtonLayout;
    private TextView mButtonPositive;
    private CharSequence mPositiveText;
    private View.OnClickListener mPositiveListener;
    private TextView mButtonNegative;
    private CharSequence mNegativeText;
    private View.OnClickListener mNegativeListener;
    private TextView mButtonNeutral;
    private CharSequence mNeutralText;
    private View.OnClickListener mNeutralListener;

    private Handler handler = new Handler();
    private Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    public EasyToastDialog(Context mContext) {
        this.mContext = mContext;
        if (mToast == null) {
            mToast = new Toast(mContext);
        }
        mView = View.inflate(mContext, R.layout.easy_dialog_layout, null);
        mView.setOnTouchListener(this);
        mView.setOnKeyListener(this);
        mVisualLayout = (LinearLayout) mView.findViewById(R.id.visualLayout);
        mTitleTV = (TextView) mView.findViewById(R.id.titleTV);
        mMessageTV = (TextView) mView.findViewById(R.id.messageTV);

        mProgressLayout = mView.findViewById(R.id.progressLayout);
        mPercentLabelTV = (TextView) mView.findViewById(R.id.percentLabelTV);
        mSizeLabelTV = (TextView) mView.findViewById(R.id.sizeLabelTV);
        mSpeedLabelTV = (TextView) mView.findViewById(R.id.speedLabelTV);
        mProgressBar = (ProgressBar) mView.findViewById(R.id.progressBar);

        mButtonLayout = mView.findViewById(R.id.buttonLayout);
        mButtonPositive = (TextView) mView.findViewById(R.id.mButtonPositive);
        mButtonNegative = (TextView) mView.findViewById(R.id.mButtonNegative);
        mButtonNeutral = (TextView) mView.findViewById(R.id.mButtonNeutral);
    }

    private void initTN() {
        try {
            Field tnField = mToast.getClass().getDeclaredField("mTN");
            tnField.setAccessible(true);
            mTN = tnField.get(mToast);
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

            //must set 'mNextView'befor call tn.show()
            Field tnNextViewField = mTN.getClass().getDeclaredField("mNextView");
            tnNextViewField.setAccessible(true);
            tnNextViewField.set(mTN, mToast.getView());
//            mWM = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setGravity(Gravity.CENTER | Gravity.TOP, 0, 0);
    }

    public void show() {
        if (isShow) return;
        mToast.setView(mView);
        mTitleTV.setText(mTitleText);
        mMessageTV.setText(mMessageText);
        mButtonPositive.setText(mPositiveText);
        mButtonPositive.setOnClickListener(mPositiveListener);
        mButtonNegative.setText(mNegativeText);
        mButtonNegative.setOnClickListener(mNegativeListener);
        if (!TextUtils.isEmpty(mNeutralText)) {
            mButtonNeutral.setText(mNeutralText);
            mButtonNeutral.setOnClickListener(mNeutralListener);
            mButtonNeutral.setVisibility(View.VISIBLE);
        }

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
            setNull();
            hide.invoke(mTN);
        } catch (Exception e) {
            e.printStackTrace();
        }
        isShow = false;
    }

    private void setNull() {
        mView = null;
        mVisualLayout = null;
        mTitleTV = null;
        mTitleText = null;
        mMessageTV = null;
        mMessageText = null;
        mProgressLayout = null;
        mPercentLabelTV = null;
        mSizeLabelTV = null;
        mSpeedLabelTV = null;
        mProgressBar = null;
        mButtonLayout = null;
        mButtonPositive = null;
        mPositiveText = null;
        mPositiveListener = null;
        mButtonNegative = null;
        mNegativeText = null;
        mNegativeListener = null;
        mButtonNeutral = null;
        mNeutralText = null;
        mNeutralListener = null;
    }

    public EasyToastDialog setMode(int mode) {
        if (mMode == mode) {
            return this;
        }
        if (mode == MODE_MESSAGE) {
            this.mMessageTV.setVisibility(View.VISIBLE);
            this.mButtonLayout.setVisibility(View.VISIBLE);
            this.mProgressLayout.setVisibility(View.GONE);
        } else if (mode == MODE_PROGRESS) {
            this.mMessageTV.setVisibility(View.GONE);
            this.mButtonLayout.setVisibility(View.GONE);
            this.mProgressLayout.setVisibility(View.VISIBLE);
        }
        mMode = mode;
        return this;
    }

    public EasyToastDialog setCancelable(boolean cancelable) {
        this.isCancelable = cancelable;
        return this;
    }

    public EasyToastDialog setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        if (canceledOnTouchOutside && !isCancelable) {
            isCancelable = true;
        }
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

    public EasyToastDialog setProgressMax(int size) {
        this.mProgressBar.setMax(size);
        this.mProgressBar.setProgress(size);
        return this;
    }

    public EasyToastDialog setProgressBarSize(int progress) {
        this.mProgressBar.setSecondaryProgress(progress);
        return this;
    }

    public int getProgressMax() {
        return this.mProgressBar.getMax();
    }

    public EasyToastDialog setDownloadPercentLabelText(CharSequence sizeLabelText) {
        this.mPercentLabelTV.setText(sizeLabelText);
        return this;
    }

    public EasyToastDialog setDownloadSpeedLabelText(CharSequence sizeLabelText) {
        this.mSpeedLabelTV.setText(sizeLabelText);
        return this;
    }

    public EasyToastDialog setDownloadSizeLabelText(CharSequence sizeLabelText) {
        this.mSizeLabelTV.setText(sizeLabelText);
        return this;
    }

    public EasyToastDialog setPositive(CharSequence mPositiveText, View.OnClickListener listener) {
        this.mPositiveText = mPositiveText;
        this.mPositiveListener = listener;
        return this;
    }

    public EasyToastDialog setNegative(CharSequence mNegativeText, View.OnClickListener listener) {
        this.mNegativeText = mNegativeText;
        this.mNegativeListener = listener;
        return this;
    }

    public EasyToastDialog setNeutral(CharSequence mNeutralText, View.OnClickListener listener) {
        this.mNeutralText = mNeutralText;
        this.mNeutralListener = listener;
        return this;
    }

    /**
     * @see #LENGTH_SHORT
     * @see #LENGTH_LONG
     * @see #LENGTH_ALWAYS
     * @param duration duration
     * @return this
     */
    public EasyToastDialog setDuration(int duration) {
        mDuration = duration;
        return this;
    }

    public EasyToastDialog setGravity(int gravity, int xOffset, int yOffset) {
        mToast.setGravity(gravity, xOffset, yOffset);
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
                float visualL = mVisualLayout.getX();
                float visualR = visualL + mVisualLayout.getWidth();
                float visualT = mVisualLayout.getY();
                float visualB = visualT + mVisualLayout.getHeight();
                if (!(eventX > visualL
                        && eventX < visualR
                        && eventY > visualT
                        && eventY < visualB)) {
                    if (isCancelable && isCanceledOnTouchOutside) {
                        hide();
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        LogUtils.d(TAG, "onKey: -----" + keyCode);
        LogUtils.d(TAG, "onKey: -----" + event.getKeyCode());
        LogUtils.d(TAG, "onKey: -----" + event.getAction());
        return false;
    }
}
