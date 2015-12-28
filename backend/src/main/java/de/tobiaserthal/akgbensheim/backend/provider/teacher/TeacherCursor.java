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
package de.tobiaserthal.akgbensheim.backend.provider.teacher;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.tobiaserthal.akgbensheim.backend.model.teacher.TeacherModel;
import de.tobiaserthal.akgbensheim.backend.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code teacher} table.
 */
public class TeacherCursor extends AbstractCursor implements TeacherModel {
    public static TeacherCursor wrap(Cursor cursor) {
        assert cursor != null;
        return new TeacherCursor(cursor);
    }

    public TeacherCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(TeacherColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The teacher's first name.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getFirstName() {
        String res = getStringOrNull(TeacherColumns.FIRSTNAME);
        if (res == null)
            throw new NullPointerException("The value of 'firstname' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The teacher's last name.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getLastName() {
        String res = getStringOrNull(TeacherColumns.LASTNAME);
        if (res == null)
            throw new NullPointerException("The value of 'lastname' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The teacher's shorthand, which is unique.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getShorthand() {
        String res = getStringOrNull(TeacherColumns.SHORTHAND);
        if (res == null)
            throw new NullPointerException("The value of 'shorthand' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * A comma seperated list of subject this teacher teaches.
     * Can be {@code null}.
     */
    @Nullable
    public String getSubjects() {
        return getStringOrNull(TeacherColumns.SUBJECTS);
    }

    /**
     * The teacher's email adress.
     * Can be {@code null}.
     */
    @Nullable
    public String getEmail() {
        return getStringOrNull(TeacherColumns.EMAIL);
    }
}
