package de.tobiaserthal.akgbensheim.preferences;

import android.os.Bundle;

import com.github.machinarius.preferencefragment.PreferenceFragment;

import de.tobiaserthal.akgbensheim.R;

/**
 * Created by tobiaserthal on 19.07.15.
 */
public class SyncPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_screen_sync);
    }
}
