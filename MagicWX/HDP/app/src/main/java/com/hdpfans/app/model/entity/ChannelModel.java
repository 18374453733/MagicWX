package com.hdpfans.app.model.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "channel", indices = @Index(value = {"itemId"}))
public class ChannelModel implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long cid;

    /**
     * 频道id
     */
    private int id;

    /**
     * 频道节目源列表（已加密）
     */
    @SerializedName("urllist")
    private String tmpUrls;

    /**
     * 频道节目源列表
     */
    @Ignore
    private List<String> urls;

    /**
     * 频道编号
     */
    private int num;

    /**
     * 频道编号别名
     */
    @Ignore
    private String numAlias;

    /**
     * 频道名称
     */
    private String name;

    /**
     * 频道区域
     */
    private String area;

    /**
     * 分类id
     */
    @SerializedName("itemid")
    private int itemId;

    private String pinyin;

    /**
     * 频道清晰度
     */
    private String quality;

    /**
     * epg编号
     */
    @SerializedName("epgid")
    private String epgId;

    /**
     * 排序
     */
    private int weigh;

    /**
     * 增量更新类型
     */
    @Ignore
    @SerializedName("type")
    private int updateType;

    /**
     * 频道角标图片地址
     */
    @Ignore
    private String markUrl;

    /**
     * 是否为地方台
     * 1：非地方台，0：地方台
     */
    private int main;

    /**
     * 是否收藏
     */
    private boolean collect;

    /**
     * 是否隐藏
     */
    private boolean hidden;

    public ChannelModel() {

    }

    protected ChannelModel(Parcel in) {
        cid = in.readLong();
        id = in.readInt();
        tmpUrls = in.readString();
        urls = in.createStringArrayList();
        num = in.readInt();
        numAlias = in.readString();
        name = in.readString();
        area = in.readString();
        itemId = in.readInt();
        pinyin = in.readString();
        quality = in.readString();
        epgId = in.readString();
        weigh = in.readInt();
        updateType = in.readInt();
        markUrl = in.readString();
        main = in.readInt();
        collect = in.readByte() != 0;
        hidden = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(cid);
        dest.writeInt(id);
        dest.writeString(tmpUrls);
        dest.writeStringList(urls);
        dest.writeInt(num);
        dest.writeString(numAlias);
        dest.writeString(name);
        dest.writeString(area);
        dest.writeInt(itemId);
        dest.writeString(pinyin);
        dest.writeString(quality);
        dest.writeString(epgId);
        dest.writeInt(weigh);
        dest.writeInt(updateType);
        dest.writeString(markUrl);
        dest.writeInt(main);
        dest.writeByte((byte) (collect ? 1 : 0));
        dest.writeByte((byte) (hidden ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ChannelModel> CREATOR = new Creator<ChannelModel>() {
        @Override
        public ChannelModel createFromParcel(Parcel in) {
            return new ChannelModel(in);
        }

        @Override
        public ChannelModel[] newArray(int size) {
            return new ChannelModel[size];
        }
    };

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTmpUrls() {
        return tmpUrls;
    }

    public void setTmpUrls(String tmpUrls) {
        this.tmpUrls = tmpUrls;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getNumAlias() {
        return numAlias;
    }

    public void setNumAlias(String numAlias) {
        this.numAlias = numAlias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getEpgId() {
        return epgId;
    }

    public void setEpgId(String epgId) {
        this.epgId = epgId;
    }

    public int getWeigh() {
        return weigh;
    }

    public void setWeigh(int weigh) {
        this.weigh = weigh;
    }

    public int getUpdateType() {
        return updateType;
    }

    public void setUpdateType(int updateType) {
        this.updateType = updateType;
    }

    public String getMarkUrl() {
        return markUrl;
    }

    public void setMarkUrl(String markUrl) {
        this.markUrl = markUrl;
    }

    public int getMain() {
        return main;
    }

    public void setMain(int main) {
        this.main = main;
    }

    public boolean isCollect() {
        return collect;
    }

    public void setCollect(boolean collect) {
        this.collect = collect;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
