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
//    public static final String UPGRADE_TAG_KEY = "UPGRADE_TAG_KEY";
//    public static final String UPGRADE_INFO_KEY = "UPGRADE_INFO_KEY";
    private static SharedPreferences mSharedPreferences = null;

    public static void init(Context mContext) {
        if (mSharedPreferences == null) {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        }
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
