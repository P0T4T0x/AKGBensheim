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

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Date;

import de.tobiaserthal.akgbensheim.backend.model.event.EventModel;
import de.tobiaserthal.akgbensheim.backend.model.event.EventModelBuilder;
import de.tobiaserthal.akgbensheim.backend.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code event} table.
 */
public class EventContentValues extends AbstractContentValues implements EventModelBuilder<EventContentValues> {

    public static EventContentValues wrap(EventModel model) {
        EventContentValues result = new EventContentValues();

        result.putId(model.getId());
        result.putTitle(model.getTitle());
        result.putEventDate(model.getEventDate());

        String dateString = model.getDateString();
        if(!TextUtils.isEmpty(dateString)) {
            result.putDateString(dateString);
        } else {
            result.putDateStringNull();
        }

        String description = model.getDescription();
        if(!TextUtils.isEmpty(description)) {
            result.putDescription(description);
        } else {
            result.putDescriptionNull();
        }

        return result;
    }

    @Override
    public Uri uri() {
        return EventColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable EventSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    @Override
    public EventContentValues putId(long id) {
        put(EventColumns._ID, id);
        return this;
    }

    @Override
    public EventContentValues putTitle(@NonNull String value) {
        put(EventColumns.TITLE, value);
        return this;
    }

    @Override
    public EventContentValues putEventDate(@NonNull Date value) {
        put(EventColumns.EVENTDATE, value.getTime());
        return this;
    }

    /**
     * Puts a date by its time value.
     * @param value The time of the date.
     */
    public EventContentValues putEventDate(long value) {
        put(EventColumns.EVENTDATE, value);
        return this;
    }

    @Override
    public EventContentValues putDateString(@Nullable String value) {
        put(EventColumns.DATESTRING, value);
        return this;
    }

    /**
     * Put {@code null} as the datestring.
     */
    public EventContentValues putDateStringNull() {
        putNull(EventColumns.DATESTRING);
        return this;
    }

    @Override
    public EventContentValues putDescription(@Nullable String value) {
        put(EventColumns.DESCRIPTION, value);
        return this;
    }

    /**
     * Put {@code null} as the description.
     */
    public EventContentValues putDescriptionNull() {
        putNull(EventColumns.DESCRIPTION);
        return this;
    }
}
