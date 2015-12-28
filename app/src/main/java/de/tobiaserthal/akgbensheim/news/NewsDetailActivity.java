package de.tobiaserthal.akgbensheim.news;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Picasso;


import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.data.SimpleAsyncQueryHandler;
import de.tobiaserthal.akgbensheim.data.model.ModelUtils;
import de.tobiaserthal.akgbensheim.data.provider.news.NewsContentValues;
import de.tobiaserthal.akgbensheim.data.provider.news.NewsCursor;
import de.tobiaserthal.akgbensheim.data.provider.news.NewsSelection;
import de.tobiaserthal.akgbensheim.ui.OverlayActivity;
import de.tobiaserthal.akgbensheim.ui.widget.BackdropImageView;

import static de.tobiaserthal.akgbensheim.R.drawable.abc_ic_menu_share_mtrl_alpha;

public class NewsDetailActivity extends OverlayActivity<ObservableScrollView>
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private View headerView;
    private TextView headerTitle;
    private TextView headerSubtitle;
    private FloatingActionButton actionButton;

    private NewsCursor news;
    private TextView titleView;
    private TextView articleView;
    private TextView imageDescView;
    private BackdropImageView imageView;
    private int statusBarColor = Color.BLACK;
    private int actionBarTitleMargin;
    private int flexibleSpaceShowFABOffset;
    private int flexibleSpaceImageHeight;
    private boolean isFABShown = false;

    private final Interpolator materialInterpolator = new AccelerateDecelerateInterpolator();
    private final ArgbEvaluator colorInterpolator = new ArgbEvaluator();

    public static final String EXTRA_PARAM_ID = "detail:_id";
    private static final float MAX_TEXT_SCALE_DELTA = 0.25f;

    public static void startDetail(FragmentActivity activity, long id) {
        Intent intent = createOverlayActivity(activity, NewsDetailActivity.class);
        intent.putExtra(EXTRA_PARAM_ID, id);

        activity.startActivity(intent);
    }

    @Override
    public void onStartEnterAnimation() {
        hideFAB(false);
        isFABShown = true;
        getScrollable().scrollVerticallyTo(0);
        onScrollChanged(0, false, false);
    }

    @Override
    public void onEndEnterAnimation() {
        showFAB(true);
        isFABShown = true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        setToolbar((Toolbar) findViewById(R.id.toolbar));
        setScrollable((ObservableScrollView) findViewById(R.id.news_detail_scrollView));
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME);
        }

        headerView = findViewById(R.id.news_header);
        headerTitle = (TextView) findViewById(R.id.news_header_title);
        headerSubtitle = (TextView) findViewById(R.id.news_header_subtitle);

        titleView = (TextView) findViewById(R.id.news_detail_title_textView);
        articleView = (TextView) findViewById(R.id.news_detail_article_textView);
        imageDescView = (TextView) findViewById(R.id.news_detail_image_desc_textView);
        imageView = (BackdropImageView) findViewById(R.id.news_header_imageView);

        flexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        flexibleSpaceShowFABOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
        actionBarTitleMargin = getResources().getDimensionPixelSize(R.dimen.flexible_space_header_margin_left);

        actionButton = (FloatingActionButton) findViewById(R.id.actionButton);
        actionButton.setImageResource(abc_ic_menu_share_mtrl_alpha);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModelUtils.startShareIntent(NewsDetailActivity.this, news);
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(Color.TRANSPARENT);

        imageView.setFactor(0.25f);
        imageView.setScrimColor(ContextCompat.getColor(this, R.color.primary));
        statusBarColor = ContextCompat.getColor(this, R.color.primaryDark);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_bookmark);

        if(news != null && news.moveToFirst()) {
            item.setChecked(news.getBookmarked());
        }

        updateMenuIcon(item, R.drawable.ic_action_bookmark);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_open_in_browser:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(news.getArticleUrl())));
                return true;

            case R.id.action_bookmark:
                NewsSelection selection = NewsSelection.get(news.getId());
                NewsContentValues values = new NewsContentValues().putBookmarked(!news.getBookmarked());
                new SimpleAsyncQueryHandler(getContentResolver()).startUpdate(selection, values);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateMenuIcon(MenuItem item, int selId) {
        if(!item.isCheckable())
            return;

        StateListDrawable listDrawable;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //noinspection deprecation
            listDrawable = (StateListDrawable) getResources().getDrawable(selId);
        } else {
            listDrawable = (StateListDrawable) getResources().getDrawable(selId, null);
        }

        if(listDrawable != null) {
            int[] state = {
                    item.isChecked() ?
                            android.R.attr.state_checked :
                            android.R.attr.state_empty
            };
            listDrawable.setState(state);
            item.setIcon(listDrawable.getCurrent());
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return NewsSelection.get(
                getIntent().getLongExtra(EXTRA_PARAM_ID, 0L)).loader(this, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data == null)
            return;

        NewsCursor oldCursor = news;
        news = NewsCursor.wrap(data);

        if(oldCursor != null) {
            oldCursor.close();
        }

        if(!news.moveToFirst()) {
            return;
        }

        supportInvalidateOptionsMenu();

        titleView.setText(news.getTitle());
        headerSubtitle.setText(news.getTitle());
        imageDescView.setText(news.getImageDesc());
        articleView.setText(news.getArticle());


        Picasso.with(this)
                .load(news.getImageUrl())
                .error(R.drawable.akg)
                .fit().centerCrop()
                .into(imageView);

        imageDescView.setVisibility(
                TextUtils.isEmpty(news.getImageDesc()) ?
                        View.GONE : View.VISIBLE
        );
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        news.close();
        news = null;
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        float flexibleRange = flexibleSpaceImageHeight - getToolbar().getHeight();
        float offset = ScrollUtils.getFloat(scrollY, 0, flexibleRange);
        float fraction = materialInterpolator.getInterpolation(offset / flexibleRange);

        // translate the whole header view
        ViewHelper.setTranslationY(headerView, -offset);

        // translate imageView content and scrim darkness
        imageView.setProgress((int) offset, fraction);

        // set status bar color
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(
                    (Integer) colorInterpolator.evaluate(
                            fraction, Color.BLACK, statusBarColor));
        }

        // translate the title and the subtitle in x direction
        float titleMargin = fraction * actionBarTitleMargin;
        ViewHelper.setTranslationX(headerTitle, titleMargin);
        ViewHelper.setTranslationX(headerSubtitle, titleMargin);

        // scale the header title
        float scale = 1 + (1 - fraction) * MAX_TEXT_SCALE_DELTA;
        ViewHelper.setPivotX(headerTitle, 0);
        ViewHelper.setPivotY(headerTitle, headerTitle.getHeight());
        ViewHelper.setScaleX(headerTitle, scale);
        ViewHelper.setScaleY(headerTitle, scale);

        // fade in/out the subtitle text view
        float fadingScrollY = scrollY - flexibleSpaceShowFABOffset;
        float fadingRange = flexibleRange - flexibleSpaceShowFABOffset;
        float fadingOffset = ScrollUtils.getFloat(fadingScrollY, 0, fadingRange);
        float fadingFraction = materialInterpolator.getInterpolation(fadingOffset / fadingRange);
        ViewHelper.setAlpha(headerSubtitle, fadingFraction);

        // translate action button
        int halfHeight = actionButton.getMeasuredHeight() / 2;
        int translationY = flexibleSpaceImageHeight - halfHeight - (int) offset;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) actionButton.getLayoutParams();
            //layoutParams.topMargin = translationY;
            actionButton.requestLayout();
        } else {
            ViewHelper.setTranslationY(actionButton, translationY);
        }

        // show or hide the action button
        if(offset > flexibleSpaceShowFABOffset) {
            if (isFABShown) {
                hideFAB(true);
                isFABShown = false;
            }
        } else {
            if (!isFABShown) {
                showFAB(true);
                isFABShown = true;
            }
        }
    }

    @Override
    public void onDownMotionEvent() {}

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {}

    private void showFAB(boolean animated) {
        if(!animated) {
            ViewHelper.setScaleX(actionButton, 1);
            ViewHelper.setScaleY(actionButton, 1);
            actionButton.setVisibility(View.VISIBLE);
            return;
        }

        actionButton.show();
    }

    private void hideFAB(boolean animated) {
        if(!animated) {
            actionButton.setVisibility(View.GONE);
            ViewHelper.setScaleX(actionButton, 0);
            ViewHelper.setScaleY(actionButton, 0);
            return;
        }

        actionButton.hide();
    }
}
