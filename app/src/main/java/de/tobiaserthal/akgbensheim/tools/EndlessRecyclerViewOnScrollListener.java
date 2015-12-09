package de.tobiaserthal.akgbensheim.tools;
import android.support.v7.widget.RecyclerView;

/**
 * See https://gist.github.com/mipreamble/b6d4b3d65b0b4775a22e#file-recyclerviewpositionhelper-java
 */
public abstract class EndlessRecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = "EndlessScrollListener";

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int currentPage = 1;
    RecyclerViewPositionHelper mRecyclerViewHelper;


    public EndlessRecyclerViewOnScrollListener(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        mRecyclerViewHelper = RecyclerViewPositionHelper.createHelper(recyclerView);
        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mRecyclerViewHelper.getItemCount();
        firstVisibleItem = mRecyclerViewHelper.findFirstVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        } else if ((totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {

            // End has been reached
            // Do something
            currentPage ++;

            onLoadMore(currentPage);

            loading = true;
        }
    }

    //Start loading
    public abstract void onLoadMore(int currentPage);

    public void reset(int previousTotal, boolean loading) {
        this.previousTotal = previousTotal;
        this.loading = loading;
    }
}
