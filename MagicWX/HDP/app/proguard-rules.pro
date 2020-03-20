-obfuscationdictionary ../buildsystem/dict.txt
-classobfuscationdictionary ../buildsystem/dict.txt
-packageobfuscationdictionary ../buildsystem/dict.txt

-useuniqueclassmembernames
-allowaccessmodification
-keepattributes SourceFile,LineNumberTable
-keepattributes InnerClasses,Signature,EnclosingMethod

-keepclasseswithmembernames class * {
    native <methods>;
}

-keep class android.support.** {*;}
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

## Model ##
-keep class com.hdpfans.app.model.entity.** { *; }

## Api ##
-keep interface com.hdpfans.api.** {
    <methods>;
}

## Presenter ##
-keep,allowobfuscation @interface com.hdpfans.app.frame.Presenter
-keepclassmembers class * {
    @com.hdpfans.app.frame.Presenter <fields>;
}

################################## dependencies

## leakcanary ##
-dontwarn com.squareup.haha.guava.**
-dontwarn com.squareup.haha.perflib.**
-dontwarn com.squareup.haha.trove.**
-dontwarn com.squareup.leakcanary.**
-keep class com.squareup.haha.** { *; }
-keep class com.squareup.leakcanary.** { *; }

# Marshmallow removed Notification.setLatestEventInfo()
-dontwarn android.app.Notification

## stetho ##
-keep class com.facebook.stetho.** {
  *;
}

## retrofit2 ##
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

## butterknife ##
# Retain generated class which implement Unbinder.
-keep public class * implements butterknife.Unbinder {public <init>(**, android.view.View); }

# Prevent obfuscation of types which use ButterKnife annotations since the simple name
# is used to reflectively look up the generated ViewBinding.
-keep class butterknife.*
-keepclasseswithmembernames class * {@butterknife.* <methods>; }
-keepclasseswithmembernames class * {@butterknife.* <fields>; }

## OKHttp ##
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** {*;}
-keep class okio.** {*;}
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

## Gson ##
-keep class com.google.gson.** {*;}

## Glide ##
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder

## EventBus ##
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

## ijkplayer ##
-keep class tv.danmaku.ijk.media.player.** {*;}
-keep class tv.danmaku.ijk.media.player.IjkMediaPlayer{*;}
-keep class tv.danmaku.ijk.media.player.ffmpeg.FFmpegApi{*;}

## oss ##
-keep class com.alibaba.sdk.android.oss.** { *; }
-dontwarn org.apache.commons.codec.binary.**

## cntv ##
-keep class cntv.player.media.player.** { *; }

## bugly ##
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

## NonaHTTPD ##
-keep class fi.iki.elonen.** { *; }

## mtj ##
-keep class com.baidu.kirin.** { *; }
-keep class com.baidu.mobstat.** { *; }
-keep class com.baidu.bottom.** { *; }

## rxdownload ##
-keep class zlc.season.rxdownload3.** { *; }
