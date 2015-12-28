package de.tobiaserthal.akgbensheim.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.WeakHashMap;

import de.tobiaserthal.akgbensheim.data.preferences.PreferenceProvider;
import de.tobiaserthal.akgbensheim.data.sync.SyncUtils;

/**
 * Created by tobiaserthal on 20.10.15.
 */
public class NetworkManager {
    public static final String TAG = "NetworkManager";
    public static final String ACTION_NETWORK = "de.tobiaserthal.akgbensheim.data.NetworkManager.ACTION_NETWORK";

    private Context context;
    private static NetworkManager instance;
    private ConnectivityManager connectivityManager;

    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent data) {
            boolean allowed = isAccessAllowed();

            Intent intent = new Intent(ACTION_NETWORK);
            intent.putExtra("allowed", allowed);
            context.sendBroadcast(intent);
        }
    };

    private NetworkManager(Context context) {
        this.context = context.getApplicationContext();
        this.connectivityManager = (ConnectivityManager) getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        IntentFilter networkFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getContext().registerReceiver(networkReceiver, networkFilter);
    }

    public static synchronized NetworkManager initialize(Context context) {
        instance = new NetworkManager(context);
        return instance;
    }

    public static synchronized NetworkManager getInstance() {
        if(instance == null) {
            throw new IllegalStateException("NetworkManager not initialized");
        }

        return instance;
    }

    public Context getContext() {
        return context;
    }

    public boolean isAccessAllowed() {
        if(connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if(networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
                Log.d(TAG, "Network is not connected and no connection attempt is scheduled.");
                return false;
            }

            boolean wifi = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
            boolean onlyWifi = PreferenceProvider.getInstance().isOnlyWifiEnabled();

            Log.d(TAG, "Network connected! OnlyWifi: %b, wifi: %b", onlyWifi, wifi);
            return !onlyWifi || wifi;
        }

        Log.d(TAG, "Could not initialize ConnectivityManager!");
        return false;
    }

    public static void destroyInstance(NetworkManager manager) {
        if(manager != null) {
            manager.getContext().unregisterReceiver(manager.networkReceiver);
        }
    }

    public static class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public final void onReceive(Context context, Intent intent) {
            onNetworkAccessibilityChanged(intent.getBooleanExtra("allowed", false));
        }

        public void onNetworkAccessibilityChanged(boolean accessAllowed) {}
    }
}
