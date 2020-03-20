package com.hdpfans.plugin.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProxyResultModel {

    private String url;

    private List<String> headers;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getHeadersMap() {
        Map<String, String> headersMap = new HashMap<>();
        if (headers != null && !headers.isEmpty()) {
            for (String header : headers) {
                String[] headerAttr = header.split(":");
                headersMap.put(headerAttr[0], headerAttr[1]);
            }
        }
        return headersMap;
    }
}
