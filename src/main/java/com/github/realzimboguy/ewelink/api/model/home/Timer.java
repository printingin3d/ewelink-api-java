
package com.github.realzimboguy.ewelink.api.model.home;


import com.google.gson.annotations.SerializedName;


public class Timer {
    private String mId;
    private String type;
    private String at;
    private String coolkitTimerType;
    private Integer enabled;
    @SerializedName("do")
    private Do _do;
    public String getmId() {
        return mId;
    }
    public String getType() {
        return type;
    }
    public String getAt() {
        return at;
    }
    public String getCoolkitTimerType() {
        return coolkitTimerType;
    }
    public Integer getEnabled() {
        return enabled;
    }
    public Do get_do() {
        return _do;
    }
}
