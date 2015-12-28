package de.tobiaserthal.akgbensheim.homework;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.backend.provider.homework.HomeworkColumns;
import de.tobiaserthal.akgbensheim.backend.provider.homework.HomeworkCursor;
import de.tobiaserthal.akgbensheim.backend.provider.homework.HomeworkSelection;
import de.tobiaserthal.akgbensheim.backend.utils.Log;
import de.tobiaserthal.akgbensheim.base.adapter.AdapterClickHandler;
import de.tobiaserthal.akgbensheim.base.tabs.TabbedListFragment;
import de.tobiaserthal.akgbensheim.homework.adapter.HomeworkAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeworkFragment extends TabbedListFragment<HomeworkAdapter>
        implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private static final String TAG = "HomeworkFragment";

    private static final String ARG_QUERY_FLAG = "query";
    private static final String ARG_VIEW_FLAG = "view";
    private static final String ARG_BTN_FLAG = "btn";

    @IntDef({TODO, DONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ViewMode {}

    public static final int TODO = 0x0;
    public static final int DONE = 0x1;

    private int viewFlag;
    private String currentFilter;

    private String emptyText;
    private String emptyQueryText;


    public static Bundle createArgs(@ViewMode int which, boolean btnNeeded) {
        Bundle args = new Bundle();
        args.putInt(ARG_VIEW_FLAG, which);
        args.putBoolean(ARG_BTN_FLAG, btnNeeded);

        return args;
    }

    public HomeworkFragment() {
        super();
    }

    public boolean needsButton() {
        return getArguments() != null && getArguments().getBoolean(ARG_BTN_FLAG);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(viewFlag, Bundle.EMPTY, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        if(getArguments() != null) {
            viewFlag = getArguments().getInt(ARG_VIEW_FLAG, TODO);
        }

        switch (viewFlag) {
            case TODO:
                emptyText = getString(R.string.empty_title_homeworkTodo);
                break;

            case DONE:
                emptyText = getString(R.string.empty_title_homeworkDone);
                break;
        }

        emptyQueryText = getString(R.string.empty_query_homework);
        setAdapter(new HomeworkAdapter(getActivity(), null));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_homework, container, false);

        getAdapter().setOnClickListener(new AdapterClickHandler() {
            @Override
            public void onClick(View view, int position, long id) {
                HomeworkEditActivity.startDetail(getActivity(), id);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(
                getResources().getInteger(R.integer.column_count_news),
                StaggeredGridLayoutManager.VERTICAL);

        setEmptyText(emptyText);
        setLayoutManager(manager);
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
        HomeworkSelection selection;
        switch(id) {
            case TODO:
                selection = (query == null) ?
                        HomeworkSelection.getTodo() :
                        HomeworkSelection.getTodoWithQuery(query);
                break;

            case DONE:
                selection = (query == null) ?
                        HomeworkSelection.getDone() :
                        HomeworkSelection.getDoneWithQuery(query);
                break;

            default:
                return null;
        }

        String[] projection = {
                HomeworkColumns._ID,
                HomeworkColumns.TODODATE,
                HomeworkColumns.TITLE,
                HomeworkColumns.NOTES
        };

        return selection.loader(getActivity(), projection);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Loader finished with id: %d and %d items", loader.getId(), data.getCount());
        getAdapter().swapCursor(HomeworkCursor.wrap(data));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "Resetting loader with id: %d", loader.getId());
        getAdapter().swapCursor(null);
    }

    @Override
    public boolean onClose() {
        setEmptyText(emptyText);
        getLoaderManager().restartLoader(viewFlag, Bundle.EMPTY, this);

        return false;
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
}
