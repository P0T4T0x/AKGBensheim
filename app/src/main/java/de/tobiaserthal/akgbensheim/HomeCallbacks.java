package de.tobiaserthal.akgbensheim;

import android.os.Bundle;

import static de.tobiaserthal.akgbensheim.MainNavigation.NavigationItem;

/**
 * Created by tobiaserthal on 17.09.15.
 */
public interface HomeCallbacks {
    void onItemClicked(@NavigationItem int type, Bundle extras);
    void onSubItemClicked(@NavigationItem int type, long id, Bundle extras);
}
