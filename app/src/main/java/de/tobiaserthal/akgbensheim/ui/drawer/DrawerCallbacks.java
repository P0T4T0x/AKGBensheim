package de.tobiaserthal.akgbensheim.ui.drawer;

/**
 * An interface to respond to click events in the navigation drawer.
 */
public interface DrawerCallbacks {
    /**
     * Called when a navigation item (not the header and not a section) registered a click event.
     * @param index The index of the item in the array committed to the adapter.
     * @param position The absolute adapter position of the item.
     * @param reselect Whether this item is reselected
     */
    void onNavigationItemSelected(int index, int position, boolean reselect);

    /**
     * Called when the header item registered a click event.
     */
    void onHeaderItemSelected();
}
