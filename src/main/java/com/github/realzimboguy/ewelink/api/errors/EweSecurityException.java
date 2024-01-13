package com.github.realzimboguy.ewelink.api.errors;

public class EweSecurityException extends EweException {
    private static final long serialVersionUID = -3685832202865748039L;

    public EweSecurityException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public EweSecurityException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public EweSecurityException(String arg0) {
        super(arg0);
    }

    public EweSecurityException(Throwable arg0) {
        super(arg0);
    }
}
