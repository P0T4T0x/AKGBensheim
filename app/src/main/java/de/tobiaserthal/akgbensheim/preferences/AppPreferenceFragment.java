package de.tobiaserthal.akgbensheim.preferences;

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.machinarius.preferencefragment.PreferenceFragment;

import de.tobiaserthal.akgbensheim.BuildConfig;
import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.backend.preferences.PreferenceProvider;
import de.tobiaserthal.akgbensheim.utils.FileHelper;

import static de.tobiaserthal.akgbensheim.backend.preferences.PreferenceKeychain.*;

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
    private Preference licencePreference;

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

        // get preferences
        phaseSetting = findPreference(getKeySubstPhase(getContext()));
        formSetting = findPreference(getKeySubstForm(getContext()));
        subjectSetting = findPreference(getKeySubstFilterSettings(getContext()));
        colorSettings = findPreference(getKeySubstColorSettings(getContext()));

        syncEnabledSetting = (CheckBoxPreference) findPreference(getKeySyncBackgroundEnabled(getContext()));
        syncSettings = findPreference(getKeySyncAdvancedSettings(getContext()));

        clearCacheSetting = findPreference(getKeyDataClearCache(getContext()));
        clearDataSetting = findPreference(getKeyDataClearData(getContext()));

        aboutPreference = findPreference(getKeyAboutVersion(getContext()));
        licencePreference = findPreference(getKeyAboutLicence(getContext()));

        // set listener
        phaseSetting.setOnPreferenceClickListener(this);
        formSetting.setOnPreferenceClickListener(this);
        subjectSetting.setOnPreferenceClickListener(this);
        colorSettings.setOnPreferenceClickListener(this);

        syncEnabledSetting.setOnPreferenceChangeListener(this);
        syncSettings.setOnPreferenceClickListener(this);

        clearCacheSetting.setOnPreferenceClickListener(this);
        clearDataSetting.setOnPreferenceClickListener(this);

        licencePreference.setOnPreferenceClickListener(this);

        setPreferenceValues();
        switchFormState();
    }

    private void setPreferenceValues() {
        phaseSetting.setSummary(PreferenceProvider.getInstance().getSubstPhaseSummary());
        formSetting.setSummary(PreferenceProvider.getInstance().getSubstFormSummary());
        subjectSetting.setSummary(PreferenceProvider.getInstance().getSubstSubjectsSummary());

        syncEnabledSetting.setChecked(PreferenceProvider.getInstance().isAutoSyncEnabled());
        aboutPreference.setSummary(getString(R.string.pref_summary_about_version, BuildConfig.VERSION_NAME));
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

        if(preference.equals(licencePreference)) {
            showLicence();
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
                .title(R.string.action_prompt_title_clearCache)
                .content(R.string.action_prompt_body_clearCache)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .autoDismiss(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        FileHelper.clearCache(getContext());
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
                .title(R.string.action_prompt_title_deleteData)
                .content(R.string.action_prompt_body_deleteData)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .autoDismiss(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        FileHelper.clearData(getContext());
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

    private void showLicence() {
        new MaterialDialog.Builder(getContext())
                .title(R.string.pref_title_about_licence)
                .iconRes(R.mipmap.ic_launcher)
                .content(Html.fromHtml(
                        FileHelper.readRawTextFile(getContext(), R.raw.licence))
                )
                .positiveText(android.R.string.ok)
                .build()
                .show();
    }

    public interface NavigationCallback {
        void loadSubjectSettings();
        void loadColorSettings();
        void loadSyncSettings();
    }
}
