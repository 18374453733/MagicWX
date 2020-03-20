package com.hdpfans.app.service;

import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.hdpfans.api.RemoteApi;
import com.hdpfans.app.data.manager.FileManager;
import com.hdpfans.app.data.repository.DiySourceRepository;
import com.hdpfans.app.data.repository.HdpRepository;
import com.hdpfans.app.model.entity.ChannelModel;
import com.hdpfans.app.model.event.InstallApkEvent;
import com.hdpfans.app.reactivex.DefaultDisposableCompletableObserver;
import com.hdpfans.app.ui.live.LivePlayActivity;
import com.hdpfans.app.utils.plugin.PluginLoader;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.DaggerIntentService;
import fi.iki.elonen.NanoHTTPD;
import hdp.player.ApiKeys;
import okio.BufferedSource;
import okio.Okio;

import static fi.iki.elonen.NanoHTTPD.MIME_HTML;

/**
 * 本地服务器
 */
public class LocalServerService extends DaggerIntentService {

    @Inject
    FileManager mFileManager;
    @Inject
    DiySourceRepository mDiySourceRepository;
    @Inject
    HdpRepository mHdpRepository;
    @Inject
    PluginLoader mPluginLoader;

    private File mHttpdFile;

    public LocalServerService() {
        super("LocalServerService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        mHttpdFile = new File(getFilesDir(), "httpd");
        try {
            if (!mHttpdFile.exists()) {
                mFileManager.unzipFile(getAssets().open("hdp.zip"), mHttpdFile.getAbsolutePath());
            }
            new AppServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(LocalServerService.class.hashCode(), new Notification());
        }
    }

    /**
     * App服务器
     */
    private class AppServer extends NanoHTTPD {


        private List<Action> mRegisteredActions = Arrays.asList(
                new UploadApkAction(),
                new UploadDiySourceAction(),
                new ImmediatePlayAction()
        );

        public AppServer() throws IOException {
            super(mDiySourceRepository.getRemotePort());
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        }

        @Override
        public Response serve(IHTTPSession session) {
            try {
                if (session.getMethod() == Method.GET) {
                    return doLocalHttpdSession(session);
                }
                for (Action mRegisteredAction : mRegisteredActions) {
                    Controller controller = mRegisteredAction.getClass().getAnnotation(Controller.class);
                    if (controller != null && session.getMethod().name().equalsIgnoreCase(controller.method()) && session.getUri().equals(controller.path())) {
                        return mRegisteredAction.execute(session);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return super.serve(session);
        }

        private Response doLocalHttpdSession(IHTTPSession session) throws FileNotFoundException {
            String uri = session.getUri();
            if ("/".equals(uri)) {
                RemoteApi api = mPluginLoader.createApi(RemoteApi.class);
                if (api == null || !api.isOpenUploadSource()) {
                    uri = "index2.html";
                } else {
                    uri = "index1.html";
                }
            }
            return newChunkedResponse(
                    NanoHTTPD.Response.Status.OK,
                    getMimeTypeForFile(uri),
                    Okio.buffer(Okio.source(new File(mHttpdFile.getAbsoluteFile() + File.separator + uri))).inputStream());
        }

    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Controller {

        String method();

        String path();
    }

    public interface Action {

        NanoHTTPD.Response execute(NanoHTTPD.IHTTPSession session) throws Exception;
    }

    /**
     * 安装apk
     */
    @Controller(method = "POST", path = "/install")
    private class UploadApkAction implements Action {

        @Override
        public NanoHTTPD.Response execute(NanoHTTPD.IHTTPSession session) {
            try {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(getApplicationContext(), "正在读取apk,请稍后..", Toast.LENGTH_SHORT).show()
                );
                Map<String, String> files = new HashMap<>();
                session.parseBody(files);
                Map<String, List<String>> parameters = session.getParameters();
                String fileName = parameters.get("file").get(0);
                if (fileName.endsWith(".apk")) {
                    File tmpFile = new File(files.get("file"));
                    File apkFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
                    if (apkFile.exists()) {
                        apkFile.delete();
                    }

                    mFileManager.copyFile(tmpFile, apkFile);
                    EventBus.getDefault().post(new InstallApkEvent(apkFile.getAbsolutePath()));
                    return NanoHTTPD.newFixedLengthResponse("上传成功！");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    /**
     * 上传节目源
     */
    @Controller(method = "POST", path = "/upload")
    private class UploadDiySourceAction implements Action {

        @Override
        public NanoHTTPD.Response execute(NanoHTTPD.IHTTPSession session) {
            try {
                Map<String, String> files = new HashMap<>();
                session.parseBody(files);
                Map<String, List<String>> parameters = session.getParameters();
                String fileName = parameters.get("file").get(0);
                int diyId = Integer.parseInt(session.getHeaders().get("diy"));
                if (fileName.endsWith(".txt") || fileName.endsWith(".tv")) {
                    File tmpFile = new File(files.get("file"));
                    BufferedSource bufferedSource = Okio.buffer(Okio.source(tmpFile));
                    String sourceLine;
                    LinkedHashMap<String, String> diySourceMap = new LinkedHashMap<>();
                    while ((sourceLine = bufferedSource.readUtf8Line()) != null) {
                        String[] sourceSplit = sourceLine.split(",");
                        diySourceMap.put(sourceSplit[0], sourceSplit[1]);
                    }
                    mDiySourceRepository.insertDiySource(diyId, diySourceMap).subscribe(new DefaultDisposableCompletableObserver() {
                        @Override
                        public void onComplete() {
                            super.onComplete();
                            Toast.makeText(getApplicationContext(), "上传成功！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return NanoHTTPD.newFixedLengthResponse(String.format(Locale.getDefault(), "自定义源%d上传成功！", diyId));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * 即时播放
     */
    @Controller(method = "POST", path = "/play")
    private class ImmediatePlayAction implements Action {

        @Override
        public NanoHTTPD.Response execute(NanoHTTPD.IHTTPSession session) {
            String url = session.getParameters().get("Url").get(0);
            if (!TextUtils.isEmpty(url)) {
                ChannelModel immediateChannel = mHdpRepository.buildImmediateChannel(url);
                Intent intent = new Intent(LivePlayActivity.ACTION_PLAY_API);
                intent.putExtra(ApiKeys.INTENT_API_CHANNEL_NUM, immediateChannel.getNum());
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
            String result = "<html lang='zh'><head><meta charset='UTF-8'></meta></head><script language='javascript'>alert('推送成功!');window.history.go(-1);</script></html>";
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, MIME_HTML, result);
        }
    }
}
