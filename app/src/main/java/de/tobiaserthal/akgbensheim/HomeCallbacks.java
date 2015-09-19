package de.tobiaserthal.akgbensheim;

import static de.tobiaserthal.akgbensheim.MainNavigation.NavigationItem;

/**
 * Created by tobiaserthal on 17.09.15.
 */
public interface HomeCallbacks {
    void onItemClicked(@NavigationItem int type);
    void onSubItemClicked(@NavigationItem int type, long id);
}
