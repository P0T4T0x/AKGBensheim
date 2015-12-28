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
package de.tobiaserthal.akgbensheim.backend.provider.teacher;

import android.net.Uri;
import android.provider.BaseColumns;

import de.tobiaserthal.akgbensheim.backend.provider.DataProvider;

/**
 * A teacher at the school.
 */
public class TeacherColumns implements BaseColumns {
    public static final String TABLE_NAME = "teacher";
    public static final Uri CONTENT_URI = Uri.parse(DataProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    /**
     * The teacher's first name.
     */
    public static final String FIRSTNAME = "firstName";

    /**
     * The teacher's last name.
     */
    public static final String LASTNAME = "lastName";

    /**
     * The teacher's shorthand, which is unique.
     */
    public static final String SHORTHAND = "shorthand";

    /**
     * A comma seperated list of subject this teacher teaches.
     */
    public static final String SUBJECTS = "subjects";

    /**
     * The teacher's email adress.
     */
    public static final String EMAIL = "email";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            FIRSTNAME,
            LASTNAME,
            SHORTHAND,
            SUBJECTS,
            EMAIL
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(FIRSTNAME) || c.contains("." + FIRSTNAME)) return true;
            if (c.equals(LASTNAME) || c.contains("." + LASTNAME)) return true;
            if (c.equals(SHORTHAND) || c.contains("." + SHORTHAND)) return true;
            if (c.equals(SUBJECTS) || c.contains("." + SUBJECTS)) return true;
            if (c.equals(EMAIL) || c.contains("." + EMAIL)) return true;
        }
        return false;
    }

}
