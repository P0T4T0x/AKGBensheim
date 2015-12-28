package de.tobiaserthal.akgbensheim.backend.preferences;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.tobiaserthal.akgbensheim.backend.utils.Log;

/**
 * Interface to listen to preference changes that are associated with the subst model type.
 * Typical implementations should react to those by refreshing the UI etc...
 */
public abstract class SubstPreferenceChangeReceiver extends BroadcastReceiver {
    protected static final int BROADCAST_COLOR = 0;
    protected static final int BROADCAST_SUBST = 1;

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
    /**
     * Called when the any color assigned to a substitution type is changed.
     * Implementations should refresh the UI but not requery the underlying data since nothing
     * in the dataset has changed.
     */
    public abstract void onColorPreferenceChange();

    /**
     * Called when preferred filtering setting for the substitutions have changed.
     * Typical implementations should requery the underlying dataset if it is affected by these changes.
     */
    public abstract  void onSubstPreferenceChange();
}
