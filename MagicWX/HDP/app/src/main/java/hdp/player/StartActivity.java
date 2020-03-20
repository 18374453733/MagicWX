package hdp.player;

import android.content.Intent;
import android.os.Bundle;

public class StartActivity extends OldHdpActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = buildVideoPlayIntent();
        ApiKeys.copyApiParams(getIntent(), intent);
        startActivity(intent);
        finish();
    }
}
