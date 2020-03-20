package com.hdpfans.plugin.spider;

import android.content.Context;
import android.util.Pair;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public abstract class SpiderBoss {

    private Context context;
    private OkHttpClient okHttpClient;

    public SpiderBoss(Context context) {
        this.context = context;
    }

    public OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            final X509TrustManager trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };

            SSLSocketFactory sslSocketFactory = null;
            try {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
                sslSocketFactory = sslContext.getSocketFactory();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            if (sslSocketFactory != null) {
                builder.sslSocketFactory(sslSocketFactory, trustManager)
                        .hostnameVerifier(new HostnameVerifier() {
                            @Override
                            public boolean verify(String hostname, SSLSession session) {
                                return true;
                            }
                        });
            }
            okHttpClient = builder.build();
        }
        return okHttpClient;
    }

    public Context getContext() {
        return context;
    }

    /**
     * 是否命中食物
     */
    public abstract boolean hint(String food);

    /**
     * 肢解食物
     */
    public abstract Pair<String, Map<String, String>> silking(String food);

    /**
     * 排泄
     */
    public void excretion() {
    }

    /**
     * 产生Http头
     */
    public Map<String, String> getHeaders(String food) {
        return null;
    }

}
