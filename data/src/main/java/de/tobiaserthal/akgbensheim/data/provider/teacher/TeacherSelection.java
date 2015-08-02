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
package de.tobiaserthal.akgbensheim.data.provider.teacher;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import de.tobiaserthal.akgbensheim.data.provider.base.AbstractSelection;

/**
 * Selection for the {@code teacher} table.
 */
public class TeacherSelection extends AbstractSelection<TeacherSelection> {

    public static TeacherSelection get(long id) {
        return new TeacherSelection().id(id);
    }

    public static TeacherSelection getTeachers() {
        return new TeacherSelection()
                .identifierNotEquals("x", "z");
    }

    public static TeacherSelection getTeachersWithQuery(String query) {
        return getTeachers().and()
                .openParen()
                .firstNameContains(query).or()
                .lastNameContains(query).or()
                .shorthandContains(query).or()
                .subjectsContains(query)
                .closeParen();
    }

    public static TeacherSelection getStudentTeachers() {
        return new TeacherSelection()
                .identifierEquals("x", "z");
    }

    public static TeacherSelection getStudentTeachersWithQuery(String query) {
        return getStudentTeachers().and()
                .openParen()
                .firstNameContains(query).or()
                .lastNameContains(query).or()
                .shorthandContains(query).or()
                .subjectsContains(query)
                .closeParen();
    }

    @Override
    protected Uri baseUri() {
        return TeacherColumns.CONTENT_URI;
    }

    @Override
    public int count(ContentResolver resolver) {
        return count(resolver, TeacherColumns._ID);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code TeacherCursor} object, which is positioned before the first entry, or null.
     */
    public TeacherCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new TeacherCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public TeacherCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    public TeacherSelection id(long... value) {
        addEquals("teacher." + TeacherColumns._ID, toObjectArray(value));
        return this;
    }

    public TeacherSelection identifierEquals(String... values) {
        addEquals("substr(" + TeacherColumns.SHORTHAND + ",1,1)", values);
        return this;
    }

    public TeacherSelection identifierNotEquals(String... values) {
        addNotEquals("substr(" + TeacherColumns.SHORTHAND + ",1,1)", values);
        return this;
    }

    public TeacherSelection firstName(String... value) {
        addEquals(TeacherColumns.FIRSTNAME, value);
        return this;
    }

    public TeacherSelection firstNameNot(String... value) {
        addNotEquals(TeacherColumns.FIRSTNAME, value);
        return this;
    }

    public TeacherSelection firstNameLike(String... value) {
        addLike(TeacherColumns.FIRSTNAME, value);
        return this;
    }

    public TeacherSelection firstNameContains(String... value) {
        addContains(TeacherColumns.FIRSTNAME, value);
        return this;
    }

    public TeacherSelection firstNameStartsWith(String... value) {
        addStartsWith(TeacherColumns.FIRSTNAME, value);
        return this;
    }

    public TeacherSelection firstNameEndsWith(String... value) {
        addEndsWith(TeacherColumns.FIRSTNAME, value);
        return this;
    }

    public TeacherSelection lastName(String... value) {
        addEquals(TeacherColumns.LASTNAME, value);
        return this;
    }

    public TeacherSelection lastNameLike(String... value) {
        addLike(TeacherColumns.LASTNAME, value);
        return this;
    }

    public TeacherSelection lastNameContains(String... value) {
        addContains(TeacherColumns.LASTNAME, value);
        return this;
    }


    public TeacherSelection lastNameStartsWith(String... value) {
        addStartsWith(TeacherColumns.LASTNAME, value);
        return this;
    }


    public TeacherSelection lastNameEndsWith(String... value) {
        addEndsWith(TeacherColumns.LASTNAME, value);
        return this;
    }

    public TeacherSelection shorthand(String... value) {
        addEquals(TeacherColumns.SHORTHAND, value);
        return this;
    }

    public TeacherSelection shorthandLike(String... value) {
        addLike(TeacherColumns.SHORTHAND, value);
        return this;
    }

    public TeacherSelection shorthandContains(String... value) {
        addContains(TeacherColumns.SHORTHAND, value);
        return this;
    }

    public TeacherSelection shorthandStartsWith(String... value) {
        addStartsWith(TeacherColumns.SHORTHAND, value);
        return this;
    }

    public TeacherSelection shorthandEndsWith(String... value) {
        addEndsWith(TeacherColumns.SHORTHAND, value);
        return this;
    }

    public TeacherSelection subjects(String... value) {
        addEquals(TeacherColumns.SUBJECTS, value);
        return this;
    }

    public TeacherSelection subjectsLike(String... value) {
        addLike(TeacherColumns.SUBJECTS, value);
        return this;
    }

    public TeacherSelection subjectsContains(String... value) {
        addContains(TeacherColumns.SUBJECTS, value);
        return this;
    }

    public TeacherSelection subjectsStartsWith(String... value) {
        addStartsWith(TeacherColumns.SUBJECTS, value);
        return this;
    }

    public TeacherSelection subjectsEndsWith(String... value) {
        addEndsWith(TeacherColumns.SUBJECTS, value);
        return this;
    }

    public TeacherSelection email(String... value) {
        addEquals(TeacherColumns.EMAIL, value);
        return this;
    }

    public TeacherSelection emailLike(String... value) {
        addLike(TeacherColumns.EMAIL, value);
        return this;
    }

    public TeacherSelection emailContains(String... value) {
        addContains(TeacherColumns.EMAIL, value);
        return this;
    }

    public TeacherSelection emailStartsWith(String... value) {
        addStartsWith(TeacherColumns.EMAIL, value);
        return this;
    }

    public TeacherSelection emailEndsWith(String... value) {
        addEndsWith(TeacherColumns.EMAIL, value);
        return this;
    }
}
