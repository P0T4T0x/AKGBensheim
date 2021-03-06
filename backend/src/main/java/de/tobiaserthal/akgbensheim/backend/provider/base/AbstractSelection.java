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
package de.tobiaserthal.akgbensheim.backend.provider.base;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class AbstractSelection<T extends AbstractSelection<?>> {
    private static final String EQ = "=?";
    private static final String PAREN_OPEN = "(";
    private static final String PAREN_CLOSE = ")";
    private static final String AND = " AND ";
    private static final String OR = " OR ";
    private static final String IS_NULL = " IS NULL";
    private static final String IS_NOT_NULL = " IS NOT NULL";
    private static final String IN = " IN (";
    private static final String NOT_IN = " NOT IN (";
    private static final String COMMA = ",";
    private static final String GT = ">?";
    private static final String LT = "<?";
    private static final String GT_EQ = ">=?";
    private static final String LT_EQ = "<=?";
    private static final String NOT_EQ = "<>?";
    private static final String LIKE = " LIKE ?";
    private static final String CONTAINS = " LIKE '%' || ? || '%'";
    private static final String STARTS = " LIKE ? || '%'";
    private static final String ENDS = " LIKE '%' || ?";
    private static final String NOT_LIKE = " NOT LIKE ?";
    private static final String NOT_CONTAINS = " NOT LIKE '%' || ? || '%'";
    private static final String NOT_STARTS = " NOT LIKE ? || '%'";
    private static final String NOT_ENDS = " LIKE '%' || ?";

    private final StringBuilder mSelection = new StringBuilder();
    private final List<String> mSelectionArgs = new ArrayList<>(5);

    Boolean mNotify;
    String mGroupBy;
    String mHaving;
    String mOrderBy;
    Integer mLimit;
    Integer mOffset;

    protected void addEquals(String column, Object[] value) {
        mSelection.append(column);

        if (value == null) {
            // Single null value
            mSelection.append(IS_NULL);
        } else if (value.length > 1) {
            // Multiple values ('in' clause)
            mSelection.append(IN);
            for (int i = 0; i < value.length; i++) {
                mSelection.append("?");
                if (i < value.length - 1) {
                    mSelection.append(COMMA);
                }
                mSelectionArgs.add(valueOf(value[i]));
            }
            mSelection.append(PAREN_CLOSE);
        } else {
            // Single value
            if (value[0] == null) {
                // Single null value
                mSelection.append(IS_NULL);
            } else {
                // Single not null value
                mSelection.append(EQ);
                mSelectionArgs.add(valueOf(value[0]));
            }
        }
    }

    protected void addNotEquals(String column, Object[] value) {
        mSelection.append(column);

        if (value == null) {
            // Single null value
            mSelection.append(IS_NOT_NULL);
        } else if (value.length > 1) {
            // Multiple values ('in' clause)
            mSelection.append(NOT_IN);
            for (int i = 0; i < value.length; i++) {
                mSelection.append("?");
                if (i < value.length - 1) {
                    mSelection.append(COMMA);
                }
                mSelectionArgs.add(valueOf(value[i]));
            }
            mSelection.append(PAREN_CLOSE);
        } else {
            // Single value
            if (value[0] == null) {
                // Single null value
                mSelection.append(IS_NOT_NULL);
            } else {
                // Single not null value
                mSelection.append(NOT_EQ);
                mSelectionArgs.add(valueOf(value[0]));
            }
        }
    }

    protected void addLike(String column, String[] values) {
        mSelection.append(PAREN_OPEN);
        for (int i = 0; i < values.length; i++) {
            mSelection.append(column);
            mSelection.append(LIKE);
            mSelectionArgs.add(values[i]);
            if (i < values.length - 1) {
                mSelection.append(OR);
            }
        }
        mSelection.append(PAREN_CLOSE);
    }

    protected void addNotLike(String column, String[] values) {
        mSelection.append(PAREN_OPEN);
        for (int i = 0; i < values.length; i++) {
            mSelection.append(column);
            mSelection.append(NOT_LIKE);
            mSelectionArgs.add(values[i]);
            if (i < values.length - 1) {
                mSelection.append(AND);
            }
        }
        mSelection.append(PAREN_CLOSE);
    }

    protected void addContains(String column, String[] values) {
        mSelection.append(PAREN_OPEN);
        for (int i = 0; i < values.length; i++) {
            mSelection.append(column);
            mSelection.append(CONTAINS);
            mSelectionArgs.add(values[i]);
            if (i < values.length - 1) {
                mSelection.append(OR);
            }
        }
        mSelection.append(PAREN_CLOSE);
    }

    protected void addNotContains(String column, String[] values) {
        mSelection.append(PAREN_OPEN);
        for (int i = 0; i < values.length; i++) {
            mSelection.append(column);
            mSelection.append(NOT_CONTAINS);
            mSelectionArgs.add(values[i]);
            if (i < values.length - 1) {
                mSelection.append(AND);
            }
        }
        mSelection.append(PAREN_CLOSE);
    }

    protected void addStartsWith(String column, String[] values) {
        mSelection.append(PAREN_OPEN);
        for (int i = 0; i < values.length; i++) {
            mSelection.append(column);
            mSelection.append(STARTS);
            mSelectionArgs.add(values[i]);
            if (i < values.length - 1) {
                mSelection.append(OR);
            }
        }
        mSelection.append(PAREN_CLOSE);
    }

    protected void addNotStartsWith(String column, String[] values) {
        mSelection.append(PAREN_OPEN);
        for (int i = 0; i < values.length; i++) {
            mSelection.append(column);
            mSelection.append(NOT_STARTS);
            mSelectionArgs.add(values[i]);
            if (i < values.length - 1) {
                mSelection.append(AND);
            }
        }
        mSelection.append(PAREN_CLOSE);
    }

    protected void addEndsWith(String column, String[] values) {
        mSelection.append(PAREN_OPEN);
        for (int i = 0; i < values.length; i++) {
            mSelection.append(column);
            mSelection.append(ENDS);
            mSelectionArgs.add(values[i]);
            if (i < values.length - 1) {
                mSelection.append(OR);
            }
        }
        mSelection.append(PAREN_CLOSE);
    }

    protected void addNotEndsWith(String column, String[] values) {
        mSelection.append(PAREN_OPEN);
        for (int i = 0; i < values.length; i++) {
            mSelection.append(column);
            mSelection.append(NOT_ENDS);
            mSelectionArgs.add(values[i]);
            if (i < values.length - 1) {
                mSelection.append(AND);
            }
        }
        mSelection.append(PAREN_CLOSE);
    }

    protected void addGreaterThan(String column, Object value) {
        mSelection.append(column);
        mSelection.append(GT);
        mSelectionArgs.add(valueOf(value));
    }

    protected void addGreaterThanOrEquals(String column, Object value) {
        mSelection.append(column);
        mSelection.append(GT_EQ);
        mSelectionArgs.add(valueOf(value));
    }

    protected void addLessThan(String column, Object value) {
        mSelection.append(column);
        mSelection.append(LT);
        mSelectionArgs.add(valueOf(value));
    }

    protected void addLessThanOrEquals(String column, Object value) {
        mSelection.append(column);
        mSelection.append(LT_EQ);
        mSelectionArgs.add(valueOf(value));
    }

    public void addRaw(String raw, Object... args) {
        mSelection.append(" ");
        mSelection.append(raw);
        mSelection.append(" ");
        for (Object arg : args) {
            mSelectionArgs.add(valueOf(arg));
        }
    }

    private String valueOf(Object obj) {
        if (obj instanceof Date) {
            return String.valueOf(((Date) obj).getTime());
        } else if (obj instanceof Boolean) {
            return (Boolean) obj ? "1" : "0";
        } else if (obj instanceof Enum) {
            return String.valueOf(((Enum<?>) obj).ordinal());
        }
        return String.valueOf(obj);
    }

    @SuppressWarnings("unchecked")
    public T openParen() {
        mSelection.append(PAREN_OPEN);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T closeParen() {
        mSelection.append(PAREN_CLOSE);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T and() {
        mSelection.append(AND);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T or() {
        mSelection.append(OR);
        return (T) this;
    }


    protected Object[] toObjectArray(int... array) {
        Object[] res = new Object[array.length];
        for (int i = 0; i < array.length; i++) {
            res[i] = array[i];
        }
        return res;
    }

    protected Object[] toObjectArray(long... array) {
        Object[] res = new Object[array.length];
        for (int i = 0; i < array.length; i++) {
            res[i] = array[i];
        }
        return res;
    }

    protected Object[] toObjectArray(float... array) {
        Object[] res = new Object[array.length];
        for (int i = 0; i < array.length; i++) {
            res[i] = array[i];
        }
        return res;
    }

    protected Object[] toObjectArray(double... array) {
        Object[] res = new Object[array.length];
        for (int i = 0; i < array.length; i++) {
            res[i] = array[i];
        }
        return res;
    }

    protected Object[] toObjectArray(Boolean value) {
        return new Object[] { value };
    }


    /**
     * Returns the selection produced by this object.
     */
    public String sel() {
        return mSelection.toString();
    }

    /**
     * Returns the selection arguments produced by this object.
     */
    public String[] args() {
        int size = mSelectionArgs.size();
        if (size == 0) return null;
        return mSelectionArgs.toArray(new String[size]);
    }

    public String order() {
        return mOrderBy;
    }

    /**
     * Returns the {@code uri} argument to pass to the {@code ContentResolver} methods.
     */
    public Uri uri() {
        Uri uri = baseUri();
        if (mNotify != null) uri = BaseContentProvider.notify(uri, mNotify);
        if (mGroupBy != null) uri = BaseContentProvider.groupBy(uri, mGroupBy);
        if (mHaving != null) uri = BaseContentProvider.having(uri, mHaving);
        if (mLimit != null) uri = BaseContentProvider.limit(uri, String.valueOf(mLimit));
        if (mOffset != null) uri = BaseContentProvider.offset(uri, String.valueOf(mOffset));
        return uri;
    }

    protected abstract Uri baseUri();
    protected int count(ContentResolver contentResolver, String countColumn) {
        Cursor cursor = contentResolver.query(
                uri(),
                new String[] {"count(" + countColumn + ")"},
                sel(),
                args(),
                null
        );

        int count = 0;

        if(cursor == null)
            return count;

        if(cursor.moveToFirst())
            count = cursor.getInt(0);

        cursor.close();
        return count;
    }

    /**
     * Deletes row(s) specified by this selection.
     *
     * @param contentResolver The content resolver to use.
     * @return The number of rows deleted.
     */
    public int delete(ContentResolver contentResolver) {
        return contentResolver.delete(uri(), sel(), args());
    }

    /**
     * Returns the number of rows that can be queried by the current selection.
     * @param resolver The content resolver to use.
     * @return The number of entries in the database
     */
    public abstract int count(ContentResolver resolver);

    public CursorLoader loader(Context context, String[] projection) {
        return new CursorLoader(context, uri(), projection, sel(), args(), order());
    }

    @SuppressWarnings("unchecked")
    public T notify(boolean notify) {
        mNotify = notify;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T groupBy(String groupBy) {
        mGroupBy = groupBy;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T having(String having) {
        mHaving = having;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T limit(int limit) {
        mLimit = limit;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T offset(int offset) {
        mOffset = offset;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T orderBy(String order, boolean caseSensitive, boolean desc) {
        mOrderBy = order
                + (caseSensitive ? "" : " COLLATE NOCASE ")
                + (desc ? " DESC" : "");
        return (T) this;
    }
}
