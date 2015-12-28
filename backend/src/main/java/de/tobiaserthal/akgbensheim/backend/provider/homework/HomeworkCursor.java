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
package de.tobiaserthal.akgbensheim.backend.provider.homework;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import de.tobiaserthal.akgbensheim.backend.model.homework.HomeworkModel;
import de.tobiaserthal.akgbensheim.backend.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code homework} table.
 */
public class HomeworkCursor extends AbstractCursor implements HomeworkModel {

    public static HomeworkCursor wrap(Cursor cursor) {
        assert cursor != null;
        return new HomeworkCursor(cursor);
    }

    public HomeworkCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(HomeworkColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The title to display. Usually contains the subject.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getTitle() {
        String res = getStringOrNull(HomeworkColumns.TITLE);
        if (res == null)
            throw new NullPointerException("The value of 'title' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The date until the work has to be done.
     * Cannot be {@code null}.
     */
    @NonNull
    public Date getTodoDate() {
        Date res = getDateOrNull(HomeworkColumns.TODODATE);
        if (res == null)
            throw new NullPointerException("The value of 'tododate' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The notes that describe what to do.
     * Can be {@code null}.
     */
    @Nullable
    public String getNotes() {
        return getStringOrNull(HomeworkColumns.NOTES);
    }

    /**
     * Whether the work has been done.
     */
    public boolean getDone() {
        Boolean res = getBooleanOrNull(HomeworkColumns.DONE);
        if (res == null)
            throw new NullPointerException("The value of 'done' in the database was null, which is not allowed according to the model definition");
        return res;
    }
}
