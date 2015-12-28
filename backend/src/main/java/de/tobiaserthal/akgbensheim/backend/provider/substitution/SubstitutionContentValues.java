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

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Date;

import de.tobiaserthal.akgbensheim.backend.model.substitution.SubstitutionModel;
import de.tobiaserthal.akgbensheim.backend.model.substitution.SubstitutionModelBuilder;
import de.tobiaserthal.akgbensheim.backend.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code substitution} table.
 */
public class SubstitutionContentValues extends AbstractContentValues
        implements SubstitutionModelBuilder<SubstitutionContentValues> {

    public static SubstitutionContentValues wrap(SubstitutionModel model) {
        SubstitutionContentValues result = new SubstitutionContentValues();

        result.putId(model.getId());
        result.putFormKey(model.getFormKey());
        result.putSubstDate(model.getSubstDate());
        result.putPeriod(model.getPeriod());
        result.putType(model.getType());
        result.putLesson(model.getLesson());
        result.putLessonSubst(model.getLessonSubst());
        result.putRoom(model.getRoom());
        result.putRoomSubst(model.getRoomSubst());

        String annotation = model.getAnnotation();
        if(!TextUtils.isEmpty(annotation)) {
            result.putAnnotation(annotation);
        } else {
            result.putAnnotationNull();
        }

        return result;
    }

    @Override
    public Uri uri() {
        return SubstitutionColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable SubstitutionSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    @Override
    public SubstitutionContentValues putId(long id) {
        put(SubstitutionColumns._ID, id);
        return this;
    }

    @Override
    public SubstitutionContentValues putFormKey(@NonNull String value) {
        put(SubstitutionColumns.FORMKEY, value);
        return this;
    }

    @Override
    public SubstitutionContentValues putSubstDate(@NonNull Date value) {
        put(SubstitutionColumns.SUBSTDATE, value.getTime());
        return this;
    }

    public SubstitutionContentValues putSubstDate(long value) {
        put(SubstitutionColumns.SUBSTDATE, value);
        return this;
    }

    @Override
    public SubstitutionContentValues putPeriod(@NonNull String value) {
        put(SubstitutionColumns.PERIOD, value);
        return this;
    }

    @Override
    public SubstitutionContentValues putType(@NonNull String value) {
        put(SubstitutionColumns.TYPE, value);
        return this;
    }

    @Override
    public SubstitutionContentValues putLesson(@NonNull String value) {
        put(SubstitutionColumns.LESSON, value);
        return this;
    }

    @Override
    public SubstitutionContentValues putLessonSubst(@NonNull String value) {
        put(SubstitutionColumns.LESSONSUBST, value);
        return this;
    }

    @Override
    public SubstitutionContentValues putRoom(@NonNull String value) {
        put(SubstitutionColumns.ROOM, value);
        return this;
    }

    @Override
    public SubstitutionContentValues putRoomSubst(@NonNull String value) {
        put(SubstitutionColumns.ROOMSUBST, value);
        return this;
    }

    @Override
    public SubstitutionContentValues putAnnotation(@Nullable String value) {
        put(SubstitutionColumns.ANNOTATION, value);
        return this;
    }

    /**
     * Set the annotation to {@code null}
     */
    public SubstitutionContentValues putAnnotationNull() {
        putNull(SubstitutionColumns.ANNOTATION);
        return this;
    }
}
