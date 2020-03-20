package hdp.player;

import android.app.Activity;
import android.content.Intent;

import com.hdpfans.app.App;
import com.hdpfans.app.ui.live.LivePlayActivity;
import com.hdpfans.app.ui.main.MainActivity;

public abstract class OldHdpActivity extends Activity {

    protected Intent buildVideoPlayIntent() {
        Intent intent = new Intent(getApplicationContext(), getActionClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    protected Class getActionClass() {
        return ((App) getApplicationContext()).isRunningActivity(LivePlayActivity.class) ? LivePlayActivity.class : MainActivity.class;
    }

}
