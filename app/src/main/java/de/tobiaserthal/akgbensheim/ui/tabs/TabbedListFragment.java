package de.tobiaserthal.akgbensheim.ui.tabs;

import android.support.v4.app.LoaderManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;

import com.github.ksoichiro.android.observablescrollview.ScrollState;

import de.tobiaserthal.akgbensheim.data.Log;
import de.tobiaserthal.akgbensheim.ui.RecyclerFragment;

// FIXME: recalculate scroll position on adapter size changes
public abstract class TabbedListFragment<A extends RecyclerView.Adapter>
        extends RecyclerFragment<A> implements TabbedFragment {

    private ScrollState lastState = ScrollState.STOP;

    @Override
    public LoaderManager getLoaderManager() {
        return getParentFragment().getLoaderManager();
    }

    @Override
    public void onScrolled(int dX, int dY) {
        getParent().moveToolbar(-dY);

        if(dY > 0) {
            lastState = ScrollState.UP;
        } else if(dY < 0) {
            lastState = ScrollState.DOWN;
        } else {
            lastState = ScrollState.STOP;
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            switch (lastState) {
                case DOWN:
                    getParent().showToolbar();
                    break;
                case UP:
                    if(getParent().getToolbarTranslation()
                            > -getParent().getToolbar().getHeight() / 2)
                        getParent().showToolbar();
                    else
                        getParent().hideToolbar();
                    break;
            }
        }
    }

    @Override
    public boolean isToolbarPreferred() {
        boolean canScrollUp = ViewCompat.canScrollVertically(getRecyclerView(), -1);
        return !canScrollUp || getScrollY() < 0;
    }

    @Override
    public TabbedHostFragment getParent() {
        try {
            if(getParentFragment() == null) {
                throw new IllegalStateException("Fragment cannot be attached to an activity!");
            }

            return (TabbedHostFragment) getParentFragment();
        } catch (ClassCastException e) {
            throw new IllegalStateException("Parent fragment must implement TabbedHostFragment!");
        }
    }
}
