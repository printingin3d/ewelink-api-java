package com.github.realzimboguy.ewelink.api.errors;

public class EweException extends RuntimeException {
    private static final long serialVersionUID = 4136270608339925695L;

    public EweException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public EweException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public EweException(String arg0) {
        super(arg0);
    }

    public EweException(Throwable arg0) {
        super(arg0);
    }
}
