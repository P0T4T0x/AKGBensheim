package de.tobiaserthal.akgbensheim;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.SyncStatusObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.tobiaserthal.akgbensheim.adapter.EventAdapter;
import de.tobiaserthal.akgbensheim.adapter.HomeAdapter;
import de.tobiaserthal.akgbensheim.adapter.tools.AdapterClickHandler;
import de.tobiaserthal.akgbensheim.data.Log;
import de.tobiaserthal.akgbensheim.data.preferences.PreferenceProvider;
import de.tobiaserthal.akgbensheim.data.provider.DataProvider;
import de.tobiaserthal.akgbensheim.data.provider.event.EventSelection;
import de.tobiaserthal.akgbensheim.data.provider.homework.HomeworkSelection;
import de.tobiaserthal.akgbensheim.data.provider.news.NewsColumns;
import de.tobiaserthal.akgbensheim.data.provider.news.NewsCursor;
import de.tobiaserthal.akgbensheim.data.provider.news.NewsSelection;
import de.tobiaserthal.akgbensheim.data.provider.substitution.SubstitutionSelection;
import de.tobiaserthal.akgbensheim.data.sync.SyncAdapter;
import de.tobiaserthal.akgbensheim.data.sync.SyncUtils;
import de.tobiaserthal.akgbensheim.data.sync.auth.AuthenticatorService;
import de.tobiaserthal.akgbensheim.ui.base.ToolbarFragment;
import de.tobiaserthal.akgbensheim.ui.base.ToolbarListFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends ToolbarListFragment<HomeAdapter> implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = "HomeFragment";

    private Object syncObserverHandle;
    private final SyncStatusObserver syncStatusObserver = new SyncStatusObserver() {
        @Override
        public void onStatusChanged(int which) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Account account = AuthenticatorService.getAccount(SyncUtils.ACCOUNT_TYPE);
                    boolean syncActive = ContentResolver.isSyncActive(account, DataProvider.AUTHORITY);
                    boolean syncPending = ContentResolver.isSyncPending(account, DataProvider.AUTHORITY);

                    boolean refresh = syncActive || syncPending;
                    Log.d(TAG, "Status change detected. Refreshing: %b", refresh);
                    swipeRefreshLayout.setRefreshing(refresh);
                }
            });
        }
    };

    private final Runnable initLoader = new Runnable() {
        @Override
        public void run() {
            getLoaderManager().initLoader(HomeAdapter.TYPE_SUBST, Bundle.EMPTY, HomeFragment.this);
            getLoaderManager().initLoader(HomeAdapter.TYPE_HOMEWORK, Bundle.EMPTY, HomeFragment.this);
            getLoaderManager().initLoader(HomeAdapter.TYPE_EVENT, Bundle.EMPTY, HomeFragment.this);
            getLoaderManager().initLoader(HomeAdapter.TYPE_NEWS, Bundle.EMPTY, HomeFragment.this);
        }
    };

    private SwipeRefreshLayout swipeRefreshLayout;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(false);
        HomeAdapter adapter = new HomeAdapter(getActivity());
        adapter.setOnItemClickListener(new AdapterClickHandler(){
            @Override
            public void onClick(View view, int position, long id) {
                // forward to detail or forward to section
            }
        });

        setAdapter(adapter);
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
                StaggeredGridLayoutManager.VERTICAL
        );
        setLayoutManager(manager);

        // start loader
        getHandler().post(initLoader);
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
        getHandler().removeCallbacks(initLoader);
        getAdapter().setOnItemClickListener(null);

        swipeRefreshLayout.setOnRefreshListener(null);
        swipeRefreshLayout = null;

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        getLoaderManager().destroyLoader(HomeAdapter.TYPE_SUBST);
        getLoaderManager().destroyLoader(HomeAdapter.TYPE_HOMEWORK);
        getLoaderManager().destroyLoader(HomeAdapter.TYPE_EVENT);
        getLoaderManager().destroyLoader(HomeAdapter.TYPE_NEWS);

        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "Force refresh triggered!");
        SyncUtils.triggerRefresh(-1); // wont work yet
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "Creating cursor loader with id: %d and args: %s", id, args.toString());

        switch (id) {
            case HomeAdapter.TYPE_SUBST: {
                SubstitutionSelection selection = SubstitutionSelection
                        .getForm(
                                PreferenceProvider.getInstance(getActivity()).getSubstPhase(),
                                PreferenceProvider.getInstance(getActivity()).getSubstForm(),
                                PreferenceProvider.getInstance(getActivity()).getSubstSubjects()
                        );
                return selection.loader(getActivity(), null);
            }

            case HomeAdapter.TYPE_HOMEWORK: {
                HomeworkSelection selection = HomeworkSelection.getNextDays();
                return selection.loader(getActivity(), null);
            }

            case HomeAdapter.TYPE_EVENT: {
                EventSelection selection = EventSelection.getNext10Days();
                return selection.loader(getActivity(), null);
            }

            case HomeAdapter.TYPE_NEWS:
                NewsSelection selection = NewsSelection.getAll().limit(3);
                return selection.loader(getActivity(), null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Loader finished with id: %d and %d items", loader.getId(), data.getCount());
        getAdapter().swapCursor(loader.getId(), data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        getAdapter().swapCursor(loader.getId(), null);
    }
}
