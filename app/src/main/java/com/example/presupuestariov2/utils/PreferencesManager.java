package com.example.presupuestariov2.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PreferencesManager {

    private static final String TAG = "Utils_Prefs";
    private static final String PREFS_NAME = "presupuestario_prefs";
    private static final String KEY_DUE_DAYS = "due_days";

    public static final int DEFAULT_DUE_DAYS = 30;
    public static final int[] DUE_DAYS_OPTIONS = {15, 30, 60, 90};

    private static volatile PreferencesManager instance;

    private SharedPreferences prefs;

    private PreferencesManager() {}

    public static PreferencesManager getInstance() {
        if (instance == null) {
            synchronized (PreferencesManager.class) {
                if (instance == null) {
                    instance = new PreferencesManager();
                }
            }
        }
        return instance;
    }

    public synchronized void init(Context context) {
        if (prefs != null) return;
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Log.d(TAG, "init ok");
    }

    public int getDueDays() {
        if (prefs == null) return DEFAULT_DUE_DAYS;
        return prefs.getInt(KEY_DUE_DAYS, DEFAULT_DUE_DAYS);
    }

    public void setDueDays(int days) {
        if (prefs == null || days <= 0) return;
        prefs.edit().putInt(KEY_DUE_DAYS, days).apply();
        Log.i(TAG, "setDueDays " + days);
    }

    // Fecha de vencimiento a partir de ahora según el plazo configurado
    public long dueDateFromNow() {
        return System.currentTimeMillis() + getDueDays() * 24L * 60 * 60 * 1000;
    }
}
