package com.hdpfans.app.ui.live.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import hdpfans.com.R;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.hdpfans.app.internal.di.ActivityScope;
import com.hdpfans.app.model.entity.ChannelModel;

@ActivityScope
public class ManagerCollectChannelListAdapter extends RecyclerView.Adapter<ManagerCollectChannelListAdapter.ViewHolder> {

    private List<ChannelModel> mChannelModelList;

    @Inject
    public ManagerCollectChannelListAdapter() {
    }

    public void addCollectedChannel(ChannelModel channelModel) {
        if (mChannelModelList == null) {
            mChannelModelList = new ArrayList<>();
        }
        mChannelModelList.add(channelModel);
        notifyDataSetChanged();
    }

    public void removeCollectedChannel(ChannelModel channelModel) {
        if (mChannelModelList == null || mChannelModelList.isEmpty()) {
            return;
        }
        for (ChannelModel model : mChannelModelList) {
            if (model.getNum() == channelModel.getNum()) {
                mChannelModelList.remove(model);
                notifyDataSetChanged();
                return;
            }
        }
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
