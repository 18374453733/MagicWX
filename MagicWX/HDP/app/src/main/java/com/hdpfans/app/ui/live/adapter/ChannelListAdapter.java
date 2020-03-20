package com.hdpfans.app.ui.live.adapter;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hdpfans.api.HdpApi;
import com.hdpfans.app.data.manager.PrefManager;
import com.hdpfans.app.internal.di.ActivityScope;
import com.hdpfans.app.model.entity.ChannelModel;
import com.hdpfans.app.model.entity.QualityEnum;
import com.hdpfans.app.reactivex.DefaultDisposableSingleObserver;
import com.hdpfans.app.utils.GlideApp;
import com.hdpfans.app.utils.ReactivexCompat;
import com.hdpfans.app.utils.plugin.PluginLoader;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import hdpfans.com.R;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.subjects.PublishSubject;

@ActivityScope
public class ChannelListAdapter extends RecyclerView.Adapter<ChannelListAdapter.ViewHolder> {

    @Inject
    PrefManager mPrefManager;
    @Inject
    PluginLoader mPluginLoader;

    private HdpApi mHdpApi;

    private List<ChannelModel> mChannelList;
    private int mFocusedPosisition;
    private Map<Integer, String> mChannelEpgMap = new ConcurrentHashMap<>();

    private PublishSubject<ChannelModel> onClickChannelPublishSubject = PublishSubject.create();
    private PublishSubject<ChannelModel> onLongClickChannelModelPublishSubject = PublishSubject.create();

    private PublishSubject<ChannelModel> onSwitchNextChannelsPublishSubject = PublishSubject.create();
    private PublishSubject<ChannelModel> onSwitchAfterChannelsPublishSubject = PublishSubject.create();

    @Inject
    public ChannelListAdapter() {
    }

    public void setChannelList(List<ChannelModel> channelList, int focusedPosition) {
        this.mChannelList = channelList;
        this.mFocusedPosisition = focusedPosition;
        notifyDataSetChanged();
    }

    public PublishSubject<ChannelModel> getOnClickChannelPublishSubject() {
        return onClickChannelPublishSubject;
    }

    public PublishSubject<ChannelModel> getOnLongClickChannelModelPublishSubject() {
        return onLongClickChannelModelPublishSubject;
    }

    public PublishSubject<ChannelModel> getOnSwitchAfterChannelsPublishSubject() {
        return onSwitchAfterChannelsPublishSubject;
    }

    public PublishSubject<ChannelModel> getOnSwitchNextChannelsPublishSubject() {
        return onSwitchNextChannelsPublishSubject;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(createItemView(parent));
    }

    public View createItemView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel_list, parent, false);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder.getAdapterPosition() == mFocusedPosisition) {
            holder.itemView.requestFocus();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChannelModel channelModel = mChannelList.get(position);
        holder.itemView.setFocusable(true);
        holder.mTxtChannelName.setText(channelModel.getName());

        if (TextUtils.isEmpty(channelModel.getNumAlias())) {
            holder.mTxtChannelNum.setText(String.valueOf(channelModel.getNum()));
        } else {
            holder.mTxtChannelNum.setText(channelModel.getNumAlias());
        }

        // 设置角标
        if (!TextUtils.isEmpty(channelModel.getMarkUrl())) {
            holder.mImgMark.setVisibility(View.VISIBLE);
            holder.mTxtQuality.setVisibility(View.GONE);

            GlideApp.with(holder.itemView.getContext()).load(channelModel.getMarkUrl()).into(holder.mImgMark);
        } else if (!TextUtils.isEmpty(channelModel.getQuality())) {
            holder.mImgMark.setVisibility(View.GONE);
            holder.mTxtQuality.setVisibility(View.VISIBLE);

            try {
                QualityEnum quality = QualityEnum.valueOf(channelModel.getQuality());
                holder.mTxtQuality.setText(quality.getName());
                holder.mTxtQuality.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), quality.getColorId()));
            } catch (Exception ignored) {
                holder.mTxtQuality.setVisibility(View.GONE);
            }

        } else {
            holder.mTxtQuality.setVisibility(View.GONE);
            holder.mImgMark.setVisibility(View.GONE);
        }

        // 设置收藏状态
        holder.mImgCollect.setVisibility(channelModel.isCollect() ? View.VISIBLE : View.INVISIBLE);

        if (mHdpApi == null) {
            mHdpApi = mPluginLoader.createApi(HdpApi.class);
        }
        // 设置EPG信息
        if (mPrefManager.isOpenChannelEpg() && mHdpApi != null && !TextUtils.isEmpty(channelModel.getEpgId())) {
            String epg = mChannelEpgMap.get(channelModel.getNum());
            if (TextUtils.isEmpty(epg)) {
                holder.mTxtChannelEPG.setText(null);
                Single.create((SingleOnSubscribe<String>) emitter -> emitter.onSuccess(mHdpApi.getCurrentEpg(channelModel.getEpgId())))
                        .compose(ReactivexCompat.singleThreadSchedule())
                        .subscribe(new DefaultDisposableSingleObserver<String>() {
                            @Override
                            public void onSuccess(String epg) {
                                super.onSuccess(epg);
                                mChannelEpgMap.put(channelModel.getNum(), epg);
                                notifyItemChanged(holder.getAdapterPosition());
                            }
                        });
            } else {
                holder.mTxtChannelEPG.setText(epg);
            }
        } else {
            holder.mTxtChannelEPG.setText(null);
        }

        holder.itemView.setOnKeyListener((v, keyCode, event) -> {
            if (holder.getAdapterPosition() == 0 && keyCode == KeyEvent.KEYCODE_DPAD_UP && event.getAction() == KeyEvent.ACTION_DOWN) {
                onSwitchNextChannelsPublishSubject.onNext(channelModel);
                return true;
            } else if (holder.getAdapterPosition() == mChannelList.size() - 1 && keyCode == KeyEvent.KEYCODE_DPAD_DOWN && event.getAction() == KeyEvent.ACTION_DOWN) {
                onSwitchAfterChannelsPublishSubject.onNext(channelModel);
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_UP) {
                onCollectOrDeleteChannelAction(channelModel, holder);
                return true;
            }
            return false;
        });

        holder.itemView.setOnClickListener(v -> onClickChannelPublishSubject.onNext(channelModel));
        holder.itemView.setOnLongClickListener(v -> {
            onCollectOrDeleteChannelAction(channelModel, holder);
            return true;
        });
    }

    private void onCollectOrDeleteChannelAction(ChannelModel channelModel, ViewHolder holder) {
        onLongClickChannelModelPublishSubject.onNext(channelModel);
        if (mChannelList.contains(channelModel)) {
            notifyItemChanged(holder.getAdapterPosition());
        } else {
            notifyItemRemoved(holder.getAdapterPosition());
        }
    }

    @Override
    public int getItemCount() {
        return mChannelList == null ? 0 : mChannelList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_channel_num)
        TextView mTxtChannelNum;
        @BindView(R.id.txt_channel_name)
        TextView mTxtChannelName;
        @BindView(R.id.txt_epg)
        TextView mTxtChannelEPG;
        @BindView(R.id.img_mark)
        ImageView mImgMark;
        @BindView(R.id.txt_quality)
        TextView mTxtQuality;
        @BindView(R.id.img_collect)
        ImageView mImgCollect;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
