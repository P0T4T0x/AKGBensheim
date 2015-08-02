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
package de.tobiaserthal.akgbensheim.data.provider.teacher;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import de.tobiaserthal.akgbensheim.data.model.teacher.TeacherModel;
import de.tobiaserthal.akgbensheim.data.model.teacher.TeacherModelBuilder;
import de.tobiaserthal.akgbensheim.data.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code teacher} table.
 */
public class TeacherContentValues extends AbstractContentValues implements TeacherModelBuilder<TeacherContentValues> {

    public static TeacherContentValues wrap(TeacherModel model) {
        TeacherContentValues result = new TeacherContentValues();

        result.putId(model.getId());
        result.putFirstName(model.getFirstName());
        result.putLastName(model.getLastName());
        result.putShorthand(model.getShorthand());

        String subjects = model.getSubjects();
        if(!TextUtils.isEmpty(subjects)) {
            result.putSubjects(subjects);
        } else {
            result.putSubjectsNull();
        }

        String email = model.getEmail();
        if(!TextUtils.isEmpty(email)) {
            result.putEmail(email);
        } else {
            result.putEmailNull();
        }

        return result;
    }

    @Override
    public Uri uri() {
        return TeacherColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable TeacherSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    @Override
    public TeacherContentValues putId(long id) {
        put(TeacherColumns._ID, id);
        return this;
    }

    @Override
    public TeacherContentValues putFirstName(@NonNull String value) {
        put(TeacherColumns.FIRSTNAME, value);
        return this;
    }

    @Override
    public TeacherContentValues putLastName(@NonNull String value) {
        put(TeacherColumns.LASTNAME, value);
        return this;
    }

    @Override
    public TeacherContentValues putShorthand(@NonNull String value) {
        put(TeacherColumns.SHORTHAND, value);
        return this;
    }

    @Override
    public TeacherContentValues putSubjects(@Nullable String value) {
        put(TeacherColumns.SUBJECTS, value);
        return this;
    }

    public TeacherContentValues putSubjectsNull() {
        putNull(TeacherColumns.SUBJECTS);
        return this;
    }

    @Override
    public TeacherContentValues putEmail(@Nullable String value) {
        put(TeacherColumns.EMAIL, value);
        return this;
    }

    public TeacherContentValues putEmailNull() {
        putNull(TeacherColumns.EMAIL);
        return this;
    }
}
