package de.tobiaserthal.akgbensheim.preferences;

import android.app.Activity;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;

import com.github.machinarius.preferencefragment.PreferenceFragment;

import de.tobiaserthal.akgbensheim.BuildConfig;
import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.data.preferences.PreferenceKey;
import de.tobiaserthal.akgbensheim.data.preferences.PreferenceProvider;

/**
 * Created by tobiaserthal on 19.07.15.
 */
public class AppPreferenceFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private NavigationCallback listener;

    private Preference phaseSetting;
    private ListPreference formSetting;
    private Preference subjectSetting;
    private Preference colorSettings;

    private Preference syncEnabledSetting;
    private Preference syncSettings;

    private Preference clearCacheSetting;
    private Preference clearDataSetting;

    private Preference aboutPreference;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (NavigationCallback) activity;
        } catch (ClassCastException e) {
            throw new IllegalStateException("Parent activity mus implement onPreferenceClickListener!");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_screen);

        PreferenceKey keys = PreferenceKey.getInstance(getActivity());

        // get preferences
        phaseSetting = findPreference(keys.getKeyPhase());
        formSetting = (ListPreference) findPreference(keys.getKeyForm());
        subjectSetting = findPreference(keys.getKeySubjectFilter());
        colorSettings = findPreference(keys.getKeySubstColorSettings());

        syncEnabledSetting = findPreference(keys.getKeySyncEnabled());
        syncSettings = findPreference(keys.getKeySyncSettings());

        clearCacheSetting = findPreference(keys.getKeyClearCache());
        clearDataSetting = findPreference(keys.getKeyClearData());

        aboutPreference = findPreference(keys.getKeyAbout());

        // set listener
        phaseSetting.setOnPreferenceChangeListener(this);
        formSetting.setOnPreferenceChangeListener(this);
        subjectSetting.setOnPreferenceClickListener(this);
        colorSettings.setOnPreferenceClickListener(this);

        syncEnabledSetting.setOnPreferenceChangeListener(this);
        syncSettings.setOnPreferenceClickListener(this);

        clearCacheSetting.setOnPreferenceClickListener(this);
        clearDataSetting.setOnPreferenceClickListener(this);

        // setup initial state
        phaseSetting.setSummary(PreferenceProvider.getInstance(getActivity()).getSubstPhaseSummary());
        formSetting.setSummary(PreferenceProvider.getInstance(getActivity()).getSubstForm());
        subjectSetting.setSummary(PreferenceProvider.getInstance(getActivity()).getSubstSubjectsSummary());

        aboutPreference.setSummary(getString(R.string.pref_summary_about, BuildConfig.VERSION_NAME));


        /*
        findPreference(getString(R.string.pref_key_sync_settings)).setOnPreferenceClickListener(listener);
        findPreference(getString(R.string.pref_key_sync_enabled)).setOnPreferenceChangeListener(listener);
        findPreference(getString(R.string.pref_key_subst_subject_settings)).setOnPreferenceClickListener(listener);
        */

    }

    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String key = preference.getKey();
        final PreferenceKey keys = PreferenceKey.getInstance(getActivity());

        if(key.equals(keys.getKeyPhase())) {
            phaseSetting.setSummary((String) newValue);

            int value = Integer.valueOf((String) newValue);
            if(value > 9) {
                formSetting.setEnabled(false);
                formSetting.setValueIndex(0);
            } else {
                formSetting.setEnabled(true);
                formSetting.setValueIndex(1);
            }
        }

        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        final String key = preference.getKey();
        final PreferenceKey keys = PreferenceKey.getInstance(getActivity());

        if(key.equals(keys.getKeySubstSubjectSettings())) {
            if(listener != null) {
                listener.loadSubjectSettings();
                return true;
            }

            return false;
        }

        if(key.equals(keys.getKeySubstColorSettings())) {
            if(listener != null) {
                listener.loadColorSettings();
                return true;
            }

            return false;
        }

        if(key.equals(keys.getKeySyncSettings())) {
            if(listener != null) {
                listener.loadSyncSettings();
                return true;
            }

            return false;
        }

        return false;
    }

    public interface NavigationCallback {
        void loadSubjectSettings();
        void loadColorSettings();
        void loadSyncSettings();
    }
}
