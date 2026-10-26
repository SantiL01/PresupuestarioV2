package com.example.presupuestariov2.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PreferencesManager {

    private static final String TAG = "Utils_Prefs";
    private static final String PREFS_NAME = "presupuestario_prefs";
    private static final String KEY_DUE_DAYS = "due_days";
    private static final String KEY_SESSION_USER_ID = "session_user_id";

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

    // ===== Sesión =====

    public long getSessionUserId() {
        if (prefs == null) return -1;
        return prefs.getLong(KEY_SESSION_USER_ID, -1);
    }

    public boolean hasSession() {
        return getSessionUserId() > 0;
    }

    public void startSession(long userId) {
        if (prefs == null || userId <= 0) return;
        prefs.edit().putLong(KEY_SESSION_USER_ID, userId).apply();
        Log.i(TAG, "startSession " + userId);
    }

    public void clearSession() {
        if (prefs == null) return;
        prefs.edit().remove(KEY_SESSION_USER_ID).apply();
        Log.i(TAG, "clearSession");
    }

    // Fecha de vencimiento a partir de ahora según el plazo configurado
    public long dueDateFromNow() {
        return System.currentTimeMillis() + getDueDays() * 24L * 60 * 60 * 1000;
    }
}
