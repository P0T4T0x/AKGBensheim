package de.tobiaserthal.akgbensheim.teacher;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.SyncStatusObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.tonicartos.superslim.LayoutManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.adapter.TeacherAdapter;
import de.tobiaserthal.akgbensheim.adapter.tools.AdapterClickHandler;
import de.tobiaserthal.akgbensheim.data.Log;
import de.tobiaserthal.akgbensheim.data.provider.DataProvider;
import de.tobiaserthal.akgbensheim.data.provider.teacher.TeacherColumns;
import de.tobiaserthal.akgbensheim.data.provider.teacher.TeacherCursor;
import de.tobiaserthal.akgbensheim.data.provider.teacher.TeacherSelection;
import de.tobiaserthal.akgbensheim.data.sync.SyncAdapter;
import de.tobiaserthal.akgbensheim.data.sync.SyncUtils;
import de.tobiaserthal.akgbensheim.data.sync.auth.AuthenticatorService;
import de.tobiaserthal.akgbensheim.ui.tabs.TabbedListFragment;
import de.tobiaserthal.akgbensheim.ui.widget.RecyclerViewFastScroll;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherFragment extends TabbedListFragment<TeacherAdapter>
        implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    public static final String TAG = "TeacherFragment";

    private static final String ARG_QUERY_FLAG = "query";
    private static final String ARG_VIEW_FLAG = "view";

    @IntDef({TEACHER, STUDENT_TEACHER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ViewMode {}

    public static final int TEACHER = 0x0;
    public static final int STUDENT_TEACHER = 0x1;

    private final Runnable initLoader = new Runnable() {
        @Override
        public void run() {
            Bundle args = new Bundle();
            args.putString(ARG_QUERY_FLAG, null);
            getLoaderManager().initLoader(viewFlag, args, TeacherFragment.this);
        }
    };

    private int viewFlag;
    private String currentFilter;

    private String emptyText;
    private String emptyQueryText;

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

    public TeacherFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        if(getArguments() != null) {
            viewFlag = getArguments().getInt(ARG_VIEW_FLAG, TEACHER);
        }

        emptyText = getResources().getString(R.string.teacher_empty_text);
        emptyQueryText = getResources().getString(R.string.teacher_empty_query_text);

        // Create the adapter
        setAdapter(new TeacherAdapter(getActivity(), null));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher, container, false);

        RecyclerViewFastScroll fastScroll = (RecyclerViewFastScroll) view.findViewById(R.id.fastscroll);
        fastScroll.setRecyclerView((RecyclerView) view.findViewById(android.R.id.list));

        getAdapter().setOnClickListener(new AdapterClickHandler() {
            @Override
            public void onClick(View view, int position, long id) {
                TeacherDetailActivity.startDetail(getActivity(), id);
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
        getHandler().post(initLoader);
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
        getHandler().removeCallbacks(initLoader);
        getAdapter().setOnClickListener(null);

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
        TeacherSelection selection;
        switch (id) {
            case TEACHER:
                selection = (query == null) ?
                        TeacherSelection.getTeachers() :
                        TeacherSelection.getTeachersWithQuery(query);
                break;

            case STUDENT_TEACHER:
                selection = (query == null) ?
                        TeacherSelection.getStudentTeachers() :
                        TeacherSelection.getStudentTeachersWithQuery(query);
                break;

            default:
                return null;
        }

        String[] projection = {
                TeacherColumns._ID,
                TeacherColumns.FIRSTNAME,
                TeacherColumns.LASTNAME,
                TeacherColumns.SHORTHAND,
                TeacherColumns.SUBJECTS
        };

        return selection
                .orderBy(TeacherColumns.LASTNAME, false, false)
                .loader(getActivity(), projection);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Loader finished with id: %d and %d items", loader.getId(), data.getCount());
        getAdapter().swapCursor(TeacherCursor.wrap(data));
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
        setEmptyText(String.format(emptyQueryText, newFilter));

        Bundle args = new Bundle();
        args.putString(ARG_QUERY_FLAG, currentFilter);
        getLoaderManager().restartLoader(viewFlag, args, this);

        return true;
    }

    @Override
    public boolean onClose() {
        setEmptyText(emptyText);

        Bundle args = new Bundle();
        args.putString(ARG_QUERY_FLAG, null);
        getLoaderManager().restartLoader(viewFlag, args, this);

        return false;
    }
}
