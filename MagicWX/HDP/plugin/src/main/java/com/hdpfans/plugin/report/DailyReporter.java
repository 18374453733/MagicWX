package com.hdpfans.plugin.report;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.hdpfans.api.BuildConfig;
import com.hdpfans.plugin.compat.RegionCompat;
import com.hdpfans.plugin.model.DailyReportModel;
import com.hdpfans.plugin.model.OperateLogModel;
import com.hdpfans.plugin.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FilenameFilter;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSource;
import okio.Okio;

/**
 * 数据上报
 */
public class DailyReporter {

    public interface OperateType {
        String START = "start";

        String CHANGE = "change";

        String PLAY = "play";

        String EXIT = "exit";
    }

    private static final String REPORT_URL = "http://stat.juyoufan.net/api/collection/index";

    private static final String LOG_FILE_REGEX = "(\\d+).rep";

    private static final String LOG_FILE_NAME = "%d.rep";

    private SharedPreferences preferences;
    private File mLogDir;
    private Context context;
    private int currentTime;
    private Gson mGson = new Gson();

    public DailyReporter(Context context) {
        this.context = context.getApplicationContext();
        this.currentTime = Integer.parseInt(new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date()));
        this.mLogDir = context.getFileStreamPath("report_log");

        onEvent(OperateType.START);
    }

    public void report() {
        File[] beforeLogFile = getBeforeLogFile();
        if (beforeLogFile != null && beforeLogFile.length > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    reportToServer();
                }
            }).start();
        }
    }

    private void reportToServer() {
        String reportJson = mGson.toJson(collectReportData());
        if (BuildConfig.DEBUG) {
            try {
                JSONObject jsonObject = new JSONObject(reportJson);
                jsonObject.put("test_date", "2018_10_09");
                reportJson = jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), reportJson);
        Request request = new Request.Builder().url(REPORT_URL).post(body).build();
        try {
            Response response = new OkHttpClient().newCall(request).execute();
            if (response.isSuccessful()) {
                File[] beforeLogFile = getBeforeLogFile();
                if (beforeLogFile != null) {
                    for (File file : beforeLogFile) {
                        file.delete();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DailyReportModel collectReportData() {
        DailyReportModel dailyReportModel = new DailyReportModel();
        dailyReportModel.setAndroidId(Utils.androidID(context));
        dailyReportModel.setMac(Utils.macAddress(context));
        if (RegionCompat.getsInstance(context).getRegionModel() != null) {
            dailyReportModel.setCity(RegionCompat.getsInstance(context).getRegionModel().getCity());
            dailyReportModel.setRegion(RegionCompat.getsInstance(context).getRegionModel().getRegion());
        }
        dailyReportModel.setOsModel(Build.MODEL + "___" + Build.BRAND);
        dailyReportModel.setOskey(Utils.getOsKey(context));
        dailyReportModel.setUseragent(Utils.getUserAgent());
        dailyReportModel.setVersion(Utils.getVersionCode(context) + "_" + Utils.getVersionName(context) + "_" + Utils.buildFlavor(context));

        String brandName = Build.MANUFACTURER == null ? "" : Build.MANUFACTURER;
        if (TextUtils.isEmpty(brandName)) {
            brandName = "unkonwm";
        } else {
            brandName = brandName.replaceAll(" ", "");
        }
        dailyReportModel.setBrandName(brandName);
        dailyReportModel.setOtherApps(getInstalledApp());
        dailyReportModel.setOperateLog(getBeforeOperateLog());
        return dailyReportModel;
    }

    /**
     * 获取已安装的应用
     */
    private String getInstalledApp() {
        try {
            JSONArray appJsonArr = new JSONArray();
            PackageManager packageManager = context.getPackageManager();
            List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
            for (PackageInfo packageInfo : packageInfos) {
                // 过滤掉系统app
                if ((ApplicationInfo.FLAG_SYSTEM & packageInfo.applicationInfo.flags) != 0) {
                    continue;
                }
                JSONObject appJsonObj = new JSONObject();
                appJsonObj.put("package", packageInfo.packageName);
                appJsonObj.put("name", packageInfo.applicationInfo.loadLabel(packageManager).toString());
                appJsonObj.put("version", packageInfo.versionName);
                appJsonArr.put(appJsonObj);
            }
            return appJsonArr.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private List<OperateLogModel> getBeforeOperateLog() {
        List<OperateLogModel> beforeAllOperateLog = new ArrayList<>();

        try {
            File[] logFiles = getBeforeLogFile();
            if (logFiles != null && logFiles.length > 0) {
                for (File logFile : logFiles) {
                    BufferedSource source = Okio.buffer(Okio.source(logFile));
                    while (true) {
                        String log = source.readUtf8Line();
                        if (TextUtils.isEmpty(log)) break;
                        beforeAllOperateLog.add(mGson.fromJson(log, OperateLogModel.class));
                    }


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beforeAllOperateLog;
    }

    private File[] getBeforeLogFile() {
        return mLogDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                try {

                    Matcher matcher = Pattern.compile(LOG_FILE_REGEX).matcher(name);
                    if (matcher.find()) {
                        int dailyTime = Integer.parseInt(matcher.group(1));
                        return dailyTime < currentTime;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    public void onEvent(String type) {
        OperateLogModel operateLogModel = new OperateLogModel();
        operateLogModel.setType(type);
        operateLogModel.setTime(System.currentTimeMillis() / 1000);
        operateLogModel.setStartTime(System.currentTimeMillis() / 1000);
        saveOperate(operateLogModel);
    }

    public void onEvent(String type, String id, String name, String itemId, String title) {
        OperateLogModel operateLogModel = new OperateLogModel();
        operateLogModel.setId(id);
        operateLogModel.setTvClass(itemId);
        operateLogModel.setName(name);
        operateLogModel.setType(type);
        operateLogModel.setTitle(title);
        operateLogModel.setTime(System.currentTimeMillis() / 1000);
        operateLogModel.setStartTime(System.currentTimeMillis() / 1000);
        saveOperate(operateLogModel);
    }

    public void onEvent(String type, String id, String name, String itemId, String title, long time) {
        OperateLogModel operateLogModel = new OperateLogModel();
        operateLogModel.setId(id);
        operateLogModel.setTvClass(itemId);
        operateLogModel.setName(name);
        operateLogModel.setType(type);
        operateLogModel.setTitle(title);
        operateLogModel.setTime(time);
        operateLogModel.setStartTime(System.currentTimeMillis() / 1000);
        saveOperate(operateLogModel);
    }

    private void saveOperate(OperateLogModel operateLogModel) {
        try {

            File file = new File(mLogDir, String.format(Locale.getDefault(), LOG_FILE_NAME, currentTime));
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(randomAccessFile.length());
            randomAccessFile.write(mGson.toJson(operateLogModel).getBytes("utf-8"));
            randomAccessFile.writeBytes(System.getProperty("line.separator"));
            randomAccessFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
