package com.hdpfans.app.ui;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hdpfans.app.data.manager.PrefManager;
import com.hdpfans.app.internal.di.ActivityScope;
import com.hdpfans.app.model.annotation.DisplayTextSizeMode;
import com.hdpfans.app.utils.BoxCompat;
import com.hdpfans.app.utils.PhoneCompat;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

@ActivityScope
public class LayoutInflaterConvert implements LayoutInflater.Factory2 {

    private static final String ANDROID_SPACE = "http://schemas.android.com/apk/res/android";


    private PrefManager mPrefManager;
    private Map<String, Integer> defaultTextSizeMap = new HashMap<>();

    @Inject
    public LayoutInflaterConvert(PrefManager prefManager) {
        this.mPrefManager = prefManager;
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return onCreateView(name, context, attrs);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        if (mPrefManager.getDisplayTextSizeMode() != DisplayTextSizeMode.MIDDLE || BoxCompat.isPhoneRunning(context)) {

            View view = createView(context, name, attrs);
            if (view != null && TextView.class.isAssignableFrom(view.getClass())) {
                Integer defaultTextSize = defaultTextSizeMap.get(name);
                if (defaultTextSize == null || defaultTextSize == 0) {
                    defaultTextSize = PhoneCompat.px2sp(context, ((TextView) view).getTextSize());
                    defaultTextSizeMap.put(name, defaultTextSize);
                }

                if (defaultTextSize == 0) {
                    return null;
                }

                try {
                    float viewTextSize = defaultTextSize;
                    // 获取设置的大小
                    String textSize = attrs.getAttributeValue(ANDROID_SPACE, "textSize");
                    if (!TextUtils.isEmpty(textSize)) {
                        Matcher matcher = Pattern.compile("([\\d]{2}\\.\\d)(.+)").matcher(textSize);
                        if (matcher.matches()) {
                            viewTextSize = Float.parseFloat(matcher.group(1));
                        }
                    }
                    // 改变字体大小
                    float newSize = viewTextSize;
                    if (mPrefManager.getDisplayTextSizeMode() == DisplayTextSizeMode.BIG) {
                        newSize += 2;
                    } else if (mPrefManager.getDisplayTextSizeMode() == DisplayTextSizeMode.SMALL) {
                        newSize -= 2;
                    }

                    if (BoxCompat.isPhoneRunning(context)) {
                        newSize = newSize / 4 * 3;
                    }

                    ((TextView) view).setTextSize(newSize);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return view;
        }
        return null;
    }

    private View createView(Context context, String name, AttributeSet attrs) {
        View view = null;
        try {
            if (!name.contains(".")) {
                if ("View".equals(name)) {
                    view = LayoutInflater.from(context).createView(name, "android.view.", attrs);
                }
                if (view == null) {
                    view = LayoutInflater.from(context).createView(name, "android.widget.", attrs);
                }
                if (view == null) {
                    view = LayoutInflater.from(context).createView(name, "android.webkit.", attrs);
                }
            } else {
                view = LayoutInflater.from(context).createView(name, null, attrs);
            }
        } catch (Exception ignored) {
        }
        return view;
    }
}
