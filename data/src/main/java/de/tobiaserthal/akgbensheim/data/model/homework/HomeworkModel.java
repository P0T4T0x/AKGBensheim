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
package de.tobiaserthal.akgbensheim.data.model.homework;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.tobiaserthal.akgbensheim.data.model.base.BaseModel;

/**
 * A typical list item for a student.
 */
public interface HomeworkModel extends BaseModel {

    /**
     * The title to display. Usually contains the subject.
     * Cannot be {@code null}.
     */
    @NonNull
    String getTitle();

    /**
     * The date until the work has to be done.
     * Cannot be {@code null}.
     */
    @NonNull
    Date getTodoDate();

    /**
     * The notes that describe what to do.
     * Can be {@code null}.
     */
    @Nullable
    String getNotes();

    /**
     * Whether the work has been done.
     */
    boolean getDone();
}
