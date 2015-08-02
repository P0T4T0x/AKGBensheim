/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Tobias Erthal
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.tobiaserthal.akgbensheim.data.provider;

import java.util.Arrays;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;

import de.tobiaserthal.akgbensheim.data.Log;
import de.tobiaserthal.akgbensheim.data.BuildConfig;
import de.tobiaserthal.akgbensheim.data.provider.base.BaseContentProvider;
import de.tobiaserthal.akgbensheim.data.provider.event.EventColumns;
import de.tobiaserthal.akgbensheim.data.provider.homework.HomeworkColumns;
import de.tobiaserthal.akgbensheim.data.provider.news.NewsColumns;
import de.tobiaserthal.akgbensheim.data.provider.substitution.SubstitutionColumns;
import de.tobiaserthal.akgbensheim.data.provider.teacher.TeacherColumns;

public class DataProvider extends BaseContentProvider {
    private static final String TAG = DataProvider.class.getSimpleName();

    private static final String TYPE_CURSOR_ITEM = "vnd.android.cursor.item/";
    private static final String TYPE_CURSOR_DIR = "vnd.android.cursor.dir/";

    public static final String AUTHORITY = "de.tobiaserthal.akgbensheim.data.provider";
    public static final String CONTENT_URI_BASE = "content://" + AUTHORITY;

    private static final int URI_TYPE_EVENT = 0;
    private static final int URI_TYPE_EVENT_ID = 1;

    private static final int URI_TYPE_HOMEWORK = 2;
    private static final int URI_TYPE_HOMEWORK_ID = 3;

    private static final int URI_TYPE_NEWS = 4;
    private static final int URI_TYPE_NEWS_ID = 5;

    private static final int URI_TYPE_SUBSTITUTION = 6;
    private static final int URI_TYPE_SUBSTITUTION_ID = 7;

    private static final int URI_TYPE_TEACHER = 8;
    private static final int URI_TYPE_TEACHER_ID = 9;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, EventColumns.TABLE_NAME, URI_TYPE_EVENT);
        URI_MATCHER.addURI(AUTHORITY, EventColumns.TABLE_NAME + "/#", URI_TYPE_EVENT_ID);
        URI_MATCHER.addURI(AUTHORITY, HomeworkColumns.TABLE_NAME, URI_TYPE_HOMEWORK);
        URI_MATCHER.addURI(AUTHORITY, HomeworkColumns.TABLE_NAME + "/#", URI_TYPE_HOMEWORK_ID);
        URI_MATCHER.addURI(AUTHORITY, NewsColumns.TABLE_NAME, URI_TYPE_NEWS);
        URI_MATCHER.addURI(AUTHORITY, NewsColumns.TABLE_NAME + "/#", URI_TYPE_NEWS_ID);
        URI_MATCHER.addURI(AUTHORITY, SubstitutionColumns.TABLE_NAME, URI_TYPE_SUBSTITUTION);
        URI_MATCHER.addURI(AUTHORITY, SubstitutionColumns.TABLE_NAME + "/#", URI_TYPE_SUBSTITUTION_ID);
        URI_MATCHER.addURI(AUTHORITY, TeacherColumns.TABLE_NAME, URI_TYPE_TEACHER);
        URI_MATCHER.addURI(AUTHORITY, TeacherColumns.TABLE_NAME + "/#", URI_TYPE_TEACHER_ID);
    }

    @Override
    protected SQLiteOpenHelper createSqLiteOpenHelper() {
        return DatabaseManager.getInstance(getContext());
    }

    @Override
    protected boolean hasDebug() {
        return BuildConfig.DEBUG;
    }

    @Override
    public String getType(Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case URI_TYPE_EVENT:
                return TYPE_CURSOR_DIR + EventColumns.TABLE_NAME;
            case URI_TYPE_EVENT_ID:
                return TYPE_CURSOR_ITEM + EventColumns.TABLE_NAME;

            case URI_TYPE_HOMEWORK:
                return TYPE_CURSOR_DIR + HomeworkColumns.TABLE_NAME;
            case URI_TYPE_HOMEWORK_ID:
                return TYPE_CURSOR_ITEM + HomeworkColumns.TABLE_NAME;

            case URI_TYPE_NEWS:
                return TYPE_CURSOR_DIR + NewsColumns.TABLE_NAME;
            case URI_TYPE_NEWS_ID:
                return TYPE_CURSOR_ITEM + NewsColumns.TABLE_NAME;

            case URI_TYPE_SUBSTITUTION:
                return TYPE_CURSOR_DIR + SubstitutionColumns.TABLE_NAME;
            case URI_TYPE_SUBSTITUTION_ID:
                return TYPE_CURSOR_ITEM + SubstitutionColumns.TABLE_NAME;

            case URI_TYPE_TEACHER:
                return TYPE_CURSOR_DIR + TeacherColumns.TABLE_NAME;
            case URI_TYPE_TEACHER_ID:
                return TYPE_CURSOR_ITEM + TeacherColumns.TABLE_NAME;

            default:
                return null;

        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (hasDebug()) {
            Log.d(TAG, "insert uri: %s, values: %s", uri.toString(), values.toString());
        }

        return super.insert(uri, values);
    }

    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
        if (hasDebug()) {
            Log.d(TAG, "bulkInsert uri: %s, values.length: %d", uri.toString(), values.length);
        }

        return super.bulkInsert(uri, values);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (hasDebug()) {
            Log.d(TAG, "update with uri: %s, values: %s, selection: %s, selectionArgs: %s",
                    uri.toString(), values.toString(), selection, Arrays.toString(selectionArgs));
        }

        return super.update(uri, values, selection, selectionArgs);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (hasDebug()) {
            Log.d(TAG, "delete with uri: %s, selection: %s, selectionArgs: %s", uri.toString(),
                    selection, Arrays.toString(selectionArgs));
        }

        return super.delete(uri, selection, selectionArgs);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (hasDebug()) {
            Log.d(TAG, "query with uri: %s, selection: %s, selectionArgs: %s, sortOrder: %s, groupBy: %s, having: %s, limit: %s",
                    uri.toString(), selection, Arrays.toString(selectionArgs), sortOrder, uri.getQueryParameter(QUERY_GROUP_BY),
                    uri.getQueryParameter(QUERY_HAVING), uri.getQueryParameter(QUERY_LIMIT));
        }

        return super.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected QueryParams getQueryParams(Uri uri, String selection, String[] projection) {
        QueryParams res = new QueryParams();
        String id = null;
        int matchedId = URI_MATCHER.match(uri);
        switch (matchedId) {
            case URI_TYPE_EVENT:
            case URI_TYPE_EVENT_ID:
                res.table = EventColumns.TABLE_NAME;
                res.idColumn = EventColumns._ID;
                res.tablesWithJoins = EventColumns.TABLE_NAME;
                res.orderBy = EventColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_HOMEWORK:
            case URI_TYPE_HOMEWORK_ID:
                res.table = HomeworkColumns.TABLE_NAME;
                res.idColumn = HomeworkColumns._ID;
                res.tablesWithJoins = HomeworkColumns.TABLE_NAME;
                res.orderBy = HomeworkColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_NEWS:
            case URI_TYPE_NEWS_ID:
                res.table = NewsColumns.TABLE_NAME;
                res.idColumn = NewsColumns._ID;
                res.tablesWithJoins = NewsColumns.TABLE_NAME;
                res.orderBy = NewsColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_SUBSTITUTION:
            case URI_TYPE_SUBSTITUTION_ID:
                res.table = SubstitutionColumns.TABLE_NAME;
                res.idColumn = SubstitutionColumns._ID;
                res.tablesWithJoins = SubstitutionColumns.TABLE_NAME;
                res.orderBy = SubstitutionColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_TEACHER:
            case URI_TYPE_TEACHER_ID:
                res.table = TeacherColumns.TABLE_NAME;
                res.idColumn = TeacherColumns._ID;
                res.tablesWithJoins = TeacherColumns.TABLE_NAME;
                res.orderBy = TeacherColumns.DEFAULT_ORDER;
                break;

            default:
                throw new IllegalArgumentException("The uri '" + uri + "' is not supported by this ContentProvider");
        }

        switch (matchedId) {
            case URI_TYPE_EVENT_ID:
            case URI_TYPE_HOMEWORK_ID:
            case URI_TYPE_NEWS_ID:
            case URI_TYPE_SUBSTITUTION_ID:
            case URI_TYPE_TEACHER_ID:
                id = uri.getLastPathSegment();
        }

        if (id != null) {
            if (selection != null) {
                res.selection = res.table + "." + res.idColumn + "=" + id + " and (" + selection + ")";
            } else {
                res.selection = res.table + "." + res.idColumn + "=" + id;
            }
        } else {
            res.selection = selection;
        }

        return res;
    }
}
