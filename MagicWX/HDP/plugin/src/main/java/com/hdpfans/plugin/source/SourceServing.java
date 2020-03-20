package com.hdpfans.plugin.source;

import android.util.Pair;

public abstract class SourceServing {

    protected abstract Pair<Integer, String> create(int channelNum);

}
