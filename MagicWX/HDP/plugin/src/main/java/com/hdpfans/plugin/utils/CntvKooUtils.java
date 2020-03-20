package com.hdpfans.plugin.utils;

import java.util.HashMap;
import java.util.Map;

public class CntvKooUtils {

    public Map<String, String> R = new HashMap<>();

    public static CntvKooUtils sInstance;

    public static CntvKooUtils getInstance() {
        if (sInstance == null) {
            synchronized (CntvKooUtils.class) {
                if (sInstance == null) {
                    sInstance = new CntvKooUtils();
                }
            }
        }
        return sInstance;
    }

    public String set(String str) {
        return set(R, str);
    }

    public String set(Map<String, String> r, String str) {
        r.clear();
        if (str.contains("#")) {
            str = str.split("#", 2)[0];
        }
        if (str.contains("?")) {
            String[] w = str.split("\\?", 2);
            if (w[1].contains("&")) {
                String[] re = w[1].split("&");
                for (int i = 0; i < re.length; i++) {
                    if (re[i].contains("=")) {
                        String[] reName = re[i].split("=", 2);
                        r.put(reName[0].trim(), reName[1].trim());
                    }
                }
            } else {
                if (w[1].contains("=")) {
                    String[] reName = w[1].split("=", 2);
                    r.put(reName[0].trim(), reName[1].trim());
                }
            }
        } else {
            if (str.contains("&")) {
                String[] re = str.split("&");
                for (int i = 0; i < re.length; i++) {
                    if (re[i].contains("=")) {
                        String[] reName = re[i].split("=", 2);
                        r.put(reName[0].trim(), reName[1].trim());
                    }
                }
            } else {
                if (str.contains("=")) {
                    String[] reName = str.split("=", 2);
                    r.put(reName[0].trim(), reName[1].trim());
                }
            }
        }
        return null;
    }

}
