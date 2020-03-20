package com.hdpfans.plugin.spider.cntv;

import android.apk.ActivityThread;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;

import com.hdpfans.plugin.spider.SpiderBoss;

import java.io.File;
import java.util.Locale;
import java.util.Map;

public class CntvSpider extends SpiderBoss {

    private static final String CNTV_PREFIX = "p2p://cn_";

    private static final String P2P_URL = "http://127.0.0.1:%d/plug_in/M3u8Mod/LiveStream.m3u8?ClientID=%s&ChannelID=%s";

    private boolean isLoadLibrary;

    private int p2pPort;
    private String clientId;
    private String playId;

    public CntvSpider(Context context) {
        super(context);
        ActivityThread.currentActivityThread(getContext());
    }

    private synchronized boolean loadLibrary() {
        if (!isLoadLibrary && ActivityThread.currentActivityThread(getContext()).isIsLoaded()) {
            final File libraryFile = new File(getContext().getDir("libs", Context.MODE_PRIVATE), "libkoolivemod.so");
            if (libraryFile.exists()) {
                ActivityThread.currentActivityThread(getContext()).kooinit(libraryFile.getAbsolutePath());
                p2pPort = ActivityThread.currentActivityThread(getContext()).getp2pport();
                clientId = "cntv.cn." + ((int) ((Math.random() * 1.0E8d) + 1.0d));
                isLoadLibrary = true;
            }
        }
        return isLoadLibrary;
    }

    @Override
    public boolean hint(String food) {
        return food.startsWith(CNTV_PREFIX);
    }

    @Override
    public Pair<String, Map<String, String>> silking(String food) {
        if (loadLibrary()) {
            playId = "pa://cctv_p2p_hd" + food.replace(CNTV_PREFIX, "");
            if (isPlayer()) {
                food = String.format(Locale.getDefault(), P2P_URL, p2pPort, clientId, playId);
            }
        }
        return new Pair<>(food, getHeaders(food));
    }

    private int instanceGetP2PState(String str) {
        return ActivityThread.currentActivityThread(getContext()).getp2pstate(str);
    }

    private boolean isPlayer() {
        boolean ret = false;
        String bufferStr = createBufferString();
        int bufferState = 0;
        int tryNum = 0;
        while (tryNum < 20) {
            try {
                bufferState = instanceGetP2PState(bufferStr);
                Log.i("CntvSpider", "run buffer, bufferState = " + bufferState);
                if (bufferState != 200) {
                    if (bufferState != 404 && bufferState != 0) {
                        Log.i("CntvSpider", "run buffer, buffer not success,sendError,bufferState =" + bufferState);
                        sendError(bufferState);
                        break;
                    }
                    tryNum++;
                    SystemClock.sleep(300);
                } else {
                    ret = true;
                    Log.i("CntvSpider", "run buffer, buffer success ");
                    break;
                }
            } catch (Exception e) {
                try {
                    Log.i("CntvSpider", "run buffer, buffer exception,sendError,bufferState =" + bufferState);
                } catch (Exception e2) {
                    Log.i("CntvSpider", Log.getStackTraceString(e2));
                }
            }
        }
        return ret;
    }

    private String createBufferString() {
        return "GetBufferState:ClientID=" + clientId + "&ChannelID=" + playId + "&Ver=1&";
    }

    private void sendError(int errorNum) {
        String errorStr;
        switch (errorNum) {
            case 501 /*501*/:
                errorStr = "您所在的地区处于限制播放区域！";
                break;
            case 502 /*502*/:
                errorStr = "您所选择的节目信号源中断！\r\n请重新选择。";
                break;
            case 503 /*503*/:
                errorStr = "播放数据正在准备。\r\n请稍候选择！";
                break;
            case 504 /*504*/:
                errorStr = "因版权原因。\r\n本时段节目暂停播放！";
                break;
            case 506 /*506*/:
                errorStr = "内核版本过低。\r\n请升级后观看！";
                break;
            default:
                errorStr = "系统错误,请重新运行!";
                break;
        }
        Log.e("CntvSpider", errorStr);
    }


}
