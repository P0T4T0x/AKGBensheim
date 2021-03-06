package de.tobiaserthal.akgbensheim.drawer.model;

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
    public int getId() {
        return -1;
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
