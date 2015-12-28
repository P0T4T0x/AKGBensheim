package de.tobiaserthal.akgbensheim.teacher;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.backend.provider.teacher.TeacherColumns;
import de.tobiaserthal.akgbensheim.backend.provider.teacher.TeacherCursor;
import de.tobiaserthal.akgbensheim.backend.provider.teacher.TeacherSelection;
import de.tobiaserthal.akgbensheim.backend.utils.Log;
import de.tobiaserthal.akgbensheim.base.adapter.AdapterClickHandler;
import de.tobiaserthal.akgbensheim.base.tabs.TabbedListFragment;
import de.tobiaserthal.akgbensheim.teacher.adapter.TeacherAdapter;

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

        emptyText = getResources().getString(R.string.empty_title_teachers);
        emptyQueryText = getResources().getString(R.string.empty_query_teachers);

        // Create the adapter
        setAdapter(new TeacherAdapter(getActivity(), null));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher, container, false);


        getAdapter().setOnClickListener(new AdapterClickHandler() {
            @Override
            public void onClick(View view, int position, long id) {
                TeacherDetailActivity.startDetail(getActivity(), id);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(viewFlag, Bundle.EMPTY, TeacherFragment.this);
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
        if(newFilter != null) {
            setEmptyText(String.format(emptyQueryText, newFilter));
        } else {
            setEmptyText(emptyText);
        }

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
