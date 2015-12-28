package de.tobiaserthal.akgbensheim.subst;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.backend.model.ModelUtils;
import de.tobiaserthal.akgbensheim.backend.preferences.PreferenceProvider;
import de.tobiaserthal.akgbensheim.backend.provider.substitution.SubstitutionCursor;
import de.tobiaserthal.akgbensheim.backend.provider.substitution.SubstitutionSelection;
import de.tobiaserthal.akgbensheim.base.OverlayActivity;

public class SubstDetailActivity extends OverlayActivity<ObservableScrollView>
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final float MAX_TEXT_SCALE_DELTA = 0.25f;

    private View headerView;
    private TextView headerTextView;
    private int flexibleSpaceHeaderHeight;
    private int headerTitleVerticalMargin;

    private SubstitutionCursor subst;
    private TextView txtForm;
    private TextView txtDate;
    private TextView txtType;
    private TextView txtLesson;
    private TextView txtRoom;
    private TextView txtAnnotation;

    private final Interpolator interpolator = new AccelerateDecelerateInterpolator();

    public static final String EXTRA_PARAM_ID = "detail:_id";
    public static final String EXTRA_PARAM_COLOR = "detail:color";

    public static void startDetail(FragmentActivity activity, long id, int color) {
        Intent intent = createOverlayActivity(activity, SubstDetailActivity.class);
        intent.putExtra(EXTRA_PARAM_ID, id);
        intent.putExtra(EXTRA_PARAM_COLOR, color);

        activity.startActivity(intent);
    }

    public static void startDetail(FragmentActivity activity, long id) {
        Intent intent = createOverlayActivity(activity, SubstDetailActivity.class);
        intent.putExtra(EXTRA_PARAM_ID, id);

        activity.startActivity(intent);
    }

    @Override
    public void onStartEnterAnimation() {
        headerTextView.setTranslationY(-headerTitleVerticalMargin);

        float scale = 1 + MAX_TEXT_SCALE_DELTA;
        headerTextView.setPivotX(0);
        headerTextView.setPivotY(headerTextView.getHeight());
        headerTextView.setScaleX(scale);
        headerTextView.setScaleY(scale);

        getScrollable().scrollVerticallyTo(0);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subst_detail);

        setToolbar((Toolbar) findViewById(R.id.toolbar));
        setScrollable((ObservableScrollView) findViewById(R.id.subst_detail_scrollView));
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME);
        }

        headerView = findViewById(R.id.subst_header);
        headerTextView = (TextView) findViewById(R.id.subst_header_title);

        flexibleSpaceHeaderHeight = getResources().getDimensionPixelSize(R.dimen.subst_header_height);
        headerTitleVerticalMargin = getResources().getDimensionPixelSize(R.dimen.subst_header_title_margin);

        setColorScheme(
                getIntent().getIntExtra(
                        EXTRA_PARAM_COLOR,
                        ContextCompat.getColor(this, R.color.primary)
                )
        );

        txtForm         = setupRow(R.id.formRow, R.drawable.ic_account_multiple);
        txtDate         = setupRow(R.id.dateRow, R.drawable.ic_calendar_clock);
        txtType         = setupRow(R.id.typeRow, R.drawable.ic_information_outline);
        txtLesson       = setupRow(R.id.lessonRow, R.drawable.ic_clipboard_outline);
        txtRoom         = setupRow(R.id.roomRow, R.drawable.ic_clipboard_outline);
        txtAnnotation   = setupRow(R.id.annotationRow, R.drawable.ic_message_text_outline);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @SuppressWarnings("deprecation")
    private TextView setupRow(int rowId, int iconRes) {
        TextView row = (TextView) findViewById(rowId);

        Drawable icon;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            icon = getResources().getDrawable(iconRes);
        } else {
            icon = getResources().getDrawable(iconRes, null);
        }

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            row.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
        } else {
            row.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null);
        }

        return row;
    }

    private void setColorScheme(int color) {
        int dark = ColorUtils.compositeColors(
                ContextCompat.getColor(this, R.color.insetsForeground), color);

        headerView.setBackgroundColor(color);
        setRequestedStatusBarTint(dark);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_subst_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_share:
                ModelUtils.startShareIntent(this, subst);
                return true;

            case R.id.action_add_to_calendar:
                ModelUtils.startCalendarIntent(this, subst);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return SubstitutionSelection.get(
                getIntent().getLongExtra(EXTRA_PARAM_ID, 0L)).loader(this, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data == null)
            return;

        SubstitutionCursor oldCursor = subst;
        subst = SubstitutionCursor.wrap(data);

        if(oldCursor != null) {
            oldCursor.close();
        }

        if(!subst.moveToFirst()) {
            return;
        }

        setColorScheme(PreferenceProvider.getInstance()
                .getColorFromType(subst.getType()));

        headerTextView.setText(ModelUtils.summarize(subst));
        txtForm.setText(getResources().getString(R.string.detail_subst_form, subst.getFormKey()));
        txtDate.setText(
                new SimpleDateFormat(
                        getResources().getString(R.string.detail_subst_date, subst.getPeriod()),
                        Locale.getDefault()
                ).format(subst.getSubstDate())
        );

        txtType.setText(subst.getType());
        txtLesson.setText(getResources().getString(R.string.detail_subst_lesson, subst.getLessonSubst(), subst.getLesson()));
        txtRoom.setText(getResources().getString(R.string.detail_subst_room, subst.getRoomSubst(), subst.getRoom()));
        txtAnnotation.setText(
                TextUtils.isEmpty(subst.getAnnotation()) ?
                        getResources().getString(R.string.detail_subst_no_annotation) : subst.getAnnotation()
        );
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        subst.close();
        subst = null;
    }

    @Override
    public void onScrollChanged(int scrollY, boolean b, boolean b1) {
        float flexibleRange = flexibleSpaceHeaderHeight - getToolbar().getHeight();
        float offset = ScrollUtils.getFloat(scrollY, 0, flexibleRange);
        float fraction = interpolator.getInterpolation(offset / flexibleRange);

        // translate the header view
        headerView.setTranslationY(-offset);

        // translate the title
        headerTextView.setTranslationY((fraction - 1) * headerTitleVerticalMargin);

        // scale the header title
        float scale = 1 + (1 - fraction) * MAX_TEXT_SCALE_DELTA;
        headerTextView.setPivotX(0);
        headerTextView.setPivotY(headerTextView.getHeight());
        headerTextView.setScaleX(scale);
        headerTextView.setScaleY(scale);
    }
}
