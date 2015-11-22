package de.tobiaserthal.akgbensheim.ui.tabs;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.FloatEvaluator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.data.Log;
import de.tobiaserthal.akgbensheim.tools.PageChangeAdapter;
import de.tobiaserthal.akgbensheim.ui.base.ToolbarFragment;

public class TabbedHostFragment extends ToolbarFragment {
    public static final String TAG = "TabbedHostFragment";

    public static final int INTERNAL_HEADER_ID = android.R.id.tabhost;
    public static final int INTERNAL_LIST_ID = android.R.id.tabcontent;

    private static final int FLOW_TAB_BOUNDARY = 3;

    private int tabHeight;
    private boolean pagerShown;
    private CacheFragmentStatePagerAdapter adapter;
    private FragmentManager retainedFragmentManager;
    private ViewPager pager;
    private TabLayout tabLayout;

    private String clazz;
    private final ArrayList<Bundle> pageArgs = new ArrayList<>();
    private final ArrayList<String> pageTitles = new ArrayList<>();

    private final Handler handler = new Handler();
    private final Runnable requestFocus = new Runnable() {
        @Override
        public void run() {
            pager.focusableViewAvailable(pager);
        }
    };

    private final PageChangeAdapter pageChangeAdapter = new PageChangeAdapter() {
        FloatEvaluator evaluator = new FloatEvaluator();
        Interpolator interpolator = new LinearInterpolator();

        int lastIndex = 0;
        int lastState = ViewPager.SCROLL_STATE_IDLE;
        float lastTranslation = 0;

        @Override
        public void onPageTransition(int page, float offset) {
            boolean right = page + offset > lastIndex;

            //fix for flicker when dragging over multiple pages
            if(right) {
                if(lastIndex != page) {
                    lastIndex = page;
                    lastTranslation = getToolbarTranslation();
                }
            } else {
                if(lastIndex - 1 != page) {
                    lastIndex = page + 1;
                    lastTranslation = getToolbarTranslation();
                }
            }

            int from = page + (right ? 0 : 1);
            int to = page + (right ? 1 : 0);

            float page1 = right ? lastTranslation :
                    isToolbarPreferred(to) ? 0 : -getToolbar().getHeight();

            float page2 = !right ? lastTranslation :
                    isToolbarPreferred(to) ? 0 : -getToolbar().getHeight();

            if (page1 != page2) {
                float fraction = interpolator.getInterpolation(offset);
                float translationY = evaluator.evaluate(fraction, page1, page2);
                setToolbarTranslation(translationY);
            }

            TabbedHostFragment.this.onPageTransition(from, to, offset);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            switch (state) {
                case ViewPager.SCROLL_STATE_IDLE:
                    lastIndex = pager.getCurrentItem();
                    lastTranslation = getToolbarTranslation();

                    if (isToolbarPreferred(getSelectedIndex())) {
                        if (!toolbarIsShown()) {
                            showToolbar(true);
                        }
                    } else {
                        if (!toolbarIsHidden()) {
                            hideToolbar(true);
                        }
                    }

                    break;

                case ViewPager.SCROLL_STATE_DRAGGING:
                    if(lastState == ViewPager.SCROLL_STATE_IDLE) {
                        lastTranslation = getToolbarTranslation();
                        interpolator = new AccelerateDecelerateInterpolator();
                    }

                    break;

                case ViewPager.SCROLL_STATE_SETTLING:
                    if(lastState == ViewPager.SCROLL_STATE_IDLE) {
                        lastTranslation = getToolbarTranslation();
                        interpolator = new LinearInterpolator();
                    }

                    break;
            }

            lastState = state;
            TabbedHostFragment.this.onPageScrollStateChanged(state);
        }

        @Override
        public void onPageSelected(int position) {
            TabbedHostFragment.this.onPageSelected(position);

            Log.d(TAG, "Page at index: %d selected.", position);
            for(int i = 0; i < pager.getAdapter().getCount(); i++) {
                Log.d(TAG, "Fragment at index: %d isToolbarPreferred: %b", i, isToolbarPreferred(i));
            }
        }
    };

    /**
     * Default constructor
     */
    public TabbedHostFragment() {
        super();
    }

    public String getPageTitle(int index) {
        return pageTitles.get(index);
    }

    public Bundle getPageOptions(int index) {
        return pageArgs.get(index);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (retainedFragmentManager != null) {
            //restore the last retained child fragment manager to the new
            //created fragment
            try {
                Field childFMField = Fragment.class.getDeclaredField("mChildFragmentManager");
                childFMField.setAccessible(true);
                childFMField.set(this, retainedFragmentManager);
            } catch (NoSuchFieldException e) {
                Log.e(TAG, e, "Could not find field to access fragment manager!");
            } catch (IllegalAccessException e) {
                Log.e(TAG, e, "Could not access fragment manager field!");
            }
        } else {
            retainedFragmentManager = getChildFragmentManager();
        }
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        clazz = getArguments().getString("clazz");
        if(clazz == null) {
            throw new IllegalStateException("No class parameter passed!" +
                    "Use Builder class to create a new instance!");
        }

        ArrayList<String> titles = getArguments().getStringArrayList("pageTitles");
        ArrayList<Bundle> args = getArguments().getParcelableArrayList("pageArgs");

        pageTitles.addAll(titles);
        pageArgs.addAll(args);

        tabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
        if(pageTitles.isEmpty() || pageArgs.isEmpty()) {
            throw new IllegalStateException("No page titles or arguments passed!");
        }

        FragmentManager manager = getChildFragmentManager();
        setAdapter(new CacheFragmentStatePagerAdapter(manager) {
            @Override
            protected Fragment createItem(int position) {
                return Fragment.instantiate(
                        getActivity(), clazz, pageArgs.get(position));
            }

            @Override
            public int getCount() {
                return pageTitles.size();
            }

            @Override
            public String getPageTitle(int position) {
                String title = pageTitles.get(position);
                if (TextUtils.isEmpty(title)) {
                    return "Page " + (position + 1);
                }

                return title;
            }
        });
    }

    @Override
    public void onDestroy() {
        pageTitles.clear();
        pageArgs.clear();

        setAdapter(null);
        super.onDestroy();
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container,
                                    Bundle savedInstanceState) {

        final Context context = container.getContext();
        FrameLayout root = new FrameLayout(context);

        ViewPager pager = new ViewPager(container.getContext());
        pager.setId(INTERNAL_LIST_ID);
        pager.setOffscreenPageLimit(getAdapter().getCount());

        root.addView(pager, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        int padding = getResources().getDimensionPixelSize(R.dimen.tab_height);
        ViewCompat.setPaddingRelative(root, 0, padding, 0, 0);

        root.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        return root;
    }

    @Override
    public View onCreateHeaderView(LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState) {

        TabLayout tabLayout = new TabLayout(container.getContext());
        tabLayout.setId(INTERNAL_HEADER_ID);

        boolean shouldFlow = getAdapter().getCount() > FLOW_TAB_BOUNDARY
                        || getResources().getBoolean(R.bool.flow_tabs);

        tabLayout.setTabGravity(!shouldFlow ?
                TabLayout.GRAVITY_FILL :
                TabLayout.GRAVITY_CENTER);

        tabLayout.setTabMode(!shouldFlow ?
                        TabLayout.MODE_FIXED :
                        TabLayout.MODE_SCROLLABLE);

        tabLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary));
        tabLayout.setTabTextColors(
                ContextCompat.getColor(getActivity(), R.color.secondaryTextInverse),
                ContextCompat.getColor(getActivity(), R.color.primaryTextInverse)
        );

        int padding = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        ViewCompat.setPaddingRelative(tabLayout, padding, 0, padding, 0);

        tabLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, tabHeight));

        return tabLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ensurePager();
        ensureTabs();
    }

    @Override
    public void onDestroyView() {
        // Remove handlers and listeners
        handler.removeCallbacks(requestFocus);
        pager.removeOnPageChangeListener(pageChangeAdapter);

        // Resetting pointers
        pager = null;
        pagerShown = false;
        tabLayout = null;

        super.onDestroyView();
    }

    public void onPageSelected(int position) {
        // empty body
    }

    public void onPageTransition(int fromPage, int toPage, float offset){
        // empty body
    }

    public void onPageScrollStateChanged(int state) {
        // empty body
    }

    public int getSelectedIndex() {
        ensurePager();
        return pager.getCurrentItem();
    }

    private boolean isToolbarPreferred(int position) {
        Fragment fragment = getFragmentAt(position);
        return !(fragment instanceof TabbedFragment)
                || ((TabbedFragment) fragment).isToolbarPreferred();
    }

    public Fragment getCurrentFragment() {
        return getFragmentAt(getSelectedIndex());
    }

    public Fragment getFragmentAt(int position) {
        if(adapter != null)
            return adapter.getItemAt(position);
        else
            return null;
    }

    public ViewPager getViewPager() {
        ensurePager();
        return pager;
    }

    public TabLayout getTabLayout() {
        ensureTabs();
        return tabLayout;
    }

    public int getTabHeight() {
        if(tabLayout == null) {
            return tabHeight;
        }

        return tabLayout.getHeight();
    }

    private void ensurePager() {
        if(pager != null)
            return;

        View root = getContentView();
        if(root == null)
            throw new IllegalStateException("Content view not created!");

        if (root instanceof ViewPager) {
            pager = (ViewPager) root;
        } else {
            View raw = root.findViewById(INTERNAL_LIST_ID);
            if(!(raw instanceof ViewPager)) {
                if(raw == null)
                    throw new IllegalStateException("You must include a view with id android.R.id.list");
                else
                    throw new IllegalStateException("View provided with id android.R.id.list is not ViewPager");
            }

            pager = (ViewPager) raw;
        }

        pagerShown = true;
        pager.addOnPageChangeListener(pageChangeAdapter);
        if(adapter != null) {
            CacheFragmentStatePagerAdapter adapter = this.adapter;
            this.adapter = null;
            setAdapter(adapter);
        } else {
            setPagerShown(false, false);
        }

        handler.post(requestFocus);
    }

    private void ensureTabs() {
        if(tabLayout != null)
            return;

        View root = getHeaderContent();
        if(root == null)
            throw new IllegalStateException("Header view not created!");

        if(root instanceof TabLayout) {
            tabLayout = (TabLayout) root;
        } else {
            View raw = root.findViewById(INTERNAL_HEADER_ID);
            if(!(raw instanceof TabLayout)) {
                if(raw == null)
                    throw new IllegalStateException("You must provide a tab layout to header view with id android.R.id.tabs");
                else
                    throw new IllegalStateException("View provided as tab layout is not a sliding tab layout");
            }

            tabLayout = (TabLayout) raw;
        }

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(getViewPager());
            }
        });
    }

    public void showPager(boolean animated) {
        setPagerShown(true, animated);
    }

    public void hidePager(boolean animated) {
        setPagerShown(false, animated);
    }

    private void setPagerShown(boolean shown, boolean animate) {
        ensurePager();

        if(getContentView() == null)
            throw new IllegalStateException("No container instance!");

        if(pagerShown == shown)
            return;

        pagerShown = shown;
        if(shown) {
            if(animate)
                getContentView().startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_in));
            else
                getContentView().clearAnimation();

            getContentView().setVisibility(View.VISIBLE);
        } else {
            if(animate)
                getContentView().startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_out));
            else
                getContentView().clearAnimation();

            getContentView().setVisibility(View.GONE);
        }
    }

    public void setAdapter(CacheFragmentStatePagerAdapter adapter) {
        boolean hadAdapter = this.adapter != null;
        this.adapter = adapter;

        if(pager != null) {
            pager.setAdapter(adapter);
            if(!pagerShown && !hadAdapter) {
                if(getView() != null)
                    setPagerShown(true, getView().getWindowToken() != null);
            }
        }
    }

    public CacheFragmentStatePagerAdapter getAdapter() {
        return this.adapter;
    }

    public static class Builder {
        private String clazz;
        private ArrayList<Bundle> pageArgs;
        private ArrayList<String> pageTitles;

        protected Builder(Class<? extends Fragment> clazz) {
            this.clazz = clazz.getName();
            this.pageArgs = new ArrayList<>();
            this.pageTitles = new ArrayList<>();
        }

        public static Builder withClass(Class<? extends Fragment> clazz) {
            return new Builder(clazz);
        }

        public Builder addPage(String title, Bundle args) {
            this.pageTitles.add(title);
            this.pageArgs.add(args);

            return this;
        }

        public Builder addPage(String title) {
            return addPage(title, null);
        }

        public Builder addPage(Bundle args) {
            return addPage("Page " + (pageTitles.size() + 1), args);
        }

        protected <T extends TabbedHostFragment> T build(Class<T> clazz) {
            Bundle args = new Bundle();
            args.putString("clazz", this.clazz);
            args.putStringArrayList("pageTitles", this.pageTitles);
            args.putParcelableArrayList("pageArgs", this.pageArgs);

            T fragment;
            try {
                fragment = clazz.newInstance();
                fragment.setArguments(args);
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
                fragment = null;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                fragment = null;
            }

            return fragment;
        }

        public TabbedHostFragment build() {
            return build(TabbedHostFragment.class);
        }
    }
}
