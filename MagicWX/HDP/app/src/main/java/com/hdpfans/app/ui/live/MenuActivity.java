package com.hdpfans.app.ui.live;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.hdpfans.app.frame.FrameActivity;
import com.hdpfans.app.ui.live.adapter.MenuMainAdapter;
import com.hdpfans.app.ui.live.fragment.ChannelManagerSettingFragment;
import com.hdpfans.app.ui.live.fragment.FeatureSettingFragment;
import com.hdpfans.app.ui.live.fragment.HelpSettingFragment;
import com.hdpfans.app.ui.live.fragment.MainSettingFragment;
import com.hdpfans.app.ui.live.fragment.OperatingSettingFragment;
import com.hdpfans.app.ui.live.fragment.OtherSettingFragment;
import com.hdpfans.app.ui.live.fragment.PersonalSettingFragment;
import com.hdpfans.app.ui.live.fragment.RegionSettingFragment;
import com.hdpfans.app.ui.live.fragment.SettingFragment;
import com.hdpfans.app.ui.widget.media.FocusKeepRecyclerView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import hdpfans.com.R;

public class MenuActivity extends FrameActivity {

    public static final String RESULT_RESULT_OPERATING_CODE = "result_operating_code";

    public static final int OPERATING_CODE_EXIT = 0x1;

    public static final int OPERATING_CODE_TOGGLE_RATIO = 0x2;

    public static final int OPERATING_CODE_TOGGLE_PLAYER = 0x3;

    public static final int OPERATING_CODE_CHANNEL_MANAGER = 0x4;

    public static final int OPERATING_CODE_HELP = 0x5;

    public static final int OPERATING_CODE_FEATURE = 0x6;

    public static final int OPERATING_CODE_REGION_CHANNEL = 0x7;

    public static final int OPERATING_CODE_BOOT_CHANNEL = 0x8;

    public static final int OPERATING_CODE_TEXT_SIZE = 0x9;

    public static final int OPERATING_CODE_EPG = 0x10;

    public static final int OPERATING_CODE_VOICE_SUPPORT = 0x11;

    public static final int OPERATING_CODE_SYSTEM_BOOT = 0x12;

    public static final int OPERATING_CODE_CLEAR_CACHE = 0x13;

    public static final int OPERATING_CODE_REGION = 0x14;

    @BindView(R.id.layout_setting_container)
    ViewGroup mLayoutSettingContainer;
    @BindView(R.id.list_menu)
    FocusKeepRecyclerView recyclerView;

    private List<String> data;
    private Map<String, SettingFragment> fragments = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        data = Arrays.asList(getResources().getStringArray(R.array.menu_setting));
        initFragments();

        MenuMainAdapter adapter = new MenuMainAdapter(data);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MenuMainAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                if (position == 8) {
                    Intent intent = new Intent();
                    intent.putExtra(RESULT_RESULT_OPERATING_CODE, OPERATING_CODE_EXIT);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    mLayoutSettingContainer.setVisibility(View.VISIBLE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.layout_setting_container, fragments.get(data.get(position))).commitAllowingStateLoss();
                }
            }

            @Override
            public void OnFocusChangeListener(int position, View view, boolean b) {
                if (b && mLayoutSettingContainer != null) {
                    if (position == 8) {
                        mLayoutSettingContainer.setVisibility(View.GONE);
                    } else {
                        mLayoutSettingContainer.setVisibility(View.VISIBLE);
                        getSupportFragmentManager().beginTransaction().replace(R.id.layout_setting_container, fragments.get(data.get(position))).commitAllowingStateLoss();
                    }
                }
            }
        });

        // 点击空白处关闭
        findViewById(android.R.id.content).setOnClickListener(v -> finish());
    }

    @Override
    public void onBackPressed() {
        recyclerView.setAdapter(null);
        finish();
    }

    private void initFragments() {
        fragments.put(data.get(0), PersonalSettingFragment.newInstance());
        fragments.put(data.get(1), ChannelManagerSettingFragment.newInstance());
        fragments.put(data.get(2), MainSettingFragment.newInstance());
        fragments.put(data.get(3), OperatingSettingFragment.newInstance());
        fragments.put(data.get(4), RegionSettingFragment.newInstance());
        fragments.put(data.get(5), FeatureSettingFragment.newInstance());
        fragments.put(data.get(6), OtherSettingFragment.newInstance());
        fragments.put(data.get(7), HelpSettingFragment.newInstance());
        fragments.put(data.get(8), null);
    }
}
