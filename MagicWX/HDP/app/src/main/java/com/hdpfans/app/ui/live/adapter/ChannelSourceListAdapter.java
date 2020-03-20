package com.hdpfans.app.ui.live.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hdpfans.app.internal.di.ActivityScope;

import java.util.List;

import javax.inject.Inject;

import hdpfans.com.R;
import io.reactivex.subjects.PublishSubject;

@ActivityScope
public class ChannelSourceListAdapter extends RecyclerView.Adapter<ChannelSourceListAdapter.ViewHolder> {

    private List<String> mSourceList;
    private int mCurrentIndex;

    private PublishSubject<Integer> mOnClickIndexPublishSubject = PublishSubject.create();
    private PublishSubject<String> mOnShowChannelSourcePublishSubject = PublishSubject.create();

    @Inject
    public ChannelSourceListAdapter() {

    }

    public void setChannelSourceList(List<String> sourceList, int index) {
        this.mSourceList = sourceList;
        this.mCurrentIndex = index;
        notifyDataSetChanged();
    }

    public PublishSubject<Integer> getOnClickIndexPublishSubject() {
        return mOnClickIndexPublishSubject;
    }

    public PublishSubject<String> getOnShowChannelSourcePublishSubject() {
        return mOnShowChannelSourcePublishSubject;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel_source_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == mCurrentIndex) {
            holder.itemView.requestFocus();
        }

        holder.mTxtIndex.setText(String.valueOf(position + 1));
        holder.itemView.setOnClickListener(v -> mOnClickIndexPublishSubject.onNext(position));

        holder.itemView.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_DOWN) {
                mOnShowChannelSourcePublishSubject.onNext(mSourceList.get(holder.getAdapterPosition()));
                return true;
            }
            return false;
        });
        holder.itemView.setOnLongClickListener(v -> {
            mOnShowChannelSourcePublishSubject.onNext(mSourceList.get(holder.getAdapterPosition()));
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return mSourceList == null ? 0 : mSourceList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTxtIndex;

        public ViewHolder(View itemView) {
            super(itemView);
            mTxtIndex = (TextView) itemView;
        }
    }

}
