package de.tobiaserthal.akgbensheim.contact;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.view.ViewHelper;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.data.model.ModelUtils;
import de.tobiaserthal.akgbensheim.ui.OverlayActivity;
import de.tobiaserthal.akgbensheim.ui.widget.BackdropImageView;

/**
 * Created by tobiaserthal on 18.09.15.
 */
public class ContactActivity extends OverlayActivity<ObservableScrollView> implements View.OnClickListener {

    private View headerView;
    private TextView headerTitle;
    private TextView headerSubtitle;
    private BackdropImageView imageView;

    private int actionBarTitleMargin;
    private int flexibleSpaceShowFABOffset;
    private int flexibleSpaceImageHeight;

    private int statusBarColor = Color.BLACK;

    private final Interpolator materialInterpolator = new AccelerateDecelerateInterpolator();
    private final ArgbEvaluator colorInterpolator = new ArgbEvaluator();

    private static final float MAX_TEXT_SCALE_DELTA = 0.25f;

    public static void startDetail(FragmentActivity activity) {
        Intent intent = createOverlayActivity(activity, ContactActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onStartEnterAnimation() {
        float scale = 1 + MAX_TEXT_SCALE_DELTA;
        ViewHelper.setPivotX(headerTitle, 0);
        ViewHelper.setPivotY(headerTitle, headerTitle.getHeight());
        ViewHelper.setScaleX(headerTitle, scale);
        ViewHelper.setScaleY(headerTitle, scale);
        ViewHelper.setAlpha(headerSubtitle, 0);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        setToolbar((Toolbar) findViewById(R.id.toolbar));
        setScrollable((ObservableScrollView) findViewById(R.id.contact_detail_scrollView));
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME);
        }

        headerView = findViewById(R.id.contact_header);
        headerTitle = (TextView) findViewById(R.id.contact_header_title);
        headerSubtitle = (TextView) findViewById(R.id.contact_header_subtitle);

        imageView = (BackdropImageView) findViewById(R.id.contact_header_imageView);
        imageView.setScrimColor(getResources().getColor(R.color.primary));
        statusBarColor = getResources().getColor(R.color.primaryDark);

        setupRow(R.id.addressRow, R.drawable.ic_drawer_home, R.string.address, this);
        setupRow(R.id.emailRow, R.drawable.ic_email, R.string.email, this);
        setupRow(R.id.phoneRow, R.drawable.ic_phone_grey600_24dp, R.string.phone, this);
        setupRow(R.id.faxRow, R.drawable.ic_message_text_outline, R.string.fax, this);

        flexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        flexibleSpaceShowFABOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
        actionBarTitleMargin = getResources().getDimensionPixelSize(R.dimen.flexible_space_header_margin_left);
    }

    private void setupRow(int rowId, @DrawableRes int iconRes, @StringRes int stringRes, View.OnClickListener listener) {
        TextView row = (TextView) findViewById(rowId);
        row.setClickable(true);
        row.setText(stringRes);
        row.setOnClickListener(listener);

        Drawable start = ContextCompat.getDrawable(row.getContext(), iconRes);
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(row, start, null, null, null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addressRow:
                ContactUtils.startMapViewIntent(this);
                break;

            case R.id.emailRow:
                ContactUtils.startEmailIntent(this);
                break;

            case R.id.phoneRow:
                ContactUtils.startDialIntent(this);
                break;

            case R.id.faxRow:
                // Are you serious?
                break;
        }
    }

    @Override
    public void onScrollChanged(int scrollY, boolean b, boolean b1) {
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
                            fraction, Color.BLACK, statusBarColor
                    )
            );
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
    }

    @Override
    public void onDownMotionEvent() {}

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {}
}
