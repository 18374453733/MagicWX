package com.hdpfans.app.ui.live.adapter;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hdpfans.app.internal.di.ActivityScope;
import com.hdpfans.app.model.entity.ChannelTypeModel;
import com.hdpfans.app.utils.BoxCompat;

import java.util.List;

import javax.inject.Inject;

import hdpfans.com.R;
import io.reactivex.subjects.PublishSubject;

@ActivityScope
public class ManagerChannelTypeAdapter extends RecyclerView.Adapter<ManagerChannelTypeAdapter.ViewHolder> {

    private List<ChannelTypeModel> mChannelTypeList;

    private PublishSubject<ChannelTypeModel> mOnFocusedTypePublishSubject = PublishSubject.create();
    private PublishSubject<ChannelTypeModel> mOnClickTypePublishSubject = PublishSubject.create();

    @Inject
    public ManagerChannelTypeAdapter() {
    }

    public void setChannelTypeList(List<ChannelTypeModel> channelTypeList) {
        this.mChannelTypeList = channelTypeList;
        notifyDataSetChanged();
    }

    public PublishSubject<ChannelTypeModel> getOnFocusedTypePublishSubject() {
        return mOnFocusedTypePublishSubject;
    }

    public PublishSubject<ChannelTypeModel> getOnClickTypePublishSubject() {
        return mOnClickTypePublishSubject;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (BoxCompat.isPhoneRunning(holder.itemView.getContext()) && holder.getAdapterPosition() == 0) {
            holder.itemView.performClick();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manager_channe_type, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChannelTypeModel channelTypeModel = mChannelTypeList.get(position);
        holder.mBtnChannelType.setText(channelTypeModel.getName());

        if (channelTypeModel.isHidden()) {
            holder.mBtnChannelType.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mBtnChannelType.setTextColor(0xff7d7d7d);
        } else {
            holder.mBtnChannelType.getPaint().setFlags(0);
            holder.mBtnChannelType.setTextColor(holder.itemView.getResources().getColorStateList(R.color.selector_item_channel_info));
        }

        if (BoxCompat.isPhoneRunning(holder.itemView.getContext())) {
            holder.mBtnChannelType.setOnClickListener(v -> mOnFocusedTypePublishSubject.onNext(channelTypeModel));
        } else {
            holder.mBtnChannelType.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    mOnFocusedTypePublishSubject.onNext(channelTypeModel);
                }
            });
            holder.mBtnChannelType.setOnClickListener(v -> {
                mOnClickTypePublishSubject.onNext(channelTypeModel);
                notifyItemChanged(position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return mChannelTypeList == null ? 0 : mChannelTypeList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        Button mBtnChannelType;

        public ViewHolder(View itemView) {
            super(itemView);
            this.mBtnChannelType = (Button) itemView;
        }
    }

}
