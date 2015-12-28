package de.tobiaserthal.akgbensheim.backend.preferences;

import android.content.Context;

import de.tobiaserthal.akgbensheim.backend.R;

/**
 * A simple helper class of static methods to retrieve the preference keys defined in XML resources.
 * Semantic of the getters: {@code pref_key_{category}_{keyName}}
 */
public class PreferenceKeychain {
    public static String getKeyDataOnlyWifi(Context context) {
        return context.getString(R.string.pref_key_data_onlyWifi);
    }

    public static String getKeyDataClearCache(Context context) {
        return context.getString(R.string.pref_key_data_clearCache);
    }

    public static String getKeyDataClearData(Context context) {
        return context.getString(R.string.pref_key_data_clearData);
    }

    public static String getKeyAboutLicence(Context context) {
        return context.getString(R.string.pref_key_about_licence);
    }

    public static String getKeyAboutVersion(Context context) {
        return context.getString(R.string.pref_key_about_version);
    }

    public static String getKeySupportMailDev(Context context) {
        return context.getString(R.string.pref_key_support_mailDev);
    }

    public static String getKeySubstFilter(Context context) {
        return context.getString(R.string.pref_key_subst_filter);
    }

    public static String getKeySubstFilterSettings(Context context) {
        return context.getString(R.string.pref_key_subst_filterSettings);
    }

    public static String getKeySubstColorSettings(Context context) {
        return context.getString(R.string.pref_key_subst_colorSettings);
    }

    public static String getKeySubstPhase(Context context) {
        return context.getString(R.string.pref_key_subst_phase);
    }

    public static String getKeySubstForm(Context context) {
        return context.getString(R.string.pref_key_subst_form);
    }

    public static String getKeySyncBackgroundEnabled(Context context) {
        return context.getString(R.string.pref_key_sync_backgroundEnabled);
    }

    public static String getKeySyncAdvancedSettings(Context context) {
        return context.getString(R.string.pref_key_sync_advancedSettings);
    }

    public static String getKeyColorSubst(Context context) {
        return context.getString(R.string.pref_key_subst_color_subst);
    }

    public static String getKeyColorChange(Context context) {
        return context.getString(R.string.pref_key_subst_color_change);
    }

    public static String getKeyColorReserv(Context context) {
        return context.getString(R.string.pref_key_subst_color_reserv);
    }

    public static String getKeyColorCancel(Context context) {
        return context.getString(R.string.pref_key_subst_color_cancel);
    }

    public static String getKeyColorSpecial(Context context) {
        return context.getString(R.string.pref_key_subst_color_special);
    }

    public static String getKeyColorRoomSubst(Context context) {
        return context.getString(R.string.pref_key_subst_color_roomSubst);
    }

    public static String getKeyColorShift(Context context) {
        return context.getString(R.string.pref_key_subst_color_shift);
    }

    public static String getKeyColorOther(Context context) {
        return context.getString(R.string.pref_key_subst_color_other);
    }
}
