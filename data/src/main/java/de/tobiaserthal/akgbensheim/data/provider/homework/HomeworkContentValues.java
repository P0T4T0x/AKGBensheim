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
package de.tobiaserthal.akgbensheim.data.provider.homework;

import java.util.Date;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.tobiaserthal.akgbensheim.data.model.homework.HomeworkModelBuilder;
import de.tobiaserthal.akgbensheim.data.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code homework} table.
 */
public class HomeworkContentValues extends AbstractContentValues implements HomeworkModelBuilder<HomeworkContentValues>{

    @Override
    public Uri uri() {
        return HomeworkColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable HomeworkSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    @Override
    public HomeworkContentValues putId(long id) {
        put(HomeworkColumns._ID, id);
        return this;
    }

    @Override
    public HomeworkContentValues putTitle(@NonNull String value) {
        put(HomeworkColumns.TITLE, value);
        return this;
    }

    @Override
    public HomeworkContentValues putTodoDate(@NonNull Date value) {
        put(HomeworkColumns.TODODATE, value.getTime());
        return this;
    }

    public HomeworkContentValues putTodoDate(long value) {
        put(HomeworkColumns.TODODATE, value);
        return this;
    }

    @Override
    public HomeworkContentValues putNotes(@Nullable String value) {
        put(HomeworkColumns.NOTES, value);
        return this;
    }

    public HomeworkContentValues putNotesNull() {
        putNull(HomeworkColumns.NOTES);
        return this;
    }

    @Override
    public HomeworkContentValues putDone(boolean value) {
        put(HomeworkColumns.DONE, value);
        return this;
    }
}
