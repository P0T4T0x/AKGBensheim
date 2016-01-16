package de.tobiaserthal.akgbensheim.subst;

import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.tonicartos.superslim.LayoutManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.backend.preferences.PreferenceProvider;
import de.tobiaserthal.akgbensheim.backend.preferences.SubstPreferenceChangeReceiver;
import de.tobiaserthal.akgbensheim.backend.provider.substitution.SubstitutionColumns;
import de.tobiaserthal.akgbensheim.backend.provider.substitution.SubstitutionCursor;
import de.tobiaserthal.akgbensheim.backend.provider.substitution.SubstitutionSelection;
import de.tobiaserthal.akgbensheim.backend.utils.Log;
import de.tobiaserthal.akgbensheim.base.adapter.AdapterClickHandler;
import de.tobiaserthal.akgbensheim.base.tabs.TabbedListFragment;
import de.tobiaserthal.akgbensheim.subst.adapter.SubstAdapter;

/**
 * A simple {@link Fragment} subclass.
 */

public class SubstFragment extends TabbedListFragment<SubstAdapter>
        implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    public static final String TAG = "SubstFragment";

    private static final String ARG_QUERY_FLAG = "query";
    private static final String ARG_VIEW_FLAG = "view";

    @IntDef({FORM, PHASE, ALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ViewMode {}

    public static final int FORM = 0x0;
    public static final int PHASE = 0x1;
    public static final int ALL = 0x2;

    private int viewFlag;
    private String currentFilter;

    private String emptyText;
    private String emptyQueryText;

    private int phase;
    private String form;
    private String[] subjects;

    private Parcelable savedLayoutState;
    private ChangeReceiver changeReceiver;

    static class ChangeReceiver extends SubstPreferenceChangeReceiver {
        private WeakReference<SubstFragment> reference;

        public ChangeReceiver(SubstFragment fragment) {
            reference = new WeakReference<>(fragment);
        }

        @Override
        public void onColorPreferenceChange() {
            SubstFragment fragment = reference.get();
            if(fragment != null) {
                fragment.getAdapter().notifyDataSetChanged();
            }
        }

        @Override
        public void onSubstPreferenceChange() {
            SubstFragment fragment = reference.get();
            if(fragment != null) {
                fragment.phase = PreferenceProvider.getInstance().getSubstPhase();
                fragment.form = PreferenceProvider.getInstance().getSubstForm();
                fragment.subjects = PreferenceProvider.getInstance().getSubstSubjects();

                fragment.getLoaderManager().restartLoader(fragment.viewFlag, Bundle.EMPTY, fragment);
            }
        }
    }

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

        phase = PreferenceProvider.getInstance().getSubstPhase();
        form = PreferenceProvider.getInstance().getSubstForm();
        subjects = PreferenceProvider.getInstance().getSubstSubjects();

        savedLayoutState = savedInstanceState != null ?
                savedInstanceState.getParcelable("layoutManager") : null;
        getLoaderManager().initLoader(viewFlag, Bundle.EMPTY, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        if(getArguments() != null) {
            viewFlag = getArguments().getInt(ARG_VIEW_FLAG, ALL);
        }

        emptyText = getString(R.string.empty_title_subst);
        emptyQueryText = getString(R.string.empty_query_subst);

        // Create the adapter
        setAdapter(new SubstAdapter(getActivity(), null, viewFlag));

        // listen for preference changes
        changeReceiver = new ChangeReceiver(this);
        IntentFilter filter = new IntentFilter(PreferenceProvider.ACTION_SUBST);
        LocalBroadcastManager
                .getInstance(getContext())
                .registerReceiver(changeReceiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate layout
        View view = inflater.inflate(R.layout.fragment_subst, container, false);

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

        setEmptyText(emptyText);
        setLayoutManager(manager);
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
    public void onDestroyView() {
        getAdapter().setOnClickListener(null);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        getLoaderManager().destroyLoader(viewFlag);

        // unregister the receiver
        LocalBroadcastManager
                .getInstance(getContext())
                .unregisterReceiver(changeReceiver);

        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("layoutManager", getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(outState);
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

            case FORM:
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

        // another ugly, yet working solution
        if(savedLayoutState != null) {
            Log.d(TAG, "Restoring layout manager position");
            getRecyclerView().post(new Runnable() {
                @Override
                public void run() {
                    getLayoutManager().onRestoreInstanceState(savedLayoutState);
                    savedLayoutState = null;
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "Resetting loader with id: %d", loader.getId());
        getAdapter().swapCursor(null);
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
        if(newFilter != null) {
            setEmptyText(String.format(emptyQueryText, newFilter));
        } else {
            setEmptyText(emptyText);
        }

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
