package de.tobiaserthal.akgbensheim.news;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.backend.provider.news.NewsColumns;
import de.tobiaserthal.akgbensheim.backend.provider.news.NewsCursor;
import de.tobiaserthal.akgbensheim.backend.provider.news.NewsSelection;
import de.tobiaserthal.akgbensheim.backend.rest.model.news.NewsKeys;
import de.tobiaserthal.akgbensheim.backend.sync.SyncUtils;
import de.tobiaserthal.akgbensheim.backend.utils.Log;
import de.tobiaserthal.akgbensheim.base.adapter.AdapterClickHandler;
import de.tobiaserthal.akgbensheim.base.tabs.TabbedListFragment;
import de.tobiaserthal.akgbensheim.news.adapter.NewsAdapter;

/**
 * A simple {@link Fragment} subclass.
 */

public class NewsFragment extends TabbedListFragment<NewsAdapter>
        implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    public static final String TAG = "NewsFragment";

    private static final String ARG_QUERY_FLAG = "query";
    private static final String ARG_VIEW_FLAG = "view";
    private static final String ARG_LIMIT_FLAG = "limit";

    @IntDef({ALL, BOOKMARKED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ViewMode {}

    public static final int ALL = 0x0;
    public static final int BOOKMARKED = 0x1;

    private int page = 1;
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

    public NewsFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        if(getArguments() != null) {
            viewFlag = getArguments().getInt(ARG_VIEW_FLAG, ALL);
        }

        switch (viewFlag) {
            case ALL:
                emptyText = getResources().getString(R.string.empty_title_news);
                emptyQueryText = getResources().getString(R.string.empty_query_news);
                break;

            case BOOKMARKED:
                emptyText = getResources().getString(R.string.empty_title_newsBookmarked);
                emptyQueryText = getResources().getString(R.string.empty_query_newsBookmarked);
                break;
        }

        // Create the adapter
        setAdapter(new NewsAdapter(getActivity(), null));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = new Bundle();
        if(savedInstanceState != null) {
            int limit = savedInstanceState.getInt(ARG_LIMIT_FLAG, NewsKeys.ITEMS_PER_PAGE);
            bundle.putInt(ARG_LIMIT_FLAG, limit);
        }

        getLoaderManager().initLoader(viewFlag, bundle, NewsFragment.this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        // setup click listener
        getAdapter().setOnClickListener(new AdapterClickHandler() {
            @Override
            public void onClick(View view, int position, long id) {
                NewsDetailActivity.startDetail(getActivity(), id);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle  savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Create the layout manager
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(
                getResources().getInteger(R.integer.column_count_news),
                StaggeredGridLayoutManager.VERTICAL
        );

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
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ARG_LIMIT_FLAG, page * NewsKeys.ITEMS_PER_PAGE);
        super.onSaveInstanceState(outState);
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
    public void onLoadMore(int page) {
        Log.d(TAG, "Requested to load page: %d", page);

        if(viewFlag == ALL && TextUtils.isEmpty(currentFilter)) {
            this.page = page;

            Bundle bundle = new Bundle();
            bundle.putString(ARG_QUERY_FLAG, null);
            bundle.putInt(ARG_LIMIT_FLAG, page * NewsKeys.ITEMS_PER_PAGE);

            getLoaderManager().restartLoader(ALL, bundle, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "Creating cursor loader with id: %d and args: %s", id, args.toString());

        int limit = args.getInt(ARG_LIMIT_FLAG, NewsKeys.ITEMS_PER_PAGE);
        String query = args.getString(ARG_QUERY_FLAG);
        NewsSelection selection;
        switch (id) {
            case ALL:
                selection = (query == null) ?
                        NewsSelection.getAll().limit(limit) :
                        NewsSelection.getAllWithQuery(query);
                break;

            case BOOKMARKED:
                selection = (query == null) ?
                        NewsSelection.getBookmarked() :
                        NewsSelection.getBookmarkedWithQuery(query);
                break;

            default:
                return null;
        }

        String[] projection = {
                NewsColumns._ID,
                NewsColumns.TITLE,
                NewsColumns.SNIPPET,
                NewsColumns.IMAGEURL
        };

        return selection.loader(getActivity(), projection);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Loader finished with id: %d and %d items", loader.getId(), data.getCount());
        getAdapter().swapCursor(NewsCursor.wrap(data));

        if(loader.getId() == ALL) {
            if (page > data.getCount() / NewsKeys.ITEMS_PER_PAGE) {
                Log.w(TAG, "Need to load new data!");
                SyncUtils.loadNews(data.getCount(), NewsKeys.ITEMS_PER_PAGE);
            }
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
