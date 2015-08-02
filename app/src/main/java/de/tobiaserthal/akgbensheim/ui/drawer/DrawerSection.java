package de.tobiaserthal.akgbensheim.ui.drawer;

/**
 * Data Model representing each item in the navigation drawer and holds its data
 * @author tobiaserthal
 */
public class DrawerSection implements Item {
    private String title;

    /**
     * Creates a new section item
     * @param title The title of the section. May be {@code null}
     */
    public DrawerSection(String title) {
        this.title = title;
    }

    @Override
    public boolean isSection() {
        return true;
    }

    /**
     * Set the title of the section.
     * @param title The title of the section. May be {@code null}
     */
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public boolean isCheckable() {
        return false;
    }
}
