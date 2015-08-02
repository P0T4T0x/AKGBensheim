package de.tobiaserthal.akgbensheim.data.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class PreferenceProvider {
    private static PreferenceProvider instance;

    private Context context;
    private PreferenceKey keys;
    private SharedPreferences preferences;

    private PreferenceProvider(Context context) {
        this.context = context.getApplicationContext();
        this.keys = PreferenceKey.getInstance(context);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    public static synchronized PreferenceProvider getInstance(Context context) {
        if(instance == null) {
            instance = new PreferenceProvider(context);
        }

        return instance;
    }

    public Context getContext() {
        return context;
    }

    public int getInteger(String key) {
        return preferences.getInt(key, 0);
    }

    public int getSubstPhase() {
        return preferences.getInt(keys.getKeyPhase(), 5);
    }

    public String getSubstPhaseSummary() {
        return String.valueOf(getSubstPhase());
    }

    public String getSubstForm() {
        String form = preferences.getString(keys.getKeyForm(), null);
        return TextUtils.isEmpty(form) ? null : form;
    }

    public String[] getSubstSubjects() {
        String csv = preferences.getString(keys.getKeySubstList(), null);
        if(TextUtils.isEmpty(csv)) {
            return null;
        }

        return csv.split(",");
    }

    public void setSubstSubjects(String csv) {
        preferences.edit()
                .putString(keys.getKeySubstList(), csv)
                .apply();
    }

    public String getSubstSubjectsSummary() {
        String[] set = getSubstSubjects();
        if(set == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        int iterations = (set.length < 3 ? set.length : 3);
        for (int i = 0; i < iterations; i++) {
            builder.append(set[i]);

            if(i < iterations - 1) {
                builder.append(", ");
            } else if(set.length > 3) {
                    builder.append("...");
            }
        }

        return builder.toString();
    }

    public boolean isAutoSyncEnabled() {
        return preferences.getBoolean(keys.getKeySyncEnabled(), true);
    }

    public boolean isSyncEventsEnabled() {
        return preferences.getBoolean(keys.getKeyEventSyncEnabled(), true);
    }

    public long getEventSyncFrequency() {
        return preferences.getLong(keys.getKeyEventSyncPeriod(), keys.getDefaultEventSyncPeriod());
    }

    public boolean isNewsSyncEnabled() {
        return preferences.getBoolean(keys.getKeyNewsSyncEnabled(), true);
    }

    public long getNewsSyncFrequency() {
        return preferences.getLong(keys.getKeyNewsSyncPeriod(), keys.getDefaultNewsSyncPeriod());
    }

    public boolean isSubstSyncEnabled() {
        return preferences.getBoolean(keys.getKeySubstSyncEnabled(), true);
    }

    public long getSubstSyncFrequency() {
        return preferences.getLong(keys.getKeySubstSyncPeriod(), keys.getDefaultSubstSyncPeriod());
    }

    public boolean isTeacherSyncEnabled() {
        return preferences.getBoolean(keys.getKeyTeacherSyncEnabled(), true);
    }

    public long getTeacherSyncFrequency() {
        return preferences.getLong(keys.getKeyTeacherSyncPeriod(), keys.getDefaultTeacherSyncPeriod());
    }

    public boolean isOnlyWifiEnabled() {
        return preferences.getBoolean(keys.getKeyOnlyWifi(), false);
    }

    public void putInteger(String key, int value) {
        preferences.edit()
                .putInt(key, value)
                .apply();
    }
}
