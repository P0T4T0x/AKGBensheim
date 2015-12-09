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
package de.tobiaserthal.akgbensheim.data.provider.base;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import de.tobiaserthal.akgbensheim.data.Log;

public abstract class BaseContentProvider extends ContentProvider {
    public static final String QUERY_NOTIFY = "QUERY_NOTIFY";
    public static final String QUERY_GROUP_BY = "QUERY_GROUP_BY";
    public static final String QUERY_HAVING = "QUERY_HAVING";
    public static final String QUERY_LIMIT = "QUERY_LIMIT";
    public static final String QUERY_OFFSET = "QUERY_OFFSET";

    public static class QueryParams {
        public String table;
        public String tablesWithJoins;
        public String idColumn;
        public String selection;
        public String orderBy;
    }


    protected abstract QueryParams getQueryParams(Uri uri, String selection, String[] projection);
    protected abstract boolean hasDebug();

    protected abstract SQLiteOpenHelper createSqLiteOpenHelper();

    protected SQLiteOpenHelper mSqLiteOpenHelper;

    @Override
    public final boolean onCreate() {
        if (hasDebug()) {
            // Enable logging of SQL statements as they are executed.
            try {
                Class<?> sqliteDebugClass = Class.forName("android.database.sqlite.SQLiteDebug");
                Field field = sqliteDebugClass.getDeclaredField("DEBUG_SQL_STATEMENTS");
                field.setAccessible(true);
                field.set(null, true);

                field = sqliteDebugClass.getDeclaredField("DEBUG_SQL_TIME");
                field.setAccessible(true);
                field.set(null, true);
            } catch (Exception e) {
                if (hasDebug())
                    Log.w(getClass().getSimpleName(), e, "Could not enable SQLiteDebug logging");
            }
        }
        mSqLiteOpenHelper = createSqLiteOpenHelper();
        return false;
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        String table = uri.getLastPathSegment();
        long rowId = mSqLiteOpenHelper.getWritableDatabase().insertOrThrow(table, null, values);
        if (rowId == -1) return null;

        Context context = getContext();
        if(context != null) {
            boolean notify = getBooleanQueryParameter(uri, QUERY_NOTIFY, true);
            if (notify) {
                context.getContentResolver().notifyChange(uri, null, false);
            }
        }

        return uri.buildUpon().appendEncodedPath(String.valueOf(rowId)).build();
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        String table = uri.getLastPathSegment();
        SQLiteDatabase db = mSqLiteOpenHelper.getWritableDatabase();
        int res = 0;
        db.beginTransaction();
        try {
            for (ContentValues v : values) {
                long id = db.insert(table, null, v);
                db.yieldIfContendedSafely();
                if (id != -1) {
                    res++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        Context context = getContext();
        if(context != null) {
            boolean notify = getBooleanQueryParameter(uri, QUERY_NOTIFY, true);
            if (res != 0 && notify) {
                context.getContentResolver().notifyChange(uri, null, false);
            }
        }

        return res;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        QueryParams queryParams = getQueryParams(uri, selection, null);
        int res = mSqLiteOpenHelper.getWritableDatabase().update(queryParams.table, values, queryParams.selection, selectionArgs);

        Context context = getContext();
        if(context != null) {
            boolean notify = getBooleanQueryParameter(uri, QUERY_NOTIFY, true);
            if (res != 0 && notify) {
                context.getContentResolver().notifyChange(uri, null, false);
            }
        }

        return res;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        QueryParams queryParams = getQueryParams(uri, selection, null);
        int res = mSqLiteOpenHelper.getWritableDatabase().delete(queryParams.table, queryParams.selection, selectionArgs);

        Context context = getContext();
        if(context != null) {
            boolean notify = getBooleanQueryParameter(uri, QUERY_NOTIFY, true);
            if (res != 0 && notify) {
                context.getContentResolver().notifyChange(uri, null, false);
            }
        }

        return res;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String groupBy = uri.getQueryParameter(QUERY_GROUP_BY);
        String having = uri.getQueryParameter(QUERY_HAVING);
        String limit = uri.getQueryParameter(QUERY_LIMIT);
        String offset = uri.getQueryParameter(QUERY_OFFSET);

        QueryParams queryParams = getQueryParams(uri, selection, projection);
        projection = ensureIdIsFullyQualified(projection, queryParams.table, queryParams.idColumn);
        Cursor res = mSqLiteOpenHelper.getReadableDatabase().query(
                queryParams.tablesWithJoins,
                projection,
                queryParams.selection,
                selectionArgs,
                groupBy,
                having,
                (sortOrder == null) ? queryParams.orderBy : sortOrder,
                (offset == null) ? limit : (offset + "," + ((limit == null) ? "-1" : limit))
        );

        Context context = getContext();
        if(context != null) {
            res.setNotificationUri(context.getContentResolver(), uri);
        }

        return res;
    }

    private String[] ensureIdIsFullyQualified(String[] projection, String tableName, String idColumn) {
        if (projection == null) return null;
        String[] res = new String[projection.length];
        for (int i = 0; i < projection.length; i++) {
            if (projection[i].equals(idColumn)) {
                res[i] = tableName + "." + idColumn + " AS " + BaseColumns._ID;
            } else {
                res[i] = projection[i];
            }
        }
        return res;
    }

    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        HashSet<Uri> urisToNotify = new HashSet<>(operations.size());
        for (ContentProviderOperation operation : operations) {
            urisToNotify.add(operation.getUri());
        }

        SQLiteDatabase db = mSqLiteOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            int numOperations = operations.size();
            ContentProviderResult[] results = new ContentProviderResult[numOperations];
            int i = 0;
            for (ContentProviderOperation operation : operations) {
                results[i] = operation.apply(this, results, i);
                if (operation.isYieldAllowed()) {
                    db.yieldIfContendedSafely();
                }
                i++;
            }
            db.setTransactionSuccessful();

            Context context = getContext();
            if(context != null) {
                for (Uri uri : urisToNotify) {
                    context.getContentResolver().notifyChange(uri, null, false);
                }
            }

            return results;
        } finally {
            db.endTransaction();
        }
    }


    public static Uri notify(Uri uri, boolean notify) {
        return uri.buildUpon().appendQueryParameter(QUERY_NOTIFY, String.valueOf(notify)).build();
    }

    public static Uri groupBy(Uri uri, String groupBy) {
        return uri.buildUpon().appendQueryParameter(QUERY_GROUP_BY, groupBy).build();
    }

    public static Uri having(Uri uri, String having) {
        return uri.buildUpon().appendQueryParameter(QUERY_HAVING, having).build();
    }

    public static Uri limit(Uri uri, String limit) {
        return uri.buildUpon().appendQueryParameter(QUERY_LIMIT, limit).build();
    }

    public static Uri offset(Uri uri, String offset) {
        return uri.buildUpon().appendQueryParameter(QUERY_OFFSET, offset).build();
    }

    private static boolean getBooleanQueryParameter(Uri uri, String key, boolean defaultValue) {
        String flag = uri.getQueryParameter(key);
        if (flag == null) {
            return defaultValue;
        }
        flag = flag.toLowerCase(Locale.ROOT);
        return (!"false".equals(flag) && !"0".equals(flag));
    }
}
