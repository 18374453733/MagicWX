package com.hdpfans.plugin.spider.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Pair;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hdpfans.plugin.spider.SpiderHttpLeader;
import okhttp3.Request;

public class Http163 extends SpiderHttpLeader {

    private static final String PLAY_URL = "https://live.wasu.cn/show/id/%s";
    private static final String VIDEO_URL = "https://apiontime.wasu.cn/Auth/getVideoUrl?id=%s&mode=2&key=%s&url=%s&type=xml";
    private static final String HEADER_UA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36";

    public Http163(Context context) {
        super(context);
    }

    @Override
    public boolean hint(String food) {
        return food.startsWith("http163://");
    }

    @Override
    public Pair<String, Map<String, String>> silking(String food) {
        try {
            String id = getHttpAttr(food).second;
            String e = e(id);
            if (!TextUtils.isEmpty(e)) {
                food = c(b(e, id), id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Pair<>(food, getHeaders(food));
    }

    public String a(String str) {
        return new String(Base64.decode(str, 0));
    }

    public String a(String str, String str2) {
        return a(str, str2, 1);
    }

    public String a(String str, String str2, int i) {
        Matcher matcher = Pattern.compile(str2).matcher(str);
        return matcher.find() ? matcher.group(i) : null;
    }

    public String a(byte[] bArr) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bArr) {
            String toHexString = Integer.toHexString(b & 255);
            if (toHexString.length() == 1) {
                stringBuilder.append('0');
            }
            stringBuilder.append(toHexString);
        }
        return stringBuilder.toString();
    }

    private String md5(String str) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(str.getBytes());
            return a(instance.digest());
        } catch (Throwable th) {
            //Log.v("http163", "fail22bbb11:"+Log.getStackTraceString(th));
            return "";
        }
    }

    public String d(String str) {
        String str2 = null;
        int i = 0;
        if (!TextUtils.isEmpty(str)) {
            try {
                if (!TextUtils.isEmpty(str)) {
                    String substring = str.substring(0, str.lastIndexOf("/") + 1);
                    String str3 = "\n";
                    String[] split = h(str).split("\n");
                    for (int i2 = 0; i2 < split.length; i2++) {
                        if (split[i2].indexOf(".ts") > 0) {
                            split[i2] = substring + split[i2];
                        }
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    while (i < split.length) {
                        stringBuilder.append(split[i] + "\r\n");
                        i++;
                    }
                    str2 = stringBuilder.toString();
                }
            } catch (Exception e) {
                //Log.v("http163", "fail258f52:"+Log.getStackTraceString(e));
            }
        }
        return str2;
    }

    private String a(String str, String str2, String str3) {

        try {
            if (!TextUtils.isEmpty(str) && str.contains(str2)) {
                String substring = str.substring(str.indexOf(str2) + str2.length());
                if (!TextUtils.isEmpty(substring) && substring.contains(str3)) {
                    return substring.substring(0, substring.indexOf(str3)).trim();
                }
            }
        } catch (Exception e) {
            //Log.v("http163", "fail6662:"+Log.getStackTraceString(e));
        }

        return "";
    }

    private String b(String str, String str2) {


        try {
            String a = a(str, "_playUrl = '(.*?)'");
            String substring = str.substring(a.length() + str.indexOf(a));
            a = a(substring, "_playUrlhls ='(.*?)'");
            if (a == null) {
                a = "aHR0cDovL2xpdmVobHMxLXlmLndhc3UuY24vaGRfemp3cy96Lm0zdTg=";
            }
            substring = a(substring, "_playKey = '(.*?)'");
            return String.format(VIDEO_URL, str2, substring, a);
        } catch (Exception e) {
            //	Log.v("http163", "fail3333:"+Log.getStackTraceString(e));
        }
        return "";

    }

    private String c(String str, String str2) {
        String i = i(a(h(str), "<video><![CDATA[", "]]></video>"));
        return i + "&vid=" + str2 + "&cid=" + "9" + "&version=MIPlayer_V1.7.0&vtype=" + "201708160001" + "&sign=" + f(str2);
    }

    private String e(String str) {
        return h(String.format(String.format(PLAY_URL, str), str));
    }

    private String f(String str) {
        return sha1(str + "-----w-----9");
    }

    private String sha1(String str) {
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-1");
            instance.update(str.getBytes());
            return a(instance.digest());
        } catch (Throwable th) {
            //Log.v("http163", "failfff:"+Log.getStackTraceString(th));
            return "";
        }
    }

    private String h(String str) {
        Request request = new Request.Builder()
                .url(str)
                .header("User-Agent", HEADER_UA)
                .build();
        try {
            return getOkHttpClient().newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String i(String str) {

        try {
            int i = 0;
            if (str.contains(".mp4")) {
                return str;
            }
            int i2;
            String a = a(str.substring(4));
            String b = md5("8b39ed3e4385a7279ffec4de9fe7dd4a".substring(0, 16));
            String stringBuilder = b + md5(b + str.substring(0, 4));
            int length = stringBuilder.length();
            int[] iArr = new int[128];
            int[] iArr2 = new int[128];
            for (i2 = 0; i2 < 128; i2++) {
                iArr[i2] = i2;
                iArr2[i2] = stringBuilder.charAt((i2 % length) & 255);
            }
            int i3 = 0;
            for (i2 = 0; i2 < 128; i2++) {
                i3 = ((i3 + iArr[i2]) + iArr2[i2]) % 128;
                length = iArr[i2];
                iArr[i2] = iArr[i3];
                iArr[i3] = length;
            }
            byte[] bArr = new byte[a.length()];
            i2 = 0;
            i3 = 0;
            while (i < a.length()) {
                i3 = (i3 + 1) % 128;
                i2 = (i2 + iArr[i3]) % 128;
                int i4 = iArr[i3];
                iArr[i3] = iArr[i2];
                iArr[i2] = i4;
                bArr[i] = (byte) ((a.charAt(i) & 255) ^ iArr[(iArr[i3] + iArr[i2]) % 128]);
                i++;
            }
            String str2 = new String(bArr);
            if (str2.length() > 26) {
                str2 = str2.substring(26);
            }
            return str2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }
}
