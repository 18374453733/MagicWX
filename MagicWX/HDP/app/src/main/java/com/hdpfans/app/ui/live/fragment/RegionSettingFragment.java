package com.hdpfans.app.ui.live.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hdpfans.api.HdpApi;
import com.hdpfans.app.data.manager.PrefManager;
import com.hdpfans.app.ui.live.MenuActivity;
import com.hdpfans.app.ui.live.adapter.RegionListAdapter;
import com.hdpfans.app.ui.widget.media.FocusKeepRecyclerView;
import com.hdpfans.app.utils.plugin.PluginLoader;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import hdpfans.com.R;

public class RegionSettingFragment extends SettingFragment {

    public static RegionSettingFragment newInstance() {
        return new RegionSettingFragment();
    }

    @BindView(R.id.recycler_region_list)
    FocusKeepRecyclerView mRecyclerRegionList;


    @Inject
    PluginLoader mPluginLoader;
    @Inject
    RegionListAdapter mRegionListAdapter;
    @Inject
    PrefManager mPrefManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_region_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HdpApi hdpApi = mPluginLoader.createApi(HdpApi.class);
        String currentRegion = mPrefManager.getSelectedRegion();
        if (TextUtils.isEmpty(currentRegion) && hdpApi != null) {
            currentRegion = hdpApi.getRegion();
        }
        mRecyclerRegionList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerRegionList.setHasFixedSize(true);

        mRecyclerRegionList.setAdapter(mRegionListAdapter);
        mRegionListAdapter.getOnClickRegionPublishSubject()
                .subscribe(region -> {
                    mPrefManager.setSelectedRegion(region);
                    mRegionListAdapter.selectRegion(region);
                    onSettingChanged(MenuActivity.OPERATING_CODE_REGION);
//                    mRegionListAdapter.selectRegion(region);
                });

        List<String> regionList = Arrays.asList(getResources().getStringArray(R.array.regions));
        mRegionListAdapter.setRegionList(regionList, currentRegion);

        if (!TextUtils.isEmpty(currentRegion)) {
            for (int i = 0; i < regionList.size(); i++) {
                String region = regionList.get(i);
                if (region.contains(currentRegion) || currentRegion.contains(region)) {
                    mRecyclerRegionList.scrollToPosition(i);
                }
            }
        }
    }
}
