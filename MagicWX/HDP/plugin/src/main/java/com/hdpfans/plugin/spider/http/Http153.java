package com.hdpfans.plugin.spider.http;

import android.content.Context;
import android.net.Uri;
import android.util.Pair;

import com.hdpfans.plugin.spider.SpiderHttpLeader;
import com.iheartradio.m3u8.Encoding;
import com.iheartradio.m3u8.Format;
import com.iheartradio.m3u8.ParsingMode;
import com.iheartradio.m3u8.PlaylistParser;
import com.iheartradio.m3u8.PlaylistWriter;
import com.iheartradio.m3u8.data.MediaPlaylist;
import com.iheartradio.m3u8.data.Playlist;
import com.iheartradio.m3u8.data.TrackData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import fi.iki.elonen.NanoHTTPD;
import okhttp3.Request;

import static com.hdpfans.plugin.utils.Utils.md5;

public class Http153 extends SpiderHttpLeader {

    private static final int DEFAULT_PROXY_PORT = 16380;
    private static final String f310c = m536b();
    private static final String f311d = m538c();
    private static final String f312e = m540d();

    public Http153(Context context) {
        super(context);
        try {
            ProxyServer proxyServer = new ProxyServer(DEFAULT_PROXY_PORT);
            proxyServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hint(String food) {
        return food.startsWith("http153://");
    }

    @Override
    public Pair<String, Map<String, String>> silking(String food) {
        try {
            Object[] b = m537b(getHttpAttr(food).second);
            if (b == null) {
                return new Pair<>(food, getHeaders(food));
            }
            String c = m539c((String) b[0]);
            for (int i = 0; i < 5; i++) {
                c = m539c((String) b[0]);
                if (m437a(c, 3000)) {
                    break;
                }
            }
            food = String.format(Locale.getDefault(), "http://127.0.0.1:%d/index.html?href=%s&differ=%s&total=%s&play=replay",
                    DEFAULT_PROXY_PORT,
                    URLEncoder.encode(c),
                    b[1], b[2]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Pair<>(food, getHeaders(food));
    }

    private static String m538c() {
        String str = "";
        try {
            String substring = md5((((double) System.currentTimeMillis()) + new Random().nextDouble()) + "").substring(0,
                    12);
            int i = 0;
            while (i < substring.length()) {
                if (i % 2 == 0 && i != 0) {
                    str = str + "-";
                }
                String str2 = str + substring.charAt(i);
                i++;
                str = str2;
            }
        } catch (Exception e) {
        }

        return str.toUpperCase();
    }

    private static String m540d() {
        return String.format("%s/%s/%s+%s==", m535a(23), m535a(11), m535a(10), m535a(35));
    }

    private boolean m437a(String str, int i) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setReadTimeout(i);
            httpURLConnection.setConnectTimeout(i);
            httpURLConnection.setRequestProperty("Connection", "close");
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == 200 || responseCode == 302 || responseCode == 301 || responseCode == 303) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String m535a(int i) {
        String str = "";
        for (int i2 = 0; i2 < i; i2++) {
            str = str + m542e();
        }
        return str;
    }

    private static char m542e() {
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        return str.charAt(new Random().nextInt(str.length()));
    }

    private String m539c(String str) {
        try {
            return new JSONObject(m541d(str)).optString("info");
        } catch (Exception e) {
        }
        return "";
    }

    private String m541d(String str) throws IOException {
        Request request = new Request.Builder().url(str).build();
        return getOkHttpClient().newCall(request).execute().body().string();
    }

    private static String m536b() {
        try {
            return md5((((double) System.currentTimeMillis()) + new Random().nextDouble()) + "")
                    + md5(new Random().nextDouble() + "").substring(0, 8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    private Object[] m537b(String str) {
        int i;
        String str2;
        String str3 = "http://ott.liveapi.mgtv.com/v1/epg/turnplay/getLivePlayUrlM3u8?platform=3&definition=高清&ticket=&buss_id=1000014&device_id=%s&mac_id=%s&uuid=mgtvmac%s&after_day=1&license=%s&net_id=05100101000000&type=3&channel_id=%s&version=5.5.115.200.2.DBEI.0.0_Release";

        JSONObject optJSONObject;
        try {
            Object[] objArr = new Object[5];
            objArr[0] = f310c;
            objArr[1] = f311d;
            objArr[2] = f311d.replace("-", "");
            objArr[3] = f312e;
            objArr[4] = str;
            optJSONObject = new JSONObject(m541d(String.format(str3, objArr))).optJSONObject("data");

            long optLong = optJSONObject.optLong("server_time");
            if (optLong <= 0) {
                optLong = System.currentTimeMillis() / 1000;
            }
            JSONArray optJSONArray = optJSONObject.optJSONArray("play_list");
            int length = optJSONArray.length();
            String str4 = null;
            int i2 = 0; // 时间差
            int i3 = 0;
            if (length != 1) {
                for (int i4 = 0; i4 < length; i4++) {
                    JSONObject optJSONObject2 = optJSONArray.optJSONObject(i4);
                    String optString = optJSONObject2.optString("play_time");
                    String optString2 = optJSONObject2.optString("end_time");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                    long time = simpleDateFormat.parse(optString).getTime() / 1000; // 开始时间
                    long time2 = simpleDateFormat.parse(optString2).getTime() / 1000;   // 结束时间
                    if (time <= optLong && time2 > optLong) {
                        str4 = optJSONObject2.optString("url");
                        i3 = optJSONObject2.optInt("duration"); // 总共时间
                        i2 = (int) (optLong - time);
                        if (i2 < 0) {
                            i2 = 0;
                        }
                    }
                }
                i = i3;
                str2 = str4;
            } else {
                i = 0;
                str2 = null;
            }
            if (length == 1 || optJSONArray != null) {
                JSONObject optJSONObject3 = optJSONArray.optJSONObject(0);
                str2 = optJSONObject3.optString("url");
                try {
                    i = optJSONObject3.getInt("duration");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (i < 0) {
                    i = 0;
                }
            }
            return new Object[]{str2, Integer.valueOf(i2), Integer.valueOf(i)};
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public class ProxyServer extends NanoHTTPD {

        public ProxyServer(int port) {
            super(port);
        }

        @Override
        public Response serve(IHTTPSession session) {
            // 获取请求参数
            List<String> hrefParams = session.getParameters().get("href");
            if (hrefParams == null || hrefParams.isEmpty()) {
                return super.serve(session);
            }

            int differ = Integer.parseInt(session.getParameters().get("differ").get(0));
            String originUrl = hrefParams.get(0);

            try {
                // 解析m3u8数据
                String respStr = m541d(originUrl);
                PlaylistParser parser = new PlaylistParser(new ByteArrayInputStream(respStr.getBytes()), Format.EXT_M3U, Encoding.UTF_8, ParsingMode.LENIENT);
                Playlist playlist = parser.parse();
                MediaPlaylist mediaPlaylist = playlist.getMediaPlaylist();
                List<TrackData> tracks = mediaPlaylist.getTracks();

                float currentIgnoreTs = 0;
                float targetDuration = 0;

                // 拼接ts地址
                Uri uri = Uri.parse(originUrl);
                String path = uri.getScheme() + "://" + uri.getHost() + uri.getPath().substring(0, uri.getPath().lastIndexOf("/") + 1);
                List<TrackData> newTracks = new ArrayList<>(tracks.size());
                for (TrackData track : tracks) {
                    currentIgnoreTs += track.getTrackInfo().duration;
                    if (currentIgnoreTs <= differ) {
                        continue;
                    }
                    TrackData.Builder builder = new TrackData.Builder()
                            .withUri(path + track.getUri());
                    if (track.hasDiscontinuity()) {
                        builder.withDiscontinuity(track.hasDiscontinuity());
                    }
                    if (track.hasProgramDateTime()) {
                        builder.withProgramDateTime(track.getProgramDateTime());
                    }
                    if (track.hasTrackInfo()) {
                        if (track.getTrackInfo().duration > targetDuration) {
                            targetDuration = track.getTrackInfo().duration;
                        }
                        builder.withTrackInfo(track.getTrackInfo());
                    }
                    if (track.hasEncryptionData()) {
                        builder.withEncryptionData(track.getEncryptionData());
                    }

                    newTracks.add(builder.build());
                }

                // 重组m3u8数据
                MediaPlaylist updatedMediaPlaylist = playlist.getMediaPlaylist()
                        .buildUpon()
                        .withTracks(newTracks)
                        .withTargetDuration((int) targetDuration)
                        .build();

                Playlist updatedPlaylist = new Playlist.Builder()
                        .withMediaPlaylist(updatedMediaPlaylist)
                        .withCompatibilityVersion(playlist.getCompatibilityVersion())
                        .build();

                OutputStream outputStream = new ByteArrayOutputStream();
                PlaylistWriter writer = new PlaylistWriter(outputStream, Format.EXT_M3U, Encoding.UTF_8);
                writer.write(updatedPlaylist);

                return newFixedLengthResponse(outputStream.toString());
            } catch (Exception e) {
                return super.serve(session);
            }
        }
    }
}
