package de.tobiaserthal.akgbensheim.base.toolbar;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.github.ksoichiro.android.observablescrollview.ScrollUtils;

import java.lang.ref.WeakReference;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.base.RecyclerFragment;
import de.tobiaserthal.akgbensheim.utils.ContextHelper;

public abstract class ToolbarListFragment<A extends RecyclerView.Adapter> extends RecyclerFragment<A> {
    private static final int INTERNAL_CONTENT_VIEW_ID = 0x000000;
    private static final int INTERNAL_HEADER_VIEW_ID = 0x000001;

    /* private pointer */
    private WeakReference<Toolbar> toolbar;
    private View headerView;
    private View contentView;
    private int toolbarAnimMillis = 200;

    public ToolbarActivity getParent() {
        try {
            return (ToolbarActivity) getActivity();
        } catch (ClassCastException e) {
            throw new IllegalStateException("Parent activity must extend ToolbarActivity!");
        }
    }

    @SuppressWarnings("ResourceType")
    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState) {

        final Context context = getActivity();
        FrameLayout root = new FrameLayout(context);

        /* Add content view */
        View contentFrame = onCreateContentView(inflater, root, savedInstanceState);
        if(contentFrame != null) {
            contentFrame.setId(INTERNAL_CONTENT_VIEW_ID);
            root.addView(contentFrame);
        }

        /* Create header layout */
        LinearLayout headerFrame = new LinearLayout(context);
        headerFrame.setId(INTERNAL_HEADER_VIEW_ID);
        headerFrame.setOrientation(LinearLayout.VERTICAL);

        View toolBarPadding = new View(context);
        int toolBarPaddingSize = ContextHelper.getPixelSize(context, R.attr.actionBarSize);
        toolBarPadding.setMinimumHeight(toolBarPaddingSize);
        headerFrame.addView(toolBarPadding, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, toolBarPaddingSize
        ));

        /* Add header content */
        View headerContent = onCreateHeaderView(inflater, headerFrame, savedInstanceState);
        if(headerContent != null)
            headerFrame.addView(headerContent);

        /* Add toolbar shadow */
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            View toolBarShadow = new View(context);
            int toolBarShadowSize = getResources().getDimensionPixelSize(R.dimen.toolbar_shadow_size);
            toolBarShadow.setMinimumHeight(toolBarShadowSize);
            toolBarShadow.setBackgroundResource(R.drawable.toolbar_shadow);

            headerFrame.setBackgroundColor(Color.TRANSPARENT);
            headerFrame.addView(toolBarShadow, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, toolBarShadowSize
            ));
        } else {
            headerFrame.setBackgroundColor(ContextHelper.getColor(context, R.attr.colorPrimary));
            ViewCompat.setElevation(headerFrame, getResources().getDimension(R.dimen.toolbar_default_elevation));
        }

        /* Add header view*/
        root.addView(headerFrame, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        /* Add root view */
        root.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return root;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ensureToolbar();
        ensureHeader();
        ensureContent();
    }

    @Override
    public void onDestroyView() {
        toolbar.clear();
        headerView = null;
        contentView = null;

        super.onDestroyView();
    }

    public void setToolbarAnimMillis(int millis) {
        this.toolbarAnimMillis = millis;
    }

    public int getToolbarAnimMillis() {
        return toolbarAnimMillis;
    }

    public abstract View onCreateContentView(LayoutInflater inflater, ViewGroup container,
                                             Bundle savedInstanceState);

    public abstract View onCreateHeaderView(LayoutInflater inflater, ViewGroup container,
                                            Bundle savedInstanceState);

    /**
     * Returns the header view. Don't get confused with {@link #getHeaderContent()}
     * @return The header view containing the toolbar wrapper, the content view and if pre-lollipop
     *      a shadow layer with 5dp height at
     */
    @SuppressWarnings("ResourceType")
    public View getHeaderView() {
        ensureHeader();
        return headerView;
    }

    /**
     * Returns the content of the header view you created in {@link #onCreateHeaderView(LayoutInflater, ViewGroup, Bundle)}
     * @return The header content view
     */
    public View getHeaderContent() {
        LinearLayout headerView = (LinearLayout) getHeaderView();

        if(headerView == null)
            return null;

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (headerView.getChildCount() == 3)
                return headerView.getChildAt(1);
            else
                return null;
        } else {
            if (headerView.getChildCount() == 2)
                return headerView.getChildAt(1);
            else
                return null;
        }
    }

    /**
     * Returns the content view you created in {@link #onCreateContentView(LayoutInflater, ViewGroup, Bundle)}
     * @return The content view
     */
    public View getContentView() {
        ensureContent();
        return contentView;
    }

    /**
     * Get the toolbar of the parent Activity
     * @return The toolbar view of the parent activity
     */
    public final Toolbar getToolbar() {
        ensureToolbar();
        return toolbar.get();
    }

    private void ensureToolbar() {
        if(toolbar != null
                && toolbar.get() != null)
            return;

        if(getActivity() != null)
            toolbar = new WeakReference<>(getParent().getToolbar());
    }

    @SuppressWarnings("ResourceType")
    private void ensureHeader() {
        if(headerView != null)
            return;

        if (getView() != null)
            headerView = getView().findViewById(INTERNAL_HEADER_VIEW_ID);
        else
            throw new IllegalStateException("getHeaderView called without a header being created!");
    }

    private void ensureContent() {
        if(contentView != null)
            return;

        if(getView() != null)
            contentView = getView().findViewById(INTERNAL_CONTENT_VIEW_ID);
        else
            throw new IllegalStateException("getHeaderView called without a header being created!");
    }

    public float getToolbarTranslation() {
        return getToolbar().getTranslationY();
    }

    /**
     * Move the toolbar and the header view by the specified amount of pixels
     * @param byTranslation the pixels to move the toolbar up
     */
    public void moveToolbar(float byTranslation) {
        float oldTrans = getToolbarTranslation();
        setToolbarTranslation(oldTrans + byTranslation);
    }

    /**
     * Set the toolbar and header view translation
     * @param translation The view translation in pixels
     */
    public void setToolbarTranslation(float translation) {
        float transY = ScrollUtils.getFloat(translation, -getToolbar().getHeight(), 0);

        getToolbar().animate().cancel();
        getToolbar().setTranslationY(transY);

        getHeaderView().animate().cancel();
        getHeaderView().setTranslationY(transY);
    }

    /**
     * Move the toolbar and header back into the visible view area
     * @param animated whether this operation should be animated or not
     */
    public void showToolbar(boolean animated) {
        if(!animated) {
            float headerTranslationY = getToolbarTranslation();
            if(headerTranslationY != 0) {
                getToolbar().animate().cancel();
                getToolbar().setTranslationY(0);

                getHeaderView().animate().cancel();
                getHeaderView().setTranslationY(0);
            }
            return;
        }

        showToolbar();
    }

    /**
     * Move the toolbar and header out of the visible view area
     * @param animated whether this operation should be animated or not
     */
    public void hideToolbar(boolean animated) {
        if(!animated) {
            float headerTranslationY = getToolbarTranslation();
            int toolbarHeight = getToolbar().getHeight();
            if (headerTranslationY != -toolbarHeight) {
                getToolbar().animate().cancel();
                getToolbar().setTranslationY(-toolbarHeight);

                getHeaderView().animate().cancel();
                getHeaderView().setTranslationY(-toolbarHeight);
            }
            return;
        }

        hideToolbar();
    }

    /**
     * Animate the toolbar and header back into the visible view area
     */
    public void showToolbar() {
        float headerTranslationY = getToolbarTranslation();
        if(headerTranslationY != 0) {
            getToolbar().animate().cancel();
            getToolbar().animate().translationY(0).setDuration(toolbarAnimMillis).start();

            getHeaderView().animate().cancel();
            getHeaderView().animate().translationY(0).setDuration(toolbarAnimMillis).start();
        }
    }

    /**
     * Animate the toolbar and header out of the visible view area
     */
    public void hideToolbar() {
        float headerTranslationY = getToolbarTranslation();
        int toolbarHeight = getToolbar().getHeight();
        if(headerTranslationY != -toolbarHeight) {
            getToolbar().animate().cancel();
            getToolbar().animate().translationY(-toolbarHeight).setDuration(toolbarAnimMillis).start();

            getHeaderView().animate().cancel();
            getHeaderView().animate().translationY(-toolbarHeight).setDuration(toolbarAnimMillis).start();
        }
    }

    public void toggleToolbar() {
        toggleToolbar(true);
    }

    public void toggleToolbar(boolean animated) {
        if(toolbarIsShown()) {
            hideToolbar(animated);
        } else {
            showToolbar(animated);
        }
    }

    /**
     * Get whether the toolbar is fully visible or not
     * @return the visibility as a boolean
     */
    public boolean toolbarIsShown() {
        return getToolbarTranslation() >= 0;
    }

    /**
     * Get whether the toolbar is completely invisible or not
     * @return the visibility as a boolean
     */
    public boolean toolbarIsHidden() {
        return getToolbarTranslation() <= -getToolbar().getHeight();
    }
}
