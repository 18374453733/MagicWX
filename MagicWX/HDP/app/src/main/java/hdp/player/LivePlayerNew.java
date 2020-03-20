package hdp.player;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.hdpfans.app.ui.live.LivePlayActivity;

public class LivePlayerNew extends OldHdpActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = buildVideoPlayIntent();
        if (!getActionClass().getName().equals(LivePlayActivity.class.getName())) {
            getIntent().putExtra(ApiKeys.INTENT_API_HIDE_LOADING_IMAGE, true);
        }
        ApiKeys.copyApiParams(getIntent(), intent);
        startActivity(intent);
        finish();
    }
}
