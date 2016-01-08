package de.tobiaserthal.akgbensheim.base.toolbar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.github.ksoichiro.android.observablescrollview.ScrollUtils;

import java.lang.ref.WeakReference;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.backend.utils.Log;
import de.tobiaserthal.akgbensheim.utils.ContextHelper;

/**
 * A simple fragment wrapper that helps you to create fragments with the ability to manage the
 * parent activity's toolbar by adding a header view or animating it.
 */
// FIXME: header sometimes not animating in position / background color for header pre lollipop
public abstract class ToolbarFragment extends Fragment {
    private static final int INTERNAL_CONTENT_VIEW_ID = 0x000000;
    private static final int INTERNAL_HEADER_VIEW_ID = 0x000001;
    private static final String TAG = "TabbedFragment";

    /* private pointer */
    private ValueAnimator toolbarAnimator;
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

        /* Add toolbar shadow */
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            View toolBarShadow = new View(context);
            int toolBarShadowSize = getResources().getDimensionPixelSize(R.dimen.toolbar_shadow_size);
            toolBarShadow.setMinimumHeight(toolBarShadowSize);
            toolBarShadow.setBackgroundResource(R.drawable.toolbar_shadow);

            FrameLayout headerContentWrapper = new FrameLayout(getContext());
            headerContentWrapper.setBackgroundColor(ContextHelper.getColor(context, R.attr.colorPrimary));

            if(headerContent != null) {
                headerContentWrapper.addView(headerContent);
            }

            headerFrame.setBackgroundColor(Color.TRANSPARENT);
            headerFrame.addView(headerContentWrapper, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            headerFrame.addView(toolBarShadow, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, toolBarShadowSize));
        } else {
            if(headerContent != null) {
                headerFrame.addView(headerContent);
            }

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

        if(savedInstanceState == null) {
            showToolbar(false);
        }
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
                return ((ViewGroup) headerView.getChildAt(1)).getChildAt(0);
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
        if(headerView != null) {
            return;
        }

        if (getView() != null) {
            headerView = getView().findViewById(INTERNAL_HEADER_VIEW_ID);
        } else {
            throw new IllegalStateException("getHeaderView called without a header being created!");
        }
    }

    private void ensureContent() {
        if(contentView != null) {
            return;
        }

        if(getView() != null) {
            contentView = getView().findViewById(INTERNAL_CONTENT_VIEW_ID);
        } else {
            throw new IllegalStateException("getContentView called without a content being created!");
        }
    }

    public float getToolbarTranslation() {
        return getToolbar().getTranslationY();
    }

    public int getToolbarHeight() {
        return getToolbar().getHeight();
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
        onToolbarMoved(transY);

        prepareAnimator();
        getHeaderView().setTranslationY(transY);
        getToolbar().setTranslationY(transY);
    }

    /**
     * Move the toolbar and header back into the visible view area
     * @param animated whether this operation should be animated or not
     */
    public void showToolbar(boolean animated) {
        if(!animated) {
            float headerTranslationY = getToolbarTranslation();
            if(headerTranslationY != 0) {
                onToolbarMoved(0);

                prepareAnimator();
                getHeaderView().setTranslationY(0);
                getToolbar().setTranslationY(0);
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
                onToolbarMoved(-toolbarHeight);

                prepareAnimator();
                getToolbar().setTranslationY(-toolbarHeight);
                getToolbar().setTranslationY(-toolbarHeight);
            }
            return;
        }

        hideToolbar();
    }

    /**
     * Animate the toolbar and header back into the visible view area
     */
    public void showToolbar() {
        Log.d(TAG, "showToolbar()");

        float headerTranslationY = getToolbarTranslation();
        if(headerTranslationY != 0) {
            prepareAnimator();

            toolbarAnimator.setFloatValues(headerTranslationY, 0);
            toolbarAnimator.start();
        }
    }

    /**
     * Animate the toolbar and header out of the visible view area
     */
    public void hideToolbar() {
        Log.d(TAG, "hideToolbar()");

        float headerTranslationY = getToolbarTranslation();
        int toolbarHeight = getToolbar().getHeight();
        if(headerTranslationY != -toolbarHeight) {
            prepareAnimator();

            toolbarAnimator.setFloatValues(headerTranslationY, -toolbarHeight);
            toolbarAnimator.start();
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

    private void prepareAnimator() {
        if(toolbarAnimator == null) {
            toolbarAnimator = new ValueAnimator();
            toolbarAnimator.setDuration(toolbarAnimMillis);
            toolbarAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float translationY = (float) animation.getAnimatedValue();
                    getHeaderView().setTranslationY(translationY);
                    getToolbar().setTranslationY(translationY);

                    onToolbarMoved(translationY);
                }
            });
        } else {
            toolbarAnimator.cancel();
        }
    }

    public void onToolbarMoved(float translationY) {
        // empty
    }
}