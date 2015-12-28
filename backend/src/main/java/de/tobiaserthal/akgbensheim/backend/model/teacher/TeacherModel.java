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
package de.tobiaserthal.akgbensheim.backend.model.teacher;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.tobiaserthal.akgbensheim.backend.model.base.BaseModel;

/**
 * A teacher at the school.
 */
public interface TeacherModel extends BaseModel {

    /**
     * The teacher's first name.
     * Cannot be {@code null}.
     */
    @NonNull
    String getFirstName();

    /**
     * The teacher's last name.
     * Cannot be {@code null}.
     */
    @NonNull
    String getLastName();

    /**
     * The teacher's shorthand, which is unique.
     * Cannot be {@code null}.
     */
    @NonNull
    String getShorthand();

    /**
     * A comma seperated list of subject this teacher teaches.
     * Can be {@code null}.
     */
    @Nullable
    String getSubjects();

    /**
     * The teacher's email adress.
     * Can be {@code null}.
     */
    @Nullable
    String getEmail();
}
