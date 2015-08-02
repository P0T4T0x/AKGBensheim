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

package de.tobiaserthal.akgbensheim.data.model.news;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.tobiaserthal.akgbensheim.data.model.base.BaseModel;

/**
 * A news article posted on the school's website.
 */
public interface NewsModel extends BaseModel {

    /**
     * The headline of this news post.
     * Cannot be {@code null}.
     */
    @NonNull
    String getTitle();

    /**
     * The short summary of the article itself.
     * Cannot be {@code null}.
     */
    @NonNull
    String getSnippet();

    /**
     * The article body text.
     * Cannot be {@code null}.
     */
    @NonNull
    String getArticle();

    /**
     * The url the article was originally fetched from
     * Cannot be {@code null}.
     */
    @NonNull
    String getArticleUrl();

    /**
     * The url for the header image.
     * Can be {@code null}.
     */
    @Nullable
    String getImageUrl();

    /**
     * The header image description.
     * Can be {@code null}.
     */
    @Nullable
    String getImageDesc();

    /**
     * Whether this news post was bookmarked by the user.
     */
    boolean getBookmarked();
}
