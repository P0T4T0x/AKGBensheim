package de.tobiaserthal.akgbensheim.subst;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SyncStatusObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.tonicartos.superslim.LayoutManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.adapter.SubstAdapter;
import de.tobiaserthal.akgbensheim.adapter.tools.AdapterClickHandler;
import de.tobiaserthal.akgbensheim.data.Log;
import de.tobiaserthal.akgbensheim.data.preferences.PreferenceKey;
import de.tobiaserthal.akgbensheim.data.preferences.PreferenceProvider;
import de.tobiaserthal.akgbensheim.data.provider.DataProvider;
import de.tobiaserthal.akgbensheim.data.provider.substitution.SubstitutionColumns;
import de.tobiaserthal.akgbensheim.data.provider.substitution.SubstitutionCursor;
import de.tobiaserthal.akgbensheim.data.provider.substitution.SubstitutionSelection;
import de.tobiaserthal.akgbensheim.data.sync.SyncAdapter;
import de.tobiaserthal.akgbensheim.data.sync.SyncUtils;
import de.tobiaserthal.akgbensheim.data.sync.auth.AuthenticatorService;
import de.tobiaserthal.akgbensheim.ui.tabs.TabbedListFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubstFragment extends TabbedListFragment<SubstAdapter>
        implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener,
        SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    public static final String TAG = "SubstFragment";

    private static final String ARG_QUERY_FLAG = "query";
    private static final String ARG_VIEW_FLAG = "view";

    @IntDef({FORM, PHASE, ALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ViewMode {}

    public static final int FORM = 0x0;
    public static final int PHASE = 0x1;
    public static final int ALL = 0x2;

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
                    refreshLayout.setRefreshing(refresh);
                }
            });
        }
    };

    private int viewFlag;
    private String currentFilter;
    private SwipeRefreshLayout refreshLayout;

    private String emptyText;
    private String emptyQueryText;

    private int phase;
    private String form;
    private String[] subjects;

    /**
     * Creates a new bundle you can pass to a instance of this fragment during
     * creation that allow the fragment to respond to the specified arguments.
     * @param which What this fragment should present.
     * @return A bundle object.
     */
    public static Bundle createArgs(@ViewMode int which) {
        Bundle args = new Bundle();
        args.putInt(ARG_VIEW_FLAG, which);

        return args;
    }

    public SubstFragment() {
        super();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        phase = PreferenceProvider.getInstance(getActivity()).getSubstPhase();
        form = PreferenceProvider.getInstance(getActivity()).getSubstForm();
        subjects = PreferenceProvider.getInstance(getActivity()).getSubstSubjects();

        System.out.println("FUUUUUUUUCK: " + form);
        getLoaderManager().initLoader(viewFlag, Bundle.EMPTY, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        if(getArguments() != null) {
            viewFlag = getArguments().getInt(ARG_VIEW_FLAG, ALL);
        }

        emptyText = getString(R.string.subst_empty_text);
        emptyQueryText = getString(R.string.subst_empty_query_text);

        // Create the adapter
        setAdapter(new SubstAdapter(getActivity(), null, viewFlag));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate layout
        View view = inflater.inflate(R.layout.fragment_subst, container, false);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeResources(R.color.primary, R.color.accent, R.color.primaryDark);
        refreshLayout.setOnRefreshListener(this);

        // set adapter
        getAdapter().setOnClickListener(new AdapterClickHandler() {
            @Override
            public void onClick(View view, int position, long id) {
                SubstDetailActivity.startDetail(getActivity(), id, (Integer) view.getTag());
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle  savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Create the layout manager
        LayoutManager manager = new LayoutManager(getActivity());
        setLayoutManager(manager);

        // start loader
        //getHandler().post(initLoader);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search_icon, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(false);
        searchView.setOnCloseListener(this);
        searchView.setOnQueryTextListener(this);
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
        getAdapter().setOnClickListener(null);

        refreshLayout.setOnRefreshListener(null);
        refreshLayout = null;

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        getLoaderManager().destroyLoader(viewFlag);
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "Creating cursor loader with id: %d and args: %s", id, args.toString());

        String query = args.getString(ARG_QUERY_FLAG);
        SubstitutionSelection selection;
        switch (id) {
            case ALL:
                selection = (query == null) ?
                        SubstitutionSelection.getAll() :
                        SubstitutionSelection.getAllWithQuery(query);
                break;

            case PHASE:
                selection = (query == null) ?
                        SubstitutionSelection.getPhase(phase) :
                        SubstitutionSelection.getPhaseWithQuery(phase, query);
                break;

            case FORM: //FIXME
                selection = (query == null) ?
                        SubstitutionSelection.getForm(phase, form, subjects) :
                        SubstitutionSelection.getFormWithQuery(phase, form, subjects, query);
                break;

            default:
                return null;
        }

        String[] projection = {
                SubstitutionColumns._ID,
                SubstitutionColumns.FORMKEY,
                SubstitutionColumns.SUBSTDATE,
                SubstitutionColumns.LESSON,
                SubstitutionColumns.TYPE,
                SubstitutionColumns.PERIOD,
                SubstitutionColumns.ROOMSUBST,
                SubstitutionColumns.LESSONSUBST
        };

        return selection.loader(getActivity(), projection);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Loader finished with id: %d and %d items", loader.getId(), data.getCount());
        getAdapter().swapCursor(SubstitutionCursor.wrap(data));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "Resetting loader with id: %d", loader.getId());
        getAdapter().swapCursor(null);
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "Force refresh triggered!");
        SyncUtils.triggerRefresh(SyncAdapter.SYNC.SUBSTITUTIONS);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String newFilter = !TextUtils.isEmpty(newText) ? newText : null;

        if (currentFilter == null && newFilter == null)
            return true;

        if (currentFilter != null && currentFilter.equals(newFilter))
            return true;

        currentFilter = newFilter;
        setEmptyText(String.format(emptyQueryText, newFilter));

        Bundle args = new Bundle();
        args.putString(ARG_QUERY_FLAG, currentFilter);
        getLoaderManager().restartLoader(viewFlag, args, SubstFragment.this);

        return true;
    }

    @Override
    public boolean onClose() {
        setEmptyText(emptyText);
        getLoaderManager().restartLoader(viewFlag, Bundle.EMPTY, SubstFragment.this);

        return false;
    }
}
