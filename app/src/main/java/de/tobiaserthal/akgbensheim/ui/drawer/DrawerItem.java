package de.tobiaserthal.akgbensheim.ui.drawer;

/**
 * Data Model representing each item in the navigation drawer and holds its data
 * @author tobiaserthal
 */
public class DrawerItem implements Item {

    private String title;
    private String count;
    private int icon;

    private boolean counterVisible;
    private boolean checkable;

    /**
     * Creates a new model for a nav drawer item
     */
    public DrawerItem() {
        this.title = "";
        this.icon = -1;
        this.count = "0";
        this.counterVisible = false;
        this.checkable = true;
    }

    /**
     * Creates a new model for a nav drawer item
     * @param title the title of the item
     * @param icon the icon to be displayed
     */
    public DrawerItem(String title, int icon) {
        this.title = title;
        this.icon = icon;
        this.count = "0";
        this.counterVisible = false;
        this.checkable = true;
    }

    /**
     * Creates a new model for a nav drawer item
     * @param title the title of the item
     * @param icon the icon to be displayed
     * @param counterVisible the visibility of the counter item
     * @param count the count to be displayed on the counter item (String)
     */
    public DrawerItem(String title, int icon, boolean counterVisible, String count) {
        this.title = title;
        this.icon = icon;
        this.counterVisible = counterVisible;
        this.checkable = true;
        this.count = count;
    }


    /**
     * Creates a new model for a nav drawer item
     * @param title the title of the item
     * @param icon the icon to be displayed
     * @param counterVisible the visibility of the counter item
     * @param count the count to be displayed on the counter item (String)
     * @param checkable Whether this item should be treated as a normal func and therefore
     *                      should be a checkable item of the drawer view.
     */
    public DrawerItem(String title, int icon, boolean counterVisible, String count, boolean checkable) {
        this.title = title;
        this.icon = icon;
        this.counterVisible = counterVisible;
        this.checkable = checkable;
        this.count = count;
    }

    @Override
    public boolean isSection() {
        return false;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    /**
     * Set the title of the item
     * @param title the title to set as a string
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the icon displayed on the item
     * @return The icon as an integer
     */
    public int getIcon() {
        return this.icon;
    }

    /**
     * Set the icon displayed on the item
     * @param icon The icon to display as an integer
     */
    public void setIcon(int icon) {
        this.icon = icon;
    }

    /**
     * Get the count displayed in the counter item
     * @return the count as a string
     */
    public String getCount() {
        return this.count;
    }

    /**
     * Set the count displayed in the counter item
     * @param count to display as a string
     */
    public void setCount(String count) {
        this.count = count;
    }

    /**
     * Get the counter item visibility
     * @return the item visibility as a boolean
     */
    public boolean getCounterVisibility() {
        return this.counterVisible;
    }

    /**
     * Set the counter item visibility
     * @param isCounterVisible the item visibility to set as a boolean
     */
    public void setCounterVisibility(boolean isCounterVisible) {
        this.counterVisible = isCounterVisible;
    }

    /**
     * Whether this item should be treated as a special func and therefore
     * should not be a checkable item of the drawer view.
     */
    @Override
    public boolean isCheckable() {
        return this.checkable;
    }

    /**
     * Whether this item should be treated as a normal func and therefore
     * should be a checkable item of the drawer view.
     */
    public void setCheckable(boolean checkable) {
        this.checkable = checkable;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " ["
                + "title="          + this.title            + ", "
                + "icon="           + this.icon             + ", "
                + "count="          + this.count            + ", "
                + "counterVisible=" + this.counterVisible   + ", "
                + "checkable="      + this.checkable        + "]";
    }
}
