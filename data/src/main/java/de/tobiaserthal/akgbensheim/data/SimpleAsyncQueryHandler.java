package de.tobiaserthal.akgbensheim.data;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import de.tobiaserthal.akgbensheim.data.provider.base.AbstractContentValues;
import de.tobiaserthal.akgbensheim.data.provider.base.AbstractSelection;

public class SimpleAsyncQueryHandler extends AsyncQueryHandler {
    public static final String TAG = "SimpleAsyncQueryHandler";
    public SimpleAsyncQueryHandler(ContentResolver cr) {
        super(cr);
    }

    public void startQuery(AbstractSelection selection, String[] projection) {
        startQuery(
                1,
                null,
                selection.uri(),
                projection,
                selection.sel(),
                selection.args(),
                selection.order()
        );
    }

    public void startInsert(AbstractContentValues values) {
        startInsert(
                1,
                null,
                values.uri(),
                values.values()
        );
    }

    public void startUpdate(AbstractSelection selection, AbstractContentValues values) {
        startUpdate(
                1,
                null,
                values.uri(),
                values.values(),
                selection.sel(),
                selection.args()
        );
    }

    public void startDelete(AbstractSelection selection) {
        startDelete(
                1,
                null,
                selection.uri(),
                selection.sel(),
                selection.args()
        );
    }

    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        Log.d(TAG, "Completed asynchronous query with resulting rows: %d", cursor.getCount());
    }

    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        Log.d(TAG, "Completed asynchronous insert with resulting path: %s", uri.toString());
    }

    protected void onUpdateComplete(int token, Object cookie, int result) {
        Log.d(TAG, "Completed asynchronous update with affected rows: %d", result);
    }

    protected void onDeleteComplete(int token, Object cookie, int result) {
        Log.d(TAG, "Completed asynchronous deletion with affected rows: %d", result);
    }
}
