package com.lijunhuayc.sample;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Desc:
 * Created by ${junhua.li} on 2016/11/02 15:32.
 * Email: lijunhuayc@sina.com
 */
public class EasyToastDialog implements View.OnTouchListener, View.OnKeyListener {
    private static final String TAG = EasyToastDialog.class.getSimpleName();

    public static final int LENGTH_ALWAYS = 0;
    public static final int LENGTH_SHORT = 2;
    public static final int LENGTH_LONG = 4;

    private Toast toast;
    private Context mContext;
    private CharSequence mText;
    private int mDuration = LENGTH_SHORT;
    private int animations = -1;
    private boolean isShow = false;

    private Object mTN;
    private Method show;
    private Method hide;
    //    private WindowManager mWM;
    private WindowManager.LayoutParams params;
    private View mView;

    private Handler handler = new Handler();

    public static EasyToastDialog makeText(Context context, CharSequence text, int duration) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        EasyToastDialog exToast = new EasyToastDialog(context);
        exToast.toast = toast;
        exToast.mDuration = duration;
        return exToast;
    }

    public static EasyToastDialog makeText(Context context, int resId, int duration) throws Resources.NotFoundException {
        return makeText(context, context.getResources().getText(resId), duration);
    }

    private EasyToastDialog(Context context) {
        this.mContext = context;
        if (toast == null) {
            toast = new Toast(mContext);
        }
        mView = View.inflate(mContext, R.layout.easy_dialog_layout, null);
        mView.setOnTouchListener(this);
        mView.setOnKeyListener(this);
    }

    private Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    public void show() {
        if (isShow) return;
        TextView tv = (TextView) mView.findViewById(R.id.expandable_text);
        tv.setText(mText);
        toast.setView(mView);
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
            hide.invoke(mTN);
        } catch (Exception e) {
            e.printStackTrace();
        }
        isShow = false;
    }

    public void setView(View view) {
        toast.setView(view);
    }

    public View getView() {
        return toast.getView();
    }

    /**
     * @see #LENGTH_SHORT
     * @see #LENGTH_LONG
     * @see #LENGTH_ALWAYS
     */
    public void setDuration(int duration) {
        mDuration = duration;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setMargin(float horizontalMargin, float verticalMargin) {
        toast.setMargin(horizontalMargin, verticalMargin);
    }

    public float getHorizontalMargin() {
        return toast.getHorizontalMargin();
    }

    public float getVerticalMargin() {
        return toast.getVerticalMargin();
    }

    public void setGravity(int gravity, int xOffset, int yOffset) {
        toast.setGravity(gravity, xOffset, yOffset);
    }

    public int getGravity() {
        return toast.getGravity();
    }

    public int getXOffset() {
        return toast.getXOffset();
    }

    public int getYOffset() {
        return toast.getYOffset();
    }

    public void setText(int resId) {
        setText(mContext.getText(resId));
    }

    public void setText(CharSequence textStr) {
//        toast.setText(s);
        this.mText = textStr;
    }

    public int getAnimations() {
        return animations;
    }

    public void setAnimations(int animations) {
        this.animations = animations;
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
            params = (WindowManager.LayoutParams) tnParamsField.get(mTN);
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                hide();
                break;
        }
        return true;
//        return false;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
//        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            hide();
//            return true;
//        }
//        switch (event.getAction()) {
//            case KeyEvent.ACTION_DOWN:
//                break;
//            case KeyEvent.ACTION_UP:
//                hide();
//                break;
//        }
        return false;
    }
}
