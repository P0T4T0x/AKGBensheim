package de.tobiaserthal.akgbensheim.drawer;

/**
 * An interface to respond to click events in the navigation drawer.
 */
public interface DrawerCallbacks {
    /**
     * Called when a navigation item (not the header and not a section) registered a click event.
     * @param position The absolute adapter position of the item.
     * @param id The item id set while adding to adapter
     * @param reselect Whether this item is reselected
     */
    void onNavigationItemSelected(int position, int id, boolean reselect);
}
