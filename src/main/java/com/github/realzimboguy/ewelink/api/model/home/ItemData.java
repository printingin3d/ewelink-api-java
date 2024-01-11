
package com.github.realzimboguy.ewelink.api.model.home;

import java.util.List;


public class ItemData {
    private String name;
    private String deviceid;
    private String apikey;
    private Extra extra;
    private String brandName;
    private String brandLogo;
    private Boolean showBrand;
    private String productModel;
    private Tags tags;
    private DevConfig devConfig;
    private Settings settings;
    private List<Object> devGroups;
    private Family__1 family;
    private List<Object> shareTo;
    private String devicekey;
    private boolean online;
    private Params params;
    private Boolean isSupportGroup;
    private Boolean isSupportedOnMP;
    private Boolean isSupportChannelSplit;
    private DeviceFeature deviceFeature;

    public String getName() {
        return name;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public String getApikey() {
        return apikey;
    }

    public Extra getExtra() {
        return extra;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getBrandLogo() {
        return brandLogo;
    }

    public Boolean getShowBrand() {
        return showBrand;
    }

    public String getProductModel() {
        return productModel;
    }

    public Tags getTags() {
        return tags;
    }

    public DevConfig getDevConfig() {
        return devConfig;
    }

    public Settings getSettings() {
        return settings;
    }

    public List<Object> getDevGroups() {
        return devGroups;
    }

    public Family__1 getFamily() {
        return family;
    }

    public List<Object> getShareTo() {
        return shareTo;
    }

    public String getDevicekey() {
        return devicekey;
    }

    public boolean isOnline() {
        return online;
    }

    public Params getParams() {
        return params;
    }

    public Boolean getIsSupportGroup() {
        return isSupportGroup;
    }

    public Boolean getIsSupportedOnMP() {
        return isSupportedOnMP;
    }

    public Boolean getIsSupportChannelSplit() {
        return isSupportChannelSplit;
    }

    public DeviceFeature getDeviceFeature() {
        return deviceFeature;
    }

    @Override
    public String toString() {
        return "ItemData{" +
                "name='" + name + '\'' +
                ", deviceid='" + deviceid + '\'' +
                ", apikey='" + apikey + '\'' +
                ", extra=" + extra +
                ", brandName='" + brandName + '\'' +
                ", brandLogo='" + brandLogo + '\'' +
                ", showBrand=" + showBrand +
                ", productModel='" + productModel + '\'' +
                ", tags=" + tags +
                ", devConfig=" + devConfig +
                ", settings=" + settings +
                ", devGroups=" + devGroups +
                ", family=" + family +
                ", shareTo=" + shareTo +
                ", devicekey='" + devicekey + '\'' +
                ", online=" + online +
                ", params=" + params +
                ", isSupportGroup=" + isSupportGroup +
                ", isSupportedOnMP=" + isSupportedOnMP +
                ", isSupportChannelSplit=" + isSupportChannelSplit +
                ", deviceFeature=" + deviceFeature +
                '}';
    }
}
