package com.hdpfans.app.ui.live.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hdpfans.app.ui.widget.ElementView;

import java.util.List;

import hdpfans.com.R;

public class MenuMainAdapter extends RecyclerView.Adapter<MenuMainAdapter.ViewHolder> {

    private List<String> data;

    private OnItemClickListener mOnItemClickListener;

    public MenuMainAdapter(List<String> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public MenuMainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_region, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuMainAdapter.ViewHolder holder, int position) {
        holder.mBtnRegion.setText(data.get(position));

        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(v -> mOnItemClickListener.onClick(position));
            holder.itemView.setOnFocusChangeListener((view, b) -> mOnItemClickListener.OnFocusChangeListener(position,view, b));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnItemClickListener {
        void onClick(int position);
        void OnFocusChangeListener(int position,View view, boolean b);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ElementView mBtnRegion;

        public ViewHolder(View itemView) {
            super(itemView);
            this.mBtnRegion = (ElementView) itemView;
        }
    }

}
