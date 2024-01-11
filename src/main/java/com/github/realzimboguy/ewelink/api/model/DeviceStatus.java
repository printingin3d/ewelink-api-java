package com.github.realzimboguy.ewelink.api.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceStatus.class);
    
    public abstract String getStringValue();
    
    public static DeviceStatus fromString(String value) {
        switch (value.toLowerCase()) {
        case "on":
            return ON;
        case "off":
            return OFF;
        }
        LOGGER.warn("Unknown device status: {}", value);
        return OFF;
    }
}
