package com.josieAlan.videoplayermicra.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

public class PreferenceUtil {
    private static PreferenceUtil sInstance;
    private final SharedPreferences mPreferences;
    private static final String LAST_SPEED = "last_speed";
    private static final String LAST_BRIGHTNESS = "last_brightness";
    private static final String SORT_ORDER = "sort_order";
    private static final String VIEW_TYPE = "view_type";
    private static final String THEME = "theme";
    private static final String LOCK = "lock";

    private PreferenceUtil(@NonNull Context context) {
        this.mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferenceUtil getInstance(@NonNull Context context) {
        if (sInstance == null) {
            sInstance = new PreferenceUtil(context.getApplicationContext());
        }
        return sInstance;
    }

    public void saveLastBrightness(float f) {
        this.mPreferences.edit().putFloat(LAST_BRIGHTNESS, f).commit();
    }

    public float getLastBrightness() {
        return this.mPreferences.getFloat(LAST_BRIGHTNESS, 0.5f);
    }

    public void saveLastSpeed(float f) {
        this.mPreferences.edit().putFloat(LAST_SPEED, f).commit();
    }

    public float getLastSpeed() {
        return this.mPreferences.getFloat(LAST_SPEED, 1.0f);
    }

    public void saveSortOrder(int x) {
        this.mPreferences.edit().putInt(SORT_ORDER, x).commit();
    }

    public int getSortOrder() {
        return this.mPreferences.getInt(SORT_ORDER, 0);
    }

    public void saveViewType(Boolean x) {
        this.mPreferences.edit().putBoolean(VIEW_TYPE, x).commit();
    }

    public boolean getViewType() {
        return this.mPreferences.getBoolean(VIEW_TYPE, true);
    }


    public void setTheme(int x) {
        this.mPreferences.edit().putInt(THEME, x).commit();
    }

    public int getTheme() {
        return this.mPreferences.getInt(THEME, 11);
    }


    public void setLock(Boolean x) {
        this.mPreferences.edit().putBoolean(LOCK, x).commit();
    }

    public boolean getLock() {
        return this.mPreferences.getBoolean(LOCK, false);
    }


}
