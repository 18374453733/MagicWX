package com.hdpfans.app.ui.live.presenter;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;

import com.hdpfans.app.data.repository.HdpRepository;
import com.hdpfans.app.frame.BasePresenter;
import com.hdpfans.app.internal.di.ActivityScope;
import com.hdpfans.app.model.entity.ChannelModel;
import com.hdpfans.app.model.entity.ChannelTypeModel;
import com.hdpfans.app.reactivex.DefaultDisposableSingleObserver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;

import static com.hdpfans.app.ui.live.ChannelManagerActivity.INTENT_PARAMS_PLAYING_CHANNEL;

@ActivityScope
public class ChannelManagerPresenter extends BasePresenter<ChannelManagerContract.View>
        implements ChannelManagerContract.Presenter {

    @Inject
    HdpRepository mHdpRepository;

    private ChannelModel mCurrentPlayingChannel;

    @Inject
    public ChannelManagerPresenter() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void loadChannelInfo() {

        mCurrentPlayingChannel = getIntent().getParcelableExtra(INTENT_PARAMS_PLAYING_CHANNEL);

        mHdpRepository.getAllChannelType(true)
                .compose(getView().getLifecycleProvider().bindToLifecycle())
                .subscribe(new DefaultDisposableSingleObserver<List<ChannelTypeModel>>() {
                    @Override
                    public void onSuccess(List<ChannelTypeModel> channelTypeModels) {
                        super.onSuccess(channelTypeModels);
                        getView().showChannelType(channelTypeModels);
                    }
                });
    }

    @Override
    public void onSelectedChannelType(ChannelTypeModel channelTypeModel) {
        mHdpRepository.getChannelsWithCollectByType(channelTypeModel.getId(), true)
                .compose(getView().getLifecycleProvider().bindToLifecycle())
                .doOnSuccess(channelModels -> getView().showChannelList(channelModels))
                .toObservable()
                .flatMap(Observable::fromIterable)
                .toMultimap(ChannelModel::isCollect)
                .subscribe(new DefaultDisposableSingleObserver<Map<Boolean, Collection<ChannelModel>>>() {
                    @Override
                    public void onSuccess(Map<Boolean, Collection<ChannelModel>> channelMap) {
                        super.onSuccess(channelMap);
                        Collection<ChannelModel> collectedChannelList = channelMap.get(true);
                        Collection<ChannelModel> unCollectChannelList = channelMap.get(false);
                        int totalSize = (collectedChannelList == null ? 0 : collectedChannelList.size()) +
                                (unCollectChannelList == null ? 0 : unCollectChannelList.size());
                        getView().showCollectTips(totalSize, collectedChannelList == null ? 0 : collectedChannelList.size());
                        if (mHdpRepository.isCollectType(channelTypeModel.getId()) || collectedChannelList == null) {
                            getView().showCollectList(null);
                        } else {
                            getView().showCollectList(new ArrayList<>(collectedChannelList));
                        }
                    }
                });
    }

    /**
     * 显示和隐藏频道
     */
    @Override
    public void hiddenChannelOrNot(ChannelModel channelModel) {
        // 不处理即时频道
        if (mHdpRepository.isImmediateChannel(channelModel.getNum())) return;

        if (channelModel.getNum() == mCurrentPlayingChannel.getNum() && !channelModel.isHidden()) {
            getView().toast("不能隐藏当前正在播放的节目");
            return;
        }
        channelModel.setHidden(!channelModel.isHidden());
        mHdpRepository.saveChannel(channelModel);

    }

    /**
     * 收藏和取消收藏频道
     */
    @Override
    public void collectChannelOrNot(ChannelModel channelModel) {
        // 不处理即时频道
        if (mHdpRepository.isImmediateChannel(channelModel.getNum())) return;

        channelModel.setCollect(!channelModel.isCollect());
        mHdpRepository.saveChannel(channelModel);

        if (channelModel.isCollect()) {
            getView().addCollectedChannel(channelModel);
        } else {
            getView().removeCollectedChannel(channelModel);
        }
    }

    /**
     * 显示和隐藏频道分类
     */
    @Override
    public void hiddenChannelTypeOrNot(ChannelTypeModel channelTypeModel) {
        // 不处理即时频道
        if (mHdpRepository.isImmediateChannelType(channelTypeModel.getId())) return;

        if (channelTypeModel.getId() == mCurrentPlayingChannel.getItemId() && !channelTypeModel.isHidden()) {
            getView().toast("不能隐藏当前正在播放节目的分类");
            return;
        }
        channelTypeModel.setHidden(!channelTypeModel.isHidden());
        mHdpRepository.hiddenOrNotChannelType(channelTypeModel);
        onSelectedChannelType(channelTypeModel);
    }
}
