package com.lijunhuayc.upgrade.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Desc:
 * Created by ${junhua.li} on 2016/10/21 17:21.
 * Email: lijunhuayc@sina.com
 */
public class SharedPreferencesUtils {
//    public static final int DATA_SET = 0;
//    public static final int DATA_GET = 1;
    private static SharedPreferences mSharedPreferences = null;

    public static void init(Context mContext) {
        if (mSharedPreferences == null) {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        }
    }

    // 保存或写入数据
    synchronized public static String refreshData(int Type, String key, String value) {
//        if (mSharedPreferences == null) {
//            Log.i("SharedPreferencesUtils", "mSharedPreferences is null!");
//            return null;
//        }
//        String sValue = null;
//        if (Type == DATA_SET) {
//            if ((key != null) && (value != null)) {
//                SharedPreferences.Editor editor = mSharedPreferences.edit();
//                editor.putString(key, value);
//                editor.apply();
//            }
//        } else if (Type == DATA_GET && (key != null)) {
//            sValue = mSharedPreferences.getString(key, value);
//        }
//        return sValue;
        return null;
    }

    synchronized public static boolean setString(String key, String value) {
        if (mSharedPreferences != null) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(key, value);
            return editor.commit();
        }
        return false;
    }

    synchronized public static String getString(String key, String defValue) {
        if (mSharedPreferences != null) {
            return mSharedPreferences.getString(key, defValue);
        }
        return defValue;
    }

    synchronized public static boolean setBoolean(String key, boolean value) {
        if (mSharedPreferences != null) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(key, value);
            return editor.commit();
        }
        return false;
    }

    synchronized public static boolean getBoolean(String key, boolean defValue) {
        if (mSharedPreferences != null) {
            return mSharedPreferences.getBoolean(key, defValue);
        }
        return defValue;
    }

    synchronized public static boolean setInt(String key, int value) {
        if (mSharedPreferences != null) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putInt(key, value);
            return editor.commit();
        }
        return false;
    }

    synchronized public static int getInt(String key, int defValue) {
        if (mSharedPreferences != null) {
            return mSharedPreferences.getInt(key, defValue);
        }
        return defValue;
    }

    synchronized public static boolean setLong(String key, long value) {
        if (mSharedPreferences != null) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putLong(key, value);
            return editor.commit();
        }
        return false;
    }

    synchronized public static long getLong(String key, long defValue) {
        if (mSharedPreferences != null) {
            return mSharedPreferences.getLong(key, defValue);
        }
        return defValue;
    }

    synchronized public static boolean setFloat(String key, float value) {
        if (mSharedPreferences != null) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putFloat(key, value);
            return editor.commit();
        }
        return false;
    }

    synchronized public static float getFloat(String key, float defValue) {
        if (mSharedPreferences != null) {
            return mSharedPreferences.getFloat(key, defValue);
        }
        return defValue;
    }

}
