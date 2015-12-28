package de.tobiaserthal.akgbensheim.home;

import android.os.Bundle;

import de.tobiaserthal.akgbensheim.base.MainNavigation;

public interface HomeCallbacks {
    void onItemClicked(@MainNavigation.NavigationItem int type, Bundle extras);
    void onSubItemClicked(@MainNavigation.NavigationItem int type, long id, Bundle extras);
}
