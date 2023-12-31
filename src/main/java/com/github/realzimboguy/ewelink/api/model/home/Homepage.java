
package com.github.realzimboguy.ewelink.api.model.home;

public class Homepage {
    private int error;
    private String msg;
    private Data data;

    @Override
    public String toString() {
        return "Homepage{" +
                "error=" + error +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

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
