package de.tobiaserthal.akgbensheim.ui.tabs;

public interface TabbedFragment {
    TabbedHostFragment getParent();

    boolean isToolbarPreferred();
}
