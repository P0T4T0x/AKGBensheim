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

import android.net.Uri;
import android.provider.BaseColumns;

import de.tobiaserthal.akgbensheim.backend.provider.DataProvider;

/**
 * A news article posted on the school's website.
 */
public class NewsColumns implements BaseColumns {
    public static final String TABLE_NAME = "news";
    public static final Uri CONTENT_URI = Uri.parse(DataProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    /**
     * The headline of this news post.
     */
    public static final String TITLE = "title";

    /**
     * The short summary of the article itself.
     */
    public static final String SNIPPET = "snippet";

    /**
     * The article body text.
     */
    public static final String ARTICLE = "article";

    /**
     * The url the article was originally fetched from
     */
    public static final String ARTICLEURL = "articleUrl";

    /**
     * The url for the header image.
     */
    public static final String IMAGEURL = "imageUrl";

    /**
     * The header image description.
     */
    public static final String IMAGEDESC = "imageDesc";

    /**
     * Whether this news post was bookmarked by the user.
     */
    public static final String BOOKMARKED = "bookmarked";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            TITLE,
            SNIPPET,
            ARTICLE,
            ARTICLEURL,
            IMAGEURL,
            IMAGEDESC,
            BOOKMARKED
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(TITLE) || c.contains("." + TITLE)) return true;
            if (c.equals(SNIPPET) || c.contains("." + SNIPPET)) return true;
            if (c.equals(ARTICLE) || c.contains("." + ARTICLE)) return true;
            if (c.equals(ARTICLEURL) || c.contains("." + ARTICLEURL)) return true;
            if (c.equals(IMAGEURL) || c.contains("." + IMAGEURL)) return true;
            if (c.equals(IMAGEDESC) || c.contains("." + IMAGEDESC)) return true;
            if (c.equals(BOOKMARKED) || c.contains("." + BOOKMARKED)) return true;
        }
        return false;
    }

}
