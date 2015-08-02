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
package de.tobiaserthal.akgbensheim.data.provider;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import de.tobiaserthal.akgbensheim.data.BuildConfig;
import de.tobiaserthal.akgbensheim.data.provider.event.EventColumns;
import de.tobiaserthal.akgbensheim.data.provider.homework.HomeworkColumns;
import de.tobiaserthal.akgbensheim.data.provider.news.NewsColumns;
import de.tobiaserthal.akgbensheim.data.provider.substitution.SubstitutionColumns;
import de.tobiaserthal.akgbensheim.data.provider.teacher.TeacherColumns;

public class DatabaseManager extends SQLiteOpenHelper {
    private static final String TAG = DatabaseManager.class.getSimpleName();

    private static final String DATABASE_FILE_NAME = "akgmobile.db";
    private static final int DATABASE_VERSION = 1;
    private static DatabaseManager sInstance;
    private final Context mContext;
    private final DatabaseManagerCallbacks mOpenHelperCallbacks;

    // @formatter:off
    public static final String SQL_CREATE_TABLE_EVENT = "CREATE TABLE IF NOT EXISTS "
            + EventColumns.TABLE_NAME + " ( "
            + EventColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + EventColumns.TITLE + " TEXT NOT NULL, "
            + EventColumns.EVENTDATE + " INTEGER NOT NULL, "
            + EventColumns.DATESTRING + " TEXT, "
            + EventColumns.DESCRIPTION + " TEXT "
            + " );";

    public static final String SQL_CREATE_TABLE_HOMEWORK = "CREATE TABLE IF NOT EXISTS "
            + HomeworkColumns.TABLE_NAME + " ( "
            + HomeworkColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + HomeworkColumns.TITLE + " TEXT NOT NULL, "
            + HomeworkColumns.TODODATE + " INTEGER NOT NULL, "
            + HomeworkColumns.NOTES + " TEXT, "
            + HomeworkColumns.DONE + " INTEGER NOT NULL DEFAULT 0 "
            + " );";

    public static final String SQL_CREATE_TABLE_NEWS = "CREATE TABLE IF NOT EXISTS "
            + NewsColumns.TABLE_NAME + " ( "
            + NewsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NewsColumns.TITLE + " TEXT NOT NULL, "
            + NewsColumns.SNIPPET + " TEXT NOT NULL, "
            + NewsColumns.ARTICLE + " TEXT NOT NULL, "
            + NewsColumns.ARTICLEURL + " TEXT NOT NULL, "
            + NewsColumns.IMAGEURL + " TEXT, "
            + NewsColumns.IMAGEDESC + " TEXT, "
            + NewsColumns.BOOKMARKED + " INTEGER NOT NULL DEFAULT 0 "
            + " );";

    public static final String SQL_CREATE_TABLE_SUBSTITUTION = "CREATE TABLE IF NOT EXISTS "
            + SubstitutionColumns.TABLE_NAME + " ( "
            + SubstitutionColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SubstitutionColumns.FORMKEY + " TEXT NOT NULL, "
            + SubstitutionColumns.SUBSTDATE + " INTEGER NOT NULL, "
            + SubstitutionColumns.PERIOD + " TEXT NOT NULL, "
            + SubstitutionColumns.TYPE + " TEXT NOT NULL DEFAULT 'Vertretung', "
            + SubstitutionColumns.LESSON + " TEXT NOT NULL DEFAULT '---', "
            + SubstitutionColumns.LESSONSUBST + " TEXT NOT NULL DEFAULT '---', "
            + SubstitutionColumns.ROOM + " TEXT NOT NULL DEFAULT '---', "
            + SubstitutionColumns.ROOMSUBST + " TEXT NOT NULL DEFAULT '---', "
            + SubstitutionColumns.ANNOTATION + " TEXT "
            + " );";

    public static final String SQL_CREATE_TABLE_TEACHER = "CREATE TABLE IF NOT EXISTS "
            + TeacherColumns.TABLE_NAME + " ( "
            + TeacherColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TeacherColumns.FIRSTNAME + " TEXT NOT NULL, "
            + TeacherColumns.LASTNAME + " TEXT NOT NULL, "
            + TeacherColumns.SHORTHAND + " TEXT NOT NULL, "
            + TeacherColumns.SUBJECTS + " TEXT, "
            + TeacherColumns.EMAIL + " TEXT "
            + ", CONSTRAINT uniqueName UNIQUE (firstName, lastName) ON CONFLICT REPLACE"
            + ", CONSTRAINT uniqueShort UNIQUE (shorthand) ON CONFLICT REPLACE"
            + " );";

    // @formatter:on

    public static DatabaseManager getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = newInstance(context.getApplicationContext());
        }
        return sInstance;
    }

    private static DatabaseManager newInstance(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return newInstancePreHoneycomb(context);
        }
        return newInstancePostHoneycomb(context);
    }


    /*
     * Pre Honeycomb.
     */
    private static DatabaseManager newInstancePreHoneycomb(Context context) {
        return new DatabaseManager(context);
    }

    private DatabaseManager(Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
        mContext = context;
        mOpenHelperCallbacks = new DatabaseManagerCallbacks();
    }


    /*
     * Post Honeycomb.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static DatabaseManager newInstancePostHoneycomb(Context context) {
        return new DatabaseManager(context, new DefaultDatabaseErrorHandler());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private DatabaseManager(Context context, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION, errorHandler);
        mContext = context;
        mOpenHelperCallbacks = new DatabaseManagerCallbacks();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreate");
        mOpenHelperCallbacks.onPreCreate(mContext, db);
        db.execSQL(SQL_CREATE_TABLE_EVENT);
        db.execSQL(SQL_CREATE_TABLE_HOMEWORK);
        db.execSQL(SQL_CREATE_TABLE_NEWS);
        db.execSQL(SQL_CREATE_TABLE_SUBSTITUTION);
        db.execSQL(SQL_CREATE_TABLE_TEACHER);
        mOpenHelperCallbacks.onPostCreate(mContext, db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            setForeignKeyConstraintsEnabled(db);
        }
        mOpenHelperCallbacks.onOpen(mContext, db);
    }

    private void setForeignKeyConstraintsEnabled(SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setForeignKeyConstraintsEnabledPreJellyBean(db);
        } else {
            setForeignKeyConstraintsEnabledPostJellyBean(db);
        }
    }

    private void setForeignKeyConstraintsEnabledPreJellyBean(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setForeignKeyConstraintsEnabledPostJellyBean(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mOpenHelperCallbacks.onUpgrade(mContext, db, oldVersion, newVersion);
    }
}
