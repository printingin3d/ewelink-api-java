package com.github.realzimboguy.ewelink.api.errors;

public class EweLoginException extends EweException {
    private static final long serialVersionUID = -7963524978395173275L;

    public EweLoginException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public EweLoginException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public EweLoginException(String arg0) {
        super(arg0);
    }

    public EweLoginException(Throwable arg0) {
        super(arg0);
    }
}
