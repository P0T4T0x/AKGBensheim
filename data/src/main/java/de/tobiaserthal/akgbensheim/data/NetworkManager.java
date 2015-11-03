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
    private static final String TAG = "NetworkManager";

    private Context context;
    private static NetworkManager instance;
    private ConnectivityManager connectivityManager;
    private ArrayList<NetworkChangeListener> listeners;

    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean allowed = isAccessAllowed();

            for(int i = 0; i < listeners.size(); i++) {
                listeners.get(i).onNetworkAccessibilityChanged(allowed);
            }
        }
    };

    private NetworkManager(Context context) {
        this.listeners = new ArrayList<>();
        this.context = context.getApplicationContext();
        this.connectivityManager = (ConnectivityManager) getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        IntentFilter networkFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getContext().registerReceiver(networkReceiver, networkFilter);
    }

    public static NetworkManager getInstance(Context context) {
        if(instance == null) {
            instance = new NetworkManager(context);
        }

        return instance;
    }

    public Context getContext() {
        return context;
    }

    public void addNetworkListener(NetworkChangeListener listener) {
        if(listener != null) {
            listeners.add(listener);
        }
    }

    public void removeNetworkListener(NetworkChangeListener listener) {
        listeners.remove(listener);
    }

    public boolean isAccessAllowed() {
        if(connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if(networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
                Log.d(TAG, "Network is not connected and no connection attempt is scheduled.");
                return false;
            }

            boolean wifi = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
            boolean onlyWifi = PreferenceProvider.getInstance(getContext()).isOnlyWifiEnabled();

            Log.d(TAG, "Network connected! OnlyWifi: %b, wifi: %b", onlyWifi, wifi);
            return !onlyWifi || wifi;
        }

        Log.d(TAG, "Could not initialize ConnectivityManager!");
        return false;
    }

    public static void destroyInstance(NetworkManager manager) {
        if(manager != null) {
            manager.getContext().unregisterReceiver(manager.networkReceiver);
            manager.listeners.clear();
        }
    }


    public interface NetworkChangeListener {
        void onNetworkAccessibilityChanged(boolean allowed);
    }
}
