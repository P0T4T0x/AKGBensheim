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

# print config
-printconfiguration build/outputs/mapping/release/config.txt

# com.android.support.appcompat-v7
-keep public class android.support.v7.widget.SearchView {
    <init>(...);
 }

-keep public class android.support.v7.internal.widget.** {
    <init>(...);
 }

-keep public class android.support.v7.internal.view.menu.** {
    <init>(...);
 }

# com.android.support:cardview-v7
-keep class android.support.v7.widget.RoundRectDrawable {
    <init>(int, float);
 }

# com.android.support:support-v4
-keep public class * extends android.support.v4.view.ActionProvider {
    <init>(android.content.Context);
 }
