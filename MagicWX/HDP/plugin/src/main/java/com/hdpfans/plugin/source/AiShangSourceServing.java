package com.hdpfans.plugin.source;

import android.text.TextUtils;
import android.util.Pair;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AiShangSourceServing extends SourceServing {

    private static final String SOURCE_URL = "p2p://proxy_dli0120/%s";

    private static final Map<Integer, String> AISHANG_SOURCE_MAP = new HashMap<Integer, String>() {{
        put(1, "10240127"); // CCTV1
        put(2, "10240244"); // CCTV2
        put(3, "10240245"); // CCTV3
        put(4, "10240316"); // CCTV4
        put(5, "10240246"); // CCTV5
        put(6, "10240247"); // CCTV6
        put(7, "10240248"); // CCTV7
        put(8, "10240249"); // CCTV8
        put(9, "10240250"); // CCTV9
        put(10, "10240251"); // CCTV10
        put(11, "10240016"); // CCTV11
        put(12, "10240252"); // CCTV12
        put(13, "10240126"); // CCTV13
        put(14, "10240253"); // CCTV14
        put(15, "10240086"); // CCTV15
        put(16, "10240128"); // CCTV5+
        put(47, "10240243"); // CETV1
        put(1010, "10240304"); // 山东教育
        put(101, "10240129"); // 北京卫视
        put(102, "10240136"); // 天津卫视
        put(103, "10240242"); // 东方卫视
        put(104, "10240012"); // 重庆卫视
        put(105, "10240130"); // 湖南卫视
        put(106, "10240134"); // 浙江卫视
        put(107, "10240133"); // 江苏卫视
        put(108, "10240254"); // 山东卫视
        put(109, "10240137"); // 广东卫视
        put(110, "10240132"); // 深圳卫视
        put(111, "10240256"); // 安徽卫视
        put(112, "10240071"); // 四川卫视
        put(113, "10240066"); // 陕西卫视
        put(114, "10240065"); // 山西卫视
        put(115, "10240135"); // 湖北卫视
        put(116, "10240040"); // 河北卫视
        put(117, "10240029"); // 福建东南卫视
        put(118, "10240041"); // 河南卫视
        put(119, "10240049"); // 江西卫视
        put(120, "10240037"); // 广西卫视
        put(121, "10240060"); // 内蒙古卫视
        put(122, "10240059"); // 海南旅游卫视
        put(123, "10240082"); // 云南卫视
        put(124, "10240038"); // 贵州卫视
        put(125, "10240063"); // 青海卫视
        put(126, "10240061"); // 宁夏卫视
        put(127, "10240034"); // 甘肃卫视
        put(128, "10240131"); // 黑龙江卫视
        put(129, "10240046"); // 吉林卫视
        put(130, "10240255"); // 辽宁卫视
        put(131, "10240076"); // 西藏卫视
        put(132, "10240079"); // 新疆卫视
        put(133, "10240280"); // 新疆兵团卫视
        put(134, "10240159"); // 福建厦门卫视
        put(224, "10240148"); // 财富天下
        put(226, "10240303"); // 金鹰纪实
        put(214, "10240050"); // 金鹰卡通
        put(211, "10240087"); // 嘉佳卡通
    }};

    @Override
    protected Pair<Integer, String> create(int channelNum) {
        String key = AISHANG_SOURCE_MAP.get(channelNum);
        if (!TextUtils.isEmpty(key)) {
            return new Pair<>(1, String.format(Locale.getDefault(), SOURCE_URL, key));
        }
        return null;
    }
}
