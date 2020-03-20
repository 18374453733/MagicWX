package com.hdpfans.app.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.ImageView;

import hdpfans.com.R;

import butterknife.BindView;
import com.hdpfans.app.frame.FrameActivity;
import com.hdpfans.app.utils.GlideApp;

public class HdpTipsActivity extends FrameActivity {

    private static final String INTENT_PARAMS_TIPS_IMAGE_URL = "intent_params_tips_image_url";

    public static Intent navigateToTipsActivity(@NonNull Context context, String url) {
        Intent intent = new Intent(context, HdpTipsActivity.class);
        intent.putExtra(INTENT_PARAMS_TIPS_IMAGE_URL, url);
        return intent;
    }

    @BindView(R.id.img_tips)
    ImageView mImgTips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hdp_tips);

        String url = getIntent().getStringExtra(INTENT_PARAMS_TIPS_IMAGE_URL);
        if (!TextUtils.isEmpty(url)) {
            GlideApp.with(this).load(url).into(mImgTips);
        } else {
            finish();
        }
    }
}
