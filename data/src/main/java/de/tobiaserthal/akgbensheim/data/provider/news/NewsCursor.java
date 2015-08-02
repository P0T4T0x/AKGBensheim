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
package de.tobiaserthal.akgbensheim.data.provider.news;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.tobiaserthal.akgbensheim.data.model.news.NewsModel;
import de.tobiaserthal.akgbensheim.data.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code news} table.
 */
public class NewsCursor extends AbstractCursor implements NewsModel {

    public static NewsCursor wrap(Cursor cursor) {
        assert cursor != null;
        return new NewsCursor(cursor);
    }

    public NewsCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(NewsColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The headline of this news post.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getTitle() {
        String res = getStringOrNull(NewsColumns.TITLE);
        if (res == null)
            throw new NullPointerException("The value of 'title' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The short summary of the article itself.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getSnippet() {
        String res = getStringOrNull(NewsColumns.SNIPPET);
        if (res == null)
            throw new NullPointerException("The value of 'snippet' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The article body text.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getArticle() {
        String res = getStringOrNull(NewsColumns.ARTICLE);
        if (res == null)
            throw new NullPointerException("The value of 'article' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The url the article was originally fetched from
     * Cannot be {@code null}.
     */
    @NonNull
    public String getArticleUrl() {
        String res = getStringOrNull(NewsColumns.ARTICLEURL);
        if (res == null)
            throw new NullPointerException("The value of 'articleurl' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The url for the header image.
     * Can be {@code null}.
     */
    @Nullable
    public String getImageUrl() {
        return getStringOrNull(NewsColumns.IMAGEURL);
    }

    /**
     * The header image description.
     * Can be {@code null}.
     */
    @Nullable
    public String getImageDesc() {
        return getStringOrNull(NewsColumns.IMAGEDESC);
    }

    /**
     * Whether this news post was bookmarked by the user.
     */
    public boolean getBookmarked() {
        Boolean res = getBooleanOrNull(NewsColumns.BOOKMARKED);
        if (res == null)
            throw new NullPointerException("The value of 'bookmarked' in the database was null, which is not allowed according to the model definition");
        return res;
    }
}
