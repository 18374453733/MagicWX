package com.hdpfans.app.ui.live.adapter;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import hdpfans.com.R;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.subjects.PublishSubject;
import com.hdpfans.app.internal.di.ActivityScope;
import com.hdpfans.app.model.entity.ChannelModel;

@ActivityScope
public class ManagerChannelListAdapter extends RecyclerView.Adapter<ManagerChannelListAdapter.ViewHolder> {

    private List<ChannelModel> mChannelModelList;

    private PublishSubject<ChannelModel> mOnClickChannelPublishSubject = PublishSubject.create();
    private PublishSubject<ChannelModel> mOnLongClickChannelPublishSubject = PublishSubject.create();

    @Inject
    public ManagerChannelListAdapter() {
    }

    public PublishSubject<ChannelModel> getOnClickChannelPublishSubject() {
        return mOnClickChannelPublishSubject;
    }

    public PublishSubject<ChannelModel> getOnLongClickChannelPublishSubject() {
        return mOnLongClickChannelPublishSubject;
    }

    public void setChannelList(List<ChannelModel> channelModelList) {
        this.mChannelModelList = channelModelList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manager_channe_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChannelModel channelModel = mChannelModelList.get(position);
        holder.mTxtChannelName.setText(channelModel.getName());
        holder.mTxtChannelNum.setText(String.valueOf(channelModel.getNum()));
        if (channelModel.isCollect()) {
            holder.mImgCollect.setVisibility(View.VISIBLE);
        } else {
            holder.mImgCollect.setVisibility(View.INVISIBLE);
        }

        if (channelModel.isHidden()) {
            holder.mTxtChannelName.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mTxtChannelName.setTextColor(0xff7d7d7d);
            holder.mTxtChannelNum.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mTxtChannelNum.setTextColor(0xff7d7d7d);
        } else {
            holder.mTxtChannelName.getPaint().setFlags(0);
            holder.mTxtChannelName.setTextColor(Color.WHITE);
            holder.mTxtChannelNum.getPaint().setFlags(0);
            holder.mTxtChannelNum.setTextColor(Color.WHITE);
        }

        holder.itemView.setOnClickListener(v -> {
            mOnClickChannelPublishSubject.onNext(channelModel);
            notifyItemChanged(position);
        });

        holder.itemView.setOnLongClickListener(v -> {
            mOnLongClickChannelPublishSubject.onNext(channelModel);
            notifyItemChanged(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mChannelModelList == null ? 0 : mChannelModelList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_channel_name)
        TextView mTxtChannelName;
        @BindView(R.id.txt_channel_num)
        TextView mTxtChannelNum;
        @BindView(R.id.img_collect)
        ImageView mImgCollect;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
