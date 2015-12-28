package de.tobiaserthal.akgbensheim.backend.utils;

/**
 * A simple wrapper class to improve logging style
 * and to help proguard remove calls.
 */
public class Log {

    /**
     * A wrapper for the {@link android.util.Log#i(String, String)} function
     * @param tag     The tag used in the logcat
     * @param message The message, which can be formatted according to String.format
     * @param args    The arguments, optional
     */
    public static void i(String tag, String message, Object... args) {
        android.util.Log.i(tag, String.format(message, args));
    }

    /**
     * A wrapper for the {@link android.util.Log#i(String, String, Throwable)} function
     * @param tag       The tag used in the logcat
     * @param throwable The throwable to print the stack trace of
     * @param message   The message, which can be formatted according to String.format
     * @param args      The arguments, optional
     */
    public static void i(String tag, Throwable throwable, String message, Object... args) {
        android.util.Log.i(tag, String.format(message, args), throwable);
    }

    /**
     * A wrapper for the {@link android.util.Log#d(String, String)} function
     * @param tag     The tag used in the logcat
     * @param message The message, which can be formatted according to String.format
     * @param args    The arguments, optional
     */
    public static void d(String tag, String message, Object... args) {
        android.util.Log.d(tag, String.format(message, args));
    }

    /**
     * A wrapper for the {@link android.util.Log#d(String, String, Throwable)} function
     * @param tag       The tag used in the logcat
     * @param throwable The throwable to print the stack trace of
     * @param message   The message, which can be formatted according to String.format
     * @param args      The arguments, optional
     */
    public static void d(String tag, Throwable throwable, String message, Object... args) {
        android.util.Log.d(tag, String.format(message, args), throwable);
    }

    /**
     * A wrapper for the {@link android.util.Log#w(String, String)} function
     * @param tag     The tag used in the logcat
     * @param message The message, which can be formatted according to String.format
     * @param args    The arguments, optional
     */
    public static void w(String tag, String message, Object... args) {
        android.util.Log.w(tag, String.format(message, args));
    }

    /**
     * A wrapper for the {@link android.util.Log#w(String, String, Throwable)} function
     * @param tag       The tag used in the logcat
     * @param throwable The throwable to print the stack trace of
     * @param message   The message, which can be formatted according to String.format
     * @param args      The arguments, optional
     */
    public static void w(String tag, Throwable throwable, String message, Object... args) {
        android.util.Log.w(tag, String.format(message, args), throwable);
    }

    /**
     * A wrapper for the {@link android.util.Log#e(String, String)} function
     * @param tag     The tag used in the logcat
     * @param message The message, which can be formatted according to String.format
     * @param args    The arguments, optional
     */
    public static void e(String tag, String message, Object... args) {
        android.util.Log.e(tag, String.format(message, args));
    }

    /**
     * A wrapper for the {@link android.util.Log#e(String, String, Throwable)} function
     * @param tag       The tag used in the logcat
     * @param throwable The throwable to print the stack trace of
     * @param message   The message, which can be formatted according to String.format
     * @param args      The arguments, optional
     */
    public static void e(String tag, Throwable throwable, String message, Object... args) {
        android.util.Log.e(tag, String.format(message, args), throwable);
    }

    /**
     * A wrapper for the {@link android.util.Log#isLoggable(String, int)} function
     * @param tag   The tag to check
     * @param level The level to check
     * @return      Whether this is allowed to be logged
     */
    public static boolean isLoggable(String tag, int level) {
        return android.util.Log.isLoggable(tag, level);
    }
}
