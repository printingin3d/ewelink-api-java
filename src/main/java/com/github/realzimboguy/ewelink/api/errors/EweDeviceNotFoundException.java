package com.github.realzimboguy.ewelink.api.errors;

public class EweDeviceNotFoundException extends EweException {
    private static final long serialVersionUID = 2507592257805844773L;

    public EweDeviceNotFoundException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public EweDeviceNotFoundException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public EweDeviceNotFoundException(String arg0) {
        super(arg0);
    }

    public EweDeviceNotFoundException(Throwable arg0) {
        super(arg0);
    }
}
