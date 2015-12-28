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
package de.tobiaserthal.akgbensheim.backend.provider.news;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import de.tobiaserthal.akgbensheim.backend.provider.base.AbstractSelection;

/**
 * Selection for the {@code news} table.
 */
public class NewsSelection extends AbstractSelection<NewsSelection> {

    public static NewsSelection get(long id) {
        return new NewsSelection().id(id);
    }

    public static NewsSelection getAll() {
        return new NewsSelection();
    }

    public static NewsSelection getAllWithQuery(String query) {
        return getAll().titleContains(query).or()
                .snippetContains(query);
    }

    public static NewsSelection getBookmarked() {
        return new NewsSelection().bookmarked(true);
    }

    public static NewsSelection getBookmarkedWithQuery(String query) {
        return getBookmarked().and()
                .openParen()
                .titleContains(query).or()
                .snippetContains(query)
                .closeParen();
    }

    @Override
    protected Uri baseUri() {
        return NewsColumns.CONTENT_URI;
    }

    @Override
    public int count(ContentResolver resolver) {
        return count(resolver, NewsColumns._ID);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code NewsCursor} object, which is positioned before the first entry, or null.
     */
    public NewsCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new NewsCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public NewsCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }


    public NewsSelection id(long... value) {
        addEquals("news." + NewsColumns._ID, toObjectArray(value));
        return this;
    }

    public NewsSelection title(String... value) {
        addEquals(NewsColumns.TITLE, value);
        return this;
    }

    public NewsSelection titleNot(String... value) {
        addNotEquals(NewsColumns.TITLE, value);
        return this;
    }

    public NewsSelection titleLike(String... value) {
        addLike(NewsColumns.TITLE, value);
        return this;
    }

    public NewsSelection titleContains(String... value) {
        addContains(NewsColumns.TITLE, value);
        return this;
    }

    public NewsSelection titleStartsWith(String... value) {
        addStartsWith(NewsColumns.TITLE, value);
        return this;
    }

    public NewsSelection titleEndsWith(String... value) {
        addEndsWith(NewsColumns.TITLE, value);
        return this;
    }

    public NewsSelection snippet(String... value) {
        addEquals(NewsColumns.SNIPPET, value);
        return this;
    }

    public NewsSelection snippetNot(String... value) {
        addNotEquals(NewsColumns.SNIPPET, value);
        return this;
    }

    public NewsSelection snippetLike(String... value) {
        addLike(NewsColumns.SNIPPET, value);
        return this;
    }

    public NewsSelection snippetContains(String... value) {
        addContains(NewsColumns.SNIPPET, value);
        return this;
    }

    public NewsSelection snippetStartsWith(String... value) {
        addStartsWith(NewsColumns.SNIPPET, value);
        return this;
    }

    public NewsSelection snippetEndsWith(String... value) {
        addEndsWith(NewsColumns.SNIPPET, value);
        return this;
    }

    public NewsSelection article(String... value) {
        addEquals(NewsColumns.ARTICLE, value);
        return this;
    }

    public NewsSelection articleNot(String... value) {
        addNotEquals(NewsColumns.ARTICLE, value);
        return this;
    }

    public NewsSelection articleLike(String... value) {
        addLike(NewsColumns.ARTICLE, value);
        return this;
    }

    public NewsSelection articleContains(String... value) {
        addContains(NewsColumns.ARTICLE, value);
        return this;
    }

    public NewsSelection articleStartsWith(String... value) {
        addStartsWith(NewsColumns.ARTICLE, value);
        return this;
    }

    public NewsSelection articleEndsWith(String... value) {
        addEndsWith(NewsColumns.ARTICLE, value);
        return this;
    }

    public NewsSelection articleUrl(String... value) {
        addEquals(NewsColumns.ARTICLEURL, value);
        return this;
    }

    public NewsSelection articleUrlNot(String... value) {
        addNotEquals(NewsColumns.ARTICLEURL, value);
        return this;
    }

    public NewsSelection articleUrlLike(String... value) {
        addLike(NewsColumns.ARTICLEURL, value);
        return this;
    }

    public NewsSelection articleUrlContains(String... value) {
        addContains(NewsColumns.ARTICLEURL, value);
        return this;
    }

    public NewsSelection articleUrlStartsWith(String... value) {
        addStartsWith(NewsColumns.ARTICLEURL, value);
        return this;
    }

    public NewsSelection articleUrlEndsWith(String... value) {
        addEndsWith(NewsColumns.ARTICLEURL, value);
        return this;
    }

    public NewsSelection imageUrl(String... value) {
        addEquals(NewsColumns.IMAGEURL, value);
        return this;
    }

    public NewsSelection imageUrlNot(String... value) {
        addNotEquals(NewsColumns.IMAGEURL, value);
        return this;
    }

    public NewsSelection imageUrlLike(String... value) {
        addLike(NewsColumns.IMAGEURL, value);
        return this;
    }

    public NewsSelection imageUrlContains(String... value) {
        addContains(NewsColumns.IMAGEURL, value);
        return this;
    }

    public NewsSelection imageUrlStartsWith(String... value) {
        addStartsWith(NewsColumns.IMAGEURL, value);
        return this;
    }

    public NewsSelection imageUrlEndsWith(String... value) {
        addEndsWith(NewsColumns.IMAGEURL, value);
        return this;
    }

    public NewsSelection imageDesc(String... value) {
        addEquals(NewsColumns.IMAGEDESC, value);
        return this;
    }

    public NewsSelection imageDescNot(String... value) {
        addNotEquals(NewsColumns.IMAGEDESC, value);
        return this;
    }

    public NewsSelection imageDescLike(String... value) {
        addLike(NewsColumns.IMAGEDESC, value);
        return this;
    }

    public NewsSelection imageDescContains(String... value) {
        addContains(NewsColumns.IMAGEDESC, value);
        return this;
    }

    public NewsSelection imageDescStartsWith(String... value) {
        addStartsWith(NewsColumns.IMAGEDESC, value);
        return this;
    }

    public NewsSelection imageDescEndsWith(String... value) {
        addEndsWith(NewsColumns.IMAGEDESC, value);
        return this;
    }

    public NewsSelection bookmarked(boolean value) {
        addEquals(NewsColumns.BOOKMARKED, toObjectArray(value));
        return this;
    }
}
