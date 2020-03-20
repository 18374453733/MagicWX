package com.hdpfans.app.data.repository;

import android.net.Uri;
import android.text.TextUtils;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;

import java.io.IOException;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import okio.Okio;

@Singleton
public class OssRepository {

    private static final String PRIVATE_URL = "http://update.juyoufan.net";
    private static final String PRIVATE_BUCKET_NAME = "ott-jyf";

    @Inject
    OSSClient mOssClient;

    @Inject
    public OssRepository() {
    }

    @Nullable
    public byte[] getObject(String fileName) {
        if (TextUtils.isEmpty(fileName))
            return null;

        GetObjectRequest get = new GetObjectRequest(PRIVATE_BUCKET_NAME, fileName);
        try {
            GetObjectResult result = mOssClient.getObject(get);
            return Okio.buffer(Okio.source(result.getObjectContent())).readByteArray();
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public byte[] getObjectByUrl(String url) {
        String filePath = getFilePath(url);
        return getObject(filePath);
    }

    public String getSignUrl(String originalUrl) {
        if (TextUtils.isEmpty(originalUrl)) {
            return originalUrl;
        }

        if (!originalUrl.startsWith(PRIVATE_URL)) {
            return originalUrl;
        }

        Uri uri = Uri.parse(originalUrl);
        String filePath = uri.getPath().replaceFirst("/", "");
        try {
            return mOssClient.presignConstrainedObjectURL(PRIVATE_BUCKET_NAME, filePath, 30 * 60);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return originalUrl;
    }

    @Nullable
    private String getFilePath(String url) {
        if (url.startsWith(PRIVATE_URL)) {
            Uri uri = Uri.parse(url);
            return uri.getPath().replaceFirst("/", "");
        }
        return null;
    }

}
