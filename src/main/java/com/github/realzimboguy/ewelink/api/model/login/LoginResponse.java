
package com.github.realzimboguy.ewelink.api.model.login;

public class LoginResponse {
    private int error;
    private String msg;
    private Data data;
    public int getError() {
        return error;
    }
    public String getMsg() {
        return msg;
    }
    public Data getData() {
        return data;
    }
}
