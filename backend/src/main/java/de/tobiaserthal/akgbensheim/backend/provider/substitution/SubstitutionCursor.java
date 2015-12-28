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
package de.tobiaserthal.akgbensheim.backend.provider.substitution;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import de.tobiaserthal.akgbensheim.backend.model.substitution.SubstitutionModel;
import de.tobiaserthal.akgbensheim.backend.provider.base.AbstractCursor;


/**
 * Cursor wrapper for the {@code substitution} table.
 */
public class SubstitutionCursor extends AbstractCursor implements SubstitutionModel {
    public static SubstitutionCursor wrap(Cursor cursor) {
        assert cursor != null;
        return new SubstitutionCursor(cursor);
    }

    public SubstitutionCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(SubstitutionColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The identifier of the form affected by the substitution.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getFormKey() {
        String res = getStringOrNull(SubstitutionColumns.FORMKEY);
        if (res == null)
            throw new NullPointerException("The value of 'formkey' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The date the substitution takes place.
     * Cannot be {@code null}.
     */
    @NonNull
    public Date getSubstDate() {
        Date res = getDateOrNull(SubstitutionColumns.SUBSTDATE);
        if (res == null)
            throw new NullPointerException("The value of 'substdate' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The peroid affected by the substitution.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getPeriod() {
        String res = getStringOrNull(SubstitutionColumns.PERIOD);
        if (res == null)
            throw new NullPointerException("The value of 'period' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The type of the substitution.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getType() {
        String res = getStringOrNull(SubstitutionColumns.TYPE);
        if (res == null)
            throw new NullPointerException("The value of 'type' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The affected lesson.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getLesson() {
        String res = getStringOrNull(SubstitutionColumns.LESSON);
        if (res == null)
            throw new NullPointerException("The value of 'lesson' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The lesson going to take place instead of "lesson".
     * Cannot be {@code null}.
     */
    @NonNull
    public String getLessonSubst() {
        String res = getStringOrNull(SubstitutionColumns.LESSONSUBST);
        if (res == null)
            throw new NullPointerException("The value of 'lessonsubst' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The room for the affected lesson.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getRoom() {
        String res = getStringOrNull(SubstitutionColumns.ROOM);
        if (res == null)
            throw new NullPointerException("The value of 'room' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The room the lesson is going to take place.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getRoomSubst() {
        String res = getStringOrNull(SubstitutionColumns.ROOMSUBST);
        if (res == null)
            throw new NullPointerException("The value of 'roomsubst' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Annotations for additional infos about this substitution.
     * Can be {@code null}.
     */
    @Nullable
    public String getAnnotation() {
        return getStringOrNull(SubstitutionColumns.ANNOTATION);
    }
}
