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
package de.tobiaserthal.akgbensheim.data.provider.substitution;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import de.tobiaserthal.akgbensheim.data.preferences.PreferenceProvider;
import de.tobiaserthal.akgbensheim.data.provider.base.AbstractSelection;

/**
 * Selection for the {@code substitution} table.
 */
public class SubstitutionSelection extends AbstractSelection<SubstitutionSelection> {

    /**
     * Query an entry of the database table by its unique id.
     * @param id The unique id of the entry to query.
     * @return A selection to query the database.
     */
    public static SubstitutionSelection get(long id) {
        return new SubstitutionSelection().id(id);
    }

    /**
     * Query all entries of the database table
     * @return A selection to query the database.
     */
    public static SubstitutionSelection getAll() {
        return new SubstitutionSelection();
    }

    public SubstitutionSelection getToday() {
        Calendar calendar = new GregorianCalendar(Locale.getDefault());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Date today = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date tomorrow = calendar.getTime();

        return and().openParen()
                .substDateAfterEq(today).and()
                .substDateBefore(tomorrow)
                .closeParen();
    }

    /**
     * Query all entries of the database table that match the given filter.
     * @param filter The text filter to search with.
     * @return A selection to query the database.
     */
    public static SubstitutionSelection getAllWithQuery(String filter) {
        return getAll().formKeyContains(filter).or()
                .lessonSubstContains(filter).or()
                .roomSubstContains(filter).or()
                .periodStartsWith(filter).or()
                .typeContains(filter);
    }

    /**
     * Query all entries with the specified form prefix.
     * @param phase The form prefix to query for.
     * @return A selection to query the database.
     */
    public static SubstitutionSelection getPhase(int phase) {
        return new SubstitutionSelection().formKeyStartsWith(
                String.format("K%02d", phase));
    }

    /**
     * Query all entries with the specified form prefix that match the given filter.
     * @param phase The form prefix to query for.
     * @param filter The text filter to search with.
     * @return A selection to query the database.
     */
    public static SubstitutionSelection getPhaseWithQuery(int phase, String filter) {
        return getPhase(phase).and()
                .openParen()
                .formKeyContains(filter).or()
                .lessonSubstContains(filter).or()
                .roomSubstContains(filter).or()
                .periodStartsWith(filter).or()
                .typeContains(filter)
                .closeParen();
    }

    /**
     * Query all entries that match the specified phase and/or the given lessons.
     * @param phase The form prefix to query for.
     * @param form The form identifier. Can be empty or null.
     * @param lessons The array of lessons to filter for.
     * @return A selection to query the database.
     */
    public static SubstitutionSelection getForm(int phase, String form, String[] lessons) {
        SubstitutionSelection selection = getPhase(phase);

        if(phase < PreferenceProvider.getSubstPhaseSek2()) {
            selection.and().formKeyContains(form);
        }

        if(lessons != null && lessons.length > 0) {
            selection.and().lessonSubstLike(lessons);
        }

        return selection;
    }

    /**
     * Query all entries that match the specified phase and/or the given lessons and the given filter.
     * @param phase The form prefix to query for.
     * @param form The form identifier. Can be empty or null.
     * @param lessons The array of lessons to filter for.
     * @param filter The text filter to search with.
     * @return A selection to query the database.
     */
    public static SubstitutionSelection getFormWithQuery(int phase, String form, String[] lessons, String filter) {
        return getForm(phase, form, lessons).and()
                .openParen()
                .lessonSubstContains(filter).or()
                .roomSubstContains(filter).or()
                .periodStartsWith(filter).or()
                .typeContains(filter)
                .closeParen();
    }

    @Override
    protected Uri baseUri() {
        return SubstitutionColumns.CONTENT_URI;
    }

    @Override
    public int count(ContentResolver resolver) {
        return count(resolver, SubstitutionColumns._ID);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code SubstitutionCursor} object, which is positioned before the first entry, or null.
     */
    public SubstitutionCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new SubstitutionCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public SubstitutionCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    public SubstitutionSelection id(long... value) {
        addEquals("substitution." + SubstitutionColumns._ID, toObjectArray(value));
        return this;
    }

    public SubstitutionSelection formKey(String... value) {
        addEquals(SubstitutionColumns.FORMKEY, value);
        return this;
    }

    public SubstitutionSelection formKeyNot(String... value) {
        addNotEquals(SubstitutionColumns.FORMKEY, value);
        return this;
    }

    public SubstitutionSelection formKeyLike(String... value) {
        addLike(SubstitutionColumns.FORMKEY, value);
        return this;
    }

    public SubstitutionSelection formKeyContains(String... value) {
        addContains(SubstitutionColumns.FORMKEY, value);
        return this;
    }

    public SubstitutionSelection formKeyStartsWith(String... value) {
        addStartsWith(SubstitutionColumns.FORMKEY, value);
        return this;
    }

    public SubstitutionSelection formKeyEndsWith(String... value) {
        addEndsWith(SubstitutionColumns.FORMKEY, value);
        return this;
    }

    public SubstitutionSelection substDate(Date... value) {
        addEquals(SubstitutionColumns.SUBSTDATE, value);
        return this;
    }

    public SubstitutionSelection substDateNot(Date... value) {
        addNotEquals(SubstitutionColumns.SUBSTDATE, value);
        return this;
    }

    public SubstitutionSelection substDate(long... value) {
        addEquals(SubstitutionColumns.SUBSTDATE, toObjectArray(value));
        return this;
    }

    public SubstitutionSelection substDateAfter(Date value) {
        addGreaterThan(SubstitutionColumns.SUBSTDATE, value);
        return this;
    }

    public SubstitutionSelection substDateAfterEq(Date value) {
        addGreaterThanOrEquals(SubstitutionColumns.SUBSTDATE, value);
        return this;
    }

    public SubstitutionSelection substDateBefore(Date value) {
        addLessThan(SubstitutionColumns.SUBSTDATE, value);
        return this;
    }

    public SubstitutionSelection substDateBeforeEq(Date value) {
        addLessThanOrEquals(SubstitutionColumns.SUBSTDATE, value);
        return this;
    }

    public SubstitutionSelection period(String... value) {
        addEquals(SubstitutionColumns.PERIOD, value);
        return this;
    }

    public SubstitutionSelection periodNot(String... value) {
        addNotEquals(SubstitutionColumns.PERIOD, value);
        return this;
    }

    public SubstitutionSelection periodLike(String... value) {
        addLike(SubstitutionColumns.PERIOD, value);
        return this;
    }

    public SubstitutionSelection periodContains(String... value) {
        addContains(SubstitutionColumns.PERIOD, value);
        return this;
    }

    public SubstitutionSelection periodStartsWith(String... value) {
        addStartsWith(SubstitutionColumns.PERIOD, value);
        return this;
    }

    public SubstitutionSelection periodEndsWith(String... value) {
        addEndsWith(SubstitutionColumns.PERIOD, value);
        return this;
    }

    public SubstitutionSelection type(String... value) {
        addEquals(SubstitutionColumns.TYPE, value);
        return this;
    }

    public SubstitutionSelection typeNot(String... value) {
        addNotEquals(SubstitutionColumns.TYPE, value);
        return this;
    }

    public SubstitutionSelection typeLike(String... value) {
        addLike(SubstitutionColumns.TYPE, value);
        return this;
    }

    public SubstitutionSelection typeContains(String... value) {
        addContains(SubstitutionColumns.TYPE, value);
        return this;
    }

    public SubstitutionSelection typeStartsWith(String... value) {
        addStartsWith(SubstitutionColumns.TYPE, value);
        return this;
    }

    public SubstitutionSelection typeEndsWith(String... value) {
        addEndsWith(SubstitutionColumns.TYPE, value);
        return this;
    }

    public SubstitutionSelection lesson(String... value) {
        addEquals(SubstitutionColumns.LESSON, value);
        return this;
    }

    public SubstitutionSelection lessonNot(String... value) {
        addNotEquals(SubstitutionColumns.LESSON, value);
        return this;
    }

    public SubstitutionSelection lessonLike(String... value) {
        addLike(SubstitutionColumns.LESSON, value);
        return this;
    }

    public SubstitutionSelection lessonContains(String... value) {
        addContains(SubstitutionColumns.LESSON, value);
        return this;
    }

    public SubstitutionSelection lessonStartsWith(String... value) {
        addStartsWith(SubstitutionColumns.LESSON, value);
        return this;
    }

    public SubstitutionSelection lessonEndsWith(String... value) {
        addEndsWith(SubstitutionColumns.LESSON, value);
        return this;
    }

    public SubstitutionSelection lessonSubst(String... value) {
        addEquals(SubstitutionColumns.LESSONSUBST, value);
        return this;
    }

    public SubstitutionSelection lessonSubstNot(String... value) {
        addNotEquals(SubstitutionColumns.LESSONSUBST, value);
        return this;
    }

    public SubstitutionSelection lessonSubstLike(String... value) {
        addLike(SubstitutionColumns.LESSONSUBST, value);
        return this;
    }

    public SubstitutionSelection lessonSubstContains(String... value) {
        addContains(SubstitutionColumns.LESSONSUBST, value);
        return this;
    }

    public SubstitutionSelection lessonSubstStartsWith(String... value) {
        addStartsWith(SubstitutionColumns.LESSONSUBST, value);
        return this;
    }

    public SubstitutionSelection lessonSubstEndsWith(String... value) {
        addEndsWith(SubstitutionColumns.LESSONSUBST, value);
        return this;
    }

    public SubstitutionSelection room(String... value) {
        addEquals(SubstitutionColumns.ROOM, value);
        return this;
    }

    public SubstitutionSelection roomNot(String... value) {
        addNotEquals(SubstitutionColumns.ROOM, value);
        return this;
    }

    public SubstitutionSelection roomLike(String... value) {
        addLike(SubstitutionColumns.ROOM, value);
        return this;
    }

    public SubstitutionSelection roomContains(String... value) {
        addContains(SubstitutionColumns.ROOM, value);
        return this;
    }

    public SubstitutionSelection roomStartsWith(String... value) {
        addStartsWith(SubstitutionColumns.ROOM, value);
        return this;
    }

    public SubstitutionSelection roomEndsWith(String... value) {
        addEndsWith(SubstitutionColumns.ROOM, value);
        return this;
    }

    public SubstitutionSelection roomSubst(String... value) {
        addEquals(SubstitutionColumns.ROOMSUBST, value);
        return this;
    }

    public SubstitutionSelection roomSubstNot(String... value) {
        addNotEquals(SubstitutionColumns.ROOMSUBST, value);
        return this;
    }

    public SubstitutionSelection roomSubstLike(String... value) {
        addLike(SubstitutionColumns.ROOMSUBST, value);
        return this;
    }

    public SubstitutionSelection roomSubstContains(String... value) {
        addContains(SubstitutionColumns.ROOMSUBST, value);
        return this;
    }

    public SubstitutionSelection roomSubstStartsWith(String... value) {
        addStartsWith(SubstitutionColumns.ROOMSUBST, value);
        return this;
    }

    public SubstitutionSelection roomSubstEndsWith(String... value) {
        addEndsWith(SubstitutionColumns.ROOMSUBST, value);
        return this;
    }

    public SubstitutionSelection annotation(String... value) {
        addEquals(SubstitutionColumns.ANNOTATION, value);
        return this;
    }

    public SubstitutionSelection annotationNot(String... value) {
        addNotEquals(SubstitutionColumns.ANNOTATION, value);
        return this;
    }

    public SubstitutionSelection annotationLike(String... value) {
        addLike(SubstitutionColumns.ANNOTATION, value);
        return this;
    }

    public SubstitutionSelection annotationContains(String... value) {
        addContains(SubstitutionColumns.ANNOTATION, value);
        return this;
    }

    public SubstitutionSelection annotationStartsWith(String... value) {
        addStartsWith(SubstitutionColumns.ANNOTATION, value);
        return this;
    }

    public SubstitutionSelection annotationEndsWith(String... value) {
        addEndsWith(SubstitutionColumns.ANNOTATION, value);
        return this;
    }
}
