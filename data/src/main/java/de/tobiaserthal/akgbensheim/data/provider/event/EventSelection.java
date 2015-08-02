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
package de.tobiaserthal.akgbensheim.data.provider.event;

import java.util.Date;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import de.tobiaserthal.akgbensheim.data.provider.base.AbstractSelection;
import de.tobiaserthal.akgbensheim.data.provider.news.NewsSelection;

/**
 * Selection for the {@code event} table.
 */
public class EventSelection extends AbstractSelection<EventSelection> {

    public static EventSelection get(long id) {
        return new EventSelection().id(id);
    }

    public static EventSelection getAll() {
        return new EventSelection();
    }

    public static EventSelection getComing() {
        return new EventSelection().eventDateAfterEq(new Date());
    }

    public static EventSelection getComingWithQuery(String query) {
        return getComing().and()
                .openParen()
                .titleContains(query).or()
                .dateStringContains(query).or()
                .descriptionContains(query)
                .closeParen();
    }

    public static EventSelection getOver() {
        return new EventSelection().eventDateBefore(new Date());
    }

    public static EventSelection getOverWithQuery(String query) {
        return getOver().and()
                .openParen()
                .titleContains(query)
                .dateStringContains(query)
                .descriptionContains(query)
                .closeParen();
    }

    @Override
    protected Uri baseUri() {
        return EventColumns.CONTENT_URI;
    }

    @Override
    public int count(ContentResolver resolver) {
        return count(resolver, EventColumns._ID);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code EventCursor} object, which is positioned before the first entry, or null.
     */
    public EventCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new EventCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public EventCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    public EventSelection id(long... value) {
        addEquals("event." + EventColumns._ID, toObjectArray(value));
        return this;
    }

    public EventSelection title(String... value) {
        addEquals(EventColumns.TITLE, value);
        return this;
    }

    public EventSelection titleNot(String... value) {
        addNotEquals(EventColumns.TITLE, value);
        return this;
    }

    public EventSelection titleLike(String... value) {
        addLike(EventColumns.TITLE, value);
        return this;
    }

    public EventSelection titleContains(String... value) {
        addContains(EventColumns.TITLE, value);
        return this;
    }

    public EventSelection titleStartsWith(String... value) {
        addStartsWith(EventColumns.TITLE, value);
        return this;
    }

    public EventSelection titleEndsWith(String... value) {
        addEndsWith(EventColumns.TITLE, value);
        return this;
    }

    public EventSelection eventDate(Date... value) {
        addEquals(EventColumns.EVENTDATE, value);
        return this;
    }

    public EventSelection eventDateNot(Date... value) {
        addNotEquals(EventColumns.EVENTDATE, value);
        return this;
    }

    public EventSelection eventDate(long... value) {
        addEquals(EventColumns.EVENTDATE, toObjectArray(value));
        return this;
    }

    public EventSelection eventDateAfter(Date value) {
        addGreaterThan(EventColumns.EVENTDATE, value);
        return this;
    }

    public EventSelection eventDateAfterEq(Date value) {
        addGreaterThanOrEquals(EventColumns.EVENTDATE, value);
        return this;
    }

    public EventSelection eventDateBefore(Date value) {
        addLessThan(EventColumns.EVENTDATE, value);
        return this;
    }

    public EventSelection eventDateBeforeEq(Date value) {
        addLessThanOrEquals(EventColumns.EVENTDATE, value);
        return this;
    }

    public EventSelection dateString(String... value) {
        addEquals(EventColumns.DATESTRING, value);
        return this;
    }

    public EventSelection dateStringNot(String... value) {
        addNotEquals(EventColumns.DATESTRING, value);
        return this;
    }

    public EventSelection dateStringLike(String... value) {
        addLike(EventColumns.DATESTRING, value);
        return this;
    }

    public EventSelection dateStringContains(String... value) {
        addContains(EventColumns.DATESTRING, value);
        return this;
    }

    public EventSelection dateStringStartsWith(String... value) {
        addStartsWith(EventColumns.DATESTRING, value);
        return this;
    }

    public EventSelection dateStringEndsWith(String... value) {
        addEndsWith(EventColumns.DATESTRING, value);
        return this;
    }

    public EventSelection description(String... value) {
        addEquals(EventColumns.DESCRIPTION, value);
        return this;
    }

    public EventSelection descriptionNot(String... value) {
        addNotEquals(EventColumns.DESCRIPTION, value);
        return this;
    }

    public EventSelection descriptionLike(String... value) {
        addLike(EventColumns.DESCRIPTION, value);
        return this;
    }

    public EventSelection descriptionContains(String... value) {
        addContains(EventColumns.DESCRIPTION, value);
        return this;
    }

    public EventSelection descriptionStartsWith(String... value) {
        addStartsWith(EventColumns.DESCRIPTION, value);
        return this;
    }

    public EventSelection descriptionEndsWith(String... value) {
        addEndsWith(EventColumns.DESCRIPTION, value);
        return this;
    }
}
