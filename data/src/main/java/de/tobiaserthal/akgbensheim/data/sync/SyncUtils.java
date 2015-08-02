package de.tobiaserthal.akgbensheim.data.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;

import de.tobiaserthal.akgbensheim.data.preferences.PreferenceKey;
import de.tobiaserthal.akgbensheim.data.sync.SyncAdapter.SYNC;

import de.tobiaserthal.akgbensheim.data.provider.DataProvider;
import de.tobiaserthal.akgbensheim.data.sync.auth.Authenticator;
import de.tobiaserthal.akgbensheim.data.sync.auth.AuthenticatorService;

public class SyncUtils {
    private static final String PREF_SETUP_COMPLETE = "setup_complete";
    public static final String ACCOUNT_TYPE = "de.tobiaserthal.akgbensheim.account";

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
        if (accountManager.addAccountExplicitly(account, null, null)) {

            // Inform the system that this account supports sync
            ContentResolver.setIsSyncable(account, DataProvider.AUTHORITY, 1);

            // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(account, DataProvider.AUTHORITY, autoSync);

            // Recommend a schedule for automatic synchronization. The system may modify this based
            // on other scheduled syncs and network utilization.
            requestPeriodic(account, SYNC.EVENTS, PreferenceKey.getInstance(context).getDefaultEventSyncPeriod());
            requestPeriodic(account, SYNC.NEWS, PreferenceKey.getInstance(context).getDefaultNewsSyncPeriod());
            requestPeriodic(account, SYNC.SUBSTITUTIONS, PreferenceKey.getInstance(context).getDefaultSubstSyncPeriod());
            requestPeriodic(account, SYNC.TEACHERS, PreferenceKey.getInstance(context).getDefaultTeacherSyncPeriod());

            newAccount = true;
        }

        // Schedule an initial sync if we detect problems with either our account or our local
        // data has been deleted. (Note that it's possible to clear app data WITHOUT affecting
        // the account list, so wee need to check both.)
        if (newAccount || !setupComplete) {
            triggerRefresh();
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

    public static void triggerRefresh() {
        Bundle options = new Bundle();
        options.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        options.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        ContentResolver.requestSync(
                AuthenticatorService.getAccount(ACCOUNT_TYPE),
                DataProvider.AUTHORITY,
                options
        );
    }

    public static void triggerRefresh(int which) {
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

    public static void requestPeriodic(int which, long minutes) {
        requestPeriodic(AuthenticatorService.getAccount(ACCOUNT_TYPE), which, minutes);
    }

    public static void requestPeriodic(Account account, int which, long minutes) {
        final long frequency = minutes * 60 /* seconds/minute */;

        Bundle options = new Bundle();
        options.putInt(SYNC.ARG, which);

        ContentResolver.addPeriodicSync(
                account, DataProvider.AUTHORITY, options, frequency);
    }
}
