package de.tobiaserthal.akgbensheim.ui.drawer;

/**
 * Interface representing each item in the navigation drawer and holds its type
 * @author tobiaserthal
 */
public interface Item {

    /**
     * Get whether this item is a section or a regular item
     * @return a boolean whether this item is a section
     */
    boolean isSection();

    /**
     * Get whether this item is checkable or not.
     * @return a boolean whether this item is checkable.
     */
    boolean isCheckable();

    /**
     * Get the title of the item
     * @return the title of the item as a string
     */
    String getTitle();
}
