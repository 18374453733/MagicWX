package com.hdpfans.app.ui.live;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import hdpfans.com.R;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import com.hdpfans.app.frame.FrameActivity;
import com.hdpfans.app.frame.Presenter;
import com.hdpfans.app.model.entity.ChannelModel;
import com.hdpfans.app.model.entity.ChannelTypeModel;
import com.hdpfans.app.ui.live.adapter.ManagerChannelListAdapter;
import com.hdpfans.app.ui.live.adapter.ManagerChannelTypeAdapter;
import com.hdpfans.app.ui.live.adapter.ManagerCollectChannelListAdapter;
import com.hdpfans.app.ui.live.presenter.ChannelManagerContract;
import com.hdpfans.app.ui.live.presenter.ChannelManagerPresenter;

public class ChannelManagerActivity extends FrameActivity
        implements ChannelManagerContract.View {

    public static final String INTENT_PARAMS_PLAYING_CHANNEL = "intent_params_playing_channel";

    public static Intent navigateToChannelManager(@NonNull Context context, ChannelModel playingChannel) {
        Intent intent = new Intent(context, ChannelManagerActivity.class);
        intent.putExtra(INTENT_PARAMS_PLAYING_CHANNEL, playingChannel);
        return intent;
    }

    @Presenter
    @Inject
    ChannelManagerPresenter presenter;
    @Inject
    ManagerChannelTypeAdapter mChannelTypeAdapter;
    @Inject
    ManagerChannelListAdapter mChannelListAdapter;
    @Inject
    ManagerCollectChannelListAdapter mCollectChannelListAdapter;

    @BindView(R.id.recycler_channel_type)
    RecyclerView mRecyclerChannelType;
    @BindView(R.id.recycler_channel_list)
    RecyclerView mRecyclerChannelList;
    @BindView(R.id.recycler_collect_channel_list)
    RecyclerView mRecyclerCollectList;
    @BindView(R.id.txt_collect_info)
    TextView mTxtCollectInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_manager);

        /* 设置分类列表 */
        mRecyclerChannelType.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerChannelType.setHasFixedSize(true);
        mRecyclerChannelType.setAdapter(mChannelTypeAdapter);
        mChannelTypeAdapter.getOnFocusedTypePublishSubject()
                .subscribe(channelTypeModel -> presenter.onSelectedChannelType(channelTypeModel));
        mChannelTypeAdapter.getOnClickTypePublishSubject()
                .subscribe(channelTypeModel -> presenter.hiddenChannelTypeOrNot(channelTypeModel));

        /* 设置分类下频道列表 */
        mRecyclerChannelList.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerChannelList.setHasFixedSize(true);
        mRecyclerChannelList.setAdapter(mChannelListAdapter);
        mChannelListAdapter.getOnClickChannelPublishSubject()
                .subscribe(channelModel -> presenter.hiddenChannelOrNot(channelModel));
        mChannelListAdapter.getOnLongClickChannelPublishSubject()
                .subscribe(channelModel -> presenter.collectChannelOrNot(channelModel));

        /* 设置分类下收藏频道列表 */
        mRecyclerCollectList.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerCollectList.setHasFixedSize(true);
        mRecyclerCollectList.setAdapter(mCollectChannelListAdapter);
    }

    @Override
    public void showChannelType(List<ChannelTypeModel> channelTypeModels) {
        mChannelTypeAdapter.setChannelTypeList(channelTypeModels);
    }

    @Override
    public void showChannelList(List<ChannelModel> channelModels) {
        mChannelListAdapter.setChannelList(channelModels);
    }

    @Override
    public void showCollectTips(int totalSize, int collectSize) {
        String collectTxt = getString(R.string.txt_collect_info, collectSize, totalSize);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(collectTxt);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(0xffffbb33), 5, 5 + String.valueOf(collectSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(new AbsoluteSizeSpan((int) (mTxtCollectInfo.getTextSize() * 1.7)), 5, 5 + String.valueOf(collectSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(new AbsoluteSizeSpan((int) (mTxtCollectInfo.getTextSize() * 1.2)), collectTxt.length() - 1 - String.valueOf(collectSize).length(), collectTxt.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTxtCollectInfo.setText(spannableStringBuilder);
    }

    @Override
    public void showCollectList(List<ChannelModel> channelModels) {
        mCollectChannelListAdapter.setChannelList(channelModels);
    }

    @Override
    public void addCollectedChannel(ChannelModel channelModel) {
        mCollectChannelListAdapter.addCollectedChannel(channelModel);
    }

    @Override
    public void removeCollectedChannel(ChannelModel channelModel) {
        mCollectChannelListAdapter.removeCollectedChannel(channelModel);
    }
}
