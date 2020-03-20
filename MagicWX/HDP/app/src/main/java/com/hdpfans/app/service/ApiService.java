package com.hdpfans.app.service;

import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.hdpfans.app.data.repository.ApiRepository;
import com.hdpfans.app.data.repository.DiySourceRepository;

import java.io.File;
import java.util.LinkedHashMap;

import javax.inject.Inject;

import dagger.android.DaggerService;
import hdp.http.Hdiy;
import okio.BufferedSource;
import okio.Okio;

public class ApiService extends DaggerService {

    @Inject
    DiySourceRepository mDiySourceRepository;
    @Inject
    ApiRepository mApiRepository;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return diyStub;
    }

    private final Hdiy.Stub diyStub = new Hdiy.Stub() {

        /**
         * 没有数据
         */
        static final int STATE_NONE_DATA = 0;

        /**
         * 文件不存在
         */
        static final int STATE_FILE_NOT_EXIST = -1;

        /**
         * 功能被关闭
         */
        static final int STATE_CLOSE = -2;

        /**
         * 解析异常
         */
        static final int STATE_EXCEPTION = -3;

        @Override
        public int InsertDiyList(String path, String type) throws RemoteException {
            try {
                File sourceFile = new File(path);
                if (!sourceFile.exists()) {
                    return STATE_FILE_NOT_EXIST;
                }
                BufferedSource bufferedSource = Okio.buffer(Okio.source(sourceFile));
                String sourceLine;
                LinkedHashMap<String, String> diySourceMap = new LinkedHashMap<>();
                while ((sourceLine = bufferedSource.readUtf8Line()) != null) {
                    String[] sourceSplit = sourceLine.split(",");
                    diySourceMap.put(sourceSplit[0], sourceSplit[1]);
                }
                mDiySourceRepository.insertDiySource(1, diySourceMap).subscribe();
                return diySourceMap.size();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return STATE_EXCEPTION;
        }

        @Override
        public String GetNamebuNum(int num) throws RemoteException {
            return mApiRepository.getChannelNameByNum(num);
        }

        @Override
        @Deprecated
        public void ChangeNum(int num) throws RemoteException {
        }

        @Override
        public String getAllChannelInfo() throws RemoteException {
            return mApiRepository.getAllChannelJson();
        }
    };
}
