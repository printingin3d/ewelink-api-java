package com.github.realzimboguy.ewelink.api.model.home;

import com.google.gson.annotations.SerializedName;

public class OutletSwitch {
	private int outlet;
	@SerializedName("switch")
	private String _switch;

	public int getOutlet() {
        return outlet;
    }
    public String get_switch() {
        return _switch;
    }

    @Override
	public String toString() {

		return "OutletSwitch{" +
				"outlet=" + outlet +
				", _switch='" + _switch + '\'' +
				'}';
	}
}
