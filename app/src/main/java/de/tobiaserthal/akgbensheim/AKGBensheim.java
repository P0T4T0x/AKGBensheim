package de.tobiaserthal.akgbensheim;

import android.app.Application;
import android.content.IntentFilter;

import com.epapyrus.plugpdf.core.PlugPDF;
import com.epapyrus.plugpdf.core.PlugPDFException;

import de.tobiaserthal.akgbensheim.backend.preferences.PreferenceProvider;
import de.tobiaserthal.akgbensheim.backend.sync.SyncUtils;
import de.tobiaserthal.akgbensheim.backend.utils.Log;
import de.tobiaserthal.akgbensheim.backend.utils.NetworkManager;

public class AKGBensheim extends Application {

    private final NetworkManager.NetworkChangeReceiver networkChangeReceiver
            = new NetworkManager.NetworkChangeReceiver() {

        @Override
        public void onNetworkAccessibilityChanged(boolean allowed) {
            if(!allowed) {
                Log.d("AKGBensheim", "Canceling syncs due to network change.");
                SyncUtils.cancelCurrentSync();
            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();

        // Create sync account to bind pending syncs to
        // Perform initial sync if necessary and initialize preferences
        SyncUtils.createSyncAccount(this);
        PreferenceProvider.initialize(this);

        // Listen to network changes by subscribing a listener
        NetworkManager.initialize(this);

        IntentFilter intent = new IntentFilter(NetworkManager.ACTION_NETWORK);
        registerReceiver(networkChangeReceiver, intent);

        try {
            PlugPDF.init(this, BuildConfig.PLUGPDF_API_KEY);
            if(BuildConfig.DEBUG) {
                PlugPDF.setUpdateCheckEnabled(true);
                PlugPDF.enableUncaughtExceptionHandler();
            } else {
                PlugPDF.setUpdateCheckEnabled(false);
            }
        } catch (PlugPDFException.InvalidLicense invalidLicense) {
            invalidLicense.printStackTrace();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        PreferenceProvider provider = PreferenceProvider.getInstance();
        PreferenceProvider.destroyInstance(provider);

        // unregister Network Manager callbacks which might hold references to context objects
        NetworkManager manager = NetworkManager.getInstance();
        NetworkManager.destroyInstance(manager);

        unregisterReceiver(networkChangeReceiver);
    }
}
