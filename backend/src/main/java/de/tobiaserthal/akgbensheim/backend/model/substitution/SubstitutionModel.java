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
package de.tobiaserthal.akgbensheim.backend.model.substitution;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;
import de.tobiaserthal.akgbensheim.backend.model.base.BaseModel;

/**
 * A substitution that was loaded from the school's website.
 */
public interface SubstitutionModel extends BaseModel {

    /**
     * The identifier of the form affected by the substitution.
     * Cannot be {@code null}.
     */
    @NonNull
    String getFormKey();

    /**
     * The date the substitution takes place.
     * Cannot be {@code null}.
     */
    @NonNull
    Date getSubstDate();

    /**
     * The peroid affected by the substitution.
     * Cannot be {@code null}.
     */
    @NonNull
    String getPeriod();

    /**
     * The type of the substitution.
     * Cannot be {@code null}.
     */
    @NonNull
    String getType();

    /**
     * The affected lesson.
     * Cannot be {@code null}.
     */
    @NonNull
    String getLesson();

    /**
     * The lesson going to take place instead of "lesson".
     * Cannot be {@code null}.
     */
    @NonNull
    String getLessonSubst();

    /**
     * The room for the affected lesson.
     * Cannot be {@code null}.
     */
    @NonNull
    String getRoom();

    /**
     * The room the lesson is going to take place.
     * Cannot be {@code null}.
     */
    @NonNull
    String getRoomSubst();

    /**
     * Annotations for additional infos about this substitution.
     * Can be {@code null}.
     */
    @Nullable
    String getAnnotation();
}
