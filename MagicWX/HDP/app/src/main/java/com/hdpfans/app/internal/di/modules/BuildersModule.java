package com.hdpfans.app.internal.di.modules;

import com.hdpfans.app.internal.di.ActivityScope;
import com.hdpfans.app.receiver.BootBroadcastReceiver;
import com.hdpfans.app.service.ApiService;
import com.hdpfans.app.service.LocalServerService;
import com.hdpfans.app.service.VoiceService;
import com.hdpfans.app.ui.live.ChannelListActivity;
import com.hdpfans.app.ui.live.ChannelManagerActivity;
import com.hdpfans.app.ui.live.ChannelSourceListActivity;
import com.hdpfans.app.ui.live.LivePlayActivity;
import com.hdpfans.app.ui.live.MenuActivity;
import com.hdpfans.app.ui.live.fragment.FeatureSettingFragment;
import com.hdpfans.app.ui.live.fragment.HelpSettingFragment;
import com.hdpfans.app.ui.live.fragment.OtherSettingFragment;
import com.hdpfans.app.ui.live.fragment.RegionSettingFragment;
import com.hdpfans.app.ui.live.fragment.SubBootChannelFragment;
import com.hdpfans.app.ui.live.fragment.SubChannelEpgFragment;
import com.hdpfans.app.ui.live.fragment.SubDisplayTextSizeFragment;
import com.hdpfans.app.ui.live.fragment.SubRegionChannelFragment;
import com.hdpfans.app.ui.live.fragment.SubSystemBootFragment;
import com.hdpfans.app.ui.live.fragment.SubVoiceSupportFragment;
import com.hdpfans.app.ui.main.ExitActivity;
import com.hdpfans.app.ui.main.HdpTipsActivity;
import com.hdpfans.app.ui.main.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class BuildersModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract MainActivity mainActivityInject();

    @ActivityScope
    @ContributesAndroidInjector
    abstract LivePlayActivity livePlayActivityInject();

    @ActivityScope
    @ContributesAndroidInjector
    abstract ChannelListActivity channelListActivityInject();

    @ActivityScope
    @ContributesAndroidInjector
    abstract ExitActivity exitActivityInject();

    @ActivityScope
    @ContributesAndroidInjector
    abstract MenuActivity menuActivityInject();

    @ActivityScope
    @ContributesAndroidInjector
    abstract ChannelSourceListActivity channelSourceListActivityInject();

    @ContributesAndroidInjector
    abstract LocalServerService localServerServiceInject();

    @ContributesAndroidInjector
    abstract ApiService apiServiceInject();

    @ContributesAndroidInjector
    abstract VoiceService voiceServiceInject();

    @ContributesAndroidInjector
    abstract BootBroadcastReceiver bootBroadcastReceiverInject();

    @ActivityScope
    @ContributesAndroidInjector
    abstract SubRegionChannelFragment subRegionChannelFragmentInject();

    @ActivityScope
    @ContributesAndroidInjector
    abstract SubBootChannelFragment subBootChannelFragmentInject();

    @ActivityScope
    @ContributesAndroidInjector
    abstract SubDisplayTextSizeFragment subTextSizeFragmentInject();

    @ActivityScope
    @ContributesAndroidInjector
    abstract SubChannelEpgFragment subChannelEpgFragmentInject();

    @ActivityScope
    @ContributesAndroidInjector
    abstract SubSystemBootFragment subSystemBootFragmentInject();

    @ActivityScope
    @ContributesAndroidInjector
    abstract SubVoiceSupportFragment subVoiceSupportFragmentInject();

    @ActivityScope
    @ContributesAndroidInjector
    abstract RegionSettingFragment regionSettingFragmentInject();

    @ActivityScope
    @ContributesAndroidInjector
    abstract FeatureSettingFragment featureSettingFragmentInject();

    @ActivityScope
    @ContributesAndroidInjector
    abstract HelpSettingFragment helpSettingFragmentInject();

    @ActivityScope
    @ContributesAndroidInjector
    abstract OtherSettingFragment otherSettingFragmentInject();

    @ActivityScope
    @ContributesAndroidInjector
    abstract HdpTipsActivity hdpTipsActivityInject();

    @ActivityScope
    @ContributesAndroidInjector
    abstract ChannelManagerActivity channelManagerActivityInject();
}
