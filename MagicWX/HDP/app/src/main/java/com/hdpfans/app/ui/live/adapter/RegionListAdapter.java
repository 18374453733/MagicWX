package com.hdpfans.app.ui.live.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hdpfans.app.internal.di.ActivityScope;
import com.hdpfans.app.ui.widget.ElementView;

import java.util.List;

import javax.inject.Inject;

import hdpfans.com.R;
import io.reactivex.subjects.PublishSubject;

@ActivityScope
public class RegionListAdapter extends RecyclerView.Adapter<RegionListAdapter.ViewHolder> {

    @Inject
    public RegionListAdapter() {
    }

    private List<String> mRegionList;
    private String mCurrentRegion;

    private PublishSubject<String> mOnClickRegionPublishSubject = PublishSubject.create();

    public void setRegionList(List<String> regionList, String currentRegion) {
        this.mRegionList = regionList;
        this.mCurrentRegion = currentRegion;
        notifyDataSetChanged();
    }

    public void selectRegion(String currentRegion) {
        this.mCurrentRegion = currentRegion;
        notifyDataSetChanged();
    }

    public PublishSubject<String> getOnClickRegionPublishSubject() {
        return mOnClickRegionPublishSubject;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_region, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        String region = mRegionList.get(position);
        holder.mBtnRegion.setText(region);
        if (!TextUtils.isEmpty(mCurrentRegion) && (region.contains(mCurrentRegion) || mCurrentRegion.contains(region))) {
            holder.mBtnRegion.isChecked(true);
        } else {
            holder.mBtnRegion.isChecked(false);
        }

        holder.itemView.setOnClickListener(v -> mOnClickRegionPublishSubject.onNext(region));
    }

    @Override
    public int getItemCount() {
        return mRegionList == null ? 0 : mRegionList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ElementView mBtnRegion;

        public ViewHolder(View itemView) {
            super(itemView);
            this.mBtnRegion = (ElementView) itemView;
        }
    }

}
