package de.tobiaserthal.akgbensheim.data.preferences;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.PeriodicSync;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.tobiaserthal.akgbensheim.data.Log;
import de.tobiaserthal.akgbensheim.data.R;
import de.tobiaserthal.akgbensheim.data.model.ModelUtils;
import de.tobiaserthal.akgbensheim.data.sync.SyncAdapter;
import de.tobiaserthal.akgbensheim.data.sync.SyncUtils;

public class PreferenceProvider {
    public static final String TAG = "PreferenceProvider";
    public static final String ACTION_SUBST = "de.tobiaserthal.akgbensheim.data.preferences.ACTION_SUBST";

    private static final int BROADCAST_COLOR = 0;
    private static final int BROADCAST_SUBST = 1;

    private static PreferenceProvider instance;

    private Context context;
    private PreferenceKey keys;
    private SharedPreferences preferences;

    private String[] phaseOptions;
    private int[] phaseOptionValues;

    private String[] formOptions;
    private String[] formOptionValues;

    private String[] frequencyOptions;
    private int[] frequencyOptionValues;

    private final SharedPreferences.OnSharedPreferenceChangeListener changeListener
            = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.d(TAG, "Preference change detected for key: %s", key);

            int type = -1;
            String action = null;
            if(keys.getKeyColorSubst().equals(key)
                    || keys.getKeyColorCancel().equals(key)
                    || keys.getKeyColorChange().equals(key)
                    || keys.getKeyColorReserv().equals(key)
                    || keys.getKeyColorRoomSubst().equals(key)
                    || keys.getKeyColorShift().equals(key)
                    || keys.getKeyColorSpecial().equals(key)
                    || keys.getKeyColorOther().equals(key)) {

                action = ACTION_SUBST;
                type = BROADCAST_COLOR;
            } else if(keys.getKeyPhase().equals(key)
                    || keys.getKeyForm().equals(key)
                    || keys.getKeySubstList().equals(key)) {

                action = ACTION_SUBST;
                type = BROADCAST_SUBST;
            }

            if(action != null) {
                Log.d(TAG, "Sending Broadcast event for type: %d", type);

                Intent intent = new Intent(action);
                intent.putExtra("type", type);

                LocalBroadcastManager
                        .getInstance(context)
                        .sendBroadcast(intent);
            }
        }
    };

    private PreferenceProvider(Context context) {
        this.keys = PreferenceKey.getInstance(context);
        this.context = context.getApplicationContext();

        this.preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        this.preferences.registerOnSharedPreferenceChangeListener(changeListener);

        this.phaseOptions = context.getResources().getStringArray(R.array.pref_subst_phase_options);
        this.phaseOptionValues = context.getResources().getIntArray(R.array.pref_subst_phase_options_values);

        this.formOptions = context.getResources().getStringArray(R.array.pref_subst_form_options);
        this.formOptionValues = context.getResources().getStringArray(R.array.pref_subst_form_options_values);

        this.frequencyOptions = context.getResources().getStringArray(R.array.pref_subst_sync_frequency_options);
        this.frequencyOptionValues = context.getResources().getIntArray(R.array.pref_subst_sync_frequency_options_values);

        ensureSubstPhase();
        ensureSubstForm();
        ensureWifiOnly();
    }

    private void ensureWifiOnly() {
        if(!preferences.contains(keys.getKeyOnlyWifi())) {
            setOnlyWifiEnabled(false);
        }
    }

    private void ensureSubstForm() {
        if(!preferences.contains(keys.getKeyForm())) {
            setSubstFormIndex(0);
        }
    }

    private void ensureSubstPhase() {
        if(!preferences.contains(keys.getKeyPhase())) {
            setSubstPhaseIndex(0);
        }
    }

    public static synchronized PreferenceProvider initialize(Context context) {
        instance = new PreferenceProvider(context);
        return instance;
    }

    public static synchronized PreferenceProvider getInstance() {
        if(instance == null) {
            throw new IllegalStateException("PreferenceProvider not initialized!");
        }

        return instance;
    }

    public static void destroyInstance(PreferenceProvider instance) {
        if (instance != null) {
            instance.preferences.unregisterOnSharedPreferenceChangeListener(instance.changeListener);
            instance.preferences = null;

            Arrays.fill(instance.phaseOptions, null);
            Arrays.fill(instance.phaseOptionValues, 0);

            Arrays.fill(instance.formOptions, null);
            Arrays.fill(instance.formOptionValues, null);

            Arrays.fill(instance.frequencyOptions, null);
            Arrays.fill(instance.frequencyOptionValues, 0);
        }
    }

    public Context getContext() {
        return context;
    }

    /**
     * Get the current selected phase for substitution filtering
     * @return The current phase or the first value in the available options as default
     */
    public int getSubstPhase() {
        return preferences.getInt(keys.getKeyPhase(), phaseOptionValues[0]);
    }

    public int getSubstPhaseIndex() {
        int index = Arrays.binarySearch(phaseOptionValues, getSubstPhase());
        return index < 0 ? 0 : index;
    }

    public String getSubstPhaseSummary() {
        return phaseOptions[getSubstPhaseIndex()];
    }

    public String[] getSubsPhaseSummaries() {
        return phaseOptions;
    }

    public void setSubstPhase(int phase) {
        int index = Arrays.binarySearch(phaseOptionValues, phase);
        setSubstPhaseIndex(index);
    }

    public void setSubstPhaseIndex(int index) {
        int phase = phaseOptionValues[Math.min(Math.max(index, 0), phaseOptionValues.length - 1)];
        preferences.edit()
                .putInt(keys.getKeyPhase(), phase)
                .apply();
    }

    public static int getSubstPhaseSek2() {
        return 10;
    }

    /**
     * Get the current selected form for substitution filtering
     * @return The current form or the first value in the available options as default
     */
    public String getSubstForm() {
        return preferences.getString(keys.getKeyForm(), formOptionValues[0]);
    }

    public int getSubstFormIndex() {
        int index = Arrays.binarySearch(formOptionValues, getSubstForm());
        return index < 0 ? 0 : index;
    }

    public String getSubstFormSummary() {
        return formOptions[getSubstFormIndex()];
    }

    public String[] getSubstFormSummaries() {
        return formOptions;
    }

    public void setSubstForm(String form) {
        int index = Arrays.binarySearch(formOptionValues, form);
        setSubstFormIndex(index);
    }

    public void setSubstFormIndex(int index) {
        String form = formOptionValues[Math.min(Math.max(index, 0), formOptionValues.length - 1)];
        preferences.edit()
                .putString(keys.getKeyForm(), form)
                .apply();
    }

    /**
     * Get a list of terms that should be used to filter
     * for specific subjects affected by substitutions.
     * @return The current form or the first value in the available options as default
     */
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

    /**
     * Find out whether the user wants data to be fetched only when connected via wifi.
     * @return The user setting as a boolean
     */
    public boolean isOnlyWifiEnabled() {
        return preferences.getBoolean(keys.getKeyOnlyWifi(), false);
    }

    public void setOnlyWifiEnabled(boolean onlyWifi) {
        preferences.edit()
                .putBoolean(keys.getKeyOnlyWifi(), onlyWifi)
                .apply();
    }

    /**
     * Find out whether the user wants data to be synced whenever the
     * system broadcasts a network tickle.
     * @return The user setting as a boolean.
     */
    public boolean isAutoSyncEnabled() {
        return SyncUtils.shouldSyncAutomatically();
    }

    public void setAutoSyncEnabled(boolean autoSync) {
        SyncUtils.setSyncAutomatically(autoSync);
    }

    public int getFrequencyIndex(int frequency) {
        int index = Arrays.binarySearch(frequencyOptionValues, frequency);
        return index < 0 ? 0 : index;
    }

    public int getFrequency(int index) {
        return frequencyOptionValues[Math.min(Math.max(index, 0), frequencyOptionValues.length)];
    }

    public String getFrequencySummary(int index) {
        return frequencyOptions[Math.min(Math.max(index, 0), frequencyOptions.length)];
    }

    public String[] getFrequencySummaries() {
        return frequencyOptions;
    }

    public ArrayList<PeriodicSyncPreference> getAllSyncs() {
        /* Create the array and fill it with the predefined dummies.
         * This is necessary since disabled syncs are not saved by the system,
         * so we need create the same static array as always by ourselves :( */
        ArrayList<PeriodicSyncPreference> periodicSyncDummies = new ArrayList<>();
        periodicSyncDummies.add(new PeriodicSyncPreference(ModelUtils.NEWS, context.getString(R.string.model_title_news)));
        periodicSyncDummies.add(new PeriodicSyncPreference(ModelUtils.EVENTS, context.getString(R.string.model_title_events)));
        periodicSyncDummies.add(new PeriodicSyncPreference(ModelUtils.TEACHERS, context.getString(R.string.model_title_teachers)));
        periodicSyncDummies.add(new PeriodicSyncPreference(ModelUtils.SUBSTITUTIONS, context.getString(R.string.model_title_subst)));

        // get the current active periodic syncs from the system
        List<PeriodicSync> periodicSyncs = SyncUtils.getPeriodicSyncs();

        // map them to their model identifier
        HashMap<Integer, PeriodicSync> syncHashMap = new HashMap<>();
        for(int i = 0; i < periodicSyncs.size(); i ++) {
            PeriodicSync sync = periodicSyncs.get(i);

            if(sync != null && sync.extras != null) {
                int which = sync.extras.getInt(SyncAdapter.ARGS.ID, -1);
                Log.d(TAG, "Periodic sync #%d: typeFlag: %d, %s",
                        i, which, sync.toString());

                syncHashMap.put(which, sync);
            }
        }

        /* Now we go through our array and look for additional info
         * about each sync provided by the array from the system */
        for(int i = 0; i < periodicSyncDummies.size(); i ++) {
            PeriodicSyncPreference syncDummy = periodicSyncDummies.get(i);
            PeriodicSync sync = syncHashMap.get(syncDummy.getWhich());

            if(sync != null) {
                syncDummy.setEnabled(true);

                int index = getFrequencyIndex((int) sync.period);
                syncDummy.setFrequency(getFrequency(index));
                syncDummy.setSubtitle(getFrequencySummary(index));
            }
        }

        // Do cleanup
        syncHashMap.clear();
        periodicSyncs.clear();

        return periodicSyncDummies;
    }

    public void dispatchAllSyncs(ArrayList<PeriodicSyncPreference> dummies) {
        if(dummies == null) {
            return;
        }

        // go through each dummy entry
        for(int i = 0; i < dummies.size(); i ++) {
            PeriodicSyncPreference dummy = dummies.get(i);
            if(dummy.isEnabled()) {
                Log.d(TAG, "Adding/updating periodic sync #%d: typeFlag: %d", i, dummy.getWhich());
                SyncUtils.requestPeriodic(dummy.getWhich(), dummy.getFrequency());
            } else {
                Log.d(TAG, "Removing periodic sync #%d: typeFlag: %d", i, dummy.getWhich());
                SyncUtils.removePeriodic(dummy.getWhich());
            }
        }
    }

    public static class PeriodicSyncPreference {
        private int which;
        private String title;
        private int frequency;
        private String subtitle;
        private boolean enabled;

        public PeriodicSyncPreference(int which, String title, int frequency, boolean enabled) {
            this.which = which;
            this.title = title;
            this.frequency = frequency;
            this.subtitle = "";
            this.enabled = enabled;
        }

        public PeriodicSyncPreference(int which, String title) {
            this.which = which;
            this.title = title;
            this.frequency = 0;
            this.subtitle = "";
            this.enabled = false;
        }

        public int getWhich() {
            return which;
        }

        public String getTitle() {
            return title;
        }

        public int getFrequency() {
            return frequency;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setWhich(int which) {
            this.which = which;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    @ColorInt
    public int getColorSubst() {
        return preferences.getInt(
                keys.getKeyColorSubst(),
                ContextCompat.getColor(context, R.color.subst_indigo));
    }

    @ColorInt
    public int getColorChange() {
        return preferences.getInt(
                keys.getKeyColorChange(),
                ContextCompat.getColor(context, R.color.subst_indigo));
    }

    @ColorInt
    public int getColorReserv() {
        return preferences.getInt(keys.getKeyColorReserv(),
                ContextCompat.getColor(context, R.color.subst_indigo));
    }

    @ColorInt
    public int getColorCancel() {
        return preferences.getInt(keys.getKeyColorCancel(),
                ContextCompat.getColor(context, R.color.subst_indigo));
    }

    @ColorInt
    public int getColorSpecial() {
        return preferences.getInt(keys.getKeyColorSpecial(),
                ContextCompat.getColor(context, R.color.subst_indigo));
    }

    @ColorInt
    public int getColorRoomSubst() {
        return preferences.getInt(keys.getKeyColorRoomSubst(),
                ContextCompat.getColor(context, R.color.subst_indigo));
    }

    @ColorInt
    public int getColorShift() {
        return preferences.getInt(keys.getKeyColorShift(),
                ContextCompat.getColor(context, R.color.subst_indigo));
    }

    @ColorInt
    public int getColorOther() {
        return preferences.getInt(keys.getKeyColorOther(),
                ContextCompat.getColor(context, R.color.subst_indigo));
    }

    @ColorInt
    public int getColorFromType(String type) {
        if(type.equalsIgnoreCase("Vertretung"))
            return getColorSubst();

        if(type.equalsIgnoreCase("Tausch"))
            return getColorChange();

        if(type.equalsIgnoreCase("Vormerkung"))
            return getColorReserv();

        if(type.equalsIgnoreCase("Fällt aus!"))
            return getColorCancel();

        if(type.equalsIgnoreCase("Sondereins."))
            return getColorSpecial();

        if(type.equalsIgnoreCase("Raum-Vtr."))
            return getColorRoomSubst();

        if(type.equalsIgnoreCase("Verlegung"))
            return getColorShift();

        return getColorOther();
    }

    public void setColorSubst(@ColorInt int color) {
        preferences.edit()
                .putInt(keys.getKeyColorSubst(), color)
                .apply();
    }

    public void setColorChange(@ColorInt int color) {
        preferences.edit()
                .putInt(keys.getKeyColorChange(), color)
                .apply();
    }

    public void setColorReserv(@ColorInt int color) {
        preferences.edit()
                .putInt(keys.getKeyColorReserv(), color)
                .apply();
    }

    public void setColorCancel(@ColorInt int color) {
        preferences.edit()
                .putInt(keys.getKeyColorCancel(), color)
                .apply();
    }

    public void setColorSpecial(@ColorInt int color) {
        preferences.edit()
                .putInt(keys.getKeyColorSpecial(), color)
                .apply();
    }

    public void setColorRoomSubst(@ColorInt int color) {
        preferences.edit()
                .putInt(keys.getKeyColorRoomSubst(), color)
                .apply();
    }

    public void setColorShift(@ColorInt int color) {
        preferences.edit()
                .putInt(keys.getKeyColorShift(), color)
                .apply();
    }

    public void setColorOther(@ColorInt int color) {
        preferences.edit()
                .putInt(keys.getKeyColorOther(), color)
                .apply();
    }

    public static class PreferenceChangeReceiver extends BroadcastReceiver {

        @Override
        public final void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra("type", -1);
            Log.d("PreferenceChangeReceiver", "Received Broadcast event for type: %d", type);

            switch (type) {
                case BROADCAST_COLOR:
                    onColorPreferenceChange();
                    break;

                case BROADCAST_SUBST:
                    onSubstPreferenceChange();
                    break;
            }
        }

        public void onColorPreferenceChange() {}
        public void onSubstPreferenceChange() {}
    }
}
