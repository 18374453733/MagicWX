package com.hdpfans.app.ui.live;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;

import com.hdpfans.app.frame.FrameActivity;
import com.hdpfans.app.frame.Presenter;
import com.hdpfans.app.model.entity.ChannelModel;
import com.hdpfans.app.ui.live.adapter.ChannelSourceListAdapter;
import com.hdpfans.app.ui.live.presenter.ChannelSourceListContract;
import com.hdpfans.app.ui.live.presenter.ChannelSourceListPresenter;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import hdpfans.com.R;

public class ChannelSourceListActivity extends FrameActivity
        implements ChannelSourceListContract.View {

    public static final String RESULT_CHANNEL_INDEX = "result_channel_index";

    public static final String INTENT_PARAMS_CHANNEL = "intent_params_channel";

    public static final String INTENT_PARAMS_INDEX = "intent_params_index";

    @Presenter
    @Inject
    ChannelSourceListPresenter presenter;
    @Inject
    ChannelSourceListAdapter mChannelSourceListAdapter;

    @BindView(R.id.recycler_channel_source_list)
    RecyclerView mRecyclerChannelSourceList;

    public static Intent navigateToChannelSourceList(@NonNull Context context, ChannelModel channelModel, int index) {
        Intent intent = new Intent(context, ChannelSourceListActivity.class);
        intent.putExtra(INTENT_PARAMS_CHANNEL, channelModel);
        intent.putExtra(INTENT_PARAMS_INDEX, index);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_source_list);

        mRecyclerChannelSourceList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerChannelSourceList.setHasFixedSize(true);
        mRecyclerChannelSourceList.setAdapter(mChannelSourceListAdapter);

        mChannelSourceListAdapter.getOnClickIndexPublishSubject()
                .subscribe(index -> {
                    Intent intent = new Intent();
                    intent.putExtra(RESULT_CHANNEL_INDEX, index);
                    setResult(RESULT_OK, intent);
                    finish();
                });
        mChannelSourceListAdapter.getOnShowChannelSourcePublishSubject()
                .subscribe(source -> toast(source));
        mRecyclerChannelSourceList.setOnTouchListener((v, event) -> {
            autoDismiss();
            return false;
        });

        // 点击空白处关闭
        findViewById(android.R.id.content).setOnClickListener(v -> finish());
        autoDismiss();
    }

    @Override
    public void showChannelSourceList(List<String> urls, int currentSourceIndex) {
        mChannelSourceListAdapter.setChannelSourceList(urls, currentSourceIndex);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        autoDismiss();
        return super.onKeyDown(keyCode, event);
    }

    private void autoDismiss() {
        getSafetyHandler().removeCallbacksAndMessages(null);
        getSafetyHandler().postDelayed(this::onBackPressed, 5 * 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSafetyHandler().removeCallbacksAndMessages(null);
    }
}
