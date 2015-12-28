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
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import de.tobiaserthal.akgbensheim.backend.model.news.NewsModel;
import de.tobiaserthal.akgbensheim.backend.model.news.NewsModelBuilder;
import de.tobiaserthal.akgbensheim.backend.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code news} table.
 */
public class NewsContentValues extends AbstractContentValues implements NewsModelBuilder<NewsContentValues> {

    public static NewsContentValues wrap(NewsModel model) {
        NewsContentValues result = new NewsContentValues();

        result.putId(model.getId());
        result.putTitle(model.getTitle());
        result.putSnippet(model.getSnippet());
        result.putArticle(model.getArticle());
        result.putArticleUrl(model.getArticleUrl());

        String imageUrl = model.getImageUrl();
        if(!TextUtils.isEmpty(imageUrl)) {
            result.putImageUrl(imageUrl);
        } else {
            result.putImageUrlNull();
        }

        String imageDesc = model.getImageDesc();
        if(!TextUtils.isEmpty(imageDesc)) {
            result.putImageDesc(imageDesc);
        } else {
            result.putImageDescNull();
        }

        return result;
    }
    
    @Override
    public Uri uri() {
        return NewsColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable NewsSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    @Override
    public NewsContentValues putId(long id) {
        put(NewsColumns._ID, id);
        return this;
    }

    @Override
    public NewsContentValues putTitle(@NonNull String value) {
        put(NewsColumns.TITLE, value);
        return this;
    }

    @Override
    public NewsContentValues putSnippet(@NonNull String value) {
        put(NewsColumns.SNIPPET, value);
        return this;
    }

    @Override
    public NewsContentValues putArticle(@NonNull String value) {
        put(NewsColumns.ARTICLE, value);
        return this;
    }

    @Override
    public NewsContentValues putArticleUrl(@NonNull String value) {
        put(NewsColumns.ARTICLEURL, value);
        return this;
    }

    @Override
    public NewsContentValues putImageUrl(@Nullable String value) {
        put(NewsColumns.IMAGEURL, value);
        return this;
    }

    /**
     * Set the imageUrl as {@code null}.
     */
    public NewsContentValues putImageUrlNull() {
        putNull(NewsColumns.IMAGEURL);
        return this;
    }

    @Override
    public NewsContentValues putImageDesc(@Nullable String value) {
        put(NewsColumns.IMAGEDESC, value);
        return this;
    }

    /**
     * Set the imageUrl as {@code null}.
     */
    public NewsContentValues putImageDescNull() {
        putNull(NewsColumns.IMAGEDESC);
        return this;
    }

    @Override
    public NewsContentValues putBookmarked(boolean value) {
        put(NewsColumns.BOOKMARKED, value);
        return this;
    }
}
