package com.hdpfans.plugin.epg;

import android.content.Context;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hdpfans.plugin.model.EpgInfoModel;
import com.hdpfans.plugin.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Okio;

public class TvSouEpgGet extends EpgGet {

    private static final String EPG_URL = "http://apks.juyoufan.net/epg_v2/epg_%s.zip";

    private static ExecutorService sSingleExecutorService = Executors.newSingleThreadExecutor();

    private static Map<String, List<EpgInfoModel>> sCachedEpgInfoModel = new ConcurrentHashMap<>();

    public TvSouEpgGet(Context context, String epgId) {
        super(context, epgId);
    }

    private String today() {
        return new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
    }

    private File getTodayEpgDir() {
        return new File(getEpgDir(), today());
    }

    private File getEpgDir() {
        File epgParentDir = new File(getContext().getFilesDir(), "epg");
        if (!epgParentDir.exists()) {
            epgParentDir.mkdirs();
        }
        return epgParentDir;
    }

    @Override
    public String getCurrentEpg() {
        return getCurrentEpgInfo().getTitle();
    }

    @Override
    public Pair<String, String> getCurrentEpgWithNext() {
        try {
            if (epgGetReady()) {
                List<EpgInfoModel> epgList = getEpgList();
                if (epgList != null && !epgList.isEmpty()) {
                    long currentTimeSecond = System.currentTimeMillis() / 1000;
                    for (int i = 0; i < epgList.size(); i++) {
                        EpgInfoModel epgInfoModel = epgList.get(i);
                        long startTime = Long.parseLong(epgInfoModel.getPlaytime());
                        long endTime = Long.parseLong(epgInfoModel.getEndtime());
                        if (currentTimeSecond >= startTime && currentTimeSecond <= endTime) {
                            String currentEpg = epgInfoModel.getTitle();
                            String nextEgp = NO_EPG;
                            if (epgList.size() > i + 1) {
                                nextEgp = epgList.get(i + 1).getTitle();
                            }
                            return new Pair<>(currentEpg, nextEgp);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Pair<>(NO_EPG, NO_EPG);
    }

    @Override
    public EpgInfoModel getCurrentEpgInfo() {
        EpgInfoModel epgInfo = new EpgInfoModel();
        try {
            if (!epgGetReady()) {
                epgInfo.setTitle("节目信息正在更新中");
                return epgInfo;
            }

            List<EpgInfoModel> epgList = getEpgList();
            if (epgList != null && !epgList.isEmpty()) {
                long currentTimeSecond = System.currentTimeMillis() / 1000;
                for (EpgInfoModel epgInfoModel : epgList) {
                    long startTime = Long.parseLong(epgInfoModel.getPlaytime());
                    long endTime = Long.parseLong(epgInfoModel.getEndtime());
                    if (currentTimeSecond >= startTime && currentTimeSecond <= endTime) {
                        return epgInfoModel;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        epgInfo.setTitle(NO_EPG);
        return epgInfo;
    }

    /**
     * 检查和下载EPG
     */
    private boolean epgGetReady() {
        if (!getTodayEpgDir().exists() || getTodayEpgDir().listFiles() == null) {
            synchronized (TvSouEpgGet.class) {
                if (!sSingleExecutorService.isShutdown()) {
                    sSingleExecutorService.execute(new EpgDownloader());
                    sSingleExecutorService.shutdown();
                }
            }
            return false;
        }
        return true;
    }

    /**
     * 分别从内存和文件中获取epg列表
     */
    private List<EpgInfoModel> getEpgList() throws Exception {
        List<EpgInfoModel> EpgInfoModels = sCachedEpgInfoModel.get(getEpgId());
        if (EpgInfoModels != null && !EpgInfoModels.isEmpty()) {
            return EpgInfoModels;
        }

        File[] files = getTodayEpgDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(getEpgId());
            }
        });
        if (files != null && files.length > 0) {
            String epgJosn = Okio.buffer(Okio.source(files[0])).readString(Charset.forName("utf-8"));
            List<EpgInfoModel> epgInfoModels = new Gson().fromJson(epgJosn, new TypeToken<List<EpgInfoModel>>() {
            }.getType());
            sCachedEpgInfoModel.put(getEpgId(), epgInfoModels);
            return epgInfoModels;
        }
        return null;
    }

    class EpgDownloader implements Runnable {

        @Override
        public void run() {
            String downloadUrl = String.format(Locale.getDefault(), EPG_URL, today());
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(downloadUrl).build();
            try {
                Response response = client.newCall(request).execute();
                File epgZipFile = new File(getEpgDir(), downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1));
                FileOutputStream fos = new FileOutputStream(epgZipFile);
                fos.write(response.body().bytes());
                fos.close();

                FileUtils.unzipFile(epgZipFile, getTodayEpgDir().getAbsolutePath());
                epgZipFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
