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

################################################
## Glide - https://github.com/bumptech/glide
################################################
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Kotlinx Serialization(https://github.com/Kotlin/kotlinx.serialization)
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt # core serialization annotations

-keep,includedescriptorclasses class au.com.rsvp.app.**$$serializer { *; }
-keepclassmembers class au.com.rsvp.app.* {
    *** Companion;
}
-keepclasseswithmembers class au.com.rsvp.app.* {
    kotlinx.serialization.KSerializer serializer(...);
}

################################################
## Get deobfuscated crash reports with the Firebase Crashlytics SDK
##  - https://firebase.google.com/docs/crashlytics/get-deobfuscated-reports?platform=android
################################################
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
