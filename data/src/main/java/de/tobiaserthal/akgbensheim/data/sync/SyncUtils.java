package de.tobiaserthal.akgbensheim.data.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.PeriodicSync;
import android.os.Bundle;
import android.preference.PreferenceManager;

import java.io.DataOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.tobiaserthal.akgbensheim.data.Log;
import de.tobiaserthal.akgbensheim.data.R;
import de.tobiaserthal.akgbensheim.data.preferences.PreferenceKey;
import de.tobiaserthal.akgbensheim.data.sync.SyncAdapter.SYNC;

import de.tobiaserthal.akgbensheim.data.provider.DataProvider;
import de.tobiaserthal.akgbensheim.data.sync.auth.AuthenticatorService;

public class SyncUtils {
    private static final String PREF_SETUP_COMPLETE = "setup_complete";
    public static final String ACCOUNT_TYPE = "de.tobiaserthal.akgbensheim.account";
    public static final String TAG = "SyncUtils";

    public static void createSyncAccount(Context context) {
        createSyncAccount(context, true);
    }

    public static void createSyncAccount(Context context, boolean autoSync) {
        boolean newAccount = false;
        boolean setupComplete = PreferenceManager
                .getDefaultSharedPreferences(context).getBoolean(PREF_SETUP_COMPLETE, false);

        // Create account, if it's missing. (Either first run, or user has deleted account.)
        Account account = AuthenticatorService.getAccount(ACCOUNT_TYPE);
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Log.d(TAG, "Adding account to system manager...");
        if (accountManager.addAccountExplicitly(account, null, null)) {
            Log.d(TAG, "Setting up sync settings...");

            // Inform the system that this account supports sync
            ContentResolver.setIsSyncable(account, DataProvider.AUTHORITY, 1);

            // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(account, DataProvider.AUTHORITY, autoSync);

            // Recommend a schedule for automatic synchronization. The system may modify this based
            // on other scheduled syncs and network utilization.
            int[] values = context.getResources().getIntArray(R.array.pref_subst_sync_frequency_options_values);
            requestPeriodic(account, SYNC.EVENTS, values[5]);
            requestPeriodic(account, SYNC.NEWS, values[6]);
            requestPeriodic(account, SYNC.SUBSTITUTIONS, values[1]);
            requestPeriodic(account, SYNC.TEACHERS, values[8]);

            newAccount = true;
        }

        // Schedule an initial sync if we detect problems with either our account or our local
        // data has been deleted. (Note that it's possible to clear app data WITHOUT affecting
        // the account list, so wee need to check both.)
        if (newAccount || !setupComplete) {
            Log.d(TAG, "Firing initial sync process...");

            triggerRefresh(SYNC.ALL);
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putBoolean(PREF_SETUP_COMPLETE, true).commit();
        } else {
            Log.d(TAG, "Canceling pending syncs...");
            cancelCurrentSync();
        }
    }

    public static void setSyncAutomatically(boolean automatically) {
        ContentResolver.setSyncAutomatically(
                AuthenticatorService.getAccount(ACCOUNT_TYPE),
                DataProvider.AUTHORITY,
                automatically
        );
    }

    public static boolean shouldSyncAutomatically() {
        return ContentResolver.getSyncAutomatically(
                AuthenticatorService.getAccount(ACCOUNT_TYPE),
                DataProvider.AUTHORITY);
    }

    public static void cancelCurrentSync() {
        ContentResolver.cancelSync(
                AuthenticatorService.getAccount(ACCOUNT_TYPE),
                DataProvider.AUTHORITY);
    }

    public static void forceRefresh(int which) {
        Log.d(TAG, "Force refresh triggered for id: %d", which);

        Bundle options = new Bundle();
        options.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        options.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        options.putInt(SYNC.ARG, which);

        ContentResolver.requestSync(
                AuthenticatorService.getAccount(ACCOUNT_TYPE),
                DataProvider.AUTHORITY,
                options
        );
    }

    public static void triggerRefresh(int which) {
        Log.d(TAG, "Adding refresh to queue for id: %d", which);

        Bundle options = new Bundle();
        options.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        options.putInt(SYNC.ARG, which);

        ContentResolver.requestSync(
                AuthenticatorService.getAccount(ACCOUNT_TYPE),
                DataProvider.AUTHORITY,
                options
        );
    }

    public static void removePeriodic(int which) {
        removePeriodic(AuthenticatorService.getAccount(ACCOUNT_TYPE), which);
    }

    public static void removePeriodic(Account account, int which) {
        Bundle options = new Bundle();
        options.putInt(SYNC.ARG, which);

        ContentResolver.removePeriodicSync(
                account, DataProvider.AUTHORITY, options);
    }

    public static void requestPeriodic(int which, long minutes) {
        requestPeriodic(AuthenticatorService.getAccount(ACCOUNT_TYPE), which, minutes);
    }

    public static void requestPeriodic(Account account, int which, long seconds) {
        Bundle options = new Bundle();
        options.putInt(SYNC.ARG, which);

        ContentResolver.addPeriodicSync(
                account, DataProvider.AUTHORITY, options, seconds);
    }

    public static List<PeriodicSync> getPeriodicSyncs() {
        return getPeriodicSyncs(AuthenticatorService.getAccount(ACCOUNT_TYPE));
    }

    public static List<PeriodicSync> getPeriodicSyncs(Account account) {
        return ContentResolver.getPeriodicSyncs(
                account, DataProvider.AUTHORITY);
    }
}
