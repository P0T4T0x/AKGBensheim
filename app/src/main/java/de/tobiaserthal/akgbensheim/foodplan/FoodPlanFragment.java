package de.tobiaserthal.akgbensheim.foodplan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.epapyrus.plugpdf.core.viewer.BasePlugPDFDisplay;
import com.epapyrus.plugpdf.core.viewer.DocumentState;
import com.epapyrus.plugpdf.core.viewer.ReaderListener;
import com.epapyrus.plugpdf.core.viewer.ReaderView;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Locale;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.data.NetworkManager;
import de.tobiaserthal.akgbensheim.data.model.ModelUtils;
import de.tobiaserthal.akgbensheim.data.provider.FoodPlanLoader;
import de.tobiaserthal.akgbensheim.data.rest.model.foodplan.FoodPlanKeys;
import de.tobiaserthal.akgbensheim.data.sync.FoodPlanService;
import de.tobiaserthal.akgbensheim.ui.base.ToolbarFragment;

/**
 * A simple {@link Fragment} subclass.
 */

//FIXME: long loading times -> ui thread blocked and crashes after reload and/or config change
public class FoodPlanFragment extends ToolbarFragment
        implements LoaderManager.LoaderCallbacks<byte[]>, TabLayout.OnTabSelectedListener, ReaderListener {

    private boolean syncing;
    private int backgroundColor;

    private static final int HIDE_DURATION = 2000;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            displayStatus(false);

            int code = intent.getIntExtra("code", FoodPlanService.CODE_FAILURE);
            switch (code) {
                case FoodPlanService.CODE_SUCCESS:
                    getLoaderManager().getLoader(0).onContentChanged();
                    break;

                case FoodPlanService.CODE_FAILURE:
                    getLoaderManager().getLoader(0).reset();
                    Snackbar.make(getContentView(), R.string.notify_foodplan_failed, Snackbar.LENGTH_LONG)
                            .setActionTextColor(ContextCompat.getColor(getActivity(), R.color.md_edittext_error))
                            .setAction(R.string.retry, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    refresh();
                                }
                            }).show();
                    break;
            }
        }
    };

    private final Runnable hideBars = new Runnable() {
        @Override
        public void run() {
            hideToolbar();
        }
    };
    private final Runnable showBars = new Runnable() {
        @Override
        public void run() {
            showToolbar();
        }
    };

    public static FoodPlanFragment newInstance() {
        return new FoodPlanFragment();
    }

    public FoodPlanFragment() {
        super();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, FoodPlanFragment.this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        backgroundColor = ContextCompat.getColor(getActivity(), R.color.background_material_light);
    }

    @Override
    public View onCreateHeaderView(LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState) {

        TabLayout tabLayout = new TabLayout(container.getContext());
        tabLayout.setId(android.R.id.tabhost);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        tabLayout.setBackgroundColor(
                ContextCompat.getColor(getActivity(), R.color.primary)
        );
        tabLayout.setTabTextColors(
                ContextCompat.getColor(getActivity(), R.color.secondaryTextInverse),
                ContextCompat.getColor(getActivity(), R.color.primaryTextInverse)
        );

        int padding = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        tabLayout.setPadding(padding, 0, padding, 0);

        tabLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.tab_height)
        ));

        tabLayout.setOnTabSelectedListener(this);

        return tabLayout;
    }

    private TabLayout getTabLayout() {
        return (TabLayout) getHeaderContent();
    }

    private ReaderView getReaderView() {
        return (ReaderView) getContentView();
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container,
                                    Bundle savedInstanceState) {

        ReaderView readerView = new ReaderView(container.getContext());
        readerView.setId(android.R.id.tabcontent);

        readerView.getPlugPDFDisplay().setBackgroundColor(backgroundColor);
        readerView.setReaderListener(this);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        int topMargin = getResources().getDimensionPixelSize(R.dimen.tab_height);
        params.setMargins(0, topMargin, 0, 0);
        readerView.setLayoutParams(params);

        return readerView;
    }

    @Override
    public void onViewCreated(View view, Bundle  savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("currentPosition", getReaderView().getPageIdx());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem refresh = menu.findItem(R.id.action_refresh);
        if(syncing) {
            MenuItemCompat.setActionView(refresh, R.layout.toolbar_progress);
        } else {
            MenuItemCompat.setActionView(refresh, null);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh();
                return true;

            case R.id.action_share:
                ModelUtils.startFoodPlanIntent(getActivity());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(FoodPlanService.ACTION_REPORT);
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();

        getReaderView().removeCallbacks(hideBars);
        getReaderView().removeCallbacks(showBars);
        getActivity().unregisterReceiver(receiver);
    }

    private void refresh() {
        boolean accessEnabled = NetworkManager.getInstance(getContext()).isAccessAllowed();
        if(accessEnabled) {
            displayStatus(true);

            Intent intent = new Intent(getActivity(), FoodPlanService.class);
            Calendar calendar = Calendar.getInstance(Locale.getDefault());

            intent.putExtra("first", calendar.get(Calendar.WEEK_OF_YEAR));
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            intent.putExtra("second", calendar.get(Calendar.WEEK_OF_YEAR));

            getActivity().startService(intent);
        } else {
            Snackbar.make(getContentView(), R.string.notify_network_unavailable, Snackbar.LENGTH_LONG)
                    .setActionTextColor(ContextCompat.getColor(getActivity(), R.color.md_edittext_error))
                    .setAction(R.string.retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            refresh();
                        }
                    }).show();
        }
    }

    private void displayStatus(boolean syncing) {
        if(this.syncing != syncing) {
            this.syncing = syncing;
            getActivity().supportInvalidateOptionsMenu();
        }
    }

    @Override
    public Loader<byte[]> onCreateLoader(int id, Bundle args) {
        return new FoodPlanLoader(
                getActivity(),
                FoodPlanKeys.getDefaultCachePath(getActivity())
        );
    }

    @Override
    public void onLoadFinished(Loader<byte[]> loader, byte[] data) {
        if(data != null) {
            getReaderView().openData(data, data.length, "");
            getReaderView().getPlugPDFDisplay().setBackgroundColor(backgroundColor);

            getTabLayout().removeAllTabs();
            for(int i = 0; i < getReaderView().getPageCount(); i++) {
                String title = MessageFormat.format(
                        getString(R.string.foodplan_tab_titles), i);

                TabLayout.Tab tab = getTabLayout().newTab().setText(title);
                getTabLayout().addTab(tab);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<byte[]> loader) {
        //if(getView() != null)
            //getReaderView().clear();
    }

    @Override
    public void onDestroyView() {
        getTabLayout().setOnTabSelectedListener(null);

        if (getReaderView() != null
                && getReaderView().getDocument() != null) {
            getReaderView().clear();
        }

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        getLoaderManager().destroyLoader(0);
        super.onDestroy();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if(getReaderView() != null)
            getReaderView().goToPage(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {}

    @Override
    public void onLoadFinish(DocumentState.OPEN open) {
        getReaderView().removeCallbacks(hideBars);
        getReaderView().postDelayed(hideBars, HIDE_DURATION);
    }

    @Override
    public void onSearchFinish(boolean b) {}

    @Override
    public void onGoToPage(int index, int count) {
        getTabLayout().getTabAt(index - 1).select();
    }

    @Override
    public void onSingleTapUp(MotionEvent motionEvent) {
        if(toolbarIsShown()) {
            getReaderView().removeCallbacks(hideBars);
            getReaderView().post(hideBars);
        } else {
            getReaderView().post(showBars);
            getReaderView().postDelayed(hideBars, HIDE_DURATION);
        }
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {}

    @Override
    public void onDoubleTapUp(MotionEvent motionEvent) {}

    @Override
    public void onScroll(int dX, int dY) {
        getReaderView().removeCallbacks(hideBars);
        getReaderView().post(hideBars);
    }

    @Override
    public void onChangeDisplayMode(BasePlugPDFDisplay.PageDisplayMode pageDisplayMode) {

    }

    @Override
    public void onChangeZoom(double v) {

    }
}