# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /usr/local/opt/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html
#
# Add any project specific keep options here:

# remove log calls to android log
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int d(...);
    public static int w(...);
    public static int e(...);
}

# remove log calls to log wrapper
-assumenosideeffects class de.tobiaserthal.akgbensheim.backend.utils.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static void i(...);
    public static void d(...);
    public static void w(...);
    public static void e(...);
}

# Rules for retrofit
-keep class retrofit.** { *; }
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }

-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

-dontwarn okio.**
-dontwarn retrofit.**
-dontwarn com.squareup.okhttp.**
