package com.github.realzimboguy.ewelink.api.errors;

public class EweResponseException extends EweException {
    private static final long serialVersionUID = -5279001280472084165L;

    public EweResponseException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public EweResponseException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public EweResponseException(String arg0) {
        super(arg0);
    }

    public EweResponseException(Throwable arg0) {
        super(arg0);
    }
}
