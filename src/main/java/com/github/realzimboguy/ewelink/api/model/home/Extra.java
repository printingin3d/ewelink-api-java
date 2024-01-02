
package com.github.realzimboguy.ewelink.api.model.home;

public class Extra {
    private Integer uiid;
    private String description;
    private String brandId;
    private String apmac;
    private String mac;
    private String ui;
    private String modelInfo;
    private String model;
    private String manufacturer;
    private String staMac;
    private String chipid;

    public Integer getUiid() {
        return uiid;
    }

    public String getDescription() {
        return description;
    }

    public String getBrandId() {
        return brandId;
    }

    public String getApmac() {
        return apmac;
    }

    public String getMac() {
        return mac;
    }

    public String getUi() {
        return ui;
    }

    public String getModelInfo() {
        return modelInfo;
    }

    public String getModel() {
        return model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getStaMac() {
        return staMac;
    }

    public String getChipid() {
        return chipid;
    }

    @Override
    public String toString() {
        return "Extra{" +
                "uiid=" + uiid +
                ", description='" + description + '\'' +
                ", brandId='" + brandId + '\'' +
                ", apmac='" + apmac + '\'' +
                ", mac='" + mac + '\'' +
                ", ui='" + ui + '\'' +
                ", modelInfo='" + modelInfo + '\'' +
                ", model='" + model + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", staMac='" + staMac + '\'' +
                ", chipid='" + chipid + '\'' +
                '}';
    }
}
