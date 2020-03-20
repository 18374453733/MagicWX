#-obfuscationdictionary ../buildsystem/dict.txt
#-classobfuscationdictionary ../buildsystem/dict.txt
#-packageobfuscationdictionary ../buildsystem/dict.txt

-keepattributes SourceFile,LineNumberTable
-keepattributes InnerClasses,Signature,EnclosingMethod

# 保留入口函数
-keep class * implements com.hdpfans.api.Api {
    public <methods>;
}

# 保留配置信息
-keep class **.BuildConfig {*;}

# 保留Model信息
-keep class com.hdpfans.plugin.model.** { *;}

# 保留lgsg
-keep class android.apk.** { *; }

# 保留Fuck
-keep class com.hdp.Fuck { *; }

# 保留TvBus
-keep class com.tvbus.engine.** { *; }

# open-m3u8
-keep class com.iheartradio.m3u8.** { *; }
-dontwarn com.iheartradio.m3u8.**
