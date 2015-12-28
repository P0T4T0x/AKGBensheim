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
package de.tobiaserthal.akgbensheim.backend.provider.event;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import de.tobiaserthal.akgbensheim.backend.model.event.EventModel;
import de.tobiaserthal.akgbensheim.backend.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code event} table.
 */
public class EventCursor extends AbstractCursor implements EventModel {
    public static EventCursor wrap(Cursor cursor) {
        assert cursor != null;
        return new EventCursor(cursor);
    }

    public EventCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(EventColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The title of the event.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getTitle() {
        String res = getStringOrNull(EventColumns.TITLE);
        if (res == null)
            throw new NullPointerException("The value of 'title' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The date the event is going to take place.
     * Cannot be {@code null}.
     */
    @NonNull
    public Date getEventDate() {
        Date res = getDateOrNull(EventColumns.EVENTDATE);
        if (res == null)
            throw new NullPointerException("The value of 'eventdate' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The date description for the event.
     * Can be {@code null}.
     */
    @Nullable
    public String getDateString() {
        return getStringOrNull(EventColumns.DATESTRING);
    }

    /**
     * The description for the event.
     * Can be {@code null}.
     */
    @Nullable
    public String getDescription() {
        return getStringOrNull(EventColumns.DESCRIPTION);
    }
}
