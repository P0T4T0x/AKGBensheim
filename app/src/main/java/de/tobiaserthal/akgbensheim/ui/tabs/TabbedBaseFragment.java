package de.tobiaserthal.akgbensheim.ui.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class TabbedBaseFragment extends Fragment implements TabbedFragment {
    private TabbedHostFragment parent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            parent = (TabbedHostFragment) getParentFragment();
        } catch (ClassCastException e) {
            throw new IllegalStateException("Parent fragment must implement TabbedHostFragment");
        }
    }

    @Override
    public void onDestroy() {
        parent = null;
        super.onDestroy();
    }

    @Override
    public boolean isToolbarPreferred() {
        return true;
    }

    @Override
    public TabbedHostFragment getParent() {
        return parent;
    }
}
