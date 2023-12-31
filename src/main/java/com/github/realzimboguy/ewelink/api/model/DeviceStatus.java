package com.github.realzimboguy.ewelink.api.model;

public enum DeviceStatus {
    ON {
        @Override
        public String getStringValue() {
            return "on";
        }
    }, OFF {
        @Override
        public String getStringValue() {
            return "off";
        }
    };
    
    public abstract String getStringValue();
}
