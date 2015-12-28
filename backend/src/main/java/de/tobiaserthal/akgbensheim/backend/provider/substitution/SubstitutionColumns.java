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
package de.tobiaserthal.akgbensheim.backend.provider.substitution;

import android.net.Uri;
import android.provider.BaseColumns;

import de.tobiaserthal.akgbensheim.backend.provider.DataProvider;

/**
 * A substitution that was loaded from the school's website.
 */
public class SubstitutionColumns implements BaseColumns {
    public static final String TABLE_NAME = "substitution";
    public static final Uri CONTENT_URI = Uri.parse(DataProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    /**
     * The identifier of the form affected by the substitution.
     */
    public static final String FORMKEY = "formKey";

    /**
     * The date the substitution takes place.
     */
    public static final String SUBSTDATE = "substDate";

    /**
     * The peroid affected by the substitution.
     */
    public static final String PERIOD = "period";

    /**
     * The type of the substitution.
     */
    public static final String TYPE = "type";

    /**
     * The affected lesson.
     */
    public static final String LESSON = "lesson";

    /**
     * The lesson going to take place instead of "lesson".
     */
    public static final String LESSONSUBST = "lessonSubst";

    /**
     * The room for the affected lesson.
     */
    public static final String ROOM = "room";

    /**
     * The room the lesson is going to take place.
     */
    public static final String ROOMSUBST = "roomSubst";

    /**
     * Annotations for additional infos about this substitution.
     */
    public static final String ANNOTATION = "annotation";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            FORMKEY,
            SUBSTDATE,
            PERIOD,
            TYPE,
            LESSON,
            LESSONSUBST,
            ROOM,
            ROOMSUBST,
            ANNOTATION
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(FORMKEY) || c.contains("." + FORMKEY)) return true;
            if (c.equals(SUBSTDATE) || c.contains("." + SUBSTDATE)) return true;
            if (c.equals(PERIOD) || c.contains("." + PERIOD)) return true;
            if (c.equals(TYPE) || c.contains("." + TYPE)) return true;
            if (c.equals(LESSON) || c.contains("." + LESSON)) return true;
            if (c.equals(LESSONSUBST) || c.contains("." + LESSONSUBST)) return true;
            if (c.equals(ROOM) || c.contains("." + ROOM)) return true;
            if (c.equals(ROOMSUBST) || c.contains("." + ROOMSUBST)) return true;
            if (c.equals(ANNOTATION) || c.contains("." + ANNOTATION)) return true;
        }
        return false;
    }

}
