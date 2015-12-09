package de.tobiaserthal.akgbensheim.homework;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.data.SimpleAsyncQueryHandler;
import de.tobiaserthal.akgbensheim.data.provider.homework.HomeworkContentValues;
import de.tobiaserthal.akgbensheim.data.provider.homework.HomeworkCursor;
import de.tobiaserthal.akgbensheim.data.provider.homework.HomeworkSelection;
import de.tobiaserthal.akgbensheim.ui.OverlayActivity;
import de.tobiaserthal.akgbensheim.ui.widget.DatePickerFragment;

public class HomeworkEditActivity extends OverlayActivity<ObservableScrollView>
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String EXTRA_PARAM_ID = "_id";
    private static final DateFormat BTN_FORMAT = SimpleDateFormat.getDateInstance(
            DateFormat.LONG, Locale.getDefault());

    private HomeworkCursor homework;
    private HomeworkContentValues values;

    private Button btnDate;
    private EditText txtTitle;
    private EditText txtNotes;
    private boolean valid = false;

    public static void startDetail(FragmentActivity activity, long id) {
        Intent intent = createOverlayActivity(activity, HomeworkEditActivity.class);
        intent.putExtra(EXTRA_PARAM_ID, id);

        activity.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework_edit);

        setToolbar((Toolbar) findViewById(R.id.toolbar));
        setScrollable((ObservableScrollView) findViewById(R.id.homework_edit_scrollView));
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(
                    ActionBar.DISPLAY_HOME_AS_UP
                            | ActionBar.DISPLAY_SHOW_TITLE);
        }

        btnDate = (Button) findViewById(R.id.btnDate);
        txtTitle = ((TextInputLayout) findViewById(R.id.txtTitle)).getEditText();
        txtNotes = ((TextInputLayout) findViewById(R.id.txtNotes)).getEditText();

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment.newInstance(homework.getTodoDate())
                        .setDateListener(new DatePickerFragment.DateListener() {
                            @Override
                            public void onDateSelected(Date date) {
                                values.putTodoDate(date);
                                btnDate.setText(format(date));

                                saveHomework();
                            }
                        })
                        .show(getSupportFragmentManager(), "datePicker");
            }
        });

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_homework_edit, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_done);

        if(homework != null && homework.moveToFirst()) {
            item.setChecked(homework.getDone());
        }

        updateMenuIcon(item, R.drawable.ic_checkbox_circle);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_done:
                values.putDone(!item.isChecked());
                saveHomework();
                return true;

            case R.id.action_delete:
                valid = false;
                deleteHomework();
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
    public void onPause() {
        if(valid) {
            saveHomework();
        }

        super.onPause();
    }

    public void deleteHomework() {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.action_delete_homework_title, homework.getTitle()))
                .content(R.string.action_delete_homework_content)
                .negativeText(R.string.action_delete)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        HomeworkSelection selection = HomeworkSelection.get(homework.getId());
                        new SimpleAsyncQueryHandler(getContentResolver()).startDelete(selection);

                        finish();
                    }
                })
                .neutralText(android.R.string.cancel)
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .build()
                .show();

    }

    public void saveHomework() {
        String title = txtTitle.getText().toString();
        String notes = txtNotes.getText().toString();

        if(!TextUtils.isEmpty(title)) {
            values.putTitle(title);
        }

        if(!TextUtils.isEmpty(notes)) {
            values.putNotes(notes);
        }

        if(!values.valueSet().isEmpty()) {
            HomeworkSelection selection = HomeworkSelection.get(homework.getId());
            new SimpleAsyncQueryHandler(getContentResolver()).startUpdate(selection, values);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return HomeworkSelection.get(
                getIntent().getLongExtra(EXTRA_PARAM_ID, 0L)).loader(this, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data == null) {
            valid = false;
            return;
        }

        HomeworkCursor oldCursor = homework;
        homework = HomeworkCursor.wrap(data);

        if(oldCursor != null) {
            oldCursor.close();
        }

        if(!homework.moveToFirst()) {
            valid = false;
            return;
        }

        valid = true;
        supportInvalidateOptionsMenu();

        if(values != null) {
            values.clear();
        } else {
            values = new HomeworkContentValues();
        }

        String dummyTitle = getString(R.string.homework_dummy_title);
        String dummyNotes = getString(R.string.homework_dummy_notes);

        String title = homework.getTitle();
        String notes = homework.getNotes();

        if(!dummyTitle.equalsIgnoreCase(title)) {
            txtTitle.setText(homework.getTitle());
        }

        if(!dummyNotes.equalsIgnoreCase(notes)) {
            txtNotes.setText(homework.getNotes());
        }

        btnDate.setText(format(homework.getTodoDate()));
    }

    private static String format(Date date) {
        return BTN_FORMAT.format(date);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        values.clear();
        homework.close();

        values = null;
        homework = null;

        valid = false;
    }
}
