package de.tobiaserthal.akgbensheim.preferences;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.machinarius.preferencefragment.PreferenceFragment;

import java.util.Arrays;

import de.tobiaserthal.akgbensheim.BuildConfig;
import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.data.preferences.PreferenceKey;
import de.tobiaserthal.akgbensheim.data.preferences.PreferenceProvider;
import de.tobiaserthal.akgbensheim.tools.FileUtils;

/**
 * Created by tobiaserthal on 19.07.15.
 */

//FIXME: Crash on devices with api level <14 because of TwoStatePreference not in the framework
public class AppPreferenceFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private NavigationCallback listener;

    private Preference phaseSetting;
    private Preference formSetting;
    private Preference subjectSetting;
    private Preference colorSettings;

    private CheckBoxPreference syncEnabledSetting;
    private Preference syncSettings;

    private Preference clearCacheSetting;
    private Preference clearDataSetting;

    private Preference aboutPreference;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (NavigationCallback) context;
        } catch (ClassCastException e) {
            throw new IllegalStateException("Parent context must implement onPreferenceClickListener!");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_screen);

        PreferenceKey keys = PreferenceKey.getInstance(getActivity());

        // get preferences
        phaseSetting = findPreference(keys.getKeyPhase());
        formSetting = findPreference(keys.getKeyForm());
        subjectSetting = findPreference(keys.getKeySubjectFilter());
        colorSettings = findPreference(keys.getKeySubstColorSettings());

        syncEnabledSetting = (CheckBoxPreference) findPreference(keys.getKeySyncEnabled());
        syncSettings = findPreference(keys.getKeySyncSettings());

        clearCacheSetting = findPreference(keys.getKeyClearCache());
        clearDataSetting = findPreference(keys.getKeyClearData());

        aboutPreference = findPreference(keys.getKeyAbout());

        // set listener
        phaseSetting.setOnPreferenceClickListener(this);
        formSetting.setOnPreferenceClickListener(this);
        subjectSetting.setOnPreferenceClickListener(this);
        colorSettings.setOnPreferenceClickListener(this);

        syncEnabledSetting.setOnPreferenceChangeListener(this);
        syncSettings.setOnPreferenceClickListener(this);

        clearCacheSetting.setOnPreferenceClickListener(this);
        clearDataSetting.setOnPreferenceClickListener(this);

        setPreferenceValues();
        switchFormState();
    }

    private void setPreferenceValues() {
        phaseSetting.setSummary(PreferenceProvider.getInstance().getSubstPhaseSummary());
        formSetting.setSummary(PreferenceProvider.getInstance().getSubstFormSummary());
        subjectSetting.setSummary(PreferenceProvider.getInstance().getSubstSubjectsSummary());

        syncEnabledSetting.setChecked(PreferenceProvider.getInstance().isAutoSyncEnabled());
        aboutPreference.setSummary(getString(R.string.pref_summary_about, BuildConfig.VERSION_NAME));
    }

    private void switchFormState() {
        formSetting.setShouldDisableView(true);
        formSetting.setEnabled(PreferenceProvider.getInstance().getSubstPhase()
                < PreferenceProvider.getSubstPhaseSek2());
    }

    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if(preference.equals(syncEnabledSetting)) {
            boolean value = (Boolean) newValue;
            PreferenceProvider.getInstance().setAutoSyncEnabled(value);
        }

        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if(preference.equals(phaseSetting)) {
            showPhaseSetting();
            return true;
        }

        if(preference.equals(formSetting)) {
            showFormSettings();
            return true;
        }

        if(preference.equals(subjectSetting)) {
            if(listener != null) {
                listener.loadSubjectSettings();
                return true;
            }

            return false;
        }

        if(preference.equals(colorSettings)) {
            if(listener != null) {
                listener.loadColorSettings();
                return true;
            }

            return false;
        }

        if(preference.equals(syncSettings)) {
            if(listener != null) {
                listener.loadSyncSettings();
                return true;
            }

            return false;
        }

        if(preference.equals(clearCacheSetting)) {
            showClearCache();
            return true;
        }

        if(preference.equals(clearDataSetting)) {
            showClearData();
            return true;
        }

        return false;
    }

    private void showPhaseSetting() {
        int current = PreferenceProvider.getInstance().getSubstPhaseIndex();
        new MaterialDialog.Builder(getContext())
                .title(R.string.pref_title_subst_phase)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .items(PreferenceProvider.getInstance().getSubsPhaseSummaries())
                .itemsCallbackSingleChoice(current, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        PreferenceProvider.getInstance()
                                .setSubstPhaseIndex(which);

                        phaseSetting.setSummary(text);
                        switchFormState();
                        return true;
                    }
                })
                .build()
                .show();
    }

    private void showFormSettings() {
        int current = PreferenceProvider.getInstance().getSubstFormIndex();
        new MaterialDialog.Builder(getContext())
                .title(R.string.pref_title_subst_form)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .items(PreferenceProvider.getInstance().getSubstFormSummaries())
                .itemsCallbackSingleChoice(current, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        PreferenceProvider.getInstance()
                                .setSubstFormIndex(which);

                        formSetting.setSummary(text);
                        return true;
                    }
                })
                .build()
                .show();
    }

    private void showClearCache() {
        new MaterialDialog.Builder(getContext())
                .title(R.string.pref_title_clear_cache)
                .content(R.string.pref_prompt_clear_cache)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .autoDismiss(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        FileUtils.clearCache(getContext());
                        dialog.dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .build()
                .show();
    }

    private void showClearData() {
        new MaterialDialog.Builder(getContext())
                .title(R.string.pref_title_clear_data)
                .content(R.string.pref_prompt_clear_data)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .autoDismiss(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        FileUtils.clearData(getContext());
                        dialog.dismiss();

                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .build()
                .show();
    }

    public interface NavigationCallback {
        void loadSubjectSettings();
        void loadColorSettings();
        void loadSyncSettings();
    }
}
