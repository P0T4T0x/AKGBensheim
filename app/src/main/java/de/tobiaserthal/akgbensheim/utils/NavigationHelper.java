package de.tobiaserthal.akgbensheim.utils;

import android.content.Intent;
import android.net.Uri;
import java.util.regex.Pattern;

import de.tobiaserthal.akgbensheim.backend.utils.Log;
import de.tobiaserthal.akgbensheim.base.MainNavigation;

public class NavigationHelper {
    private static final String TAG = "NavigationHelper";

    private static final Pattern PATH_PREFIX_EVENTS         = Pattern.compile("^/?(termine).*");
    private static final Pattern PATH_PREFIX_NEWS           = Pattern.compile("^/?(aktuelles).*");
    private static final Pattern PATH_PREFIX_FOODPLAN       = Pattern.compile("^/?(home/mensa-cafeteria).*");
    private static final Pattern PATH_PREFIX_CONTACT        = Pattern.compile("^/?(kontakt).*");
    private static final Pattern PATH_PREFIX_TEACHER        = Pattern.compile("^/?(home/wer-ist-wer/lehrer).*");
    private static final Pattern PATH_PREFIX_SUBSTITUTION   = Pattern.compile("^/?(akgweb2011/content/Vertretung|unterricht-co/vertretungsplan).*");

    public static boolean isHostAKG(Uri uri) {
        return  uri != null
                && "http".equals(uri.getScheme())
                && "www.akg-bensheim.de".equals(uri.getHost());
    }

    @MainNavigation.NavigationItem
    public static int getActivityItemFromIntent(Intent intent) {
        return ContextHelper.isUrlRespondIntent(intent) ?
                getActivityItemFromServerUri(intent.getData()) :
                MainNavigation.ACTIVITY_UNKNOWN;
    }

    @MainNavigation.NavigationItem
    public static int getFragmentItemFromIntent(Intent intent) {
        return ContextHelper.isUrlRespondIntent(intent) ?
                getFragmentItemFromServerUri(intent.getData()) :
                MainNavigation.FRAGMENT_HOME;
    }

    @MainNavigation.NavigationItem
    public static int getActivityItemFromServerUri(Uri uri) {
        int id = getItemFromServerUri(uri);
        return isActivityId(id) ? id : MainNavigation.ACTIVITY_UNKNOWN;
    }

    @MainNavigation.NavigationItem
    public static int getFragmentItemFromServerUri(Uri uri) {
        int id = getItemFromServerUri(uri);
        return isFragmentId(id) ? id : MainNavigation.FRAGMENT_HOME;
    }

    /**
     * Test whether the provided intent is a url response intent that contains
     * a path that can be interpreted <b>directly</b> as a fragment navigation item.
     * @param intent The intent to test.
     * @return Whether the intent passed all checks.
     */
    public static boolean isActivityIntent(Intent intent) {
        return ContextHelper.isUrlRespondIntent(intent)
                && isHostAKG(intent.getData())
                && isActivityId(getItemFromServerUri(intent.getData()));
    }

    /**
     * Test whether the provided intent is a url response intent that contains
     * a path that can be interpreted <b>directly</b> as a fragment navigation item.
     * @param intent The intent to test.
     * @return Whether the intent passed all checks.
     */
    public static boolean isFragmentIntent(Intent intent) {
        return ContextHelper.isUrlRespondIntent(intent)
                && isHostAKG(intent.getData())
                && isFragmentId(getItemFromServerUri(intent.getData()));

    }

    public static boolean isActivityId(@MainNavigation.NavigationItem int id) {
        return id == MainNavigation.ACTIVITY_FAQ
                || id == MainNavigation.ACTIVITY_CONTACT
                || id == MainNavigation.ACTIVITY_SETTINGS;
    }

    public static boolean isFragmentId(@MainNavigation.NavigationItem int id) {
        return id == MainNavigation.FRAGMENT_HOME
                || id == MainNavigation.FRAGMENT_NEWS
                || id == MainNavigation.FRAGMENT_EVENT
                || id == MainNavigation.FRAGMENT_TEACHER
                || id == MainNavigation.FRAGMENT_FOODPLAN
                || id == MainNavigation.FRAGMENT_HOMEWORK
                || id == MainNavigation.FRAGMENT_SUBSTITUTION;
    }

    @MainNavigation.NavigationItem
    public static int getItemFromServerUri(Uri uri) {
        if(!isHostAKG(uri)) {
            Log.w(TAG, "The uri does not match with the AKG Website's url scheme.");
            return MainNavigation.ACTIVITY_UNKNOWN;
        }

        String path = uri.getPath();
        if(PATH_PREFIX_EVENTS.matcher(path).matches()) {
            Log.d(TAG, "The uri path matches segments associated section: EVENT");
            return MainNavigation.FRAGMENT_EVENT;
        }

        if(PATH_PREFIX_NEWS.matcher(path).matches()) {
            Log.d(TAG, "The uri path matches segments associated section: NEWS");
            return MainNavigation.FRAGMENT_NEWS;
        }

        if(PATH_PREFIX_FOODPLAN.matcher(path).matches()) {
            Log.d(TAG, "The uri path matches segments associated section: FOODPLAN");
            return MainNavigation.FRAGMENT_FOODPLAN;
        }

        if(PATH_PREFIX_CONTACT.matcher(path).matches()) {
            Log.d(TAG, "The uri path matches segments associated section: CONTACT");
            return MainNavigation.ACTIVITY_CONTACT;
        }

        if(PATH_PREFIX_TEACHER.matcher(path).matches()) {
            Log.d(TAG, "The uri path matches segments associated section: TEACHER");
            return MainNavigation.FRAGMENT_TEACHER;
        }

        if(PATH_PREFIX_SUBSTITUTION.matcher(path).matches()) {
            Log.d(TAG, "The uri path matches segments associated section: SUBSTITUTION");
            return MainNavigation.FRAGMENT_SUBSTITUTION;
        }

        Log.d(TAG, "The uri path matches unknown sections");
        return MainNavigation.ACTIVITY_UNKNOWN;
    }
}
