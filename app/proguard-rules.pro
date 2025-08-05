# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

## ------------ Retrofit & OkHttp ------------
#-dontwarn okhttp3.**
#-dontwarn retrofit2.**
#-keep class retrofit2.** { *; }
#-keep class com.squareup.okhttp3.** { *; }
#-keepattributes Signature
#-keepattributes *Annotation*
#
## ------------ Gson (for JSON parsing) ------------
#-keep class com.google.gson.** { *; }
#-keepattributes *Annotation*
#
## ------------ Glide ------------
#-keep public class * implements com.bumptech.glide.module.GlideModule
#-keep public class * extends com.bumptech.glide.AppGlideModule
#-keep class com.bumptech.glide.** { *; }
#-dontwarn com.bumptech.glide.**
#
## ------------ Room Database ------------
#-keep class androidx.room.Entity
#-keep class androidx.room.Dao
#-keep class androidx.room.Database
#-keep class androidx.room.RoomDatabase
#-keepclassmembers class * {
#    @androidx.room.* <methods>;
#}
#
## ------------ Hilt / Dagger ------------
#-keep class dagger.hilt.** { *; }
#-keep class javax.inject.** { *; }
#-keep class dagger.** { *; }
#-dontwarn dagger.**
#-dontwarn javax.inject.**
#
## ------------ ViewModel (Lifecycle) ------------
#-keep class androidx.lifecycle.ViewModel
#-keepclassmembers class * extends androidx.lifecycle.ViewModel {
#    <init>(...);
#}
#
## ------------ Optional (Keep Model Classes) ------------
## -keep class com.zappcrm.myapplication.model.** { *; }
#
## ------------ Misc ------------
## Retain method/field names for debugging
#-keepattributes SourceFile,LineNumberTable
#
## Keep enums
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#
## Remove unused code
#-dontnote
#-dontwarn
#
