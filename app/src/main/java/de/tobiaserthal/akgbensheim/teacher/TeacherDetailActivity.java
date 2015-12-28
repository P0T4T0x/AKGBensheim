package de.tobiaserthal.akgbensheim.teacher;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.backend.model.ModelUtils;
import de.tobiaserthal.akgbensheim.backend.provider.teacher.TeacherCursor;
import de.tobiaserthal.akgbensheim.backend.provider.teacher.TeacherSelection;
import de.tobiaserthal.akgbensheim.base.OverlayActivity;
import de.tobiaserthal.akgbensheim.utils.widget.BackdropImageView;

public class TeacherDetailActivity extends OverlayActivity<ObservableScrollView>
        implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private View headerView;
    private TextView headerTitle;
    private TextView headerSubtitle;

    private TeacherCursor teacher;
    private BackdropImageView imageView;
    private TextView nameView;
    private TextView shorthandView;
    private TextView subjectsView;
    private TextView mailView;
    private TextView addView;

    private int actionBarTitleMargin;
    private int flexibleSpaceShowFABOffset;
    private int flexibleSpaceImageHeight;

    private int statusBarColor = Color.BLACK;

    private final Interpolator materialInterpolator = new AccelerateDecelerateInterpolator();
    private final ArgbEvaluator colorInterpolator = new ArgbEvaluator();

    public static final String EXTRA_PARAM_ID = "detail:_id";
    private static final float MAX_TEXT_SCALE_DELTA = 0.25f;

    public static void startDetail(FragmentActivity activity, long id) {
        Intent intent = createOverlayActivity(activity, TeacherDetailActivity.class);
        intent.putExtra(EXTRA_PARAM_ID, id);

        activity.startActivity(intent);
    }

    @Override
    public void onStartEnterAnimation() {
        float scale = 1 + MAX_TEXT_SCALE_DELTA;
        headerTitle.setPivotX(0);
        headerTitle.setPivotY(headerTitle.getHeight());
        headerTitle.setScaleX(scale);
        headerTitle.setScaleY(scale);
        headerSubtitle.setAlpha(0);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_detail);

        setToolbar((Toolbar) findViewById(R.id.toolbar));
        setScrollable((ObservableScrollView) findViewById(R.id.teacher_detail_scrollView));
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME);
        }

        headerView = findViewById(R.id.teacher_header);
        headerTitle = (TextView) findViewById(R.id.teacher_header_title);
        headerSubtitle = (TextView) findViewById(R.id.teacher_header_subtitle);

        imageView = (BackdropImageView) findViewById(R.id.teacher_header_imageView);
        imageView.setFactor(0.25f);
        imageView.setScrimColor(ContextCompat.getColor(this, R.color.primary));
        statusBarColor = ContextCompat.getColor(this, R.color.primaryDark);

        nameView        = setupRow(R.id.nameRow, R.drawable.ic_information_outline, R.string.detail_title_teacherName);
        shorthandView   = setupRow(R.id.shorthandRow, R.drawable.ic_information_outline, R.string.detail_title_teacherShorthand);
        subjectsView    = setupRow(R.id.subjectsRow, R.drawable.ic_information_outline, R.string.detail_title_teacherSubjects);

        mailView    = setupFuncRow(R.id.mailRow, R.drawable.ic_email, this);
        addView     = setupFuncRow(R.id.addRow, R.drawable.ic_account_plus, this);

        flexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        flexibleSpaceShowFABOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
        actionBarTitleMargin = getResources().getDimensionPixelSize(R.dimen.flexible_space_header_margin_left);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    private TextView setupRow(int rowId, int iconRes, int titleRes) {
        View row = findViewById(rowId);
        ((ImageView) row.findViewById(android.R.id.icon)).setImageResource(iconRes);
        ((TextView) row.findViewById(android.R.id.text1)).setText(titleRes);

        return (TextView) row.findViewById(android.R.id.text2);
    }

    private TextView setupFuncRow(int rowId, int iconRes, View.OnClickListener listener) {
        View row = findViewById(rowId);
        row.setClickable(true);
        row.setOnClickListener(listener);

        ((ImageView) row.findViewById(android.R.id.icon)).setImageResource(iconRes);
        return (TextView) row.findViewById(android.R.id.text1);
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
            case R.id.mailRow:
                ModelUtils.startMailIntent(this, teacher);
                break;

            case R.id.addRow:
                ModelUtils.startContactsIntent(this, teacher);
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return TeacherSelection.get(
                getIntent().getLongExtra(EXTRA_PARAM_ID, 0L)).loader(this, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data == null)
            return;

        TeacherCursor oldCursor = teacher;
        teacher = TeacherCursor.wrap(data);

        if(oldCursor != null) {
            oldCursor.close();
        }

        if(!teacher.moveToFirst()) {
            return;
        }

        String name = getString(
                R.string.detail_teacher_name,
                teacher.getFirstName(),
                teacher.getLastName()
        );

        nameView.setText(name);
        headerTitle.setText(name);

        headerSubtitle.setText(teacher.getSubjects());
        subjectsView.setText(teacher.getSubjects());

        shorthandView.setText(teacher.getShorthand());

        mailView.setText(teacher.getEmail());
        addView.setText(R.string.action_title_addToContacts);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        teacher.close();
        teacher = null;
    }

    @Override
    public void onScrollChanged(int scrollY, boolean b, boolean b1) {
        float flexibleRange = flexibleSpaceImageHeight - getToolbar().getHeight();
        float offset = ScrollUtils.getFloat(scrollY, 0, flexibleRange);
        float fraction = materialInterpolator.getInterpolation(offset / flexibleRange);

        // translate the whole header view
        headerView.setTranslationY(-offset);

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
        headerTitle.setTranslationX(titleMargin);
        headerSubtitle.setTranslationX(titleMargin);

        // scale the header title
        float scale = 1 + (1 - fraction) * MAX_TEXT_SCALE_DELTA;
        headerTitle.setPivotX(0);
        headerTitle.setPivotY(headerTitle.getHeight());
        headerTitle.setScaleX(scale);
        headerTitle.setScaleY(scale);

        // fade in/out the subtitle text view
        float fadingScrollY = scrollY - flexibleSpaceShowFABOffset;
        float fadingRange = flexibleRange - flexibleSpaceShowFABOffset;
        float fadingOffset = ScrollUtils.getFloat(fadingScrollY, 0, fadingRange);
        float fadingFraction = materialInterpolator.getInterpolation(fadingOffset / fadingRange);
        headerSubtitle.setAlpha(fadingFraction);
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }
}
