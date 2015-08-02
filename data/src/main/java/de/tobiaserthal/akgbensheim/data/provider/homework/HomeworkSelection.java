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
package de.tobiaserthal.akgbensheim.data.provider.homework;

import java.util.Date;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import de.tobiaserthal.akgbensheim.data.provider.base.AbstractSelection;

/**
 * Selection for the {@code homework} table.
 */
public class HomeworkSelection extends AbstractSelection<HomeworkSelection> {

    /**
     * Query an entry of the database table by its unique id.
     * @param id The unique id of the entry to query.
     * @return A selection to query the database.
     */
    public static HomeworkSelection get(long id) {
        return new HomeworkSelection().id(id);
    }

    /**
     * Query all entries of the database table
     * @return A selection to query the database.
     */
    public static HomeworkSelection getAll() {
        return new HomeworkSelection();
    }

    /**
     * Query all entries with the done flag set to false
     * and the date being after or equal to the current
     * @return A selection to query the database.
     */
    public static HomeworkSelection getTodo() {
        return new HomeworkSelection()
                .done(false).and()
                .todoDateAfterEq(new Date());
    }

    /**
     * Query all entries with the done flag set to false
     * and the date being after or equal to the current
     * and the title or the notes match the filter.
     * @param filter The filter to query the entries.
     * @return A selection to query the database.
     */
    public static HomeworkSelection getTodoWithQuery(String filter) {
        return getTodo().and()
                .openParen()
                .titleContains(filter).or()
                .notesContains(filter)
                .closeParen();
    }

    /**
     * Query all entries with the done flag set to true
     * and the date being after the current.
     * @return A selection to query the database.
     */
    public static HomeworkSelection getDone() {
        return new HomeworkSelection()
                .openParen()
                .done(true).or()
                .todoDateBefore(new Date())
                .closeParen();
    }

    /**
     * Query all entries with the done flag set to true
     * and the date being after the current
     * and the title or the notes match the filter.
     * @param filter The filter to query the entries
     * @return A selection to query the database.
     */
    public static HomeworkSelection getDoneWithQuery(String filter) {
        return getDone().and()
                .openParen()
                .titleContains(filter).or()
                .notesContains(filter)
                .closeParen();
    }

    @Override
    protected Uri baseUri() {
        return HomeworkColumns.CONTENT_URI;
    }

    @Override
    public int count(ContentResolver resolver) {
        return count(resolver, HomeworkColumns._ID);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code HomeworkCursor} object, which is positioned before the first entry, or null.
     */
    public HomeworkCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new HomeworkCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public HomeworkCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    public HomeworkSelection id(long... value) {
        addEquals("homework." + HomeworkColumns._ID, toObjectArray(value));
        return this;
    }

    public HomeworkSelection title(String... value) {
        addEquals(HomeworkColumns.TITLE, value);
        return this;
    }

    public HomeworkSelection titleNot(String... value) {
        addNotEquals(HomeworkColumns.TITLE, value);
        return this;
    }

    public HomeworkSelection titleLike(String... value) {
        addLike(HomeworkColumns.TITLE, value);
        return this;
    }

    public HomeworkSelection titleContains(String... value) {
        addContains(HomeworkColumns.TITLE, value);
        return this;
    }

    public HomeworkSelection titleStartsWith(String... value) {
        addStartsWith(HomeworkColumns.TITLE, value);
        return this;
    }

    public HomeworkSelection titleEndsWith(String... value) {
        addEndsWith(HomeworkColumns.TITLE, value);
        return this;
    }

    public HomeworkSelection todoDate(Date... value) {
        addEquals(HomeworkColumns.TODODATE, value);
        return this;
    }

    public HomeworkSelection todoDateNot(Date... value) {
        addNotEquals(HomeworkColumns.TODODATE, value);
        return this;
    }

    public HomeworkSelection todoDate(long... value) {
        addEquals(HomeworkColumns.TODODATE, toObjectArray(value));
        return this;
    }

    public HomeworkSelection todoDateAfter(Date value) {
        addGreaterThan(HomeworkColumns.TODODATE, value);
        return this;
    }

    public HomeworkSelection todoDateAfterEq(Date value) {
        addGreaterThanOrEquals(HomeworkColumns.TODODATE, value);
        return this;
    }

    public HomeworkSelection todoDateBefore(Date value) {
        addLessThan(HomeworkColumns.TODODATE, value);
        return this;
    }

    public HomeworkSelection todoDateBeforeEq(Date value) {
        addLessThanOrEquals(HomeworkColumns.TODODATE, value);
        return this;
    }

    public HomeworkSelection notes(String... value) {
        addEquals(HomeworkColumns.NOTES, value);
        return this;
    }

    public HomeworkSelection notesNot(String... value) {
        addNotEquals(HomeworkColumns.NOTES, value);
        return this;
    }

    public HomeworkSelection notesLike(String... value) {
        addLike(HomeworkColumns.NOTES, value);
        return this;
    }

    public HomeworkSelection notesContains(String... value) {
        addContains(HomeworkColumns.NOTES, value);
        return this;
    }

    public HomeworkSelection notesStartsWith(String... value) {
        addStartsWith(HomeworkColumns.NOTES, value);
        return this;
    }

    public HomeworkSelection notesEndsWith(String... value) {
        addEndsWith(HomeworkColumns.NOTES, value);
        return this;
    }

    public HomeworkSelection done(boolean value) {
        addEquals(HomeworkColumns.DONE, toObjectArray(value));
        return this;
    }
}
