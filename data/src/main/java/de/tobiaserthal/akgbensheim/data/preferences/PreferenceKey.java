package de.tobiaserthal.akgbensheim.data.preferences;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.ContextThemeWrapper;

import de.tobiaserthal.akgbensheim.data.R;

/**
 * Created by tobiaserthal on 19.07.15.
 */
public class PreferenceKey {

    private static PreferenceKey instance;
    private Context context;

    public static synchronized PreferenceKey getInstance(Context context) {
        if(instance == null) {
            instance = new PreferenceKey(context);
        }

        return instance;
    }

    private PreferenceKey(Context context) {
        this.context = context.getApplicationContext();
    }

    public String getKeyOnlyWifi() {
        return context.getString(R.string.pref_key_only_wifi);
    }

    public String getKeyClearCache() {
        return context.getString(R.string.pref_key_clear_cache);
    }

    public String getKeyClearData() {
        return context.getString(R.string.pref_key_clear_data);
    }

    public String getKeyLicence() {
        return context.getString(R.string.pref_key_licence);
    }

    public String getKeyMailDev() {
        return context.getString(R.string.pref_key_mail_dev);
    }

    public String getKeyAbout() {
        return context.getString(R.string.pref_key_about);
    }

    public String getKeySubstList() {
        return context.getString(R.string.pref_key_subst_list);
    }

    public String getKeyPhase() {
        return context.getString(R.string.pref_key_subst_phase);
    }

    public String getKeyForm() {
        return context.getString(R.string.pref_key_subst_form);
    }

    public String getKeySubjectFilter() {
        return context.getString(R.string.pref_key_subst_subject_settings);
    }

    public String getKeySyncEnabled() {
        return context.getString(R.string.pref_key_sync_enabled);
    }

    public String getKeySyncSettings() {
        return context.getString(R.string.pref_key_sync_settings);
    }

    public String getKeyEventSyncEnabled() {
        return context.getString(R.string.pref_key_event_sync_enabled);
    }

    public String getKeyEventSyncPeriod() {
        return context.getString(R.string.pref_key_event_sync_period);
    }

    public long getDefaultEventSyncPeriod() {
        return 24 * 60;
    }

    public String getKeyNewsSyncEnabled() {
        return context.getString(R.string.pref_key_news_sync_enabled);
    }

    public String getKeyNewsSyncPeriod() {
        return context.getString(R.string.pref_key_news_sync_period);
    }

    public long getDefaultNewsSyncPeriod() {
        return 24 * 60;
    }

    public String getKeySubstSyncEnabled() {
        return context.getString(R.string.pref_key_subst_sync_enabled);
    }

    public String getKeySubstSyncPeriod() {
        return context.getString(R.string.pref_key_subst_sync_period);
    }

    public long getDefaultSubstSyncPeriod() {
        return 30;
    }

    public String getKeyTeacherSyncEnabled() {
        return context.getString(R.string.pref_key_teacher_sync_enabled);
    }

    public String getKeyTeacherSyncPeriod() {
        return context.getString(R.string.pref_key_teacher_sync_period);
    }

    public long getDefaultTeacherSyncPeriod() {
        return 7 * 24 * 60;
    }

    public String getKeySubstSubjectSettings() {
        return context.getString(R.string.pref_key_subst_subject_settings);
    }

    public String getKeySubstColorSettings() {
        return context.getString(R.string.pref_key_subst_color_settings);
    }
}
