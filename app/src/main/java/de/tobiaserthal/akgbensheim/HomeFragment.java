package de.tobiaserthal.akgbensheim;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SyncStatusObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

import de.tobiaserthal.akgbensheim.adapter.HomeAdapter;
import de.tobiaserthal.akgbensheim.data.Log;
import de.tobiaserthal.akgbensheim.data.NetworkManager;
import de.tobiaserthal.akgbensheim.data.preferences.PreferenceProvider;
import de.tobiaserthal.akgbensheim.data.provider.DataProvider;
import de.tobiaserthal.akgbensheim.data.provider.event.EventColumns;
import de.tobiaserthal.akgbensheim.data.provider.event.EventSelection;
import de.tobiaserthal.akgbensheim.data.provider.homework.HomeworkColumns;
import de.tobiaserthal.akgbensheim.data.provider.homework.HomeworkSelection;
import de.tobiaserthal.akgbensheim.data.provider.news.NewsColumns;
import de.tobiaserthal.akgbensheim.data.provider.news.NewsSelection;
import de.tobiaserthal.akgbensheim.data.provider.substitution.SubstitutionColumns;
import de.tobiaserthal.akgbensheim.data.provider.substitution.SubstitutionSelection;
import de.tobiaserthal.akgbensheim.data.sync.SyncAdapter;
import de.tobiaserthal.akgbensheim.data.sync.SyncUtils;
import de.tobiaserthal.akgbensheim.data.sync.auth.AuthenticatorService;
import de.tobiaserthal.akgbensheim.event.EventDetailActivity;
import de.tobiaserthal.akgbensheim.homework.HomeworkEditActivity;
import de.tobiaserthal.akgbensheim.news.NewsDetailActivity;
import de.tobiaserthal.akgbensheim.subst.SubstDetailActivity;
import de.tobiaserthal.akgbensheim.ui.base.ToolbarListFragment;

import static de.tobiaserthal.akgbensheim.MainNavigation.FRAGMENT_EVENT;
import static de.tobiaserthal.akgbensheim.MainNavigation.FRAGMENT_HOMEWORK;
import static de.tobiaserthal.akgbensheim.MainNavigation.FRAGMENT_NEWS;
import static de.tobiaserthal.akgbensheim.MainNavigation.FRAGMENT_SUBSTITUTION;
import static de.tobiaserthal.akgbensheim.MainNavigation.NavigationItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends ToolbarListFragment<HomeAdapter>
        implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<Cursor>, HomeCallbacks {

    public static final String TAG = "HomeFragment";

    private Object syncObserverHandle;
    private final SyncStatusObserver syncStatusObserver = new SyncStatusObserver() {
        @Override
        public void onStatusChanged(int which) {
            Account account = AuthenticatorService.getAccount(SyncUtils.ACCOUNT_TYPE);
            boolean syncActive = ContentResolver.isSyncActive(account, DataProvider.AUTHORITY);
            boolean syncPending = ContentResolver.isSyncPending(account, DataProvider.AUTHORITY);

            final boolean refresh = syncActive || syncPending;
            Log.d(TAG, "Status change detected. Active: %b, pending: %b, refreshing: %b", syncActive, syncPending, refresh);

            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(refresh);
                }
            });
        }
    };

    static class ChangeReceiver extends PreferenceProvider.PreferenceChangeReceiver {
        private WeakReference<HomeFragment> reference;

        public ChangeReceiver(HomeFragment fragment) {
            reference = new WeakReference<>(fragment);
        }

        @Override
        public void onColorPreferenceChange() {
            HomeFragment fragment = reference.get();
            if(fragment != null) {
                fragment.getAdapter().notifyDataSetChanged();
            }
        }

        @Override
        public void onSubstPreferenceChange() {
            HomeFragment fragment = reference.get();
            if(fragment != null) {
                fragment.getLoaderManager().restartLoader(FRAGMENT_SUBSTITUTION, Bundle.EMPTY, fragment);
            }
        }
    }

    private ChangeReceiver preferenceChangeReceiver;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MainNavigation mainNavigation;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mainNavigation = (MainNavigation) context;
        } catch (ClassCastException e) {
            throw new IllegalStateException("Parent context must implement MainNavigation callbacks!");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(false);
        HomeAdapter adapter = new HomeAdapter(getActivity());
        adapter.setCallbacks(this);

        setAdapter(adapter);

        // listen for preference changes
        preferenceChangeReceiver = new ChangeReceiver(this);
        IntentFilter filter = new IntentFilter(PreferenceProvider.ACTION_SUBST);
        LocalBroadcastManager
                .getInstance(getContext())
                .registerReceiver(preferenceChangeReceiver, filter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(FRAGMENT_SUBSTITUTION, Bundle.EMPTY, this);
        getLoaderManager().initLoader(FRAGMENT_HOMEWORK, Bundle.EMPTY, this);
        getLoaderManager().initLoader(FRAGMENT_EVENT, Bundle.EMPTY, this);
        getLoaderManager().initLoader(FRAGMENT_NEWS, Bundle.EMPTY, this);
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    @Override
    public View onCreateHeaderView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Create the layout manager
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(
                getResources().getInteger(R.integer.news_list_columns),
                StaggeredGridLayoutManager.VERTICAL);
        setLayoutManager(manager);
    }

    @Override
    public void onResume() {
        super.onResume();

        syncStatusObserver.onStatusChanged(0);
        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING
                | ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
        syncObserverHandle = ContentResolver.addStatusChangeListener(mask, syncStatusObserver);
    }

    @Override
    public void onPause() {
        super.onPause();

        if(syncObserverHandle != null) {
            ContentResolver.removeStatusChangeListener(syncObserverHandle);
            syncObserverHandle = null;
        }
    }

    @Override
    public void onDestroyView() {
        getAdapter().setCallbacks(null);

        swipeRefreshLayout.setOnRefreshListener(null);
        swipeRefreshLayout = null;

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        getLoaderManager().destroyLoader(FRAGMENT_SUBSTITUTION);
        getLoaderManager().destroyLoader(FRAGMENT_HOMEWORK);
        getLoaderManager().destroyLoader(FRAGMENT_EVENT);
        getLoaderManager().destroyLoader(FRAGMENT_NEWS);

        // unregister listener
        LocalBroadcastManager
                .getInstance(getContext())
                .unregisterReceiver(preferenceChangeReceiver);

        super.onDestroy();
    }

    @Override
    public void onDetach() {
        mainNavigation = null;
        super.onDetach();
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "Force refresh triggered!");

        boolean allowed = NetworkManager.getInstance(getActivity()).isAccessAllowed();
        if(allowed) {
            SyncUtils.forceRefresh(SyncAdapter.SYNC.NEWS | SyncAdapter.SYNC.EVENTS | SyncAdapter.SYNC.SUBSTITUTIONS);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Snackbar.make(getContentView(), R.string.notify_network_unavailable, Snackbar.LENGTH_SHORT)
                    .setActionTextColor(ContextCompat.getColor(getActivity(), R.color.md_edittext_error))
                    .setAction(R.string.retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onRefresh();
                        }
                    }).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "Creating cursor loader with id: %d and args: %s", id, args.toString());

        switch (id) {
            case FRAGMENT_SUBSTITUTION: {
                SubstitutionSelection selection = SubstitutionSelection.getForm(
                        PreferenceProvider.getInstance(getActivity()).getSubstPhase(),
                        PreferenceProvider.getInstance(getActivity()).getSubstForm(),
                        PreferenceProvider.getInstance(getActivity()).getSubstSubjects())
                        .getToday();

                String[] projection = {
                        SubstitutionColumns._ID,
                        SubstitutionColumns.TYPE,
                        SubstitutionColumns.PERIOD,
                        SubstitutionColumns.LESSON,
                        SubstitutionColumns.ROOMSUBST,
                        SubstitutionColumns.LESSONSUBST
                };

                return selection.loader(getActivity(), projection);
            }

            case FRAGMENT_HOMEWORK: {
                HomeworkSelection selection = HomeworkSelection.getNextDays();
                String[] projection = {
                        HomeworkColumns._ID,
                        HomeworkColumns.TITLE,
                        HomeworkColumns.TODODATE
                };

                return selection.loader(getActivity(), projection);
            }

            case FRAGMENT_EVENT: {
                EventSelection selection = EventSelection.getNext7Days();
                String[] projection = {
                        EventColumns._ID,
                        EventColumns.TITLE,
                        EventColumns.EVENTDATE,
                        EventColumns.DATESTRING
                };

                return selection.loader(getActivity(), projection);
            }

            case FRAGMENT_NEWS: {
                NewsSelection selection = NewsSelection.getAll().limit(3);
                String[] projection = {
                        NewsColumns._ID,
                        NewsColumns.TITLE,
                        NewsColumns.IMAGEURL,
                        NewsColumns.ARTICLEURL
                };

                return selection.loader(getActivity(), projection);
            }

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Loader finished with id: %d and %d items", loader.getId(), data.getCount());
        //noinspection ResourceType
        getAdapter().swapCursor(loader.getId(), data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //noinspection ResourceType
        getAdapter().swapCursor(loader.getId(), null);
    }

    @Override
    public void onItemClicked(@NavigationItem int type) {
        if(mainNavigation != null) {
            mainNavigation.switchToNavigationItem(type);
        }
    }

    @Override
    public void onSubItemClicked(@NavigationItem int type, long id) {
        switch(type) {
            case FRAGMENT_EVENT:
                EventDetailActivity.startDetail(getActivity(), id);
                break;

            case FRAGMENT_NEWS:
                NewsDetailActivity.startDetail(getActivity(), id);
                break;

            case FRAGMENT_HOMEWORK:
                HomeworkEditActivity.startDetail(getActivity(), id);
                break;

            case FRAGMENT_SUBSTITUTION:
                SubstDetailActivity.startDetail(getActivity(), id);
                break;
        }
    }
}
