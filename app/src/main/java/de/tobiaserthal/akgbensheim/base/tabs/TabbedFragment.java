package de.tobiaserthal.akgbensheim.base.tabs;

public interface TabbedFragment {
    TabbedHostFragment getParent();

    boolean isToolbarPreferred();
}
