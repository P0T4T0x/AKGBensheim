package de.tobiaserthal.akgbensheim.backend.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.PeriodicSync;
import android.os.Bundle;
import android.preference.PreferenceManager;

import java.util.List;

import de.tobiaserthal.akgbensheim.backend.R;
import de.tobiaserthal.akgbensheim.backend.model.ModelUtils;
import de.tobiaserthal.akgbensheim.backend.provider.DataProvider;
import de.tobiaserthal.akgbensheim.backend.sync.auth.AuthenticatorService;
import de.tobiaserthal.akgbensheim.backend.utils.Log;

public class SyncUtils {
    private static final String PREF_SETUP_COMPLETE = "setup_complete";
    public static final String ACCOUNT_TYPE = "de.tobiaserthal.akgbensheim.account";
    public static final String TAG = "SyncUtils";

    public static void createSyncAccount(Context context) {
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
            ContentResolver.setSyncAutomatically(account, DataProvider.AUTHORITY, true);

            // Recommend a schedule for automatic synchronization. The system may modify this based
            // on other scheduled syncs and network utilization.
            int[] values = context.getResources().getIntArray(R.array.pref_subst_sync_frequency_options_values);
            requestPeriodic(account, ModelUtils.EVENTS, values[5]);
            requestPeriodic(account, ModelUtils.NEWS, values[6]);
            requestPeriodic(account, ModelUtils.SUBSTITUTIONS, values[1]);
            requestPeriodic(account, ModelUtils.TEACHERS, values[8]);

            newAccount = true;
        }

        // Schedule an initial sync if we detect problems with either our account or our local
        // data has been deleted. (Note that it's possible to clear app data WITHOUT affecting
        // the account list, so wee need to check both.)
        if (newAccount || !setupComplete) {
            Log.d(TAG, "Firing initial sync process...");

            forceRefresh(ModelUtils.ALL);
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putBoolean(PREF_SETUP_COMPLETE, true).commit();
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
        options.putInt(SyncAdapter.ARGS.ID, which);

        ContentResolver.requestSync(
                AuthenticatorService.getAccount(ACCOUNT_TYPE),
                DataProvider.AUTHORITY,
                options
        );
    }

    public static void loadNews(int start, int count) {
        Log.d(TAG, "Loading news starting from index: %d to index: %d", start, (start - 1) + count);

        Bundle options = new Bundle();
        options.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        options.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        options.putInt(SyncAdapter.ARGS.ID, ModelUtils.NEWS);
        options.putInt(SyncAdapter.ARGS.NEWS_START, start);
        options.putInt(SyncAdapter.ARGS.NEWS_COUNT, count);

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
        options.putInt(SyncAdapter.ARGS.ID, which);

        ContentResolver.removePeriodicSync(
                account, DataProvider.AUTHORITY, options);
    }

    public static void requestPeriodic(int which, long seconds) {
        requestPeriodic(AuthenticatorService.getAccount(ACCOUNT_TYPE), which, seconds);
    }

    public static void requestPeriodic(Account account, int which, long seconds) {
        Bundle options = new Bundle();
        options.putInt(SyncAdapter.ARGS.ID, which);

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
