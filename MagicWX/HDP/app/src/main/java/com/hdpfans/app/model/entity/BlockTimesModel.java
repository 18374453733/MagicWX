package com.hdpfans.app.model.entity;

/**
 * 节目频道屏蔽时间
 */
public class BlockTimesModel {
    /**
     * 开始时间，以秒为单位
     */
    private long start;

    /**
     * 结束时间，以秒为单位
     */
    private long end;

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }
}
