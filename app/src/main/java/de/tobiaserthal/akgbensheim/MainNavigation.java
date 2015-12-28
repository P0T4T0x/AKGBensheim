package de.tobiaserthal.akgbensheim;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A simple interface to manage user navigation containing constant ids
 * for each application component ans a method declaration implementations should use to switch views.
 */
public interface MainNavigation {
    int FRAGMENT_HOME = 0;
    int FRAGMENT_NEWS = 1;
    int FRAGMENT_EVENT = 2;
    int FRAGMENT_TEACHER = 3;
    int FRAGMENT_HOMEWORK = 4;
    int FRAGMENT_FOODPLAN = 5;
    int FRAGMENT_SUBSTITUTION = 6;

    int ACTIVITY_CONTACT = 8;
    int ACTIVITY_SETTINGS = 9;
    int ACTIVITY_FAQ = 10;

    @IntDef({
            FRAGMENT_HOME,
            FRAGMENT_NEWS,
            FRAGMENT_EVENT,
            FRAGMENT_TEACHER,
            FRAGMENT_HOMEWORK,
            FRAGMENT_FOODPLAN,
            FRAGMENT_SUBSTITUTION,
            ACTIVITY_CONTACT,
            ACTIVITY_SETTINGS,
            ACTIVITY_FAQ
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface NavigationItem {}

    void callNavigationItem(@NavigationItem int item);
}
